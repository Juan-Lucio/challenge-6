package com.collectibles;

import com.collectibles.exception.NotFoundException;
import com.collectibles.item.ItemController;
import com.collectibles.item.ItemService;
import com.collectibles.user.UserController;
import com.collectibles.user.UserService;
import com.collectibles.utils.JsonUtil;
import spark.ModelAndView;
import spark.template.mustache.MustacheTemplateEngine;

import java.util.Map;
import static spark.Spark.*;

/**
 * Main application class.
 * This class initializes services, controllers, and Spark configuration.
 */
public class App {
    public static void main(String[] args) {

        // --- SPRINT 2: Static File Configuration ---
        // Serve CSS/JS files from src/main/resources/public
        // This line MUST be before other route definitions
        staticFiles.location("/public");

        // 1. Configure server port
        port(8080);

        // --- SPRINT 2: Template Engine Initialization ---
        MustacheTemplateEngine templateEngine = new MustacheTemplateEngine();

        // 2. Service Instantiation (Dependency Injection)
        ItemService itemService = new ItemService();
        UserService userService = new UserService();
        
        // 3. Controller Instantiation
        ItemController itemController = new ItemController(itemService);
        UserController userController = new UserController(userService);
        // (SPRINT 2) Create the new WebController
        WebController webController = new WebController(itemService, templateEngine);


        // --- SPRINT 1: API Routes ---
        // All API routes are grouped under /api
        path("/api", () -> {
            // Call register methods *inside* the path group
            itemController.registerRoutes();
            userController.registerRoutes();
        });

        // --- SPRINT 2: Web Routes ---
        // Web routes are registered at the root level (/)
        webController.registerRoutes();


        // 5. Global Filters and Exception Handlers

        // SPRINT 2: API JSON Response Filter
        // Ensures all /api/ routes return JSON
        after("/api/*", (req, res) -> {
            if (res.type() == null) {
                res.type("application/json; charset=utf-8");
            }
        });

        // --- SPRINT 2: Exception Handling (Req 1) ---
        // Handler for our custom NotFoundException (renders HTML)
        exception(NotFoundException.class, (exception, req, res) -> {
            res.status(404);
            Map<String, Object> model = Map.of("message", exception.getMessage());
            res.body(templateEngine.render(new ModelAndView(model, "404.mustache")));
        });

        // Handler for general API (JSON) errors
        exception(Exception.class, (exception, req, res) -> {
            exception.printStackTrace(); // Always log the stack trace
            
            if (req.pathInfo().startsWith("/api/")) {
                res.status(500);
                res.type("application/json");
                res.body(JsonUtil.toJson(Map.of("error", "Internal Server Error")));
            } else {
                // Return a generic web error
                res.status(500);
                res.body("<h1>500 Internal Server Error</h1>");
            }
        });
        
        // SPRINT 2: Updated 404 Handler for API vs Web
        notFound((req, res) -> {
            if (req.pathInfo().startsWith("/api/")) {
                res.status(404);
                res.type("application/json");
                return JsonUtil.toJson(Map.of("error", "Not Found: " + req.pathInfo()));
            }
            
            // Render the 404.mustache template for web routes
            res.status(404);
            Map<String, Object> model = Map.of("message", "No route matched " + req.pathInfo());
            return templateEngine.render(new ModelAndView(model, "404.mustache"));
        });

        System.out.println("Servidor API y Web iniciado en http://localhost:8080");
    }
}