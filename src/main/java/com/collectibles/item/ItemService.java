package com.collectibles.item;

import com.collectibles.utils.JsonUtil;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class ItemService {

    // Usamos un Map para un acceso rápido por ID (escalable)
    private final Map<String, Item> itemMap;

    public ItemService() {
        // Cargamos los datos del JSON al iniciar el servicio
        this.itemMap = loadItemsFromJson();
    }

    private Map<String, Item> loadItemsFromJson() {
        try {
            // Lee el archivo desde 'src/main/resources'
            InputStream is = getClass().getClassLoader().getResourceAsStream("items.json");
            if (is == null) {
                System.err.println("items.json no encontrado!");
                return Collections.emptyMap();
            }
            
            InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8);
            
            // Define el tipo de la lista que Gson debe crear
            Type itemListType = new TypeToken<List<Item>>(){}.getType();
            List<Item> items = JsonUtil.fromJson(reader, itemListType);
            
            // Convierte la Lista en un Map (id -> Item) para búsquedas rápidas
            return items.stream()
                    .collect(Collectors.toMap(Item::getId, item -> item));
            
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyMap();
        }
    }

    /**
     * Requerimiento 1.3: Regresar una lista completa de artículos.
     * (Modificado para cumplir Requerimiento 1.2: solo nombre, precio e ID)
     */
    public List<Map<String, String>> getAllItems() {
        // Transformamos los datos al formato solicitado (ID, name, price)
        return itemMap.values().stream()
                .map(item -> Map.of(
                        "id", item.getId(),
                        "name", item.getName(),
                        "price", item.getPrice()
                ))
                .collect(Collectors.toList());
    }

    /**
     * Requerimiento 1.3: Regresar la descripción del artículo dado un ID.
     */
    public Optional<String> getItemDescription(String id) {
        // Optional es una forma segura de manejar valores que pueden ser nulos
        return Optional.ofNullable(itemMap.get(id))
                .map(Item::getDescription);
    }
}