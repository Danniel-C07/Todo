package edu.programacion.ui;

import edu.programacion.dao.UserDAO;
import edu.programacion.models.User;

public class AuthMenuView {
    private static final UserDAO userDAO = new UserDAO();

    public static void mostrar() {
        boolean salir = false;
        while (!salir) {
            System.out.println("\n=== BIENVENIDO A TO-DO LIST APP ===");
            System.out.println("1. Iniciar sesión");
            System.out.println("2. Registrar nuevo usuario");
            System.out.println("0. Salir");
            int opcion = InputValidator.leerEntero("Opción: ", 0, 2);
            switch (opcion) {
                case 1 -> iniciarSesion();
                case 2 -> registrarUsuario();
                case 0 -> {
                    SessionData.cerrarSesion();
                    System.out.println("Hasta pronto.");
                    salir = true;
                }
            }
        }
    }

    private static void iniciarSesion() {
        SessionData.cerrarSesion();
        String username = InputValidator.leerTextoNoVacio("Usuario: ", 50);
        String password = InputValidator.leerPassword("Contraseña: ");
        User usuario = userDAO.login(username, password);
        if (usuario == null) {
            System.out.println("No se pudo iniciar sesión. Revisa tus credenciales y la conexión.");
            return;
        }
        SessionData.iniciarSesion(usuario);
        System.out.println("Bienvenido, " + usuario.getUsername() + ".");
        try {
            MainMenuView.mostrar(SessionData.obtenerUsuarioActual());
        } finally {
            SessionData.cerrarSesion();
        }
    }

    private static void registrarUsuario() {
        String username = InputValidator.leerTextoNoVacio("Nuevo usuario: ", 50);
        String email;
        while (true) {
            email = InputValidator.leerTextoNoVacio("Correo: ", 100);
            if (email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) break;
            System.out.println("Correo inválido. Ejemplo: nombre@dominio.com");
        }
        String password = InputValidator.leerPassword("Contraseña: ");
        if (userDAO.registrar(username, email, password)) {
            System.out.println("Usuario registrado con su categoría General. Ya puedes iniciar sesión.");
        } else {
            System.out.println("No se pudo registrar. Revisa los datos y la conexión.");
        }
    }
}
