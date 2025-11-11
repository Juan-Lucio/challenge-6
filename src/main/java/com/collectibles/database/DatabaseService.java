package com.collectibles.database;

import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class DatabaseService {

    private Jdbi jdbi;

    public DatabaseService() {
        String dbUrl = "jdbc:postgresql://localhost:5432/collectibles";
        String user = "postgres";
        
        // --- ¡LA CORRECCIÓN ESTÁ AQUÍ! ---
        // La contraseña debe ser un string de Java válido.
        // Asegúrate de que esta sea tu contraseña real de PostgreSQL.
        String password = "Millon123"; 

        try {
            this.jdbi = Jdbi.create(dbUrl, user, password);
            
            // Solo instalamos el plugin que SÍ usamos
            this.jdbi.installPlugin(new SqlObjectPlugin());

            System.out.println("Database connection established.");

            // Create the tables
            initializeDatabaseSchema();

        } catch (Exception e) {
            System.err.println("FATAL: Could not connect to the database.");
            e.printStackTrace();
            throw new RuntimeException("Database connection failed", e);
        }
    }

    private void initializeDatabaseSchema() {
        System.out.println("Initializing database schema...");
        String schemaSql;
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("schema.sql")) {
            if (is == null) {
                throw new RuntimeException("schema.sql not found in resources");
            }
            schemaSql = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read schema.sql", e);
        }

        this.jdbi.withHandle(handle -> 
            handle.createScript(schemaSql).execute()
        );
        System.out.println("Database schema initialized.");
    }

    public Jdbi getJdbi() {
        return this.jdbi;
    }
}