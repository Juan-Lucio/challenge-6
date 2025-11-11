/*
 * jest.setup.js
 * This file runs before every test.
 */

// Mock 1: fetch
global.fetch = jest.fn();

// Mock 2: WebSocket (Interactive)
const mockSocketInstance = {
  onopen: () => {},
  onclose: () => {},
  onmessage: () => {},
  onerror: () => {},
  send: jest.fn(),
};

global.mockSocketInstance = mockSocketInstance;

global.WebSocket = jest.fn((url) => {
  console.log(`[MockWebSocket] Connecting to: ${url}`);
  return mockSocketInstance;
});