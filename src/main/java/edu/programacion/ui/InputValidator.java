package edu.programacion.ui;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Scanner;

public class InputValidator {
    private static final Scanner scanner = new Scanner(System.in);
    private static final DateTimeFormatter FORMATO = DateTimeFormatter
            .ofPattern("uuuu-MM-dd HH:mm:ss").withResolverStyle(ResolverStyle.STRICT);

    public static int leerEntero(String mensaje, int min, int max) {
        if (min > max) throw new IllegalArgumentException("Rango inválido.");
        while (true) {
            System.out.print(mensaje);
            try {
                int valor = Integer.parseInt(scanner.nextLine().trim());
                if (valor >= min && valor <= max) return valor;
                System.out.println("Debe estar entre " + min + " y " + max + ".");
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Ingrese un número entero.");
            }
        }
    }

    public static String leerTextoNoVacio(String mensaje) {
        return leerTextoNoVacio(mensaje, Integer.MAX_VALUE);
    }

    public static String leerTextoNoVacio(String mensaje, int max) {
        while (true) {
            System.out.print(mensaje);
            String texto = scanner.nextLine().trim();
            if (!texto.isEmpty() && texto.length() <= max) return texto;
            System.out.println("Ingrese de 1 a " + max + " caracteres.");
        }
    }

    public static String leerTextoOpcional(String mensaje, int max) {
        while (true) {
            System.out.print(mensaje);
            String texto = scanner.nextLine().trim();
            if (texto.length() <= max) return texto;
            System.out.println("Máximo: " + max + " caracteres.");
        }
    }

    public static LocalDateTime leerFechaOpcional(String mensaje) {
        while (true) {
            String texto = leerTextoOpcional(mensaje, 19);
            if (texto.isEmpty()) return null;
            try {
                LocalDateTime fecha = LocalDateTime.parse(texto, FORMATO);
                if (fecha.getYear() >= 1000 && fecha.getYear() <= 9999) return fecha;
                System.out.println("El año debe estar entre 1000 y 9999.");
            } catch (DateTimeParseException e) {
                System.out.println("Fecha inválida. Ejemplo: 2026-10-21 18:30:00. Enter: sin fecha.");
            }
        }
    }

    public static boolean leerConfirmacion(String mensaje) {
        while (true) {
            String texto = leerTextoNoVacio(mensaje + " (s/n): ");
            if (texto.equalsIgnoreCase("s")) return true;
            if (texto.equalsIgnoreCase("n")) return false;
            System.out.println("Responde s o n.");
        }
    }

    public static String leerPassword(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            java.io.Console console = System.console();
            String pass;
            if (console != null) {
                char[] chars = console.readPassword();
                pass = (chars != null) ? new String(chars).trim() : "";
            } else {
                pass = scanner.nextLine().trim();
            }
            if (!pass.isEmpty()) return pass;
            System.out.println("La contraseña no puede estar vacía.");
        }
    }
}
