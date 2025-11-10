/*
 * item-detail-app.js
 * Main JavaScript application for the item detail page (Opción 3).
 * Handles fetching data, rendering the DOM, and WebSocket connection.
 */

// --- MODULE: State & DOM ---
// This object holds references to our main DOM elements
const DOM = {
    appContainer: document.getElementById('app-container'),
    headerTitle: document.getElementById('item-name-header'),
};

// Global state to hold the current item ID
let currentItemId = null;

/**
 * Entry point. Runs when the script loads.
 */
function init() {
    console.log("Item Detail App Initialized.");
    
    // 1. Get the item ID from the URL (e.g., /item.html?id=item1)
    const urlParams = new URLSearchParams(window.location.search);
    currentItemId = urlParams.get('id');

    if (!currentItemId) {
        DOM.appContainer.innerHTML = '<h1>Error: No item ID provided.</h1>';
        return;
    }

    // 2. Load all necessary data
    loadItemData();
    
    // 3. Connect to WebSocket
    connectWebSocket();
}

/**
 * Fetches all item and offer data from the APIs
 */
async function loadItemData() {
    try {
        // We use Promise.all to fetch item data and offer data in parallel
        const [itemRes, offersRes] = await Promise.all([
            fetch(`/api/items/${currentItemId}`),
            fetch(`/api/offers/${currentItemId}`)
        ]);

        if (!itemRes.ok) {
            throw new Error(`Could not fetch item. Status: ${itemRes.status}`);
        }
        if (!offersRes.ok) {
            throw new Error(`Could not fetch offers. Status: ${offersRes.status}`);
        }

        const item = await itemRes.json();
        const offers = await offersRes.json();

        // 4. Render the full page
        renderPage(item, offers);

    } catch (error) {
        console.error("Failed to load item data:", error);
        DOM.appContainer.innerHTML = `<h1>Error: ${error.message}</h1>`;
    }
}

/**
 * Connects to the price update WebSocket.
 */
function connectWebSocket() {
    const protocol = window.location.protocol === "https" ? "wss" : "ws";
    const socket = new WebSocket(`${protocol}://${window.location.host}/ws/price-updates`);

    socket.onopen = () => console.log("[WebSocket] Connection established.");
    socket.onclose = () => console.log("[WebSocket] Connection closed.");
    socket.onerror = (err) => console.error("[WebSocket] Error:", err);

    socket.onmessage = (event) => {
        const message = JSON.parse(event.data);

        // Listen for price updates for THIS item
        if (message.type === "PRICE_UPDATE" && message.itemId === currentItemId) {
            console.log("[WebSocket] Price update received!", message);
            // Update the price in the DOM
            const priceEl = document.getElementById("price-display");
            if (priceEl) {
                priceEl.textContent = `${message.newPrice} USD`;
            }
        }
    };
}


// --- MODULE: Rendering ---

/**
 * Renders the entire page content into the app container.
 * @param {object} item - The item object from the API.
 * @param {Array} offers - The list of offer objects from the API.
 */
function renderPage(item, offers) {
    // Update header
    DOM.headerTitle.textContent = item.name;

    // Generate the HTML
    const html = `
        <div class="item-main-info">
            <div class="item-image-large">
                <img src="${item.imageUrl}" alt="${item.name}">
            </div>
            <div class="item-text-info">
                <p class="item-description">${item.description}</p>
                <h3 class="current-price">Current Value: 
                    <span id="price-display">${item.price} USD</span>
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

    // Replace the loading spinner with the new HTML
    DOM.appContainer.innerHTML = html;

    // 5. Attach event listener to the new form
    attachFormListener(item.id);
}

/**
 * Renders the HTML for the offer form.
 * @param {string} itemId - The item's ID.
 */
function renderOfferForm(itemId) {
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
 * @param {Array} offers - The list of offer objects.
 */
function renderOfferList(offers) {
    if (offers.length === 0) {
        return '<p class="no-offers">No bids yet. Be the first to make an offer!</p>';
    }
    
    // Use map() to transform each offer object into an HTML string
    return offers.map(offer => `
        <div class="offer-card">
            <p class="offer-amount"><strong>$${offer.amount.toFixed(2)}</strong></p>
            <p class="offer-bidder">From: ${offer.name}</p>
            <p class="offer-email">(${offer.email})</p>
        </div>
    `).join(''); // Join all strings into one block
}


// --- MODULE: Event Handlers ---

/**
 * Attaches the 'submit' event listener to the offer form.
 */
function attachFormListener(itemId) {
    const form = document.getElementById('offer-form');
    if (!form) return;

    form.addEventListener('submit', async (e) => {
        // Prevent the default form submission (which causes a page reload)
        e.preventDefault();

        const formMessage = document.getElementById('form-message');
        formMessage.textContent = 'Submitting...';

        try {
            // Send the form data using fetch
            const response = await fetch(form.action, {
                method: 'POST',
                body: new URLSearchParams(new FormData(form)) // Serialize form
            });

            if (!response.ok) {
                const errData = await response.json();
                throw new Error(errData.error || "Submission failed");
            }

            // Success!
            const result = await response.json();
            formMessage.textContent = 'Offer submitted successfully!';
            
            // Clear the form
            form.reset();
            
            // Manually refresh the offer list to show the new one
            const offersRes = await fetch(`/api/offers/${currentItemId}`);
            const offers = await offersRes.json();
            document.getElementById('offers-container').innerHTML = renderOfferList(offers);

            // Note: We don't need to manually update the price here,
            // because the WebSocket broadcast (triggered by the server)
            // will handle it.

        } catch (error) {
            formMessage.textContent = `Error: ${error.message}`;
            formMessage.style.color = '#d9534f';
        }
    });
}

// --- Run the app ---
init();