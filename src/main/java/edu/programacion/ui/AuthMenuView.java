package edu.programacion.ui;

import edu.programacion.dao.UserDAO;
import edu.programacion.models.User;

public class AuthMenuView {

    private static final UserDAO userDAO = new UserDAO();

    public static void mostrar() {
        boolean salir = false;

        while (!salir) {
            System.out.println("\n==========================================");
            System.out.println("       📋BIENVENIDO A TO-DO LIST APP");
            System.out.println("==========================================");
            System.out.println("1. Iniciar Sesión");
            System.out.println("2. Registrar Nuevo Usuario");
            System.out.println("0. Salir de la aplicación");
            System.out.println("------------------------------------------");

            int opcion = InputValidator.leerEntero(
                    "Seleccione una opción: ", 0, 2);

            switch (opcion) {
                case 1 -> iniciarSesion();
                case 2 -> registrarUsuario();
                case 0 -> {
                    System.out.println("¡Hasta pronto!");
                    salir = true;
                }
            }
        }
    }

    private static void iniciarSesion() {
        System.out.println("\n--- INICIAR SESIÓN ---");

        String username = InputValidator.leerTextoNoVacio("Usuario: ");
        String password = InputValidator.leerTextoNoVacio("Contraseña: ");

        User usuarioAutenticado = userDAO.login(username, password);

        if (usuarioAutenticado != null) {
            System.out.println(
                    "¡Bienvenido, " +
                            usuarioAutenticado.getUsername() + "!");

            MainMenuView.mostrar(usuarioAutenticado);

        } else {
            System.out.println("No se pudo iniciar sesión. " + "Verifica tu usuario y contraseña.");
        }
    }

    private static void registrarUsuario() {
        System.out.println("\n--- REGISTRO DE USUARIO ---");

        String username = InputValidator.leerTextoNoVacio(
                "Nuevo nombre de usuario: ");

        String email = InputValidator.leerTextoNoVacio(
                "Correo electrónico: ");

        String password = InputValidator.leerTextoNoVacio(
                "Contraseña: ");

        boolean registrado = userDAO.registrar(
                username, email, password);

        if (registrado) {
            System.out.println("Usuario '" + username + "' registrado exitosamente.");

            System.out.println("Se creó su categoría predeterminada 'General'.");
        } else {
            System.out.println("No se pudo registrar el usuario. " + "Verifica los datos e inténtalo nuevamente.");
        }
    }
}