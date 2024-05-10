package com.example.tars01.Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {

    public static void createTable() {
        try (Connection connection = DriverManager.getConnection(Constans.URL1);
             Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS productos (" +
                    "codigo_barras TEXT PRIMARY KEY," +
                    "nombre TEXT," +
                    "precio REAL)");
        } catch (SQLException e) {
            System.err.println("Error al crear la tabla de productos: " + e.getMessage());
        }
    }

    public static void createTableUser() {
        try (Connection connection = DriverManager.getConnection(Constans.URL2);
             Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS users (" +
                    "code TEXT PRIMARY KEY," +
                    "name TEXT,"+
                    "user TEXT," +
                    "pass TEXT)");
        } catch (SQLException e) {
            System.err.println("Error al crear la tabla de users: " + e.getMessage());
        }
    }
}
