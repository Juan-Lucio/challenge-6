/*
 * item-detail-app.test.js
 * Unit tests for the Item Detail application.
 * FINAL SPRINT 2 VERSION (with init tests).
 */

import * as app from './item-detail-app.js';

describe('Item Detail App (Frontend)', () => {

    // --- Suite 1: Pure Function (PASSED) ---
    describe('renderOfferList', () => {
        test('should render "no offers" message when list is empty', () => {
            const html = app.renderOfferList([]);
            expect(html).toContain("No bids yet");
        });
        test('should render "no offers" message when list is null', () => {
            const html = app.renderOfferList(null);
            expect(html).toContain("No bids yet");
        });
        test('should render a list of offers', () => {
            const mockOffers = [
                { amount: 100, name: 'User A', email: 'a@test.com' },
                { amount: 150, name: 'User B', email: 'b@test.com' }
            ];
            const html = app.renderOfferList(mockOffers);
            expect(html).toContain("$100.00");
        });
    });

    // --- Suite 2: RenderPage (PASSED) ---
    describe('renderPage', () => {
        beforeEach(() => {
            document.body.innerHTML = `
                <h1 id="item-name-header"></h1>
                <main id="app-container"></main>
            `;
        });
        
        const mockItem = { id: 'item1', name: 'Test Helmet', description: 'A test desc.', price: 123.45, imageUrl: 'test.jpg' };
        const mockOffers = [{ amount: 100, name: 'User A', email: 'a@test.com' }];
        
        test('should render the item details', () => {
            app.renderPage(mockItem, mockOffers);
            const container = document.getElementById('app-container');
            expect(container.innerHTML).toContain("Test Helmet");
        });
        test('should render the offer form', () => {
            app.renderPage(mockItem, mockOffers);
            const form = document.getElementById('offer-form');
            expect(form).not.toBeNull();
        });
        test('should render the offer list', () => {
            app.renderPage(mockItem, mockOffers);
            const offers = document.getElementById('offers-container');
            expect(offers.innerHTML).toContain("$100.00");
        });
        test('should update the header title', () => {
            app.renderPage(mockItem, mockOffers);
            const header = document.getElementById('item-name-header');
            expect(header.textContent).toBe("Test Helmet");
        });
    });

    // --- Suite 3: loadItemData (PASSED) ---
    describe('loadItemData', () => {
        beforeEach(() => {
            fetch.mockClear();
            document.body.innerHTML = `
                <h1 id="item-name-header"></h1>
                <main id="app-container"></main>
            `;
        });

        test('should fetch data, call renderPage, and update DOM', async () => {
            const mockItem = { id: 'item1', name: 'Test Item', price: 100, description: 'Test', imageUrl: 'test.jpg' };
            const mockOffers = [{ amount: 50, name: 'Test Bidder' }];
            
            fetch.mockImplementation((url) => {
                if (url.includes('/api/items/')) return Promise.resolve({ ok: true, json: () => Promise.resolve(mockItem) });
                if (url.includes('/api/offers/')) return Promise.resolve({ ok: true, json: () => Promise.resolve(mockOffers) });
            });

            await app.loadItemData('item1'); 
            const container = document.getElementById('app-container');
            expect(fetch).toHaveBeenCalledTimes(2); 
            expect(container.innerHTML).toContain("Test Item");
        });

        test('should render an error message if item fetch fails (404)', async () => {
            fetch.mockImplementation(() => Promise.resolve({ ok: false, status: 404 }));
            const consoleErrorMock = jest.spyOn(console, 'error').mockImplementation(() => {});
            
            await app.loadItemData('item1'); 
            
            const container = document.getElementById('app-container');
            expect(fetch).toHaveBeenCalledTimes(2); 
            expect(container.innerHTML).toContain("Error: Could not fetch item. Status: 404");
            consoleErrorMock.mockRestore();
        });
        
        test('should render an error message if offers fetch fails (404)', async () => {
            const mockItem = { id: 'item1', name: 'Test Item', price: 100 };
            fetch.mockImplementation((url) => {
                if (url.includes('/api/items/')) return Promise.resolve({ ok: true, json: () => Promise.resolve(mockItem) });
                if (url.includes('/api/offers/')) return Promise.resolve({ ok: false, status: 404 });
            });
            const consoleErrorMock = jest.spyOn(console, 'error').mockImplementation(() => {});

            await app.loadItemData('item1'); 
            
            const container = document.getElementById('app-container');
            expect(fetch).toHaveBeenCalledTimes(2); 
            expect(container.innerHTML).toContain("Error: Could not fetch offers. Status: 404");
            consoleErrorMock.mockRestore();
        });
    });

    // --- Suite 4: attachFormListener (PASSED) ---
    describe('attachFormListener', () => {
        beforeEach(() => {
            fetch.mockClear();
            document.body.innerHTML = `
                <main id="app-container">
                    <form id="offer-form" action="/item1/offer" method="POST">
                        <input name="offerAmount" value="150">
                        <input name="bidderName" value="Test Bidder">
                        <input name="bidderEmail" value="test@test.com">
                        <div id="form-message"></div>
                    </form>
                    <div id="offers-container"></div>
                </main>
            `;
        });

        test('should submit form data and refresh offer list on success', async () => {
            const form = document.getElementById('offer-form');
            const formMessage = document.getElementById('form-message');
            const offersContainer = document.getElementById('offers-container');
            const newOffers = [{ amount: 150, name: 'Test Bidder', email: 'test@test.com' }];

            fetch.mockImplementation((url) => {
                if (url.includes('/api/offers/')) return Promise.resolve({ ok: true, json: () => Promise.resolve(newOffers) });
                if (url.includes('/offer')) return Promise.resolve({ ok: true, json: () => Promise.resolve({ success: true }) });
            });

            app.attachFormListener('item1');
            form.dispatchEvent(new Event('submit', { bubbles: true, cancelable: true }));
            await new Promise(resolve => setTimeout(resolve, 0));

            expect(fetch).toHaveBeenCalledTimes(2);
            expect(formMessage.textContent).toBe('Offer submitted successfully!');
        });

        test('should display error message if form submission fails (400)', async () => {
            const form = document.getElementById('offer-form');
            const formMessage = document.getElementById('form-message');
            fetch.mockImplementation((url) => {
                if (url.includes('/offer')) return Promise.resolve({ ok: false, json: () => Promise.resolve({ error: "API Failure" }) });
            });
            
            app.attachFormListener('item1');
            form.dispatchEvent(new Event('submit', { bubbles: true, cancelable: true }));
            await new Promise(resolve => setTimeout(resolve, 0));

            expect(fetch).toHaveBeenCalledTimes(1); 
            expect(formMessage.textContent).toBe("Error: API Failure");
        });

        test('should display error message if fetch itself throws exception', async () => {
            const form = document.getElementById('offer-form');
            const formMessage = document.getElementById('form-message');
            fetch.mockImplementation(() => Promise.reject(new Error("Network Down")));
            
            app.attachFormListener('item1');
            form.dispatchEvent(new Event('submit', { bubbles: true, cancelable: true }));
            await new Promise(resolve => setTimeout(resolve, 0)); 

            expect(fetch).toHaveBeenCalledTimes(1); 
            expect(formMessage.textContent).toBe("Error: Network Down");
        });
    });

    // --- Suite 5: connectWebSocket (PASSED) ---
    describe('connectWebSocket', () => {
        beforeEach(() => {
            jest.clearAllMocks();
            document.body.innerHTML = `<span id="price-display">100.00 USD</span>`;
        });

        test('should connect to the correct WebSocket URL', () => {
            app.connectWebSocket('item1'); 
            expect(global.WebSocket).toHaveBeenCalledWith(expect.stringContaining('ws://localhost/ws/price-updates'));
        });

        test('should update price DOM when the correct message is received', () => {
            const priceEl = document.getElementById('price-display');
            expect(priceEl.textContent).toBe('100.00 USD');
            app.connectWebSocket('item1'); 
            const mockMessage = { type: 'PRICE_UPDATE', itemId: 'item1', newPrice: '999.00' };
            const mockEvent = { data: JSON.stringify(mockMessage) };
            global.mockSocketInstance.onmessage(mockEvent);
            expect(priceEl.textContent).toBe('$999.00');
        });

        test('should NOT update price DOM if message is for a different item', () => {
            const priceEl = document.getElementById('price-display');
            expect(priceEl.textContent).toBe('100.00 USD');
            app.connectWebSocket('item1'); 
            const mockMessage = { type: 'PRICE_UPDATE', itemId: 'item2', newPrice: '999.00' };
            const mockEvent = { data: JSON.stringify(mockMessage) };
            global.mockSocketInstance.onmessage(mockEvent);
            expect(priceEl.textContent).toBe('100.00 USD');
        });

        test('should log an error when the socket connection fails', () => {
            const consoleErrorMock = jest.spyOn(console, 'error').mockImplementation(() => {});
            app.connectWebSocket('item1');
            global.mockSocketInstance.onerror(new Error("Connection Failed"));
            expect(consoleErrorMock).toHaveBeenCalledTimes(1);
            expect(consoleErrorMock).toHaveBeenCalledWith("[WebSocket] Error:", new Error("Connection Failed"));
            consoleErrorMock.mockRestore();
        });
    });

    // --- Suite 6: init (Main Function) - NEW TESTS ---
    describe('init (Main Function)', () => {
        
        // We must spy on the *module* itself
        const loadItemDataMock = jest.spyOn(app, 'loadItemData');
        const connectWebSocketMock = jest.spyOn(app, 'connectWebSocket');

        beforeEach(() => {
            // Reset spies
            loadItemDataMock.mockClear();
            connectWebSocketMock.mockClear();
            // Mock implementations to prevent them from *actually* running
            loadItemDataMock.mockImplementation(() => Promise.resolve()); 
            connectWebSocketMock.mockImplementation(() => {});
            
            // Set up the DOM *before* each test
            document.body.innerHTML = `
                <h1 id="item-name-header"></h1>
                <main id="app-container"></main>
            `;
        });

        test('should call services if ID is in URL', () => {
            // 1. Arrange: Mock the URL
            Object.defineProperty(window, 'location', {
                value: { search: '?id=item123' },
                writable: true
            });

            // 2. Act: Run the init function
            app.init();

            // 3. Assert: Check that the functions were called
            expect(loadItemDataMock).toHaveBeenCalledTimes(1);
            expect(loadItemDataMock).toHaveBeenCalledWith('item123');
            expect(connectWebSocketMock).toHaveBeenCalledTimes(1);
            expect(connectWebSocketMock).toHaveBeenCalledWith('item123');
        });

        test('should render error if no ID is in URL', () => {
            // 1. Arrange: Mock an empty URL
            Object.defineProperty(window, 'location', {
                value: { search: '' },
                writable: true
            });
            const container = document.getElementById('app-container');
            
            // 2. Act: Run the init function
            app.init();
            
            // 3. Assert: Check that services were NOT called
            expect(loadItemDataMock).not.toHaveBeenCalled();
            expect(connectWebSocketMock).not.toHaveBeenCalled();
            // Check that the error was rendered
            expect(container.innerHTML).toContain("Error: No item ID provided.");
        });
    });
});