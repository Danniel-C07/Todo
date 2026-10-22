package edu.programacion.ui;

import edu.programacion.dao.CategoryDAO;
import edu.programacion.dao.SubtaskDAO;
import edu.programacion.dao.TagDAO;
import edu.programacion.dao.TaskDAO;
import edu.programacion.enums.Status;
import edu.programacion.models.Category;
import edu.programacion.models.Subtask;
import edu.programacion.models.Tag;
import edu.programacion.models.Task;
import edu.programacion.models.TaskAudit;
import edu.programacion.models.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class TaskMenuView {
    private static final TaskDAO taskDAO = new TaskDAO();
    private static final SubtaskDAO subtaskDAO = new SubtaskDAO();
    private static final CategoryDAO categoryDAO = new CategoryDAO();
    private static final TagDAO tagDAO = new TagDAO();

    public static void mostrar(User usuarioActual) {
        if (!SessionData.haySesion() || usuarioActual == null
                || SessionData.obtenerUsuarioActual().getIdUser() != usuarioActual.getIdUser()) {
            System.out.println("Debes iniciar sesión.");
            return;
        }
        boolean volver = false;
        while (!volver) {
            System.out.println("\n=== GESTIÓN DE TAREAS: " + usuarioActual.getUsername() + " ===");
            System.out.println("1. Listar todas mis tareas");
            System.out.println("2. Agregar tarea");
            System.out.println("3. Cambiar estado");
            System.out.println("4. Marcar como COMPLETADA");
            System.out.println("5. Editar título, descripción y fecha");
            System.out.println("6. Eliminar tarea");
            System.out.println("7. Listar por categoría");
            System.out.println("8. Gestionar subtareas");
            System.out.println("9. Gestionar etiquetas de una tarea");
            System.out.println("10. Ver historial de cambios / auditoría (Trigger)");
            System.out.println("0. Volver");
            int opcion = InputValidator.leerEntero("Opción: ", 0, 10);
            try {
                switch (opcion) {
                    case 1 -> imprimirTareas(taskDAO.listarPorUsuario(usuarioActual.getIdUser()), usuarioActual.getIdUser());
                    case 2 -> agregarTarea(usuarioActual);
                    case 3 -> cambiarEstado(usuarioActual, false);
                    case 4 -> cambiarEstado(usuarioActual, true);
                    case 5 -> editarTarea(usuarioActual);
                    case 6 -> eliminarTarea(usuarioActual);
                    case 7 -> listarPorCategoria(usuarioActual);
                    case 8 -> gestionarSubtareas(usuarioActual);
                    case 9 -> gestionarEtiquetasDeTarea(usuarioActual);
                    case 10 -> verHistorialAuditoria(usuarioActual);
                    case 0 -> volver = true;
                }
            } catch (IllegalStateException | IllegalArgumentException e) {
                System.out.println("[ERROR] " + e.getMessage());
            }
        }
    }

    private static void imprimirTareas(List<Task> tareas, int idUser) {
        if (tareas.isEmpty()) System.out.println("No hay tareas para mostrar.");
        for (Task t : tareas) {
            List<Tag> tags = tagDAO.listarPorTarea(t.getIdTask(), idUser);
            String tagsStr = tags.isEmpty() ? "Sin etiquetas" : tags.stream().map(tg -> "#" + tg.getName()).collect(Collectors.joining(", "));
            System.out.println("ID: " + t.getIdTask() + " | " + t.getTitle() + " | " + t.getStatus()
                    + " | Categoría ID: " + t.getIdCategory() + " | Fecha límite: "
                    + (t.getDueDate() == null ? "Sin fecha" : t.getDueDate())
                    + " | Etiquetas: [" + tagsStr + "]");
            System.out.println("  Descripción: " + (t.getDescription() == null ? "" : t.getDescription()));
        }
    }

    private static Task seleccionarTarea(User usuario) {
        List<Task> tareas = taskDAO.listarPorUsuario(usuario.getIdUser());
        imprimirTareas(tareas, usuario.getIdUser());
        if (tareas.isEmpty()) return null;
        int id = InputValidator.leerEntero("ID de tarea (0: cancelar): ", 0, Integer.MAX_VALUE);
        if (id == 0) return null;
        Task tarea = taskDAO.buscarPorId(id, usuario.getIdUser());
        if (tarea == null) System.out.println("Tarea no encontrada entre tus tareas.");
        return tarea;
    }

    private static Integer seleccionarCategoria(User usuario, boolean permitirGeneral) {
        List<Category> categorias = categoryDAO.listarPorUsuario(usuario.getIdUser());
        if (categorias.isEmpty()) {
            System.out.println("No se obtuvieron categorías. Revisa tus datos y la conexión.");
            return null;
        }
        for (Category c : categorias) {
            System.out.println("ID: " + c.getIdCategory() + " | " + c.getName()
                    + (c.isDefault() ? " (Predeterminada)" : ""));
        }
        if (permitirGeneral) {
            int opcion = InputValidator.leerEntero("1. Elegir categoría / 2. Usar predeterminada: ", 1, 2);
            if (opcion == 2) {
                Integer id = taskDAO.buscarCategoriaPredeterminada(usuario.getIdUser());
                if (id == null) System.out.println("Este usuario no tiene categoría predeterminada. Revisa su carga inicial.");
                return id;
            }
        }
        int id = InputValidator.leerEntero("ID de categoría (0: cancelar): ", 0, Integer.MAX_VALUE);
        if (id == 0) return null;
        for (Category c : categorias) if (c.getIdCategory() == id) return id;
        System.out.println("Categoría no encontrada entre tus categorías.");
        return null;
    }

    private static void agregarTarea(User usuario) {
        Integer idCategoria = seleccionarCategoria(usuario, true);
        if (idCategoria == null) return;
        String titulo = InputValidator.leerTextoNoVacio("Título: ", 200);
        String descripcion = InputValidator.leerTextoOpcional("Descripción (Enter: vacía): ", 5000);
        LocalDateTime fecha = InputValidator.leerFechaOpcional("Fecha límite AAAA-MM-DD HH:mm:ss (Enter: sin fecha): ");
        Task tarea = new Task(titulo, descripcion, fecha, idCategoria);
        if (taskDAO.insertar(tarea, usuario.getIdUser())) {
            System.out.println("Tarea creada con estado NUEVA. ID: " + tarea.getIdTask());
        } else System.out.println("No se creó: la categoría no existe o no te pertenece.");
    }

    private static void listarPorCategoria(User usuario) {
        Integer id = seleccionarCategoria(usuario, false);
        if (id != null) imprimirTareas(taskDAO.listarPorCategoria(id, usuario.getIdUser()), usuario.getIdUser());
    }

    private static void cambiarEstado(User usuario, boolean completar) {
        Task tarea = seleccionarTarea(usuario);
        if (tarea == null) return;
        Status estado = Status.COMPLETADA;
        if (!completar) {
            Status[] estados = Status.values();
            for (int i = 0; i < estados.length; i++) System.out.println((i + 1) + ". " + estados[i]);
            estado = estados[InputValidator.leerEntero("Nuevo estado: ", 1, estados.length) - 1];
        }
        if (tarea.getStatus() == estado) {
            System.out.println("La tarea ya tiene ese estado; no se hizo ningún cambio.");
            return;
        }
        if (taskDAO.actualizarEstado(tarea.getIdTask(), estado, usuario.getIdUser())) {
            System.out.println("Estado actualizado a " + estado + ".");
            if (estado == Status.COMPLETADA) {
                System.out.println("Si el trigger está instalado, registra esta transición en task_audit. Verifícalo en Workbench.");
            }
        } else System.out.println("Sin cambios: revisa la tarea y su estado actual.");
    }

    private static void editarTarea(User usuario) {
        Task tarea = seleccionarTarea(usuario);
        if (tarea == null) return;
        System.out.println("Introduce los nuevos valores. Descripción vacía la borra; fecha vacía la elimina.");
        String titulo = InputValidator.leerTextoNoVacio("Nuevo título: ", 200);
        String descripcion = InputValidator.leerTextoOpcional("Nueva descripción: ", 5000);
        LocalDateTime fecha = InputValidator.leerFechaOpcional("Nueva fecha AAAA-MM-DD HH:mm:ss (Enter: sin fecha): ");
        if (taskDAO.editarTarea(tarea.getIdTask(), titulo, descripcion, fecha, usuario.getIdUser())) {
            System.out.println("Datos guardados. No se modificó el estado.");
        } else System.out.println("No se modificaron filas: los datos pueden ser iguales o la tarea ya no estar disponible.");
    }

    private static void eliminarTarea(User usuario) {
        Task tarea = seleccionarTarea(usuario);
        if (tarea == null) return;
        if (!InputValidator.leerConfirmacion("¿Eliminar '" + tarea.getTitle()
                + "' y sus subtareas/vínculos con etiquetas?")) {
            System.out.println("Eliminación cancelada.");
            return;
        }
        System.out.println(taskDAO.eliminar(tarea.getIdTask(), usuario.getIdUser())
                ? "Tarea eliminada." : "No se eliminó ninguna tarea.");
    }

    private static void mostrarSubtareas(List<Subtask> subtareas) {
        if (subtareas.isEmpty()) System.out.println("No hay subtareas.");
        for (Subtask s : subtareas) System.out.println("ID: " + s.getIdSubtask() + " | "
                + (s.isCompleted() ? "[X] " : "[ ] ") + s.getTitle());
    }

    private static void gestionarSubtareas(User usuario) {
        Task tarea = seleccionarTarea(usuario);
        if (tarea == null) return;
        boolean volver = false;
        while (!volver) {
            System.out.println("\nSubtareas de: " + tarea.getTitle());
            System.out.println("1. Listar / 2. Agregar / 3. Completar / 0. Volver");
            int opcion = InputValidator.leerEntero("Opción: ", 0, 3);
            switch (opcion) {
                case 1 -> mostrarSubtareas(subtaskDAO.listarPorTarea(tarea.getIdTask(), usuario.getIdUser()));
                case 2 -> {
                    String titulo = InputValidator.leerTextoNoVacio("Título de subtarea: ", 200);
                    Subtask s = new Subtask(titulo, tarea.getIdTask());
                    System.out.println(subtaskDAO.agregar(s, usuario.getIdUser())
                            ? "Subtarea agregada. ID: " + s.getIdSubtask() : "No se agregó la subtarea.");
                }
                case 3 -> {
                    List<Subtask> lista = subtaskDAO.listarPorTarea(tarea.getIdTask(), usuario.getIdUser());
                    mostrarSubtareas(lista);
                    if (lista.isEmpty()) continue;
                    int id = InputValidator.leerEntero("ID de subtarea (0: cancelar): ", 0, Integer.MAX_VALUE);
                    if (id == 0) continue;
                    System.out.println(subtaskDAO.marcarCompletada(id, tarea.getIdTask(), usuario.getIdUser())
                            ? "Subtarea completada." : "Sin cambios: no existe en esta tarea o ya estaba completada.");
                }
                case 0 -> volver = true;
            }
        }
    }

    private static void gestionarEtiquetasDeTarea(User usuario) {
        Task tarea = seleccionarTarea(usuario);
        if (tarea == null) return;
        boolean volver = false;
        while (!volver) {
            List<Tag> actuales = tagDAO.listarPorTarea(tarea.getIdTask(), usuario.getIdUser());
            System.out.println("\n--- 🏷️  ETIQUETAS DE LA TAREA: " + tarea.getTitle() + " ---");
            if (actuales.isEmpty()) {
                System.out.println("La tarea no tiene etiquetas asociadas actualmente.");
            } else {
                System.out.print("Etiquetas actuales: ");
                for (Tag tg : actuales) System.out.print("#" + tg.getName() + " (ID:" + tg.getIdTag() + ") ");
                System.out.println();
            }
            System.out.println("1. Asociar etiqueta a esta tarea");
            System.out.println("2. Quitar etiqueta de esta tarea");
            System.out.println("0. Volver");
            int op = InputValidator.leerEntero("Opción: ", 0, 2);
            switch (op) {
                case 1 -> {
                    List<Tag> todas = tagDAO.listarPorUsuario(usuario.getIdUser());
                    if (todas.isEmpty()) {
                        System.out.println("No tienes etiquetas creadas. Ve al Menú Principal -> Gestionar mis Etiquetas para crear una.");
                        break;
                    }
                    System.out.println("Tus etiquetas disponibles:");
                    for (Tag tg : todas) System.out.println("ID: " + tg.getIdTag() + " | #" + tg.getName());
                    int idTag = InputValidator.leerEntero("ID de etiqueta a asociar (0: cancelar): ", 0, Integer.MAX_VALUE);
                    if (idTag == 0) break;
                    if (tagDAO.asignarATarea(tarea.getIdTask(), idTag, usuario.getIdUser())) {
                        System.out.println("Etiqueta asociada exitosamente a la tarea.");
                    } else {
                        System.out.println("No se pudo asociar la etiqueta (verifique que exista o si ya estaba asociada).");
                    }
                }
                case 2 -> {
                    if (actuales.isEmpty()) {
                        System.out.println("No hay etiquetas asociadas para quitar.");
                        break;
                    }
                    int idTag = InputValidator.leerEntero("ID de etiqueta a quitar (0: cancelar): ", 0, Integer.MAX_VALUE);
                    if (idTag == 0) break;
                    if (tagDAO.desasignarDeTarea(tarea.getIdTask(), idTag, usuario.getIdUser())) {
                        System.out.println("Etiqueta desvinculada de la tarea.");
                    } else {
                        System.out.println("No se pudo desvincular la etiqueta.");
                    }
                }
                case 0 -> volver = true;
            }
        }
    }

    private static void verHistorialAuditoria(User usuario) {
        System.out.println("\n=== 📋 HISTORIAL DE AUDITORÍA DE TAREAS (TRIGGER: after_task_status_update) ===");
        List<TaskAudit> historial = taskDAO.listarAuditoriaPorUsuario(usuario.getIdUser());
        if (historial.isEmpty()) {
            System.out.println("No hay eventos de auditoría registrados.");
            System.out.println("Nota: El trigger en MySQL registra automáticamente cuando una tarea pasa al estado 'COMPLETADA'.");
            return;
        }
        for (TaskAudit a : historial) {
            System.out.println(a.toString());
        }
    }
}
