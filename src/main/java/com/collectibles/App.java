package com.collectibles;

import com.collectibles.database.DatabaseService; // <-- (NUEVO)
import com.collectibles.exception.InvalidOfferException; // <-- (NUEVO)
import com.collectibles.exception.NotFoundException;
import com.collectibles.item.ItemController;
import com.collectibles.item.ItemService;
import com.collectibles.offer.OfferController;
import com.collectibles.offer.OfferService;
import com.collectibles.user.UserController;
import com.collectibles.user.UserService;
import com.collectibles.utils.JsonUtil;
import com.collectibles.websocket.PriceUpdateWebSocketHandler;
import org.jdbi.v3.core.Jdbi; // <-- (NUEVO)
import spark.ModelAndView;
import spark.template.mustache.MustacheTemplateEngine;
import java.util.Map;
import static spark.Spark.*;

public class App {
    public static void main(String[] args) {

        staticFiles.location("/public");
        port(8080);
        webSocket("/ws/price-updates", PriceUpdateWebSocketHandler.class);
        
        // --- SPRINT "BIG BANG" (NUEVA INICIALIZACIÓN DE BD) ---
        // 1. Initialize the Database Service and get the Jdbi instance
        DatabaseService dbService = new DatabaseService();
        Jdbi jdbi = dbService.getJdbi();

        // 2. Initialize Template Engine
        MustacheTemplateEngine templateEngine = new MustacheTemplateEngine();

        // 3. Service Instantiation
        // (Inject the Jdbi instance into the services)
        ItemService itemService = new ItemService(jdbi);
        OfferService offerService = new OfferService(jdbi);
        // (UserService remains in-memory for now, as it's not part of the new reqs)
        UserService userService = new UserService(); 
        
        // 4. Controller Instantiation
        ItemController itemController = new ItemController(itemService);
        UserController userController = new UserController(userService);
        OfferController offerController = new OfferController(offerService);
        WebController webController = new WebController(itemService, offerService, templateEngine);


        // 5. Register Routes
        path("/api", () -> {
            itemController.registerRoutes();
            userController.registerRoutes();
            offerController.registerRoutes();
        });
        webController.registerRoutes();


        // 6. Exception Handlers
        
        // (NUEVO) Handler for our new business logic
        exception(InvalidOfferException.class, (exception, req, res) -> {
            res.status(400); // 400 Bad Request
            res.type("application/json");
            res.body(JsonUtil.toJson(Map.of("error", exception.getMessage())));
        });
        
        exception(NotFoundException.class, (exception, req, res) -> {
            res.status(404);
            // ... (el resto de los handlers no cambian) ...
            if (req.pathInfo().startsWith("/api/")) {
                res.type("application/json");
                res.body(JsonUtil.toJson(Map.of("error", exception.getMessage())));
            } else {
                Map<String, Object> model = Map.of("message", exception.getMessage());
                res.body(templateEngine.render(new ModelAndView(model, "400.mustache")));
            }
        });
        
        // (El resto de los handlers no cambian)
        after("/api/*", (req, res) -> {
            if (res.type() == null) {
                res.type("application/json; charset=utf-8");
            }
        });
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