package com.example.tars01;

import com.example.tars01.Database.Constans;
import com.example.tars01.Database.DatabaseManager;
import com.example.tars01.Database.Producto;
import com.example.tars01.Servidor.ServerManager;
import io.github.palexdev.materialfx.controls.MFXListView;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.swing.*;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.sql.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


public class MainController {


    boolean onTurno = false;


    private ServerSocket serverSocket;
    static double totalVenta = 0.0;
    double totalPagado = 0.0;
    private Timeline timeline;
    String cleanText, PagoOption;
    String fecha, hora, NameUser;


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
    public TextField Nventa;
    @FXML
    public TextField textFieldTotalQuantity;
    @FXML
    public TextField textFieldTotalPaidAmount;
    @FXML
    public TextField textFieldChange;
    @FXML
    public TextArea ReciboViewVenta;
    @FXML
    public AnchorPane busquedaB;

    @FXML
    public TextField QueryInput;
    @FXML
    public TableView<Producto> tableSearchQuery = new TableView<>();

    private BuscarProductoController vista2Controller;


    JTextArea productosArea = new JTextArea();

    @FXML

    public TableView<Producto> tableView = new TableView<>();

    @FXML
    private MFXListView productListView;

    private final Map<String, Integer> productCounts = new HashMap<>();
    @FXML
    private ComboBox optionSold = new ComboBox<>();


    @FXML
    public Button buttonSave;
    @FXML
    public MenuItem see;
    @FXML
    public MenuItem tON;
    @FXML
    public MenuItem tOFF;
    @FXML
    public Button buttonClear1, buttonClear;


    @FXML
    private void OnServer() throws IOException {


        ServerManager.OnServer(see, info, textFieldItem, productCounts, tableView, precio, textFieldTotalQuantity);

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
        precio.clear();
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

    public void setUser(String user) {
        this.NameUser = user;
        //Obtener permisos del usuario
        System.out.println("User in controller: " + NameUser);
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

        String numeroFacturaFormateado = String.format("%03d", DatabaseManager.NVentas() + 1);

        Platform.runLater(() -> Nventa.setText(numeroFacturaFormateado));

        //Ejecuta la función searchProduct cuando precionemos la tecla ENTER
        textFieldItem.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (keyEvent.getCode() == KeyCode.ENTER) {
                    String searchText = textFieldItem.getText();
                    if (!searchText.isEmpty()) {
                        searchProductT(searchText);

                    }
                }
            }
        });


        precio.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (keyEvent.getCode() == KeyCode.ENTER) {
                    String searchText = textFieldItem.getText();
                    if (!searchText.isEmpty()) {
                        searchProductT(searchText);
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

        textFieldTotalQuantity.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                Platform.runLater(() -> textFieldTotalPaidAmount.setDisable(false));
            } else {
                Platform.runLater(() -> textFieldTotalPaidAmount.setDisable(true));
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

                String formattedValue = jTextField1KeyTyped(newValue);

                if (!formattedValue.equals(newValue)) {

                    textFieldTotalPaidAmount.setText(formattedValue);


                }
            }
        });


        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yy HH:mm:ss");
        DateTimeFormatter formatterOther = DateTimeFormatter.ofPattern("dd/MM/yy");
        DateTimeFormatter formatterTime = DateTimeFormatter.ofPattern("HH:mm:ss");

        timeline = new Timeline(
                new KeyFrame(Duration.seconds(1), event -> {
                    LocalDateTime now = LocalDateTime.now();
                    horaActual.setText(formatter.format(now));
                    fecha = formatterOther.format(now);
                    hora = formatterTime.format(now);


                })
        );
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();


        productListView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                String selectedItem = productListView.getSelectionModel().getSelection().toString();
                if (selectedItem != null) {
                    String id = selectedItem.substring(selectedItem.lastIndexOf("ID: ") + 4, selectedItem.length() - 2);
                    searchProductT(id);
                    System.out.println("Selected ID: " + id);
                    productListView.setVisible(false);
                    textFieldItem.clear();
                }
            }
        });

        optionSold.getItems().addAll(
                "Efectivo",
                "Nequi",
                "Crédito"
        );


        optionSold.getSelectionModel().selectedItemProperty().addListener((ov, t, t1) -> {
            PagoOption = t1.toString();

        });


        TableColumn<Producto, String> nameColumnS = new TableColumn<>("Code");
        nameColumnS.setPrefWidth(142);
        nameColumnS.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getId()));

        TableColumn<Producto, String> priceColumnS = new TableColumn<>("Nombre");
        priceColumnS.setPrefWidth(142);
        priceColumnS.setCellValueFactory(cellData -> cellData.getValue().nameProperty());

        TableColumn<Producto, String> quantityColumnS = new TableColumn<>("Precio");
        quantityColumnS.setPrefWidth(142);
        quantityColumnS.setCellValueFactory(cellData -> cellData.getValue().priceProperty());

        tableSearchQuery.getColumns().addAll(nameColumnS, priceColumnS, quantityColumnS);

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
                Platform.runLater(() -> textFieldItem.setText(selectedProduct.getId()));
                searchProductT(selectedProduct.getId());

            }
        });

        QueryInput.setOnKeyPressed(event ->{
            if (event.getCode() == KeyCode.DOWN){
                tableSearchQuery.requestFocus();
            }

        });

//        tableSearchQuery.setOnKeyPressed(event->{
//            if (event.getCode() == KeyCode.DOWN) {
////                int selectedIndex = tableSearchQuery.getSelectionModel().getSelectedIndex();
////                if (selectedIndex < tableSearchQuery.getItems().size() - 1) {
////                    tableSearchQuery.getSelectionModel().selectNext();
////                    System.out.println("Position: " + (selectedIndex + 1));
////                }
//                QueryInput.requestFocus();
//                System.out.println("hola");
//            } else if (event.getCode() == KeyCode.UP) {
////                int selectedIndex = tableSearchQuery.getSelectionModel().getSelectedIndex();
////                if (selectedIndex > 0) {
////                    tableSearchQuery.getSelectionModel().selectPrevious();
////                    System.out.println("Position: " + (selectedIndex - 1));
////                } else {
////                    System.out.println("Already at position 0, do something special here.");
////                }
//                System.out.println("adios");
//            }
//        });

        tableSearchQuery.setOnKeyPressed(event ->{
            if (event.getCode() == KeyCode.ENTER && tableSearchQuery.getSelectionModel().getSelectedItem() != null){

                Producto selectedProduct = tableSearchQuery.getSelectionModel().getSelectedItem();
                System.out.println(selectedProduct.getId());
                Platform.runLater(() -> textFieldItem.setText(selectedProduct.getId()));
                searchProductT(selectedProduct.getId());
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
                    Platform.runLater(() -> textFieldTotalQuantity.setText(getTotalT()));
                    tableView.refresh();
                    System.out.println(totalVenta);
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Cantidad inválida",
                            "Ingrese una cantidad válida entre 1 y " + valueG);
                }

                if (totalVenta == 0.0) {
                    Platform.runLater(() -> textFieldTotalQuantity.clear());
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
//


    public static void searchProduct(String searchText,
                                     Map<String, Integer> productCounts,
                                     TableView<Producto> tableView,
                                     TextField precio,
                                     TextField textFieldItem,
                                     TextField textFieldTotalQuantity,
                                     Label info) {
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

                if (precio.getText().isEmpty()) {
                    Platform.runLater(() -> info.setText("El campo de precio no puede estar vacío"));
                } else {
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
                }


            } else {
                Platform.runLater(() -> info.setText("Producto añadido"));
            }

            tableView.refresh();
            DecimalFormatSymbols symbols = new DecimalFormatSymbols();
            symbols.setGroupingSeparator('\'');
            DecimalFormat df = new DecimalFormat("#,##0", symbols);
            Platform.runLater(() -> textFieldTotalQuantity.setText(df.format(totalVenta)));

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void searchProductT(String searchText) {
        try (Connection connection = DriverManager.getConnection(Constans.URL1);
             PreparedStatement statement = connection.prepareStatement("SELECT nombre, precio FROM productos WHERE codigo_barras = ?");
        ) {
            statement.setString(1, searchText);
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

                precio.setDisable(false);
                if (precio.getText().isEmpty()) {
                    Platform.runLater(() -> info.setText("El producto con el codigo ingresado no existe, ingrese un precio"));
                } else {
                    if (productCounts.containsKey(textFieldItem.getText())) {
                        int indice = 0;

                        for (Producto producto : tableView.getItems()) {
                            if (producto.getName().equals(textFieldItem.getText())) {

                                totalVenta += Double.parseDouble(precio.getText());
                                tableView.getItems().get(indice).setPrice(precio.getText());
                                productCounts.put(textFieldItem.getText(), productCounts.get(textFieldItem.getText()) + 1);
                                tableView.refresh();
                                precio.clear();
                                precio.setDisable(true);

                                break;
                            }
                            indice++;

                        }
                    } else {

                        tableView.getItems().add(new Producto(textFieldItem.getText(), precio.getText()));
                        productCounts.put(textFieldItem.getText(), 1);
                        totalVenta += Double.parseDouble(precio.getText());
                        precio.setDisable(true);

                    }
                }


            } else {
                Platform.runLater(() -> info.setText("Producto añadido"));
            }

            tableView.refresh();
            Platform.runLater(() -> textFieldTotalQuantity.setText(getTotalT()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public String getTotalT() {

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

                textFieldTotalPaidAmount.clear();
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
        int numeroFacturaActual = DatabaseManager.NVentas();
        int numeroFacturaSiguiente = numeroFacturaActual + 1;
        String numeroFacturaFormateado = String.format("%03d", numeroFacturaSiguiente);

        if (PagoOption == null) {
            PagoOption = "Efectivo";
        }


        StringBuilder reciboBuilder = new StringBuilder();
        reciboBuilder.append("        TIENDA LA BENDICIÓN DE DIOS\n")
                .append("-----------------------------------------\n")
                .append(" Factura de venta: #").append(numeroFacturaFormateado).append("\n")
                .append(" Fecha de venta: ").append(fecha).append(" ").append(hora).append("\n")
                .append(" Atendido por: ").append(NameUser).append("\n")
                .append(" Pago: ").append(PagoOption).append("\n")
                .append(" Observaciones: ").append("").append("\n\n")
                .append(" Productos comprados ↓\n\n")
                .append(productosArea.getText()).append("\n")
                .append(" Total:$ ").append(format(String.valueOf(totalVenta))).append("\n")
                .append(" Pago:$ ").append(format(String.valueOf(totalPagado))).append("\n")
                .append(" Cambio:$ ").append(format(String.valueOf(result))).append("\n")
                .append("-----------------------------------------\n\n")
                .append("           ¡GRACIAS POR SU COMPRA!\n");

        String contenidoRecibo = reciboBuilder.toString();
        String rutaArchivo = "/home/matt/Documentos/FACTURAS/" + UUID.randomUUID();
        FileWriter writer = new FileWriter(rutaArchivo);
        writer.write(contenidoRecibo);
        writer.close();
        DatabaseManager.SaveSold(numeroFacturaFormateado, fecha, totalVenta, totalPagado, result, contenidoRecibo, info);
        int actual = DatabaseManager.NVentas();
        int sig = actual + 1;
        String prox = String.format("%03d", sig);
        Platform.runLater(() -> Nventa.setText(prox));
        Platform.runLater(() -> ReciboViewVenta.setText(String.valueOf(reciboBuilder)));
        Platform.runLater(() -> ReciboViewVenta.setVisible(true));


    }


    public void IniciarTurno() {
        onTurno = true;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        logArea.setText(formatter.format(now));
        tON.setDisable(true);
        tOFF.setDisable(false);
        optionSold.setDisable(false);
        textFieldItem.setDisable(false);
        textFieldTotalQuantity.setDisable(false);
        tableView.setDisable(false);
        buttonClear1.setDisable(false);
        buttonClear.setDisable(false);
        see.setDisable(false);
        info.setText("Turno iniciado correctamente");


    }


    public void TerminarTurno() {

        System.out.println(productCounts.size());


        if (productCounts.isEmpty()) {
            if (!ServerManager.serverRunning) {
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
                see.setDisable(true);
                textFieldChange.setDisable(true);
                buttonClear.setDisable(true);
                optionSold.setDisable(true);
                logArea.setText("El usuario ha terminado su turno a las: " + formatter.format(now));
                Platform.runLater(() -> info.setText("Turno terminado correctamente"));
            } else {
                Platform.runLater(() -> info.setText("Para finalizar su turno, apague el servidor"));
            }

        } else {
            Platform.runLater(() -> info.setText("No puede terminar su turno hay una venta en curso"));
        }


    }

    public void cargarVistaEditar() throws IOException {

        HelloEditProduct.go();
    }

    public void cargarVistaCrear() throws IOException {

        HelloCreateProduct.go();
    }

    public void cargarVistaBuscar() throws IOException {

        HelloBuscar.go();
    }


    public void actualizarProductosArea() {


        Font font = new Font("Monospaced", Font.PLAIN, 12);
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
