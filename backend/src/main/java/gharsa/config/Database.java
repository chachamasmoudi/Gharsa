package gharsa.config;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {

    private static final String URL = "jdbc:sqlite:data/gharsa.db";

    public static Connection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(URL);
        try (Statement st = conn.createStatement()) {
            st.execute("PRAGMA foreign_keys = ON");
        }
        return conn;
    }

    public static void init() {
        try (InputStream in = Database.class.getResourceAsStream("/schema.sql")) {
            if (in == null) {
                throw new IllegalStateException("schema.sql introuvable dans resources");
            }
            String sql = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            try (Connection conn = getConnection(); Statement st = conn.createStatement()) {
                for (String part : sql.split(";")) {
                    if (!part.isBlank()) {
                        st.execute(part);
                    }
                }
            }
            System.out.println("Base de données initialisée : data/gharsa.db");
        } catch (Exception e) {
            throw new RuntimeException("Échec de l'initialisation de la base", e);
        }
    }
}