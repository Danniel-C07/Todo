package edu.programacion.dao;

import edu.programacion.db.DatabaseConnection;
import edu.programacion.models.Category;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO {

    private final Connection connection;

    public CategoryDAO() {
        this.connection = DatabaseConnection.getConnection();
    }

    // Crear una categoría para un usuario
    public boolean crear(Category category) {

        if (connection == null) {
            return false;
        }

        String sql = "INSERT INTO categories (name, id_user, is_default) " +
                        "VALUES (?, ?, FALSE)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, category.getName());
            ps.setInt(2, category.getIdUser());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error al crear categoría: " + e.getMessage());
            return false;
        }
    }

    // Listar las categorías de un usuario
    public List<Category> listarPorUsuario(int idUser) {

        List<Category> categorias = new ArrayList<>();

        if (connection == null) {
            return categorias;
        }

        String sql = "SELECT id_category, name, id_user, is_default " +
                        "FROM categories WHERE id_user = ? " +
                        "ORDER BY id_category";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, idUser);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    Category category = new Category(
                            rs.getInt("id_category"),
                            rs.getString("name"),
                            rs.getInt("id_user"),
                            rs.getBoolean("is_default")
                    );

                    categorias.add(category);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al listar categorías: " + e.getMessage());
        }

        return categorias;
    }

    // Actualizar el nombre de una categoría propia
    public boolean actualizar(int idCategory, int idUser, String nuevoNombre) {

        if (connection == null) {
            return false;
        }

        String sql = "UPDATE categories SET name = ? " +
                        "WHERE id_category = ? AND id_user = ? " +
                        "AND is_default = FALSE";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, nuevoNombre);
            ps.setInt(2, idCategory);
            ps.setInt(3, idUser);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error al actualizar categoría: " + e.getMessage());
            return false;
        }
    }

    // Eliminar una categoría propia que no tenga tareas asociadas
    public boolean eliminar(int idCategory, int idUser) {

        if (connection == null) {
            return false;
        }

        String sql = "DELETE FROM categories " +
                        "WHERE id_category = ? AND id_user = ? " +
                        "AND is_default = FALSE " +
                        "AND NOT EXISTS (" +
                        "SELECT 1 FROM tasks " +
                        "WHERE tasks.id_category = categories.id_category)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, idCategory);
            ps.setInt(2, idUser);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error al eliminar categoría: " + e.getMessage());
            return false;
        }
    }
}
