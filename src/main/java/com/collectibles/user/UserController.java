package com.collectibles.user;

import com.collectibles.utils.JsonUtil;
import static spark.Spark.*;
import java.util.Map;

public class UserController {

    private final UserService userService;

    // <<< CORRECCIÓN AQUÍ >>>
    // El constructor ahora solo guarda el servicio.
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * <<< CORRECIÓN AQUÍ >>>
     * Este es el nuevo método público que App.java llamará
     * DESPUÉS de haber creado el grupo /api.
     */
    public void registerRoutes() {
        
        // Grupo de rutas para /users (será /api/users)
        path("/users", () -> {

            // GET /api/users
            get("", (req, res) -> {
                return userService.getAllUsers();
            }, JsonUtil::toJson);

            // GET /api/users/:id
            get("/:id", (req, res) -> {
                String id = req.params(":id");
                return userService.getUserById(id).orElse(null);
            }, JsonUtil::toJson);

            // POST /api/users
            post("", (req, res) -> {
                User newUser = userService.createUser(req.body());
                res.status(201);
                return newUser;
            }, JsonUtil::toJson);

            // PUT /api/users/:id
            put("/:id", (req, res) -> {
                String id = req.params(":id");
                return userService.updateUser(id, req.body()).orElse(null);
            }, JsonUtil::toJson);

            // DELETE /api/users/:id
            delete("/:id", (req, res) -> {
                String id = req.params(":id");
                if (userService.deleteUser(id)) {
                    return Map.of("message", "User deleted successfully");
                } else {
                    res.status(404);
                    return Map.of("message", "User not found");
                }
            }, JsonUtil::toJson);

            // OPTIONS /api/users/:id
            options("/:id", (req, res) -> {
                String id = req.params(":id");
                if (userService.userExists(id)) {
                    res.status(200);
                    return "User exists";
                } else {
                    res.status(404);
                    return "User not found";
                }
            });

        });
    }
}