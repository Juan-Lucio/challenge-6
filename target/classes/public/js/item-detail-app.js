/*
 * item-detail-app.js
 * REFACTORED: Functions now find their own DOM elements
 * and format currency correctly.
 */

// --- (NUEVA FUNCIÓN DE AYUDA) ---
/**
 * Helper function to format a number into USD currency.
 * @param {number} amount The number to format.
 */
function formatCurrency(amount) {
    if (typeof amount !== 'number') {
        amount = parseFloat(amount) || 0;
    }
    return new Intl.NumberFormat('en-US', {
        style: 'currency',
        currency: 'USD',
        maximumFractionDigits: 2,
        minimumFractionDigits: 2
    }).format(amount);
}

/**
 * Entry point.
 */
export function init() {
    console.log("Item Detail App Initialized.");
    const appContainer = document.getElementById('app-container');
    const urlParams = new URLSearchParams(window.location.search);
    const currentItemId = urlParams.get('id');

    if (!currentItemId) {
        if(appContainer) appContainer.innerHTML = '<h1>Error: No item ID provided.</h1>';
        return;
    }

    loadItemData(currentItemId);
    connectWebSocket(currentItemId);
}

/**
 * Fetches all item and offer data from the APIs
 * @param {string} itemId The ID of the item to fetch
 */
export async function loadItemData(itemId) {
    try {
        const [itemRes, offersRes] = await Promise.all([
            fetch(`/api/items/${itemId}`),
            fetch(`/api/offers/${itemId}`)
        ]);

        if (!itemRes.ok) {
            throw new Error(`Could not fetch item. Status: ${itemRes.status}`);
        }
        if (!offersRes.ok) {
            throw new Error(`Could not fetch offers. Status: ${offersRes.status}`);
        }

        const item = await itemRes.json();
        const offers = await offersRes.json();

        renderPage(item, offers);

    } catch (error) {
        console.error("Failed to load item data:", error);
        const appContainer = document.getElementById('app-container');
        if(appContainer) appContainer.innerHTML = `<h1>Error: ${error.message}</h1>`;
    }
}

/**
 * Connects to the price update WebSocket.
 * @param {string} itemId The ID of the item to watch
 */
export function connectWebSocket(itemId) {
    const protocol = window.location.protocol === "https" ? "wss" : "ws";
    const socket = new WebSocket(`${protocol}://${window.location.host}/ws/price-updates`);

    socket.onopen = () => console.log("[WebSocket] Connection established.");
    socket.onclose = () => console.log("[WebSocket] Connection closed.");
    socket.onerror = (err) => console.error("[WebSocket] Error:", err);

    socket.onmessage = (event) => {
        const message = JSON.parse(event.data);
        
        if (message.type === "PRICE_UPDATE" && message.itemId === itemId) {
            console.log("[WebSocket] Price update received!", message);
            const priceEl = document.getElementById("price-display");
            if (priceEl) {
                // --- ¡CORRECCIÓN! Usamos el formateador ---
                priceEl.textContent = formatCurrency(message.newPrice);
            }
        }
    };
    return socket;
}

/**
 * Renders the entire page content into the app container.
 */
export function renderPage(item, offers) {
    const headerTitle = document.getElementById('item-name-header');
    const appContainer = document.getElementById('app-container');

    if (headerTitle) headerTitle.textContent = item.name;

    const html = `
        <div class="item-main-info">
            <div class="item-image-large">
                <img src="${item.imageUrl}" alt="${item.name}">
            </div>
            <div class="item-text-info">
                <p class="item-description">${item.description}</p>
                <h3 class="current-price">Current Value: 
                    <span id="price-display">${formatCurrency(item.price)}</span>
                </h3>
                <hr>
                ${renderOfferForm(item.id)}
            </div>
        </div>
        <div class="offer-list-section">
            <h2>Current Bids</h2>
            <div class="offers-container" id="offers-container">
                ${renderOfferList(offers)}
            </div>
        </div>
    `;
    
    if (appContainer) appContainer.innerHTML = html;
    attachFormListener(item.id);
}

/**
 * Renders the HTML for the offer form.
 */
export function renderOfferForm(itemId) {
    // (Esta función no cambia)
    return `
    <div class="offer-section">
        <h2>Make an Offer</h2>
        <form id="offer-form" action="/${itemId}/offer" method="POST">
            <div class="form-group">
                <label for="offerAmount">Your Offer (USD):</label>
                <input type="number" id="offerAmount" name="offerAmount" step="0.01" min="0" required class="form-control">
            </div>
            <div class="form-group">
                <label for="bidderName">Your Name:</label>
                <input type="text" id="bidderName" name="bidderName" required class="form-control">
            </div>
            <div class="form-group">
                <label for="bidderEmail">Your Email:</label>
                <input type="email" id="bidderEmail" name="bidderEmail" required class="form-control">
            </div>
            <button type="submit" class="submit-button">Submit Offer</button>
            <div id="form-message" class="form-message"></div>
        </form>
    </div>
    `;
}

/**
 * Renders the HTML for the list of offers.
 */
export function renderOfferList(offers) {
    if (!offers || offers.length === 0) {
        return '<p class="no-offers">No bids yet. Be the first to make an offer!</p>';
    }
    
    return offers.map(offer => `
        <div class="offer-card">
            <p class="offer-amount"><strong>${formatCurrency(offer.amount)}</strong></p>
            <p class="offer-bidder">From: ${offer.name}</p>
            <p class="offer-email">(${offer.email})</p>
        </div>
    `).join('');
}

/**
 * Attaches the 'submit' event listener to the offer form.
 */
export function attachFormListener(itemId) {
    const form = document.getElementById('offer-form');
    if (!form) return;

    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        
        const formMessage = document.getElementById('form-message');
        if (formMessage) formMessage.textContent = 'Submitting...';

        try {
            const response = await fetch(form.action, {
                method: 'POST',
                body: new URLSearchParams(new FormData(form))
            });

            if (!response.ok) {
                const errData = await response.json();
                throw new Error(errData.error || "Submission failed");
            }

            const result = await response.json();
            if (formMessage) formMessage.textContent = 'Offer submitted successfully!';
            form.reset();
            
            // Manually refresh the offer list
            const offersRes = await fetch(`/api/offers/${itemId}`);
            const offers = await offersRes.json();
            
            const offersContainer = document.getElementById('offers-container');
            if (offersContainer) offersContainer.innerHTML = renderOfferList(offers);

        } catch (error) {
            if (formMessage) {
                formMessage.textContent = `Error: ${error.message}`;
                formMessage.style.color = '#d9534f';
            }
        }
    });
}