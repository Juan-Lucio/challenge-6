package com.collectibles;

import com.collectibles.item.ItemController;
import com.collectibles.item.ItemService;
import com.collectibles.user.UserController;
import com.collectibles.user.UserService;
import com.collectibles.utils.JsonUtil;
import java.util.Map;

import static spark.Spark.*;

public class App {
    public static void main(String[] args) {

        // 1. Configurar el puerto del servidor
        port(8080); // (El puerto estándar para APIs es 8080)

        // 2. Inyección de Dependencias (Manual)
        ItemService itemService = new ItemService();
        UserService userService = new UserService();
        
        // 3. Creamos los Controladores
        // (Nota: ya no registran rutas en el constructor)
        ItemController itemController = new ItemController(itemService);
        UserController userController = new UserController(userService);

        // 4. DECISIÓN: Prefijo /api (Escalabilidad)
        path("/api", () -> {
            
            // <<< CORRECCIÓN AQUÍ >>>
            // Llamamos a los métodos de registro DESDE AQUÍ,
            // asegurando que estén DENTRO del grupo /api.
            itemController.registerRoutes();
            userController.registerRoutes();

        });

        // 5. Filtros (Manejo global)
        after((req, res) -> {
            if (res.type() == null) {
                res.type("application/json; charset=utf-8");
            }
        });

        // Manejo de excepciones
        exception(Exception.class, (exception, req, res) -> {
            exception.printStackTrace();
            res.status(500);
            res.body(JsonUtil.toJson(Map.of("error", "Internal Server Error")));
        });
        
        // Manejo de rutas no encontradas
        notFound((req, res) -> {
            res.status(404);
            return JsonUtil.toJson(Map.of("error", "Not Found: " + req.pathInfo()));
        });

        System.out.println("Servidor API de Coleccionables iniciado en http://localhost:8080");
    }
}