package edu.programacion;

import edu.programacion.db.DatabaseConnection;
import edu.programacion.ui.AuthMenuView;
import java.sql.Connection;

public class Main {
    public static void main(String[] args) {
        Connection conn = DatabaseConnection.getConnection();
        if (conn != null) {
            AuthMenuView.mostrar();
        }
    }
}