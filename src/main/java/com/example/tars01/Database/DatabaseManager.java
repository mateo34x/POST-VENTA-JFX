package com.example.tars01.Database;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {


    public static void insertarProducto(Producto producto, Label info, TableView tableView,TextField n,TextField p,TextField c,TextField cs) {
        DatabaseManager.createTable();
        String sqlSelect = "SELECT COUNT(*) AS count FROM productos WHERE codigo_barras = ?";
        String sqlInsert = "INSERT INTO productos (codigo_barras, nombre, precio, stock) VALUES (?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(Constans.URL1);
             PreparedStatement selectStatement = connection.prepareStatement(sqlSelect);
             PreparedStatement insertStatement = connection.prepareStatement(sqlInsert)) {

            selectStatement.setString(1, producto.getId());
            ResultSet resultSet = selectStatement.executeQuery();
            resultSet.next();
            int count = resultSet.getInt("count");

            if (count > 0) {
                Platform.runLater(() -> info.setText("El id: " + producto.getId() + " pertenece a un producto existente"));

            } else {
                insertStatement.setString(1, producto.getId());
                insertStatement.setString(2, producto.getName());
                insertStatement.setString(3, producto.getPrice());
                insertStatement.setString(4, producto.getStock());
                insertStatement.executeUpdate();
                obtenerTodosLosProductos(tableView, info);
                Platform.runLater(() -> {
                    info.setText("Producto creado correctamente");
                    n.clear();
                    p.clear();
                    c.clear();
                    cs.clear();
                });
            }


        } catch (SQLException e) {
            Platform.runLater(() -> info.setText("Error al guardar el producto: " + e.getMessage()));

        }
    }


    public static void insertarCliente(Cliente cliente, Label info, TextField n,TextField l,TextField i,TextField p,TextField c,TextField a) {
        DatabaseManager.createTableClients();
        String sqlSelect = "SELECT COUNT(*) AS count FROM clientes WHERE CC = ?";
        String sqlInsert = "INSERT INTO clientes (CC, Name, lastName, Address, Number, Email) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(Constans.URL4);
             PreparedStatement selectStatement = connection.prepareStatement(sqlSelect);
             PreparedStatement insertStatement = connection.prepareStatement(sqlInsert)) {

            selectStatement.setString(1, cliente.getId());
            ResultSet resultSet = selectStatement.executeQuery();
            resultSet.next();
            int count = resultSet.getInt("count");

            if (count > 0) {
                Platform.runLater(() -> info.setText("La cédula: " + cliente.getId() + " pertenece a un cliente ya existente"));

            } else {
                insertStatement.setString(1, cliente.getId());
                insertStatement.setString(2, cliente.getName());
                insertStatement.setString(3, cliente.getLastname());
                insertStatement.setString(4, cliente.getAddress());
                insertStatement.setString(5, cliente.getPhone());
                insertStatement.setString(6, cliente.getEmail());
                insertStatement.executeUpdate();
                //obtenerTodosLosProductos(tableView, info);
                Platform.runLater(() -> {
                    info.setText("Cliente guardado correctamente");
                    n.clear();
                    l.clear();
                    i.clear();
                    p.clear();
                    c.clear();
                    a.clear();
                });
            }


        } catch (SQLException e) {
            Platform.runLater(() -> info.setText("Error al crear el cliente: " + e.getMessage()));
            System.out.println(e.getMessage());

        }
    }


    public static void createTable() {
        try (Connection connection = DriverManager.getConnection(Constans.URL1);
             Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS productos (" +
                    "codigo_barras TEXT PRIMARY KEY," +
                    "nombre TEXT," +
                    "precio TEXT," +
                    "stock  TEXT)");
        } catch (SQLException e) {
            System.err.println("Error al crear la tabla de productos: " + e.getMessage());
        }
    }

    public static void createTableUser() {
        try (Connection connection = DriverManager.getConnection(Constans.URL2);
             Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS users (" +
                    "code TEXT PRIMARY KEY," +
                    "name TEXT," +
                    "user TEXT," +
                    "per TEXT," +
                    "pass TEXT)");
        } catch (SQLException e) {
            System.err.println("Error al crear la tabla de users: " + e.getMessage());
        }
    }


    public static void createTableVentas() {
        try (Connection connection = DriverManager.getConnection(Constans.URL3);
             Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS Ventas (" +
                    "idVenta TEXT PRIMARY KEY," +
                    "fecha TEXT," +
                    "totalVenta DECIMAL(10, 2)," +
                    "cantidadPagada DECIMAL(10, 2)," +
                    "cambio DECIMAL(10, 2)," +
                    "detallesVenta TEXT)");
        } catch (SQLException e) {
            System.err.println("Error al crear la tabla de Ventas: " + e.getMessage());
        }
    }
    public static void createTableClients(){
        try (Connection connection = DriverManager.getConnection(Constans.URL4);
             Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS clientes (" +
                    "CC TEXT PRIMARY KEY," +
                    "Name TEXT," +
                    "lastName TEXT," +
                    "Address TEXT," +
                    "Number TEXT," +
                    "Email TEXT)");
        } catch (SQLException e) {
            System.err.println("Error al crear la tabla de Clientes: " + e.getMessage());
        }
    }

    public static void SaveSold(String id, String fecha, double totalVenta, double cantidadPagada, double cambio, String detalles, Label info) {
        DatabaseManager.createTableVentas();

        String sqlInsert = "INSERT INTO Ventas (idVenta, fecha, totalVenta, cantidadPagada, cambio, detallesVenta) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(Constans.URL3);
             PreparedStatement statement = connection.prepareStatement(sqlInsert)) {

            statement.setString(1, id);
            statement.setString(2, fecha);
            statement.setDouble(3, totalVenta);
            statement.setDouble(4, cantidadPagada);
            statement.setDouble(5, cambio);
            statement.setString(6, detalles);
            statement.executeUpdate();


        } catch (SQLException e) {
            Platform.runLater(() -> info.setText("Error al insertar el producto: " + e.getMessage()));
        }
    }


    public static void actualizarProducto(String nombre, String precio, String nuevoCodigo, String codigoExistente, Label info, TextField name, TextField price, TextField code) {
        // Construir la consulta SQL base
        String sqlUpdate = "UPDATE productos SET ";

        // Lista para almacenar las partes de la consulta SQL que necesitan ser actualizadas
        List<String> updates = new ArrayList<>();

        // Lista para almacenar los valores de los parámetros a establecer en la consulta preparada
        List<Object> parameters = new ArrayList<>();

        // Comprobar y construir la parte de la consulta SQL para cada campo que se quiere actualizar
        if (nombre != null) {
            updates.add("nombre = ?");
            parameters.add(nombre);
        }
        if (precio != null) {
            updates.add("precio = ?");
            parameters.add(precio);
        }
        if (nuevoCodigo != null) {
            updates.add("codigo_barras = ?");
            parameters.add(nuevoCodigo);
        }


        // Combinar las partes de la consulta SQL para construir la consulta final
        sqlUpdate += String.join(", ", updates);
        sqlUpdate += " WHERE codigo_barras = ?"; // Condición de actualización basada en el código de barras existente

        try (Connection connection = DriverManager.getConnection(Constans.URL1);
             PreparedStatement updateStatement = connection.prepareStatement(sqlUpdate)) {

            // Establecer los valores de los parámetros en la consulta preparada
            for (int i = 0; i < parameters.size(); i++) {
                updateStatement.setObject(i + 1, parameters.get(i));
            }

            // Establecer el código de barras existente como último parámetro
            updateStatement.setString(parameters.size() + 1, codigoExistente);

            // Ejecutar la consulta SQL y obtener el número de filas afectadas
            int rowsAffected = updateStatement.executeUpdate();

            // Verificar si se actualizaron filas
            if (rowsAffected > 0) {
                Platform.runLater(() -> info.setText("Producto actualizado correctamente."));
                Platform.runLater(name::clear);
                Platform.runLater(price::clear);
                Platform.runLater(code::clear);


            } else {
                Platform.runLater(() -> info.setText("El producto a actualizar no existe.."));
            }
        } catch (SQLException e) {
            Platform.runLater(() -> info.setText("Error al actualizar el producto: " + e.getMessage()));
        }
    }


    public static void obtenerTodosLosProductos(TableView tableView, Label info) {
        int cantidad = 0;
        String sql = "SELECT codigo_barras, nombre, precio FROM productos";
        try (Connection connection = DriverManager.getConnection(Constans.URL1);
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                String id = resultSet.getString("codigo_barras");
                String name = resultSet.getString("nombre");
                String price = resultSet.getString("precio");
                Producto producto = new Producto(id,name, price);
                tableView.getItems().add(producto);
                tableView.refresh();
                cantidad++;
                int finalCantidad = cantidad;
                Platform.runLater(() -> info.setText(finalCantidad + " productos obtenidos correctamente"));
            }
        } catch (SQLException e) {
            Platform.runLater(() -> info.setText("Error al obtener todos los productos"));

        }

    }

    public static void searchFactura(TextArea viewFactura, TextArea info, String idFactura) {
        try (Connection connection = DriverManager.getConnection(Constans.URL3);
             PreparedStatement statement = connection.prepareStatement("SELECT detallesVenta  FROM ventas WHERE idVenta = ?");
        ) {
            statement.setString(1, idFactura.replace(" ", ""));
            ResultSet resultSet = statement.executeQuery();

            boolean productoEncontrado = false; // Variable para verificar si se encontró el producto

            while (resultSet.next()) {
                String facturaInfo = resultSet.getString("detallesVenta");
                Platform.runLater(() -> viewFactura.setText(facturaInfo));
                Platform.runLater(() -> info.setText("Factura encontrada"));


                productoEncontrado = true; // Se encontró al menos un producto
            }

            if (!productoEncontrado) {

                Platform.runLater(() -> info.setText("No hemos encontrado la factura con ID: " + idFactura));
                Platform.runLater(() -> viewFactura.setText("No hay información sobre la factura solicitada"));

            }


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static int NVentas() {
        int count = 0;
        try (Connection connection = DriverManager.getConnection(Constans.URL3);
             PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) AS count FROM ventas");
        ) {

            ResultSet resultSet = statement.executeQuery();


            count = resultSet.getInt("count");


        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }


    public static void filtrarVenta(TableView tableView, TextArea info, String fechaI, String FechaE) {

    }



}



