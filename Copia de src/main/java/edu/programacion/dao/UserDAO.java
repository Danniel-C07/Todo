package edu.programacion.dao;

import edu.programacion.db.DatabaseConnection;
import edu.programacion.models.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    private final Connection connection;

    public UserDAO() {
        this.connection = DatabaseConnection.getConnection();
    }

    // Registrar un usuario y crear su categoría predeterminada

    public boolean registrar(String username, String email, String password) {

        if (connection == null) {
            System.out.println("No hay conexión con MySQL.");
            return false;
        }

        String sqlUsuario = "INSERT INTO users (username, email, password_hash) " + "VALUES (?, ?, SHA2(?, 256))";

        String sqlCategoria = "INSERT INTO categories (name, id_user, is_default) " + "VALUES ('General', ?, TRUE)";

        boolean autoCommitOriginal;

        try {
            autoCommitOriginal = connection.getAutoCommit();
            connection.setAutoCommit(false);

            try {
                int idUsuario;

                try (PreparedStatement ps = connection.prepareStatement(
                        sqlUsuario,
                        java.sql.Statement.RETURN_GENERATED_KEYS)) {

                    ps.setString(1, username);
                    ps.setString(2, email);
                    ps.setString(3, password);
                    ps.executeUpdate();

                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (!rs.next()) {
                            throw new SQLException("No se obtuvo el ID del usuario.");
                        }

                        idUsuario = rs.getInt(1);
                    }
                }

                try (PreparedStatement ps = connection.prepareStatement(sqlCategoria)) {
                    ps.setInt(1, idUsuario);
                    ps.executeUpdate();
                }

                connection.commit();
                return true;

            } catch (SQLException e) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackError) {
                    e.addSuppressed(rollbackError);
                }

                System.out.println("Error al registrar: " + e.getMessage());
                return false;

            } finally {
                try {
                    connection.setAutoCommit(autoCommitOriginal);
                } catch (SQLException e) {
                    System.err.println("No se pudo restaurar la conexión: " + e.getMessage());
                }
            }

        } catch (SQLException e) {
            System.out.println("Error en la transacción: " + e.getMessage());
            return false;
        }
    }

    // Iniciar sesión con el nombre de usuario y la contraseña
    public User login(String username, String password) {

        if (connection == null) {
            System.out.println("No hay conexión con la base de datos.");
            return null;
        }

        String sql = "SELECT id_user, username, email, password_hash, created_at " + "FROM users " + "WHERE username = ? AND password_hash = SHA2(?, 256)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    User user = new User();

                    user.setIdUser(rs.getInt("id_user"));
                    user.setUsername(rs.getString("username"));
                    user.setEmail(rs.getString("email"));
                    user.setPasswordHash(rs.getString("password_hash"));
                    user.setCreatedAt(rs.getTimestamp("created_at"));

                    return user;
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al iniciar sesión: " + e.getMessage());
        }

        return null;
    }
}
