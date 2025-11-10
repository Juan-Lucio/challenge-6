package com.collectibles;

import com.collectibles.exception.NotFoundException;
import com.collectibles.item.ItemController;
import com.collectibles.item.ItemService;
// --- IMPORT NUEVO ---
import com.collectibles.offer.OfferController;
import com.collectibles.offer.OfferService;
import com.collectibles.user.UserController;
import com.collectibles.user.UserService;
import com.collectibles.utils.JsonUtil;
import com.collectibles.websocket.PriceUpdateWebSocketHandler;
import spark.ModelAndView;
import spark.template.mustache.MustacheTemplateEngine;
import java.util.Map;
import static spark.Spark.*;

public class App {
    public static void main(String[] args) {

        staticFiles.location("/public");
        port(8080);
        webSocket("/ws/price-updates", PriceUpdateWebSocketHandler.class);

        MustacheTemplateEngine templateEngine = new MustacheTemplateEngine();

        // --- Instanciación de Servicios ---
        ItemService itemService = new ItemService();
        UserService userService = new UserService();
        OfferService offerService = new OfferService();
        
        // --- Instanciación de Controladores ---
        ItemController itemController = new ItemController(itemService);
        UserController userController = new UserController(userService);
        // --- (NUEVO) Instanciamos el OfferController ---
        OfferController offerController = new OfferController(offerService);
        // (ACTUALIZADO) WebController ahora solo necesita OfferService para el POST
        WebController webController = new WebController(itemService, offerService, templateEngine);


        // --- SPRINT 1: API Routes ---
        path("/api", () -> {
            itemController.registerRoutes();
            userController.registerRoutes();
            // --- (NUEVO) Registramos las rutas de la API de Ofertas ---
            offerController.registerRoutes();
        });

        // --- SPRINT 2: Web Routes ---
        webController.registerRoutes();

        // ... (Todos los manejadores de excepciones y filtros permanecen igual) ...
        
        after("/api/*", (req, res) -> {
            if (res.type() == null) {
                res.type("application/json; charset=utf-8");
            }
        });

        exception(NotFoundException.class, (exception, req, res) -> {
            res.status(404);
            // This now primarily serves the 404 for the homepage
            Map<String, Object> model = Map.of("message", exception.getMessage());
            res.body(templateEngine.render(new ModelAndView(model, "404.mustache")));
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

        System.out.println("Servidor API y Web iniciado en http://localhost:8080");
    }
}