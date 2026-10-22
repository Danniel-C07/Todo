package edu.programacion.dao;

import edu.programacion.db.DatabaseConnection;
import edu.programacion.models.Subtask;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SubtaskDAO {
    private final Connection connection;

    public SubtaskDAO() { connection = DatabaseConnection.getConnection(); }

    private void comprobarConexion() throws SQLException {
        if (connection == null || connection.isClosed()) {
            throw new SQLException("No hay una conexión abierta. Revisa MySQL y reinicia la aplicación.");
        }
    }

    private IllegalStateException error(SQLException e) {
        return new IllegalStateException("No se pudo completar la operación de subtareas: " + e.getMessage(), e);
    }

    public boolean agregar(Subtask subtask, int idUser) {
        if (subtask == null || subtask.getTitle() == null || subtask.getTitle().isBlank()
                || subtask.getTitle().length() > 200 || subtask.getIdTask() <= 0 || idUser <= 0) {
            throw new IllegalArgumentException("Datos de subtarea inválidos.");
        }
        String sql = "INSERT INTO subtasks (title, is_completed, id_task) "
                + "SELECT ?, FALSE, t.id_task FROM tasks t "
                + "INNER JOIN categories c ON c.id_category = t.id_category "
                + "WHERE t.id_task = ? AND c.id_user = ?";
        try {
            comprobarConexion();
            try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, subtask.getTitle().trim());
                ps.setInt(2, subtask.getIdTask());
                ps.setInt(3, idUser);
                if (ps.executeUpdate() == 0) return false;
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) subtask.setIdSubtask(rs.getInt(1));
                }
                subtask.setCompleted(false);
                return true;
            }
        } catch (SQLException e) { throw error(e); }
    }

    public List<Subtask> listarPorTarea(int idTask, int idUser) {
        String sql = "SELECT s.id_subtask, s.title, s.is_completed, s.id_task FROM subtasks s "
                + "INNER JOIN tasks t ON t.id_task = s.id_task "
                + "INNER JOIN categories c ON c.id_category = t.id_category "
                + "WHERE s.id_task = ? AND c.id_user = ? ORDER BY s.id_subtask";
        List<Subtask> lista = new ArrayList<>();
        try {
            comprobarConexion();
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, idTask);
                ps.setInt(2, idUser);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) lista.add(new Subtask(rs.getInt("id_subtask"), rs.getString("title"),
                            rs.getBoolean("is_completed"), rs.getInt("id_task")));
                }
            }
            return lista;
        } catch (SQLException e) { throw error(e); }
    }

    public boolean marcarCompletada(int idSubtask, int idTask, int idUser) {
        String sql = "UPDATE subtasks s INNER JOIN tasks t ON t.id_task = s.id_task "
                + "INNER JOIN categories c ON c.id_category = t.id_category "
                + "SET s.is_completed = TRUE WHERE s.id_subtask = ? AND s.id_task = ? "
                + "AND c.id_user = ? AND s.is_completed = FALSE";
        try {
            comprobarConexion();
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, idSubtask);
                ps.setInt(2, idTask);
                ps.setInt(3, idUser);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) { throw error(e); }
    }
}
