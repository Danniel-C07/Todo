package edu.programacion.ui;

import edu.programacion.models.User;

public final class SessionData {
    private static User usuarioActual;

    private SessionData() { }

    public static void iniciarSesion(User usuario) {
        if (usuario == null || usuario.getIdUser() <= 0) {
            throw new IllegalArgumentException("Usuario inválido para iniciar sesión.");
        }
        User copia = new User();
        copia.setIdUser(usuario.getIdUser());
        copia.setUsername(usuario.getUsername());
        copia.setEmail(usuario.getEmail());
        copia.setCreatedAt(usuario.getCreatedAt());
        usuarioActual = copia;
    }

    public static boolean haySesion() { return usuarioActual != null; }

    public static User obtenerUsuarioActual() {
        if (!haySesion()) throw new IllegalStateException("No hay una sesión iniciada.");
        return usuarioActual;
    }

    public static void cerrarSesion() { usuarioActual = null; }
}
