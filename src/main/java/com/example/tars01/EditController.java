package com.example.tars01;

import com.example.tars01.Database.Constans;
import com.example.tars01.Database.DatabaseManager;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.util.Duration;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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

    public void cargarVistaCreate() {
        cargarVista("CreateProducto-View.fxml");
    }
    public void cargarVistaVenta() {
        cargarVista("Main-View.fxml");
    }


    public void cargarVista(String fxmlFile) {
        try {
            // Cargar el archivo FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();
            double screenWidth = Screen.getPrimary().getVisualBounds().getWidth();
            double screenHeight = Screen.getPrimary().getVisualBounds().getHeight();

            // Obtener la referencia al VBox principal en el archivo FXML principal
            VBox mainContainer = (VBox) textFieldItemEdit.getScene().getRoot();
            mainContainer.setPrefHeight(screenHeight);
            mainContainer.setPrefWidth(screenWidth);

            // Limpiar el contenedor principal y agregar la nueva vista
            mainContainer.getChildren().clear();
            mainContainer.getChildren().add(root);

        } catch (IOException e) {
            e.printStackTrace();
            // Manejo de errores
        }
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


        if (seeEdit.getText().equals("Apagar")) {
            serverSocket.close();
            serverRunning = false;
            seeEdit.setText("Encender");
        } else if (seeEdit.getText().equals("Encender")) {
            serverSocket = new ServerSocket(SERVER_PORT);
            SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {

                    startServer();

                    return null;
                }
            };

            worker.execute();
            seeEdit.setText("Apagar");
        }

    }


    public void startServer() {
        try {


            serverRunning = true;
            Platform.runLater(() -> info.setText("Servidor iniciado. Esperando conexiones..."));


            while (serverRunning) {
                Socket clientSocket = serverSocket.accept();
                Platform.runLater(() -> info.setText("Cliente conectado desde " + clientSocket.getInetAddress()));
                System.out.println("Cliente conectado desde " + clientSocket.getInetAddress());


                try {
                    BufferedReader inputReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));


                    String message;
                    while ((message = inputReader.readLine()) != null) {
                        String codigoP = message.trim();

                        Platform.runLater(() -> info.setText("Mensaje recibido"));
                        Platform.runLater(() -> textFieldItemEdit.setText(codigoP));
                        searchProduct(codigoP);

                        System.out.println(codigoP);

                    }
                } catch (IOException e) {
                    Platform.runLater(() -> info.setText("Error al leer mensaje del cliente: " + e.getMessage()));


                }
            }
        } catch (IOException e) {
            Platform.runLater(() -> info.setText(e.getMessage()));


        }
    }



}
