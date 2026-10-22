
package edu.programacion.ui;

import edu.programacion.dao.CategoryDAO;
import edu.programacion.models.Category;
import edu.programacion.models.User;

import java.util.List;

public class CategoryMenuView {

    private static final CategoryDAO categoryDAO = new CategoryDAO();

    public static void mostrar(User usuarioActual) {
        boolean volver = false;

        while (!volver) {
            System.out.println("\n==========================================");
            System.out.println("   GESTIÓN DE CATEGORÍAS (" + usuarioActual.getUsername() + ")");
            System.out.println("==========================================");
            System.out.println("1. Listar mis categorías");
            System.out.println("2. Crear nueva categoría");
            System.out.println("3. Editar nombre de una categoría");
            System.out.println("4. Eliminar una categoría");
            System.out.println("0. Volver al Menú Principal");
            System.out.println("------------------------------------------");

            int opcion = InputValidator.leerEntero(
                    "Seleccione una opción: ", 0, 4);

            switch (opcion) {
                case 1 -> listarCategorias(usuarioActual);
                case 2 -> crearCategoria(usuarioActual);
                case 3 -> editarCategoria(usuarioActual);
                case 4 -> eliminarCategoria(usuarioActual);
                case 0 -> volver = true;
            }
        }
    }

    private static void listarCategorias(User usuarioActual) {
        System.out.println("\n--- LISTA DE CATEGORÍAS ---");

        List<Category> categorias = categoryDAO.listarPorUsuario(usuarioActual.getIdUser());

        if (categorias.isEmpty()) {
            System.out.println("No tienes categorías registradas.");
            return;
        }

        for (Category categoria : categorias) {
            String predeterminada = categoria.isDefault()
                    ? " (Por defecto)" : "";

            System.out.println("ID: " + categoria.getIdCategory() + " | Nombre: " + categoria.getName() + predeterminada);
        }
    }

    private static void crearCategoria(User usuarioActual) {
        System.out.println("\n--- NUEVA CATEGORÍA ---");

        String nombre = InputValidator.leerTextoNoVacio(
                "Nombre de la nueva categoría: ");

        Category categoria = new Category(nombre, usuarioActual.getIdUser(), false);

        boolean creada = categoryDAO.crear(categoria);

        if (creada) {
            System.out.println("Categoría '" + nombre + "' creada correctamente.");
        } else {
            System.out.println("No se pudo crear la categoría.");
        }
    }

    private static void editarCategoria(User usuarioActual) {
        System.out.println("\n--- EDITAR CATEGORÍA ---");

        listarCategorias(usuarioActual);

        int idCategoria = InputValidator.leerEntero(
                "ID de la categoría que deseas editar (0: cancelar): ", 0, 999999);
        if (idCategoria == 0) {
            System.out.println("Operación cancelada.");
            return;
        }

        String nuevoNombre = InputValidator.leerTextoNoVacio(
                "Nuevo nombre: ", 100);

        boolean actualizada = categoryDAO.actualizar(idCategoria, usuarioActual.getIdUser(), nuevoNombre);

        if (actualizada) {
            System.out.println(
                    "Categoría actualizada correctamente.");
        } else {
            System.out.println(
                    "No se pudo actualizar. Comprueba que la categoría " +
                            "sea tuya y que no sea la predeterminada.");
        }
    }

    private static void eliminarCategoria(User usuarioActual) {
        System.out.println("\n--- ELIMINAR CATEGORÍA ---");

        listarCategorias(usuarioActual);

        int idCategoria = InputValidator.leerEntero(
                "ID de la categoría que deseas eliminar (0: cancelar): ", 0, 999999);
        if (idCategoria == 0) {
            System.out.println("Operación cancelada.");
            return;
        }

        if (!InputValidator.leerConfirmacion("¿Estás seguro de eliminar la categoría con ID " + idCategoria + "?")) {
            System.out.println("Eliminación cancelada.");
            return;
        }

        boolean eliminada = categoryDAO.eliminar(idCategoria, usuarioActual.getIdUser());

        if (eliminada) {
            System.out.println("Categoría eliminada correctamente.");
        } else {
            System.out.println("No se pudo eliminar la categoría. Comprueba que " + "sea tuya, que no sea la predeterminada y que " + "no tenga tareas asociadas.");
        }
    }
}
