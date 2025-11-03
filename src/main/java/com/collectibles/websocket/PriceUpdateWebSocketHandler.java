package com.collectibles.websocket;

import com.collectibles.utils.JsonUtil;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Handles all WebSocket connections for real-time price updates.
 */
@WebSocket
public class PriceUpdateWebSocketHandler {

    // A thread-safe map to store all connected client sessions
    private static final Map<Session, Session> sessions = new ConcurrentHashMap<>();

    @OnWebSocketConnect
    public void onConnect(Session session) {
        System.out.println("[WebSocket] Client connected: " + session.getRemoteAddress());
        // Add session to the map
        sessions.put(session, session);
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        System.out.println("[WebSocket] Client disconnected: " + session.getRemoteAddress());
        // Remove session from the map
        sessions.remove(session);
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
        // We don't expect messages from clients, but we could handle them here
        System.out.println("[WebSocket] Message received: " + message);
    }

    /**
     * Broadcasts a price update to ALL connected clients.
     * @param itemId The ID of the item that was updated.
     * @param newPrice The new price.
     */
    public static void broadcastPriceUpdate(String itemId, double newPrice) {
        // Create the JSON message payload
        Map<String, Object> message = Map.of(
            "type", "PRICE_UPDATE",
            "itemId", itemId,
            "newPrice", String.format("%.2f", newPrice) // Format as string
        );
        String jsonMessage = JsonUtil.toJson(message);

        // Iterate over all connected sessions and send the message
        for (Session session : sessions.keySet()) {
            try {
                if (session.isOpen()) {
                    session.getRemote().sendString(jsonMessage);
                }
            } catch (IOException e) {
                System.err.println("Error broadcasting message: " + e.getMessage());
            }
        }
    }
}