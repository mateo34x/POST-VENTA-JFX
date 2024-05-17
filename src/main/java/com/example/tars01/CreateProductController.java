package com.example.tars01;

import com.example.tars01.Database.Constans;
import com.example.tars01.Database.DatabaseManager;
import com.example.tars01.Database.Producto;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
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

import static com.example.tars01.Funtions.cargarVista;


public class CreateProductController {


    public static final int SERVER_PORT = 8080;
    private ServerSocket serverSocket;
    private boolean serverRunning = false;
    private Timeline timeline;
    @FXML
    MenuItem seeCreate,editar,venta;
    @FXML
    TextField nameCreate,priceCreate,codeCreate;
    @FXML
    Label infoCreate,horaCreate;
    @FXML
    Button saveCreate;
    @FXML
    TableView tableViewShow;


    @FXML
    private void initialize() {

        TableColumn<Producto,String> idColum = new TableColumn<>("#Facura");
        idColum.setPrefWidth(300);
        idColum.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getId()));


        TableColumn<Producto, String> nameColumn = new TableColumn<>("Nombre");
        nameColumn.setPrefWidth(240);
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());

        TableColumn<Producto, String> priceColumn = new TableColumn<>("Precio");
        priceColumn.setCellValueFactory(cellData -> cellData.getValue().priceProperty());

        tableViewShow.getColumns().addAll(idColum,nameColumn, priceColumn);


        //Ejecuta la función searchProduct cuando precionemos la tecla ENTER
//        textFieldItemCreate.setOnKeyPressed(new EventHandler<KeyEvent>() {
//            @Override
//            public void handle(KeyEvent keyEvent) {
//                if (keyEvent.getCode() == KeyCode.ENTER) {
//                    String searchText = textFieldItemCreate.getText();
//                    if (!searchText.isEmpty()) {
//                        save();
//
//                    }
//                }
//           }
//         });


        nameCreate.textProperty().addListener((observable, oldValue, newValue) -> {
            updateSaveButtonState();
        });
        priceCreate.textProperty().addListener((observable, oldValue, newValue) -> {
            updateSaveButtonState();
        });

        codeCreate.textProperty().addListener((observable, oldValue, newValue) -> {
            updateSaveButtonState();
        });





        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yy HH:mm:ss");

        timeline = new Timeline(
                new KeyFrame(Duration.seconds(1), event -> {
                    LocalDateTime now = LocalDateTime.now();
                    horaCreate.setText(formatter.format(now));
                })
        );
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();

    }


    @FXML
    private void OnServer() throws IOException {


        if (seeCreate.getText().equals("Apagar")) {
            serverSocket.close();
            serverRunning = false;
            seeCreate.setText("Encender");
        } else if (seeCreate.getText().equals("Encender")) {
            serverSocket = new ServerSocket(SERVER_PORT);
            SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {

                    startServer();

                    return null;
                }
            };

            worker.execute();
            seeCreate.setText("Apagar");
        }

    }


    public void startServer() {
        try {


            serverRunning = true;
            Platform.runLater(() -> infoCreate.setText("Servidor iniciado. Esperando conexiones..."));


            while (serverRunning) {
                Socket clientSocket = serverSocket.accept();
                Platform.runLater(() -> infoCreate.setText("Cliente conectado desde " + clientSocket.getInetAddress()));
                System.out.println("Cliente conectado desde " + clientSocket.getInetAddress());


                try {
                    BufferedReader inputReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));


                    String message;
                    while ((message = inputReader.readLine()) != null) {
                        String codigoP = message.trim();

                        Platform.runLater(() -> infoCreate.setText("Mensaje recibido"));
//                      Platform.runLater(() -> textFieldItemCreate.setText(codigoP));
                        Platform.runLater(() -> codeCreate.setText(codigoP));
                        save();

                        System.out.println(codigoP);

                    }
                } catch (IOException e) {
                    Platform.runLater(() -> infoCreate.setText("Error al leer mensaje del cliente: " + e.getMessage()));


                }
            }
        } catch (IOException e) {
            Platform.runLater(() -> infoCreate.setText(e.getMessage()));



        }
    }


    public void save() {
        String name = nameCreate.getText();
        String price = priceCreate.getText();
        String id = codeCreate.getText();
        Producto p = new Producto(name,price);
        p.setId(id);
        DatabaseManager.insertarProducto(p,infoCreate,tableViewShow);
    }


    public  void obtenerTodosLosProductos() {
        int cantidad = 0;
        String sql = "SELECT codigo_barras, nombre, precio FROM productos";
        try (Connection connection = DriverManager.getConnection(Constans.URL1);
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                String id = resultSet.getString("codigo_barras");
                String name = resultSet.getString("nombre");
                String price = resultSet.getString("precio");
                Producto producto = new Producto(name,price);
                producto.setId(id);
                tableViewShow.getItems().add(producto);
                tableViewShow.refresh();
                cantidad++;
                int finalCantidad = cantidad;
                Platform.runLater(()->infoCreate.setText(finalCantidad +" productos obtenidos correctamente"));
            }
        } catch (SQLException e) {
            Platform.runLater(()->infoCreate.setText("Error al obtener todos los productos"));

        }

    }

    private void updateSaveButtonState() {
        String nameValue = nameCreate.getText();
        String priceValue = priceCreate.getText();
        String codeValue = codeCreate.getText();

        boolean isEmpty = nameValue.isEmpty() || priceValue.isEmpty() || codeValue.isEmpty();


        if (isEmpty){
            Platform.runLater(() -> infoCreate.setText("Hay campos vacios, complételos"));
        }else{
            Platform.runLater(() -> infoCreate.setText("Listo para crear el producto"));
        }

        // Desactiva el botón si alguno de los campos está vacío o si el precio no ha cambiado
        Platform.runLater(() -> saveCreate.setDisable(isEmpty));
    }


    public void cargarVistaEditar() {
        cargarVista("Editar-View.fxml",nameCreate);
    }
    public void cargarVistaVenta() {
        cargarVista("Main-View.fxml",nameCreate);
    }
    public void cargarVistaBuscar() {
        cargarVista("BuscarVenta-View.fxml",nameCreate);
    }






}
