package com.collectibles.utils;

import com.google.gson.Gson;
import java.io.InputStreamReader; // <<< CORRECCIÓN AQUÍ
import java.lang.reflect.Type;   // <<< CORRECCIÓN AQUÍ

public class JsonUtil {

    private static final Gson gson = new Gson();

    // Convierte un objeto Java a un String JSON
    public static String toJson(Object object) {
        return gson.toJson(object);
    }

    // Convierte un String JSON a un objeto Java (usando Class)
    public static <T> T fromJson(String json, Class<T> clazz) {
        return gson.fromJson(json, clazz);
    }

    // <<< CORRECCIÓN AQUÍ >>>
    // Sobrecarga del método para leer desde un InputStreamReader y usar un 'Type'
    // Esto es lo que ItemService necesita para leer el archivo JSON
    public static <T> T fromJson(InputStreamReader reader, Type type) {
        return gson.fromJson(reader, type);
    }
}