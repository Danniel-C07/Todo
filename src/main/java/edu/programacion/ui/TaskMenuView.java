package edu.programacion.ui;

import edu.programacion.models.User;

public class TaskMenuView {

    public static void mostrar(User usuarioActual) {
        boolean volver = false;

        while (!volver) {
            System.out.println("\n==========================================");
            System.out.println("   GESTIÓN DE TAREAS (" + usuarioActual.getUsername() + ")");
            System.out.println("==========================================");
            System.out.println("1. Listar todas mis tareas");
            System.out.println("2. Agregar nueva tarea");
            System.out.println("3. Cambiar estado de una tarea");
            System.out.println("4. Marcar tarea como COMPLETADA");
            System.out.println("5. Editar datos de una tarea");
            System.out.println("6. Eliminar una tarea");
            System.out.println("0. Volver al Menú Principal");
            System.out.println("------------------------------------------");

            int opcion = InputValidator.leerEntero("Seleccione una opción: ", 0, 6);

            switch (opcion) {
                case 1 -> listarTareas(usuarioActual);
                case 2 -> agregarTarea(usuarioActual);
                case 3 -> cambiarEstadoTarea(usuarioActual);
                case 4 -> marcarComoCompletada(usuarioActual);
                case 5 -> editarTarea(usuarioActual);
                case 6 -> eliminarTarea(usuarioActual);
                case 0 -> volver = true;
            }
        }
    }

    private static void listarTareas(User usuarioActual) {
        System.out.println("\n--- 📋 MIS TAREAS ---");
        // TODO: Llamar a TaskDAO.listarPorUsuario(usuarioActual.getIdUser())
        System.out.println("[ID: 1] Terminar modelo ER | Estado: COMPLETADA | Categoría: Universidad");
        System.out.println("[ID: 2] Programar DAOs     | Estado: EN_PROGRESO | Categoría: Universidad");
    }

    private static void agregarTarea(User usuarioActual) {
        System.out.println("\n--- ➕ AGREGAR NUEVA TAREA ---");
        String titulo = InputValidator.leerTextoNoVacio("Título de la tarea: ");
        String descripcion = InputValidator.leerTextoNoVacio("Descripción: ");
        String fechaVencimiento = InputValidator.leerTextoNoVacio("Fecha límite (YYYY-MM-DD HH:MM:SS): ");

        System.out.println("¿Desea asignarla a una categoría específica? (1: Sí / 2: No, usar 'General' por defecto)");
        int opCat = InputValidator.leerEntero("Opción: ", 1, 2);
        int idCategoria = 1; // Por defecto General
        if (opCat == 1) {
            idCategoria = InputValidator.leerEntero("Ingrese el ID de la categoría: ", 1, 9999);
        }

        // TODO: Llamar a TaskDAO.insertar(...)
        System.out.println("✅ Tarea '" + titulo + "' creada con estado NUEVA en la categoría ID " + idCategoria + ".");
    }

    private static void cambiarEstadoTarea(User usuarioActual) {
        listarTareas(usuarioActual);
        int idTarea = InputValidator.leerEntero("Ingrese el ID de la tarea: ", 1, 9999);

        System.out.println("Seleccione el nuevo estado:");
        System.out.println("1. NUEVA");
        System.out.println("2. EN_PROGRESO");
        System.out.println("3. PENDIENTE");
        System.out.println("4. COMPLETADA");
        int opEstado = InputValidator.leerEntero("Opción: ", 1, 4);

        String nuevoEstado = switch (opEstado) {
            case 1 -> "NUEVA";
            case 2 -> "EN_PROGRESO";
            case 3 -> "PENDIENTE";
            default -> "COMPLETADA";
        };

        // TODO: Llamar a TaskDAO.actualizarEstado(idTarea, nuevoEstado)
        System.out.println("✅ Estado de la tarea #" + idTarea + " actualizado a " + nuevoEstado + ".");
    }

    private static void marcarComoCompletada(User usuarioActual) {
        listarTareas(usuarioActual);
        int idTarea = InputValidator.leerEntero("Ingrese el ID de la tarea a completar: ", 1, 9999);
        // TODO: Llamar a TaskDAO.actualizarEstado(idTarea, "COMPLETADA") -> Disparará el Trigger en MySQL
        System.out.println("🎉 ¡Tarea #" + idTarea + " marcada como COMPLETADA! (Registrado en auditoría).");
    }

    private static void editarTarea(User usuarioActual) {
        listarTareas(usuarioActual);
        int idTarea = InputValidator.leerEntero("Ingrese el ID de la tarea a editar: ", 1, 9999);
        String nuevoTitulo = InputValidator.leerTextoNoVacio("Nuevo título: ");
        String nuevaDesc = InputValidator.leerTextoNoVacio("Nueva descripción: ");
        String nuevaFecha = InputValidator.leerTextoNoVacio("Nueva fecha límite (YYYY-MM-DD HH:MM:SS): ");

        // TODO: Llamar a TaskDAO.editarTarea(idTarea, nuevoTitulo, nuevaDesc, nuevaFecha)
        System.out.println("✅ Tarea #" + idTarea + " actualizada correctamente.");
    }

    private static void eliminarTarea(User usuarioActual) {
        listarTareas(usuarioActual);
        int idTarea = InputValidator.leerEntero("Ingrese el ID de la tarea a eliminar: ", 1, 9999);
        // TODO: Llamar a TaskDAO.eliminar(idTarea)
        System.out.println("🗑️ Tarea #" + idTarea + " eliminada.");
    }
}