package edu.programacion.ui;

import edu.programacion.dao.TagDAO;
import edu.programacion.models.Tag;
import edu.programacion.models.User;

import java.util.List;

public class TagMenuView {

    private static final TagDAO tagDAO = new TagDAO();

    public static void mostrar(User usuarioActual) {
        boolean volver = false;

        while (!volver) {
            System.out.println("\n==========================================");
            System.out.println("   🏷️  GESTIÓN DE ETIQUETAS (" + usuarioActual.getUsername() + ")");
            System.out.println("==========================================");
            System.out.println("1. Listar mis etiquetas");
            System.out.println("2. Crear nueva etiqueta");
            System.out.println("3. Eliminar una etiqueta");
            System.out.println("0. Volver al Menú Principal");
            System.out.println("------------------------------------------");

            int opcion = InputValidator.leerEntero("Seleccione una opción: ", 0, 3);

            switch (opcion) {
                case 1 -> listarEtiquetas(usuarioActual);
                case 2 -> crearEtiqueta(usuarioActual);
                case 3 -> eliminarEtiqueta(usuarioActual);
                case 0 -> volver = true;
            }
        }
    }

    public static void listarEtiquetas(User usuarioActual) {
        System.out.println("\n--- LISTA DE ETIQUETAS ---");
        List<Tag> etiquetas = tagDAO.listarPorUsuario(usuarioActual.getIdUser());

        if (etiquetas.isEmpty()) {
            System.out.println("No tienes etiquetas registradas.");
            return;
        }

        for (Tag tag : etiquetas) {
            System.out.println("ID: " + tag.getIdTag() + " | Nombre: #" + tag.getName());
        }
    }

    private static void crearEtiqueta(User usuarioActual) {
        System.out.println("\n--- NUEVA ETIQUETA ---");
        String nombre = InputValidator.leerTextoNoVacio("Nombre de la etiqueta: ", 50);

        Tag tag = new Tag(nombre, usuarioActual.getIdUser());
        if (tagDAO.crear(tag)) {
            System.out.println("Etiqueta '#" + nombre + "' creada correctamente con ID: " + tag.getIdTag());
        } else {
            System.out.println("No se pudo crear la etiqueta.");
        }
    }

    private static void eliminarEtiqueta(User usuarioActual) {
        System.out.println("\n--- ELIMINAR ETIQUETA ---");
        listarEtiquetas(usuarioActual);

        int idTag = InputValidator.leerEntero("ID de la etiqueta a eliminar (0: cancelar): ", 0, 999999);
        if (idTag == 0) {
            System.out.println("Operación cancelada.");
            return;
        }

        if (!InputValidator.leerConfirmacion("¿Eliminar la etiqueta con ID " + idTag + " y desvincularla de todas las tareas?")) {
            System.out.println("Eliminación cancelada.");
            return;
        }

        if (tagDAO.eliminar(idTag, usuarioActual.getIdUser())) {
            System.out.println("Etiqueta eliminada correctamente.");
        } else {
            System.out.println("No se pudo eliminar la etiqueta. Verifica que te pertenezca.");
        }
    }
}
