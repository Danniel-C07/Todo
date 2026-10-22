package edu.programacion.dao;

import edu.programacion.db.DatabaseConnection;
import edu.programacion.enums.Status;
import edu.programacion.models.Task;
import edu.programacion.models.TaskAudit;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TaskDAO {
    private final Connection connection;

    public TaskDAO() {
        connection = DatabaseConnection.getConnection();
    }

    private void comprobarConexion() throws SQLException {
        if (connection == null || connection.isClosed()) {
            throw new SQLException("No hay una conexión abierta. Revisa MySQL y reinicia la aplicación.");
        }
    }

    private IllegalStateException error(SQLException e) {
        return new IllegalStateException("No se pudo completar la operación de tareas: " + e.getMessage(), e);
    }

    public boolean insertar(Task task, int idUser) {
        if (task == null || task.getTitle() == null || task.getTitle().isBlank()
                || task.getTitle().length() > 200 || task.getIdCategory() <= 0 || idUser <= 0) {
            throw new IllegalArgumentException("Datos de tarea inválidos.");
        }
        String sql = "INSERT INTO tasks (title, description, status, due_date, id_category) "
                + "SELECT ?, ?, 'NUEVA', ?, c.id_category FROM categories c "
                + "WHERE c.id_category = ? AND c.id_user = ?";
        try {
            comprobarConexion();
            try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, task.getTitle().trim());
                ps.setString(2, task.getDescription());
                ponerFecha(ps, 3, task.getDueDate());
                ps.setInt(4, task.getIdCategory());
                ps.setInt(5, idUser);
                if (ps.executeUpdate() == 0) return false;
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) task.setIdTask(rs.getInt(1));
                }
                task.setStatus(Status.NUEVA);
                return true;
            }
        } catch (SQLException e) { throw error(e); }
    }

    public Integer buscarCategoriaPredeterminada(int idUser) {
        String sql = "SELECT id_category FROM categories WHERE id_user = ? AND is_default = TRUE ORDER BY id_category";
        try {
            comprobarConexion();
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, idUser);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) return null;
                    int id = rs.getInt("id_category");
                    if (rs.next()) throw new IllegalStateException("El usuario tiene más de una categoría predeterminada. Revisa sus datos.");
                    return id;
                }
            }
        } catch (SQLException e) { throw error(e); }
    }

    public List<Task> listarPorUsuario(int idUser) {
        return consultar(idUser, null, null);
    }

    public List<Task> listarPorCategoria(int idCategory, int idUser) {
        return consultar(idUser, idCategory, null);
    }

    public Task buscarPorId(int idTask, int idUser) {
        List<Task> tareas = consultar(idUser, null, idTask);
        return tareas.isEmpty() ? null : tareas.get(0);
    }

    private List<Task> consultar(int idUser, Integer idCategory, Integer idTask) {
        String sql = "SELECT t.id_task, t.title, t.description, t.status, t.creation_date, t.due_date, t.id_category "
                + "FROM tasks t INNER JOIN categories c ON c.id_category = t.id_category WHERE c.id_user = ?";
        if (idCategory != null) sql += " AND t.id_category = ?";
        if (idTask != null) sql += " AND t.id_task = ?";
        sql += " ORDER BY t.id_task";
        List<Task> tareas = new ArrayList<>();
        try {
            comprobarConexion();
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, idUser);
                int posicion = 2;
                if (idCategory != null) ps.setInt(posicion++, idCategory);
                if (idTask != null) ps.setInt(posicion, idTask);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Timestamp fecha = rs.getTimestamp("due_date");
                        tareas.add(new Task(rs.getInt("id_task"), rs.getString("title"),
                                rs.getString("description"), Status.valueOf(rs.getString("status")),
                                rs.getTimestamp("creation_date"),
                                fecha == null ? null : fecha.toLocalDateTime(), rs.getInt("id_category")));
                    }
                }
            }
            return tareas;
        } catch (SQLException e) { throw error(e); }
    }

    public boolean actualizarEstado(int idTask, Status nuevoEstado, int idUser) {
        if (nuevoEstado == null) throw new IllegalArgumentException("Selecciona un estado válido.");
        String sql = "UPDATE tasks t INNER JOIN categories c ON c.id_category = t.id_category "
                + "SET t.status = ? WHERE t.id_task = ? AND c.id_user = ? AND t.status <> ?";
        try {
            comprobarConexion();
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, nuevoEstado.name());
                ps.setInt(2, idTask);
                ps.setInt(3, idUser);
                ps.setString(4, nuevoEstado.name());
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) { throw error(e); }
    }

    public boolean editarTarea(int idTask, String titulo, String descripcion,
                               LocalDateTime fecha, int idUser) {
        if (titulo == null || titulo.isBlank() || titulo.length() > 200) {
            throw new IllegalArgumentException("El título debe tener de 1 a 200 caracteres.");
        }
        String sql = "UPDATE tasks t INNER JOIN categories c ON c.id_category = t.id_category "
                + "SET t.title = ?, t.description = ?, t.due_date = ? WHERE t.id_task = ? AND c.id_user = ?";
        try {
            comprobarConexion();
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, titulo.trim());
                ps.setString(2, descripcion);
                ponerFecha(ps, 3, fecha);
                ps.setInt(4, idTask);
                ps.setInt(5, idUser);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) { throw error(e); }
    }

    public boolean eliminar(int idTask, int idUser) {
        String sql = "DELETE t FROM tasks t INNER JOIN categories c ON c.id_category = t.id_category "
                + "WHERE t.id_task = ? AND c.id_user = ?";
        try {
            comprobarConexion();
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, idTask);
                ps.setInt(2, idUser);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) { throw error(e); }
    }

    public List<TaskAudit> listarAuditoriaPorUsuario(int idUser) {
        String sql = "SELECT a.id_audit, a.id_task, t.title, a.old_status, a.new_status, a.changed_at "
                + "FROM task_audit a "
                + "INNER JOIN tasks t ON t.id_task = a.id_task "
                + "INNER JOIN categories c ON c.id_category = t.id_category "
                + "WHERE c.id_user = ? "
                + "ORDER BY a.changed_at DESC, a.id_audit DESC";
        List<TaskAudit> registros = new ArrayList<>();
        try {
            comprobarConexion();
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, idUser);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        registros.add(new TaskAudit(
                                rs.getInt("id_audit"),
                                rs.getInt("id_task"),
                                rs.getString("title"),
                                rs.getString("old_status"),
                                rs.getString("new_status"),
                                rs.getTimestamp("changed_at")
                        ));
                    }
                }
            }
            return registros;
        } catch (SQLException e) { throw error(e); }
    }

    private void ponerFecha(PreparedStatement ps, int posicion, LocalDateTime fecha) throws SQLException {
        if (fecha == null) ps.setNull(posicion, Types.TIMESTAMP);
        else ps.setTimestamp(posicion, Timestamp.valueOf(fecha));
    }
}
