package edu.programacion.ui;

import edu.programacion.models.User;

public class MainMenuView {

    public static void mostrar(User usuarioActual) {
        boolean cerrarSesion = false;

        while (!cerrarSesion) {
            System.out.println("\n==========================================");
            System.out.println("   🏠 MENÚ PRINCIPAL | Usuario: " + usuarioActual.getUsername());
            System.out.println("==========================================");
            System.out.println("1. 📂 Gestionar mis Categorías");
            System.out.println("2. ✅ Gestionar mis Tareas");
            System.out.println("3. 👤 Ver información de mi cuenta");
            System.out.println("0. 🚪 Cerrar Sesión");
            System.out.println("------------------------------------------");

            int opcion = InputValidator.leerEntero("Seleccione una opción: ", 0, 3);

            switch (opcion) {
                case 1 -> CategoryMenuView.mostrar(usuarioActual);
                case 2 -> TaskMenuView.mostrar(usuarioActual);
                case 3 -> {
                    System.out.println("\n--- 👤 PERFIL DE USUARIO ---");
                    System.out.println("ID: " + usuarioActual.getIdUser());
                    System.out.println("Username: " + usuarioActual.getUsername());
                    System.out.println("Email: " + usuarioActual.getEmail());
                }
                case 0 -> {
                    System.out.println("🔒 Cerrando sesión de " + usuarioActual.getUsername() + "...");
                    cerrarSesion = true;
                }
            }
        }
    }
}