package com.collectibles;

import com.collectibles.database.DatabaseService;
import com.collectibles.exception.InvalidOfferException;
import com.collectibles.exception.NotFoundException;
import com.collectibles.item.ItemController;
import com.collectibles.item.ItemService;
import com.collectibles.offer.OfferController;
import com.collectibles.offer.OfferService;
import com.collectibles.user.UserController;
import com.collectibles.user.UserService;
import com.collectibles.utils.JsonUtil;
import com.collectibles.websocket.PriceUpdateWebSocketHandler;
import org.jdbi.v3.core.Jdbi;
import spark.ModelAndView;
import spark.template.mustache.MustacheTemplateEngine;
import java.util.Map;
import static spark.Spark.*;

/**
 * Main application class.
 * Responsible for initializing the server, database connection, services,
 * controllers, and all web routes and exception handlers.
 */
public class App {
    public static void main(String[] args) {

        // --- 1. Server Configuration ---
        staticFiles.location("/public"); // Serve static files from src/main/resources/public
        port(8080);
        
        // Register the WebSocket handler
        webSocket("/ws/price-updates", PriceUpdateWebSocketHandler.class);
        
        // --- 2. Database & Template Engine Initialization ---
        
        // Initialize the Database Service and get the Jdbi instance
        DatabaseService dbService = new DatabaseService();
        Jdbi jdbi = dbService.getJdbi();

        // Initialize the template engine
        MustacheTemplateEngine templateEngine = new MustacheTemplateEngine();

        // --- 3. Service Instantiation (Dependency Injection) ---
        // Services are injected with the Jdbi instance
        ItemService itemService = new ItemService(jdbi);
        OfferService offerService = new OfferService(jdbi);
        // UserService remains in-memory as per our "Big Bang" plan
        UserService userService = new UserService(); 
        
        // --- 4. Controller Instantiation ---
        ItemController itemController = new ItemController(itemService);
        UserController userController = new UserController(userService);
        OfferController offerController = new OfferController(offerService);
        WebController webController = new WebController(itemService, offerService, templateEngine);

        // --- 5. Register Routes ---
        
        // API routes are grouped under /api
        path("/api", () -> {
            itemController.registerRoutes();
            userController.registerRoutes();
            offerController.registerRoutes();
        });
        
        // Web routes are registered at the root (/)
        webController.registerRoutes();

        // --- 6. Global Exception Handlers & Filters ---
        
        // Filter to ensure all /api/ responses are JSON
        after("/api/*", (req, res) -> {
            if (res.type() == null) {
                res.type("application/json; charset=utf-8");
            }
        });

        // (New) Handler for our custom "Offer must be higher" business logic
        exception(InvalidOfferException.class, (exception, req, res) -> {
            res.status(400); // 400 Bad Request
            res.type("application/json");
            res.body(JsonUtil.toJson(Map.of("error", exception.getMessage())));
        });
        
        // Handler for 404 Not Found (API vs Web)
        exception(NotFoundException.class, (exception, req, res) -> {
            res.status(404);
            if (req.pathInfo().startsWith("/api/")) {
                res.type("application/json");
                res.body(JsonUtil.toJson(Map.of("error", exception.getMessage())));
            } else {
                Map<String, Object> model = Map.of("message", exception.getMessage());
                res.body(templateEngine.render(new ModelAndView(model, "404.mustache")));
            }
        });
        
        // Handler for generic 500 Internal Server Error (API vs Web)
        exception(Exception.class, (exception, req, res) -> {
            exception.printStackTrace(); 
            if (req.pathInfo().startsWith("/api/")) {
                res.status(500);
                res.type("application/json");
                res.body(JsonUtil.toJson(Map.of("error", "Internal Server Error")));
            } else {
                res.status(500);
                res.body("<h1>500 Internal Server Error</h1>");
            }
        });

        // Handler for any route not matched
        notFound((req, res) -> {
            if (req.pathInfo().startsWith("/api/")) {
                res.status(404);
                res.type("application/json");
                return JsonUtil.toJson(Map.of("error", "Not Found: " + req.pathInfo()));
            }
            res.status(404);
            Map<String, Object> model = Map.of("message", "No route matched " + req.pathInfo());
            return templateEngine.render(new ModelAndView(model, "404.mustache"));
        });

        System.out.println("Servidor API y Web (con DB) iniciado en http://localhost:8080");
    }
}