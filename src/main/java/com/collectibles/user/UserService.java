package com.collectibles.user;

import com.collectibles.utils.JsonUtil;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class UserService {

    // Usamos ConcurrentHashMap para seguridad en un entorno web (múltiples hilos)
    // Esto es sostenible: podemos reemplazar este Map por una conexión a BD
    // sin cambiar el Controlador.
    private final Map<String, User> userDatabase = new ConcurrentHashMap<>();

    public UserService() {
        // Datos de prueba
        User testUser = new User("u1", "rafael", "rafael@test.com");
        userDatabase.put(testUser.getId(), testUser);
    }

    /**
     * GET /users
     */
    public List<User> getAllUsers() {
        return new ArrayList<>(userDatabase.values());
    }

    /**
     * GET /users/:id
     */
    public Optional<User> getUserById(String id) {
        return Optional.ofNullable(userDatabase.get(id));
    }

    /**
     * POST /users (Decisión estándar de industria)
     */
    public User createUser(String jsonBody) {
        // 1. Convertimos el JSON (String) a un objeto User
        // Nota: El JSON de entrada no debe tener "id"
        UserDTO createRequest = JsonUtil.fromJson(jsonBody, UserDTO.class);
        
        // 2. Generamos un ID (la base de datos haría esto)
        String id = "u" + (userDatabase.size() + 1);
        
        // 3. Creamos el usuario
        User newUser = new User(id, createRequest.getUsername(), createRequest.getEmail());
        
        // 4. Guardamos
        userDatabase.put(id, newUser);
        return newUser;
    }

    /**
     * PUT /users/:id
     */
    public Optional<User> updateUser(String id, String jsonBody) {
        if (!userDatabase.containsKey(id)) {
            return Optional.empty(); // No se encontró
        }
        
        UserDTO updateRequest = JsonUtil.fromJson(jsonBody, UserDTO.class);
        User userToUpdate = userDatabase.get(id);
        
        // Actualizamos los campos
        userToUpdate.setUsername(updateRequest.getUsername());
        userToUpdate.setEmail(updateRequest.getEmail());
        
        userDatabase.put(id, userToUpdate); // Re-guardamos
        return Optional.of(userToUpdate);
    }

    /**
     * DELETE /users/:id
     */
    public boolean deleteUser(String id) {
        User removed = userDatabase.remove(id);
        return removed != null; // Retorna true si se borró algo
    }

    /**
     * OPTIONS /users/:id
     */
    public boolean userExists(String id) {
        return userDatabase.containsKey(id);
    }

    // DTO (Data Transfer Object) para la creación/actualización.
    // Esto es una buena práctica para no exponer el modelo 'User' completo
    // (ej. el 'id' no debe venir en la solicitud de creación).
    private static class UserDTO {
        private String username;
        private String email;

        public String getUsername() {
            return username;
        }
        public String getEmail() {
            return email;
        }
    }
}