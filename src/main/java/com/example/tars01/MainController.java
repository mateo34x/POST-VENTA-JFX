package com.example.tars01;

import com.example.tars01.Database.Constans;
import com.example.tars01.Database.DatabaseManager;
import com.example.tars01.Database.Producto;
import io.github.palexdev.materialfx.controls.MFXListView;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


public class MainController {

    // TODO y vaciar este cuando se cierre la venta, tambien hacer algunas R al cerrar caja y sesión

    private boolean serverRunning = false;
    boolean onTurno = false;


    public static final int SERVER_PORT = 8080;
    private ServerSocket serverSocket;
    double totalVenta = 0.0;
    double totalPagado = 0.0;
    private Timeline timeline;
    String cleanText;
    String str;


    @FXML
    public Label logArea;
    @FXML
    public Label info;
    @FXML
    public Label horaActual;

    @FXML
    public TextField textFieldItem;
    @FXML
    public TextField precio;
    @FXML
    public TextField textFieldPrice;
    @FXML
    public TextField textFieldTotalQuantity;
    @FXML
    public TextField textFieldTotalPaidAmount;
    @FXML
    public TextField textFieldChange;

    JTextArea productosArea = new JTextArea();

    @FXML

    public TableView<Producto> tableView = new TableView<>();

    @FXML
    private MFXListView productListView;

    private final Map<String, Integer> productCounts = new HashMap<>();


    @FXML
    public Button buttonSave;

    @FXML
    public MenuItem see;
    @FXML
    public MenuItem tON;
    @FXML
    public MenuItem tOFF;
    @FXML
    public Button buttonClear1;


    public MainController() throws IOException {
    }


    @FXML
    private void OnServer() throws IOException {


        if (see.getText().equals("Apagar")) {
            serverSocket.close();
            serverRunning = false;
            see.setText("Encender");
        } else if (see.getText().equals("Encender")) {
            serverSocket = new ServerSocket(SERVER_PORT);
            SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {

                    startServer();

                    return null;
                }
            };

            worker.execute();
            see.setText("Apagar");
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
                        Platform.runLater(() -> textFieldItem.setText(codigoP));
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

    public void Logout() throws IOException {

        if (!onTurno) {
            if (serverSocket != null) {
                serverSocket.close();
            }
            Stage stage = (Stage) textFieldItem.getScene().getWindow();
            stage.close();
            HelloApplication.go();
        }
        Platform.runLater(() -> info.setText("Hay un turno en activo, termine su turno primero"));

    }


    public void clear() {
        textFieldItem.clear();
        precio.clear();

    }


    public void ClearSould() {
        tableView.getItems().clear();
        totalVenta = 0.0;
        totalPagado = 0.0;
        textFieldItem.clear();
        textFieldTotalPaidAmount.clear();
        textFieldTotalQuantity.clear();
        textFieldChange.clear();
        productCounts.clear();
        tableView.refresh();
        Platform.runLater(() -> info.setText("Venta cancelada con éxito"));


    }


    public void ClearSouldV() {
        tableView.getItems().clear();
        totalVenta = 0.0;
        totalPagado = 0.0;
        textFieldItem.clear();
        textFieldTotalPaidAmount.clear();
        textFieldTotalQuantity.clear();
        textFieldChange.clear();
        productCounts.clear();
        tableView.refresh();
        Platform.runLater(() -> info.setText("Venta realizada con éxito"));
        Funtions.ClearMessage(textFieldChange, 0);


    }


    @FXML
    private void initialize() {


        TableColumn<Producto, String> nameColumn = new TableColumn<>("Nombre");
        nameColumn.setPrefWidth(240);
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());

        TableColumn<Producto, String> priceColumn = new TableColumn<>("Precio");
        priceColumn.setCellValueFactory(cellData -> cellData.getValue().priceProperty());

        TableColumn<Producto, Integer> quantityColumn = new TableColumn<>("Cantidad");
        quantityColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(productCounts.getOrDefault(cellData.getValue().getName(), 0)).asObject());

        tableView.getColumns().addAll(nameColumn, priceColumn, quantityColumn);


        //Ejecuta la función searchProduct cuando precionemos la tecla ENTER
        textFieldItem.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (keyEvent.getCode() == KeyCode.ENTER) {
                    String searchText = textFieldItem.getText();
                    if (!searchText.isEmpty()) {
                        searchProduct(searchText);
                    }
                }
            }
        });


        textFieldTotalPaidAmount.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                Platform.runLater(() -> buttonSave.setDisable(false));
            } else {
                Platform.runLater(() -> buttonSave.setDisable(true));
            }
        });


        tableView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1 && tableView.getSelectionModel().getSelectedItem() != null) {
                Producto selectedProduct = tableView.getSelectionModel().getSelectedItem();
                System.out.println(selectedProduct.getName());

                if (productCounts.containsKey(selectedProduct.getName())) {
                    System.out.println(productCounts.get(selectedProduct.getName()));
                    openDeleteDialog(productCounts.get(selectedProduct.getName()), selectedProduct);
                }

                //
            }
        });


        // Asignar el TextFormatter al TextField
        textFieldTotalPaidAmount.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                // Obtener el valor formateado

                String formattedValue = jTextField1KeyTyped(newValue);

                if (!formattedValue.equals(newValue)) {

                    textFieldTotalPaidAmount.setText(formattedValue);


                }
            }
        });


        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yy HH:mm:ss");

        timeline = new Timeline(
                new KeyFrame(Duration.seconds(1), event -> {
                    LocalDateTime now = LocalDateTime.now();
                    horaActual.setText(formatter.format(now));
                })
        );
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();

    }

    private void openDeleteDialog(int valueG, Producto producto) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Eliminar productos");
        dialog.setHeaderText("Eliminar productos de la venta");
        dialog.setContentText("Ingrese la cantidad de elementos a eliminar:");


        // Mostrar el diálogo y esperar a que el usuario ingrese la cantidad
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(quantityStr -> {
            try {
                int quantity = Integer.parseInt(quantityStr);
                int r = valueG - quantity;
                double minus = Double.parseDouble(producto.getPrice());

                if (quantity > 0 && quantity == valueG) {
                    Platform.runLater(() -> productCounts.remove(producto.getName()));
                    tableView.getItems().remove(producto);
                    tableView.refresh();

                }
                if (quantity > 0 && quantity <= valueG) {


                    productCounts.put(producto.getName(), r);
                    totalVenta -= minus * quantity;
                    Platform.runLater(() -> textFieldTotalQuantity.setText(getTotal()));
                    tableView.refresh();
                    System.out.println(totalVenta);
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Cantidad inválida",
                            "Ingrese una cantidad válida entre 1 y " + valueG);
                }
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Cantidad inválida",
                        "Ingrese un número entero válido");
            }
        });
    }

    private void showAlert(Alert.AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
//    private void updateProductList(String searchText) {
//        // Limpiamos el ListView
//        productListView.getItems().clear();
//
//        // Si el campo está vacío, ocultamos el ListView
//        if (searchText.isEmpty()) {
//            productListView.setVisible(false);
//            return;
//        }
//
//        try (Connection connection = DriverManager.getConnection(Constans.URL1);
//             PreparedStatement statement = connection.prepareStatement("SELECT nombre FROM productos WHERE codigo_barras LIKE ?");
//        ) {
//            // Hacemos la búsqueda en la base de datos
//            statement.setString(1, "%" + searchText + "%");
//            ResultSet resultSet = statement.executeQuery();
//
//            // Si no se encuentran resultados, ocultamos el ListView
//            if (!resultSet.next()) {
//                productListView.setVisible(false);
//                return;
//            }
//
//            // Mostramos el ListView y agregamos los resultados
//            productListView.setVisible(true);
//            do {
//                productListView.getItems().add(resultSet.getString("nombre"));
//            } while (resultSet.next());
//
//            // Ajustamos el tamaño del ListView según la cantidad de elementos
//            productListView.setPrefHeight(productListView.getItems().size() * 22); // 24 es la altura de cada elemento
//        }  catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//    }


    private void searchProduct(String searchText) {
        try (Connection connection = DriverManager.getConnection(Constans.URL1);
             PreparedStatement statement = connection.prepareStatement("SELECT nombre, precio FROM productos WHERE codigo_barras = ?");
        ) {
            statement.setString(1, searchText.replace(" ", ""));
            ResultSet resultSet = statement.executeQuery();

            boolean productoEncontrado = false; // Variable para verificar si se encontró el producto

            while (resultSet.next()) {
                String productName = resultSet.getString("nombre");
                String productPrice = resultSet.getString("precio");

                if (productCounts.containsKey(productName)) {
                    productCounts.put(productName, productCounts.get(productName) + 1);
                } else {
                    // Agregar el producto a la tabla
                    tableView.getItems().add(new Producto(productName, productPrice));
                    productCounts.put(productName, 1);
                }

                totalVenta += Double.parseDouble(productPrice);
                productoEncontrado = true; // Se encontró al menos un producto
            }

            if (!productoEncontrado) {

                if (productCounts.containsKey(textFieldItem.getText())) {
                    int indice = 0;

                    for (Producto producto : tableView.getItems()) {
                        // Verificar si el nombre del producto coincide con el nombre buscado
                        if (producto.getName().equals(textFieldItem.getText())) {
                            double antPrice = Double.parseDouble(producto.getPrice());
                            double newPrice = Double.parseDouble(precio.getText());
                            String precioFinal = String.valueOf(antPrice + newPrice);
                            totalVenta += Double.parseDouble(precio.getText());
                            tableView.getItems().get(indice).setPrice(precioFinal);


                            // Refrescar la vista de la tabla para reflejar el cambio
                            tableView.refresh();

                            // Salir del bucle una vez que se haya encontrado y actualizado el producto
                            break;
                        }
                        indice++;

                    }
                } else {

                    tableView.getItems().add(new Producto(textFieldItem.getText(), precio.getText()));
                    productCounts.put(textFieldItem.getText(), 1);
                    totalVenta += Double.parseDouble(precio.getText());
                    Platform.runLater(() -> info.setText("Producto no encontrado"));
                }
            } else {
                Platform.runLater(() -> info.setText("Producto añadido"));
            }

            tableView.refresh();
            Platform.runLater(() -> textFieldTotalQuantity.setText(getTotal()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public String getTotal() {

        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator('\'');
        DecimalFormat df = new DecimalFormat("#,##0", symbols);
        return df.format(totalVenta);
    }


    public String getChange(double result) {


        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator('\'');
        DecimalFormat df = new DecimalFormat("#,##0", symbols);
        return df.format(result);
    }

    public String format(String result) {


        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator('\'');
        DecimalFormat df = new DecimalFormat("#,##0", symbols);
        double number;
        number = Double.parseDouble(result);
        return df.format(number);
    }


    private String jTextField1KeyTyped(String text) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator('\'');
        DecimalFormat df = new DecimalFormat("#,##0", symbols);

        if (!text.isEmpty()) {
            try {
                // Convertir el texto a un número
                long number = Long.parseLong(text.replaceAll("'", ""));
                // Formatear el número
                String formattedText = df.format(number);
                // Agregar los separadores de miles nuevamente
                return formattedText.replace(",", "','");
            } catch (NumberFormatException e) {
                // Manejar la excepción si el texto no es un número válido
                e.printStackTrace();
            }
        }
        return text;
    }


    public void entregar() {


        cleanText = textFieldTotalPaidAmount.getText().replaceAll("'", "");
        try {
            totalPagado = Double.parseDouble(cleanText);
            if (totalPagado >= totalVenta) {
                double result = totalPagado - totalVenta;
                actualizarProductosArea();
                generarRecibo(result);
                Platform.runLater(() -> textFieldChange.setText(getChange(result)));
                System.out.println("Valor a devolver: " + (totalPagado - totalVenta));
                productCounts.clear();
                ClearSouldV();


            } else {
                info.setText("El valor pagado debe ser mayor al total de venta");
            }

        } catch (NumberFormatException e) {
            // Manejar la excepción si el texto no es un número válido
            e.printStackTrace();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void generarRecibo(double result) throws IOException {
        int numeroFacturaActual = Funtions.obtenerNumeroFactura();

        int numeroFacturaSiguiente = numeroFacturaActual + 1;

        String numeroFacturaFormateado = String.format("%03d", numeroFacturaSiguiente);

        Funtions.guardarNumeroFactura(numeroFacturaSiguiente);

        String contenidoRecibo = "        TIENDA LA BENDICIÓN DE DIOS\n" +
                "-----------------------------------------\n" +
                " Factura de venta: #" + numeroFacturaFormateado + "\n" +
                " Fecha de venta: " + horaActual.getText() + "\n" +
                " Atendido por: " + "usersesion" + "\n\n" +
                " Productos comprados ↓\n\n" +
                productosArea.getText() + "\n" +
                " Total: $ " + format(String.valueOf(totalVenta)) + "\n" +
                " Pago: $ " + format(String.valueOf(totalPagado)) + "\n" +
                " Cambio: $" + format(String.valueOf(result)) + "\n" +
                "-----------------------------------------\n\n" +
                "           GRACIAS POR SU COMPRA\n";
        String rutaArchivo = "/home/tars/Documentos/FACTURAS/" + UUID.randomUUID();
        FileWriter writer = new FileWriter(rutaArchivo);
        writer.write(contenidoRecibo);
        writer.close();
        DatabaseManager.SaveSold(numeroFacturaFormateado, horaActual.getText(), totalVenta, totalPagado, result, contenidoRecibo, info);
    }


    public void IniciarTurno() {
        onTurno = true;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        logArea.setText(formatter.format(now));
        tON.setDisable(true);
        tOFF.setDisable(false);
        textFieldItem.setDisable(false);
        precio.setDisable(false);
        textFieldTotalPaidAmount.setDisable(false);
        textFieldTotalQuantity.setDisable(false);
        tableView.setDisable(false);
        buttonClear1.setDisable(false);
        info.setText("Turno iniciado correctamente");


    }


    public void TerminarTurno() {

        System.out.println(productCounts.size());

        if (productCounts.isEmpty()) {
            onTurno = false;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            tON.setDisable(false);
            tOFF.setDisable(true);
            textFieldItem.setDisable(true);
            precio.setDisable(true);
            buttonSave.setDisable(true);
            textFieldTotalPaidAmount.setDisable(true);
            textFieldTotalQuantity.setDisable(true);
            tableView.setDisable(true);
            buttonClear1.setDisable(true);
            logArea.setText("El usuario ha terminado el turno a las: " + formatter.format(now));
            Platform.runLater(() -> info.setText("Turno terminado correctamente"));
        } else {
            Platform.runLater(() -> info.setText("No puede terminar su turno hay una venta en curso"));
        }


    }

    public void cargarVistaEditar() {
        cargarVista("Editar-View.fxml");
    }
    public void cargarVistaCrear() {
        cargarVista("CreateProducto-View.fxml");
    }


    private void cargarVista(String fxmlFile) {
        try {

            double screenWidth = Screen.getPrimary().getVisualBounds().getWidth();
            double screenHeight = Screen.getPrimary().getVisualBounds().getHeight();
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();


            // Obtener la referencia al VBox principal en el archivo FXML principal
            VBox mainContainer = (VBox) textFieldItem.getScene().getRoot();
            mainContainer.setPrefWidth(screenWidth);
            mainContainer.setPrefHeight(screenHeight);

            // Limpiar el contenedor principal y agregar la nueva vista
            mainContainer.getChildren().clear();
            mainContainer.getChildren().add(root);

        } catch (IOException e) {
            e.printStackTrace();
            // Manejo de errores
        }
    }

    public void actualizarProductosArea() {


        Font font = new Font("Arial", Font.PLAIN, 7);
        productosArea.setFont(font);


        productosArea.setText("  Nombre             Precio     Cantidad\n");
        productosArea.append("|---------------------------------------|\n");

        ObservableList<Producto> backUpVenta = tableView.getItems();

        for (Producto p : backUpVenta) {

            if (p.getName().length() >= 15) {
                productosArea.append(String.format("| %-15s", p.getName().substring(0, 15))); // Ajusta el ancho de la columna "Nombre"
            } else {
                productosArea.append(String.format("| %-15s", p.getName())); // Ajusta el ancho de la columna "Nombre"

            }


            productosArea.append(String.format("| %-11s", "$ " + format(p.getPrice()))); // Ajusta el ancho de la columna "Precio"
            productosArea.append(String.format("| %-8s", "  x " + productCounts.get(p.getName()))); // Ajusta el ancho de la columna "Cantidad"
            productosArea.append("|\n");
            productosArea.append("|---------------------------------------|\n");
        }


    }


}
