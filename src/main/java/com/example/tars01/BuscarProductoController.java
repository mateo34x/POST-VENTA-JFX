package com.example.tars01;

import com.example.tars01.Database.Constans;
import com.example.tars01.Database.Producto;
import com.example.tars01.Servidor.ServerManager;
import com.sun.javafx.property.adapter.PropertyDescriptor;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.util.Duration;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

public class BuscarProductoController {


    static String mensaje;
    @FXML
    public TextField QueryInput;
    @FXML
    public TableView<Producto> tableSearchQuery = new TableView<>();








    @FXML
    public void initialize() {
        TableColumn<Producto, String> nameColumn = new TableColumn<>("Code");
        nameColumn.setPrefWidth(204);
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getId()));

        TableColumn<Producto, String> priceColumn = new TableColumn<>("Nombre");
        priceColumn.setPrefWidth(204);
        priceColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());

        TableColumn<Producto, String> quantityColumn = new TableColumn<>("Precio");
        quantityColumn.setPrefWidth(204);
        quantityColumn.setCellValueFactory(cellData -> cellData.getValue().priceProperty());

        tableSearchQuery.getColumns().addAll(nameColumn, priceColumn, quantityColumn);

        QueryInput.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                updateProductList(newValue);
            } else {
                tableSearchQuery.getItems().clear();
            }
        });

        tableSearchQuery.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1 && tableSearchQuery.getSelectionModel().getSelectedItem() != null) {
                Producto selectedProduct = tableSearchQuery.getSelectionModel().getSelectedItem();
                System.out.println(selectedProduct.getId());
                mensaje = selectedProduct.getId();
                update();





            }
        });
    }
    private void updateProductList(String searchText) {
        tableSearchQuery.getItems().clear();

        if (searchText.isEmpty()) {
            tableSearchQuery.getItems().clear();
            return;
        }

        try (Connection connection = DriverManager.getConnection(Constans.URL1);
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT nombre, codigo_barras, precio FROM productos WHERE nombre LIKE ? OR codigo_barras LIKE ?")
        ) {
            String searchPattern = "%" + searchText + "%";
            statement.setString(1, searchPattern);
            statement.setString(2, searchPattern);
            ResultSet resultSet = statement.executeQuery();

            if (!resultSet.next()) {
                tableSearchQuery.getItems().clear();
                return;
            }


            do {
                String nombre = resultSet.getString("nombre");
                String codigoBarras = resultSet.getString("codigo_barras");
                String precio = resultSet.getString("precio");
                Producto p = new Producto(nombre, precio);
                p.setId(codigoBarras);
                tableSearchQuery.getItems().add(p);
            } while (resultSet.next());

        } catch (SQLException e) {
            System.err.println("Error al buscar productos: " + e.getMessage());
        }
    }

    public String update(){
        return mensaje;
    }

}








