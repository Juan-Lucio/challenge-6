package com.collectibles.database;

import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Manages the Jdbi instance and database connection.
 * It's responsible for setting up the connection and creating the schema.
 */
public class DatabaseService {

    private Jdbi jdbi;

    /**
     * Creates a new DatabaseService, connects to PostgreSQL,
     * and initializes the database schema.
     */
    public DatabaseService() {
        // TODO: Move connection details to environment variables
        String dbUrl = "jdbc:postgresql://localhost:5432/collectibles";
        String user = "postgres";
        String password = "Millon123"; // <-- (PASSWORD IS HERE)

        try {
            this.jdbi = Jdbi.create(dbUrl, user, password);
            this.jdbi.installPlugin(new SqlObjectPlugin());

            System.out.println("Database connection established.");

            // Create the tables if they don't exist
            initializeDatabaseSchema();

        } catch (Exception e) {
            System.err.println("FATAL: Could not connect to the database.");
            e.printStackTrace();
            throw new RuntimeException("Database connection failed", e);
        }
    }

    /**
     * Reads the schema.sql file from resources and executes it.
     */
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

        // Execute the entire DDL script
        this.jdbi.withHandle(handle -> 
            handle.createScript(schemaSql).execute()
        );
        System.out.println("Database schema initialized.");
    }

    /**
     * Provides the configured Jdbi instance to other services.
     * @return The singleton Jdbi instance.
     */
    public Jdbi getJdbi() {
        return this.jdbi;
    }
}