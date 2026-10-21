package edu.programacion.db;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {

    private static Connection connection = null;

    private DatabaseConnection() {
    }

    public static Connection getConnection() {
        if (connection == null) {
            Properties props = new Properties();

            // Intentamos leer el archivo de configuración local
            try (InputStream input = new FileInputStream("config.properties")) {
                props.load(input);

                String url = props.getProperty("db.url");
                String user = props.getProperty("db.user");
                String password = props.getProperty("db.password");

                connection = DriverManager.getConnection(url, user, password);
                System.out.println("[SISTEMA] Conexión establecida con MySQL.");

            } catch (IOException e) {
                System.err.println("[ERROR] No se encontró el archivo config.properties. Por favor copia config.example.properties, renómbralo y pon tus datos.");
            } catch (SQLException e) {
                System.err.println("[ERROR] Error de base de datos. Verifica si tu MySQL está encendido y la contraseña es correcta.");
            }
        }
        return connection;
    }
}