package edu.programacion.dao;

import edu.programacion.db.DatabaseConnection;
import edu.programacion.models.Tag;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TagDAO {

    private final Connection connection;

    public TagDAO() {
        this.connection = DatabaseConnection.getConnection();
    }

    public boolean crear(Tag tag) {
        if (connection == null || tag == null || tag.getName() == null || tag.getName().isBlank() || tag.getIdUser() <= 0) {
            return false;
        }
        String sql = "INSERT INTO tags (name, id_user) VALUES (?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, tag.getName().trim());
            ps.setInt(2, tag.getIdUser());
            if (ps.executeUpdate() > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) tag.setIdTag(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            System.out.println("Error al crear etiqueta: " + e.getMessage());
        }
        return false;
    }

    public List<Tag> listarPorUsuario(int idUser) {
        List<Tag> lista = new ArrayList<>();
        if (connection == null) return lista;
        String sql = "SELECT id_tag, name, id_user FROM tags WHERE id_user = ? ORDER BY id_tag";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idUser);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Tag(rs.getInt("id_tag"), rs.getString("name"), rs.getInt("id_user")));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al listar etiquetas: " + e.getMessage());
        }
        return lista;
    }

    public boolean eliminar(int idTag, int idUser) {
        if (connection == null) return false;
        String sql = "DELETE FROM tags WHERE id_tag = ? AND id_user = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idTag);
            ps.setInt(2, idUser);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al eliminar etiqueta: " + e.getMessage());
        }
        return false;
    }

    public boolean asignarATarea(int idTask, int idTag, int idUser) {
        if (connection == null) return false;
        String sql = "INSERT IGNORE INTO task_tags (id_task, id_tag) "
                + "SELECT t.id_task, tg.id_tag FROM tasks t "
                + "INNER JOIN categories c ON c.id_category = t.id_category "
                + "INNER JOIN tags tg ON tg.id_tag = ? AND tg.id_user = c.id_user "
                + "WHERE t.id_task = ? AND c.id_user = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idTag);
            ps.setInt(2, idTask);
            ps.setInt(3, idUser);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al asociar etiqueta a la tarea: " + e.getMessage());
        }
        return false;
    }

    public boolean desasignarDeTarea(int idTask, int idTag, int idUser) {
        if (connection == null) return false;
        String sql = "DELETE tt FROM task_tags tt "
                + "INNER JOIN tasks t ON t.id_task = tt.id_task "
                + "INNER JOIN categories c ON c.id_category = t.id_category "
                + "WHERE tt.id_task = ? AND tt.id_tag = ? AND c.id_user = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idTask);
            ps.setInt(2, idTag);
            ps.setInt(3, idUser);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al desvincular etiqueta: " + e.getMessage());
        }
        return false;
    }

    public List<Tag> listarPorTarea(int idTask, int idUser) {
        List<Tag> lista = new ArrayList<>();
        if (connection == null) return lista;
        String sql = "SELECT tg.id_tag, tg.name, tg.id_user FROM tags tg "
                + "INNER JOIN task_tags tt ON tt.id_tag = tg.id_tag "
                + "INNER JOIN tasks t ON t.id_task = tt.id_task "
                + "INNER JOIN categories c ON c.id_category = t.id_category "
                + "WHERE tt.id_task = ? AND c.id_user = ? ORDER BY tg.id_tag";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idTask);
            ps.setInt(2, idUser);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Tag(rs.getInt("id_tag"), rs.getString("name"), rs.getInt("id_user")));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al listar etiquetas de la tarea: " + e.getMessage());
        }
        return lista;
    }
}
