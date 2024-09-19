package com.example.tars01;

import com.example.tars01.Database.Constans;
import com.example.tars01.Database.DatabaseManager;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.Duration;

import java.io.IOException;
import java.net.ServerSocket;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.example.tars01.Funtions.cargarVista;

public class EditController {

    public static final int SERVER_PORT = 8080;
    private ServerSocket serverSocket;
    private boolean serverRunning = false;
    @FXML
    TextField textFieldItemEdit;
    @FXML
    TextField name;
    @FXML
    TextField price;
    @FXML
    TextField code;
    @FXML
    Button saveChange;

    String priceOriginal;
    String codeOriginal;

    @FXML
    MenuItem seeEdit;

    @FXML
    Label hora;
    @FXML
    Label info;
    private Timeline timeline;


    @FXML
    private void initialize() {




        textFieldItemEdit.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (keyEvent.getCode() == KeyCode.ENTER) {
                    String searchText = textFieldItemEdit.getText();
                    if (!searchText.isEmpty()) {
                        searchProduct(searchText);
                    }
                }
            }
        });


        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yy HH:mm:ss");

        timeline = new Timeline(
                new KeyFrame(Duration.seconds(1), event -> {
                    LocalDateTime now = LocalDateTime.now();
                    hora.setText(formatter.format(now));
                })
        );
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();

        name.textProperty().addListener((observable, oldValue, newValue) -> {
            updateSaveButtonState();
        });

        price.textProperty().addListener((observable, oldValue, newValue) -> {
            updateSaveButtonState();
        });

        code.textProperty().addListener((observable, oldValue, newValue) -> {
            updateSaveButtonState();
        });

    }

    private void updateSaveButtonState() {
        String nameValue = name.getText();
        String priceValue = price.getText();
        String codeValue = code.getText();

        boolean isEmpty = nameValue.isEmpty() || priceValue.isEmpty() || codeValue.isEmpty();

        boolean isPriceChanged = !priceValue.equals(priceOriginal);


        // Desactiva el botón si alguno de los campos está vacío o si el precio no ha cambiado
        Platform.runLater(() -> saveChange.setDisable(isEmpty || !isPriceChanged));
    }

    public void cargarVistaCreate() throws IOException {
        Logout("CreateProducto-View.fxml",textFieldItemEdit);
    }
    public void cargarVistaVenta() throws IOException {

        Logout("Main-View.fxml",textFieldItemEdit);
    }

    public void cargarVistaBuscar() throws IOException {
        Logout("BuscarVenta-View.fxml",textFieldItemEdit);
    }





    public void update(){
        String nameValue = name.getText();
        String priceValue = price.getText();
        String codeValue = code.getText();
        DatabaseManager.actualizarProducto(nameValue,priceValue,codeValue,codeOriginal,info,name,price,code);
    }


    private void searchProduct(String value) {
        try (Connection connection = DriverManager.getConnection(Constans.URL1);
             PreparedStatement statement = connection.prepareStatement("SELECT nombre, precio, codigo_barras FROM productos WHERE codigo_barras = ?");
        ) {
            statement.setString(1, value.replace(" ", ""));
            ResultSet resultSet = statement.executeQuery();

            boolean productoEncontrado = false; // Variable para verificar si se encontró el producto

            while (resultSet.next()) {
                String productName = resultSet.getString("nombre");
                String productPrice = resultSet.getString("precio");
                String id = resultSet.getString("codigo_barras");
                productoEncontrado = true;
                priceOriginal = productPrice;
                codeOriginal = id;

                Platform.runLater(() -> name.setText(productName));
                Platform.runLater(() -> price.setText(productPrice));
                Platform.runLater(() -> code.setText(id));

                Platform.runLater(() -> info.setText("Modifique los valores, para guardar los cambios"));


            }

            if (!productoEncontrado) {

                Platform.runLater(() -> info.setText("Producto no encontrado"));

            }


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void OnServer() throws IOException {


    }




    public void Logout(String fxml,TextField textField) throws IOException {

        if (serverRunning) {
            if (serverSocket != null) {
                serverSocket.close();
                Platform.runLater(() -> info.setText("Servidor cerrado"));
            }

        }

        cargarVista(fxml,textField);



    }



}
