package com.example.tars01;

import com.example.tars01.Database.Constans;
import com.example.tars01.Database.DatabaseManager;
import com.example.tars01.Database.Producto;
import com.example.tars01.Printer.Command;
import com.example.tars01.Printer.PrinterCommand;
import com.example.tars01.Servidor.ServerManager;
import com.jfoenix.controls.JFXTreeTableView;
import io.github.palexdev.materialfx.controls.MFXListView;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.ServerSocket;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.Date;

import static com.example.tars01.Printer.PicturePrinterThermal.*;
import static com.example.tars01.Printer.PrinterCommandsAct.*;
import static com.example.tars01.TextToBinaryConverter.SendDataByte;
import static com.example.tars01.TextToBinaryConverter.SendDataString;


public class MainController {


    boolean onTurno = false;


    private ServerSocket serverSocket;
    static double totalVenta = 0.0;
    double totalPagado = 0.0;
    private Timeline timeline;
    String cleanText, PagoOption;
    String fecha, hora, NameUser, Permission;


    @FXML
    public Label logArea, labelItem121, Tpago, TCambio;
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
    public TextArea ReciboViewVenta, obser;
    @FXML
    public AnchorPane busquedaB;

    @FXML
    public TextField QueryInput;
    @FXML
    public TableView<Producto> tableSearchQuery = new TableView<>();

    JTextArea productosArea = new JTextArea();

    @FXML

    public JFXTreeTableView<Producto> tableView = new JFXTreeTableView<>();

    @FXML
    private MFXListView<Producto> productListView;

    private final Map<String, Integer> productCounts = new HashMap<>();
    @FXML
    private ComboBox<String> optionSold = new ComboBox<>();


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
    static DecimalFormat df;


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


        tableView.getRoot().getChildren().clear();
        totalVenta = 0.0;
        totalPagado = 0.0;
        textFieldItem.clear();
        textFieldItem.requestFocus();
        textFieldTotalPaidAmount.clear();
        textFieldTotalPaidAmount.setVisible(false);
        Tpago.setVisible(false);
        TCambio.setVisible(false);
        textFieldTotalQuantity.clear();
        textFieldChange.clear();
        textFieldChange.setVisible(false);
        precio.clear();
        productCounts.clear();
        tableView.refresh();
        Platform.runLater(() -> info.setText("Venta cancelada con éxito"));
        optionSold.setValue("Tipo de pago");
        optionSold.setButtonCell(new ListCell<String>() {
            @Override
            protected void updateItem(String s, boolean b) {
                super.updateItem(s, b);
                if (b || s == null) {
                    setText("Select Subject");
                } else {
                    setText(s);
                }
            }
        });


    }


    public void ClearSouldV() {
        tableView.getRoot().getChildren().clear();
        totalVenta = 0.0;
        totalPagado = 0.0;
        textFieldItem.clear();
        textFieldItem.requestFocus();
        textFieldTotalPaidAmount.clear();
        textFieldTotalPaidAmount.setVisible(false);
        textFieldTotalQuantity.clear();
        textFieldChange.clear();
        textFieldChange.setVisible(false);
        Tpago.setVisible(false);
        TCambio.setVisible(false);
        productCounts.clear();
        tableView.refresh();
        optionSold.setValue("Tipo de pago");
        optionSold.setButtonCell(new ListCell<String>() {
            @Override
            protected void updateItem(String s, boolean b) {
                super.updateItem(s, b);
                if (b || s == null) {
                    setText("Select Subject");
                } else {
                    setText(s);
                }
            }
        });
        Platform.runLater(() -> info.setText("Venta realizada con éxito"));
        Funtions.ClearMessage(textFieldChange, 0);


    }

    public void setUser(String user, String per) {
        this.NameUser = user;
        this.Permission = per;
        //Obtener permisos del usuario
        System.out.println("User in controller: " + NameUser);
        System.out.println("User permission: " + Permission);
    }


    @FXML
    private void initialize() {

        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator('\'');
        df = new DecimalFormat("#,##0", symbols);


        TreeTableColumn<Producto, String> codeColumn = new TreeTableColumn<>("CODE");
        codeColumn.setPrefWidth(163);
        codeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getValue().getId()));
        codeColumn.setResizable(false);
        codeColumn.setReorderable(false);
        codeColumn.setVisible(false);


        TreeTableColumn<Producto, String> nameColumn = new TreeTableColumn<>("NOMBRE");
        nameColumn.setPrefWidth(163);
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getValue().getName()));
        nameColumn.setResizable(false);
        nameColumn.setReorderable(false);


        TreeTableColumn<Producto, String> priceColumn = getProductoStringTreeTableColumn();

        TreeTableColumn<Producto, Integer> quantityColumn = new TreeTableColumn<>("CANTIDAD");
        quantityColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(productCounts.get(cellData.getValue().getValue().getId())).asObject());
        quantityColumn.setPrefWidth(163);
        quantityColumn.setResizable(false);
        quantityColumn.setReorderable(false);
        quantityColumn.setStyle("-fx-alignment: CENTER;");

        tableView.getColumns().addAll(codeColumn, nameColumn, priceColumn, quantityColumn);
        ScrollPane scrollPane = (ScrollPane) tableView.lookup(".scroll-pane");
        if (scrollPane != null) {
            scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        }

        TreeItem<Producto> root = new TreeItem<>(new Producto("code", "Root", "Item"));
        tableView.setRoot(root);
        tableView.setShowRoot(false);

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


        tableView.setRowFactory(tv -> {
            TreeTableRow<Producto> row = new TreeTableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 1 && !row.isEmpty()) {
                    Producto rowData = row.getItem();
                    System.out.println(rowData.getName());
                    System.out.println(rowData.getId());

                    if (productCounts.containsKey(rowData.getId())) {
                        System.out.println(productCounts.get(rowData.getName()));
                        openDeleteDialog(productCounts.get(rowData.getId()), rowData);
                    }
                }
            });
            return row;
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

        precio.textProperty().addListener((observable, oldValue, newValue) ->{
            if (!newValue.isEmpty()) {

                String formattedValue = jTextField1KeyTyped(newValue);

                if (!formattedValue.equals(newValue)) {

                    precio.setText(formattedValue);


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

        if (!productCounts.isEmpty()) {
            buttonClear1.setDisable(false);
        }
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

            switch (t1) {
                case "Efectivo":
                    textFieldTotalPaidAmount.setVisible(true);
                    textFieldChange.setVisible(true);
                    textFieldTotalPaidAmount.requestFocus();
                    Tpago.setVisible(true);
                    TCambio.setVisible(true);
                    break;
                case "Nequi":

                    buttonSave.setDisable(false);
                    totalPagado = totalVenta;
                    break;
                default:
                    textFieldTotalPaidAmount.setVisible(false);
                    textFieldChange.setVisible(false);
                    Tpago.setVisible(false);
                    TCambio.setVisible(false);
                    break;
            }


            PagoOption = t1;

        });


        TableColumn<Producto, String> nameColumnS = new TableColumn<>("Code");
        nameColumnS.setPrefWidth(142);
        nameColumnS.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getId()));

        TableColumn<Producto, String> priceColumnS = new TableColumn<>("Nombre");
        priceColumnS.setPrefWidth(142);
        priceColumnS.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));

        TableColumn<Producto, String> quantityColumnS = new TableColumn<>("Precio");
        quantityColumnS.setPrefWidth(142);
        quantityColumnS.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPrice()));

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
                searchProductT(selectedProduct.getId());

            }
        });

        QueryInput.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.DOWN) {
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

        tableSearchQuery.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER && tableSearchQuery.getSelectionModel().getSelectedItem() != null) {

                Producto selectedProduct = tableSearchQuery.getSelectionModel().getSelectedItem();
                System.out.println(selectedProduct.getId());
                searchProductT(selectedProduct.getId());
            }
        });

    }

    private static TreeTableColumn<Producto, String> getProductoStringTreeTableColumn() {
        TreeTableColumn<Producto, String> priceColumn = new TreeTableColumn<>("PRECIO");
        priceColumn.setCellFactory(column -> {
            return new TreeTableCell<Producto, String>() {
                @Override
                protected void updateItem(String price, boolean empty) {
                    super.updateItem(price, empty);
                    if (empty || price == null) {
                        setText(null);
                    } else {
                        try {
                            double parsedPrice = Double.parseDouble(price);
                            setText(df.format(parsedPrice));
                        } catch (NumberFormatException e) {
                            setText(price);
                        }
                    }
                }
            };
        });
        priceColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getValue().getPrice()));
        priceColumn.setPrefWidth(163);
        priceColumn.setResizable(false);
        priceColumn.setReorderable(false);
        priceColumn.setStyle("-fx-alignment: CENTER;");
        return priceColumn;
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
                Producto p = new Producto(codigoBarras, nombre, precio);
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
                // Verificar si la entrada no está vacía
                if (quantityStr.trim().isEmpty()) {
                    System.out.println("vacia");
                }

                // Verificar si la entrada es un número entero válido
                if (!quantityStr.matches("\\d+")) {
                    System.out.println("Entrada no numérica");
                }

                int quantity = Integer.parseInt(quantityStr);
                int r = valueG - quantity;
                double minus = Double.parseDouble(producto.getPrice());

                if (quantity > 0 && quantity == valueG) {
                    Platform.runLater(() -> productCounts.remove(producto.getId()));
                    TreeItem<Producto> itemToRemove = null;
                    for (TreeItem<Producto> item : tableView.getRoot().getChildren()) {
                        if (item.getValue().equals(producto)) {
                            itemToRemove = item;
                            break;
                        }
                    }
                    if (itemToRemove != null) {
                        tableView.getRoot().getChildren().remove(itemToRemove);
                    }
                    totalVenta = 0.0;
                    Platform.runLater(() -> textFieldTotalQuantity.setText(getTotalT()));
                    Platform.runLater(()->info.setText("Producto borrado correctamente"));
                    tableView.refresh();
                } else if (quantity > 0 && quantity <= valueG) {
                    productCounts.put(producto.getId(), r);
                    totalVenta -= minus * quantity;
                    Platform.runLater(()->info.setText(quantityStr+" producto(s) borrados correctamente"));
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


    public void searchProductT(String searchText) {
        DatabaseManager.createTable();
        try (Connection connection = DriverManager.getConnection(Constans.URL1);
             PreparedStatement statement = connection.prepareStatement("SELECT nombre, precio, codigo_barras FROM productos WHERE codigo_barras = ?");
        ) {
            statement.setString(1, searchText);
            ResultSet resultSet = statement.executeQuery();

            boolean productoEncontrado = false; // Variable para verificar si se encontró el producto

            while (resultSet.next()) {
                String productName = resultSet.getString("nombre");
                String productPrice = resultSet.getString("precio");
                String code = resultSet.getString("codigo_barras");


                if (productCounts.containsKey(code)) {
                    productCounts.put(code, productCounts.get(code) + 1);
                } else {
                    tableView.getRoot().getChildren().add(new TreeItem<>(new Producto(code, productName, productPrice)));
                    productCounts.put(code, 1);
                    tableView.refresh();
                }

                totalVenta += Double.parseDouble(productPrice);
                textFieldItem.clear();
                productoEncontrado = true;
                System.out.println("Codigo obtenido " + code + " N°items " + productCounts.get(code));// Se encontró al menos un producto
            }

            if (!productoEncontrado) {

                if (productCounts.containsKey(textFieldItem.getText())) {
                    for (TreeItem<Producto> item : tableView.getRoot().getChildren()) {
                        if (item.getValue().getName().equals(textFieldItem.getText())) {
                            double priceEq = Double.parseDouble(item.getValue().getPrice());
                            System.out.println(priceEq);
                            totalVenta += priceEq;
//                            TreeItem<Producto> itemToUpdate = tableView.getRoot().getChildren().get(indice);
//                            Producto producto = itemToUpdate.getValue();
//                            producto.setPrice(precio.getText());
                            productCounts.put(textFieldItem.getText(), productCounts.get(textFieldItem.getText()) + 1);
                            Platform.runLater(() -> info.setText("Producto añadido"));
                            Platform.runLater(() -> textFieldTotalQuantity.setText(getTotalT()));
                            tableView.refresh();


                            break;
                        }

                    }

                } else {

                    Platform.runLater(() -> info.setText("El producto con el codigo ingresado no existe, ingrese un precio"));
                    String cleanTextPrecio = precio.getText().replaceAll("'","");
                    precio.setDisable(false);
                    precio.requestFocus();
                    if (!precio.getText().isEmpty()) {
                        tableView.getRoot().getChildren().add(new TreeItem<>(new Producto(textFieldItem.getText(), textFieldItem.getText(), cleanTextPrecio)));
                        productCounts.put(textFieldItem.getText(), 1);
                        totalVenta += Double.parseDouble(cleanTextPrecio);
                        Platform.runLater(() -> precio.clear());
                        precio.setDisable(true);
                        Platform.runLater(() -> info.setText("Producto añadido"));
                        textFieldItem.clear();
                        textFieldItem.requestFocus();
                        Platform.runLater(() -> textFieldTotalQuantity.setText(getTotalT()));
                    }

                }


            } else {
                Platform.runLater(() -> info.setText("Producto añadido"));
                tableView.refresh();
                Platform.runLater(() -> textFieldTotalQuantity.setText(getTotalT()));
            }


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
                Platform.runLater(() -> {
                    info.setText("Valor no númerico ingresado");
                    Funtions.ChangeMessage(info, 0, "");
                });
                e.printStackTrace();
            }
        }
        return text;
    }


    public void entregar() throws IOException {

        if (PagoOption.equals("Nequi")) {
            double result = totalPagado - totalVenta;
            actualizarProductosArea();
            generarRecibo(result);
            productCounts.clear();
            ClearSouldV();
        } else if (PagoOption.equals("Efectivo")) {
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
                .append(" Observaciones: ").append(" ").append("\n").append(" " + obser.getText().toString()).append("\n\n")
                .append(" Productos comprados ↓\n\n")
                .append(productosArea.getText()).append("\n")
                .append(" TOTAL:$ ").append(format(String.valueOf(totalVenta))).append("\n")
                .append(" RECIBIDO:$ ").append(format(String.valueOf(totalPagado))).append("\n")
                .append(" CAMBIO:$ ").append(format(String.valueOf(result))).append("\n")
                .append("-----------------------------------------\n\n")
                .append("           ¡GRACIAS POR SU COMPRA!\n");

        String contenidoRecibo = reciboBuilder.toString();
        String rutaArchivo = "/home/matt/Documents/FACTURAS/" + UUID.randomUUID();
        FileWriter writer = new FileWriter(rutaArchivo);
        writer.write(contenidoRecibo);
        writer.close();
        DatabaseManager.SaveSold(numeroFacturaFormateado, fecha, totalVenta, totalPagado, result, contenidoRecibo, info);
        Print_Ex(result);


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
        textFieldItem.requestFocus();
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

    public void cargarVistaNCliente() throws IOException {

        HelloClientes.go();
    }


    public void actualizarProductosArea() {


        Font font = new Font("Monospaced", Font.PLAIN, 12);
        productosArea.setFont(font);


        productosArea.setText("  Nombre             Precio     Cantidad\n");
        productosArea.append("Ú---------------------------------------¿\n");

        ObservableList<TreeItem<Producto>> backUpVenta = FXCollections.observableArrayList(tableView.getRoot().getChildren());
//        ObservableList<Producto> productosList = FXCollections.observableArrayList();
        int contador = backUpVenta.size();
        int contando = 0;


        for (TreeItem<Producto> item : backUpVenta) {


            // Ajusta el ancho de la columna "Nombre"
            if (item.getValue().getName().length() >= 15) {
                productosArea.append(String.format("Ý %-15s", item.getValue().getName().substring(0, 15))); // Ajusta el ancho de la columna "Nombre"
            } else {
                productosArea.append(String.format("Ý %-15s", item.getValue().getName())); // Ajusta el ancho de la columna "Nombre"

            }


            productosArea.append(String.format("| %-11s", "$ " + item.getValue().getPrice())); // Ajusta el ancho de la columna "Precio"
            productosArea.append(String.format("| %-8s", "  x " + productCounts.get(item.getValue().getName()))); // Ajusta el ancho de la columna "Cantidad"
            productosArea.append("Ý\n");
            contando++;

            if (contando == contador) {
                productosArea.append("À---------------------------------------Ù\n");
            } else {
                productosArea.append("Ý---------------------------------------Ý\n");
            }

        }


    }


    public void Print_Ex(Double result) throws IOException {

        String USB_PRINTER_PATH = "/dev/usb/lp0";

        SimpleDateFormat formatter = new SimpleDateFormat(" yyyy/MM/dd/ HH:mm:ss ");
        Date curDate = new Date(System.currentTimeMillis());
        String str = formatter.format(curDate);
        String date = str + "\n\n\n\n\n\n";

        int numeroFacturaActual = DatabaseManager.NVentas();

//

        String Nfactura = " Factura de venta: #" + String.format("%03d", numeroFacturaActual) + "\n" +
                " Fecha: " + fecha + " " + hora + "\n" +
                " Atendido por: " + NameUser + "\n" +
                " Pago: " + PagoOption + "\n" +
                " Observaciones: " + " " + "\n" + " " + obser.getText().toString() + "\n\n" +
                " Productos comprados: " + "\n\n" +
                productosArea.getText() + "\n" +
                " TOTAL:$ " + format(String.valueOf(totalVenta)) + "\n" +
                " RECIBIDO:$ " + format(String.valueOf(totalPagado)) + "\n" +
                " CAMBIO:$ " + format(String.valueOf(result)) + "\n" +
                "-----------------------------------------\n";

        String Nfactura2 = " Factura: #" + String.format("%03d", numeroFacturaActual) + "\n" +
                " Fecha:" + fecha + " " + hora + "\n" +
                " Atendido por: " + NameUser + "\n\n" +

                " Cliente: Josefina \n" +
                " Observaciones: " + " " + "\n" + " " + "* Ninguna" + "\n\n" +
                " Productos comprados  " + "\n\n" +
                productosArea.getText() + "\n";

        try (FileOutputStream out = new FileOutputStream(USB_PRINTER_PATH)) {


            sendData(out, IniciarImpresora());
            sendData(out, SetCodePageOEM850());
            //byte[] qrcode = PrinterCommand.getBarCommand("Zijiang Electronic Thermal Receipt Printer!", 1, 3, 8);
            Command.ESC_Align[2] = 0x01;
            sendData(out, Command.ESC_Align);
            printImage(ImageIO.read(new File("/home/matt/Downloads/logoa.jpg")), out, false);
            sendData(out, setBold(true));
            sendData(out, ("VITAL CLINICA VETERINARIA\n" +
                    "&\n" +
                    "PET SHOP\n").getBytes());
            sendData(out, "NIT: 1110482049-8\n".getBytes());
            sendData(out, "Dir: MZ2 CS23 1etp.Jordan IBAGUE-TOLIMA\n".getBytes());
            sendData(out, "TEL: 3144658553\n\n".getBytes());

            sendData(out, setBold(false));


            Command.ESC_Align[2] = 0x00;
            sendData(out, Command.ESC_Align);
            sendData(out, Nfactura2.getBytes(StandardCharsets.ISO_8859_1));

            sendData(out, Objects.requireNonNull(printMixedText("TOTAL:$ ", format(String.valueOf(totalVenta)) + "\n")));
            sendData(out, Objects.requireNonNull(printMixedText("RECIBIDO:$ ", format(String.valueOf(totalPagado)) + "\n")));
            sendData(out, Objects.requireNonNull(printMixedText("CAMBIO:$ ", format(String.valueOf(result)) + "\n")));
            sendData(out, "-----------------------------------------\n\n".getBytes());
            Command.ESC_Align[2] = 0x01;
            sendData(out, Command.ESC_Align);


            byte[] code = PrinterCommand.getCodeBarCommand(String.format("%03d", numeroFacturaActual), 69, 3, 168, 1, 2);

            if (code != null) {
                sendData(out, code);
            } else {
                System.err.println("Error creando el comando de código de barras.");
            }

            sendData(out, "\nGRACIAS POR SU COMPRA!\n".getBytes());

            ArrayList<String> Banner = new ArrayList<>();
            Banner.add("/home/matt/Downloads/cat1.png");
            Banner.add("/home/matt/Downloads/cats2.jpg");
            Banner.add("/home/matt/Downloads/dogs2.jpg");
            printImage(ImageIO.read(new File(RandomImageBannerDown(Banner))), out, true);


        }
    }


}
