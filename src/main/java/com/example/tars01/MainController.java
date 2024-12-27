package com.example.tars01;

import com.example.tars01.Database.Constans;
import com.example.tars01.Database.DatabaseManager;
import com.example.tars01.Database.Producto;
import com.example.tars01.Printer.Command;
import com.example.tars01.Servidor.ServerManager;
import com.example.tars01.Utils.FileEditor;
import com.jfoenix.controls.JFXTreeTableView;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.imageio.ImageIO;
import javax.print.*;
import javax.print.attribute.HashPrintRequestAttributeSet;
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

import static com.example.tars01.Printer.PrinterCommandsAct.*;
import static com.example.tars01.Utils.FileEditor.leerLineaEspecifica;


public class MainController {


    boolean onTurno = false;


    private ServerSocket serverSocket;
    static double totalVenta = 0.0;
    static double totalPagado = 0.0;
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
        textFieldItem.setFocusTraversable(true);

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


        textFieldItem.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty() && !newValue.matches("\\d+")) {
                busquedaB.setVisible(true);
                QueryInput.setText(newValue);
                QueryInput.requestFocus();
                Platform.runLater(() -> {
                    QueryInput.positionCaret(newValue.length());
                });
                updateProductList(newValue, 1);

            } else {
                tableSearchQuery.getItems().clear();
                busquedaB.setVisible(false);
                textFieldItem.requestFocus();


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

        textFieldTotalPaidAmount.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (keyEvent.getCode() == KeyCode.ENTER) {
                    if (textFieldTotalPaidAmount.getText().isEmpty()) {
                        textFieldTotalPaidAmount.setText(textFieldTotalQuantity.getText());
                    } else {
                        try {
                            entregar();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        } catch (PrintException e) {
                            throw new RuntimeException(e);
                        }
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
                if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 1) {
                    if (!row.isEmpty()) {

                        Producto rowData = row.getItem();
                        System.out.println(rowData.getName());
                        System.out.println(rowData.getId());

                        if (productCounts.containsKey(rowData.getId())) {
                            openDeleteDialog(productCounts.get(rowData.getId()), rowData);
                        }
                    }
                } else if (event.getButton() == MouseButton.SECONDARY && event.getClickCount() == 1) {

                    if (!row.isEmpty()) {
                        Producto rowData = row.getItem();
                        double priceEq = Double.parseDouble(rowData.getPrice());
                        System.out.println(priceEq);
                        totalVenta += priceEq;
                        productCounts.put(rowData.getId(), productCounts.get(rowData.getId()) + 1);
                        Platform.runLater(() -> info.setText("Producto añadido"));
                        Platform.runLater(() -> textFieldTotalQuantity.setText(getTotalT()));
                        tableView.refresh();
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

        precio.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {

                String formattedValue = jTextField1KeyTyped(newValue);

                if (!formattedValue.equals(newValue)) {

                    precio.setText(formattedValue);


                }
            }
        });


        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yy HH:mm:ss");
        DateTimeFormatter formatterOther = DateTimeFormatter.ofPattern("yy/MM/dd");
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
                updateProductList(newValue, 0);
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


        buttonSave.addEventFilter(MouseEvent.MOUSE_CLICKED,event->{
            if (buttonSave.isDisable()){
                Platform.runLater(() -> info.setText("Para cerrar la venta debe seleccionar un método de pago"));

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

    private void updateProductList(String searchText, int origin) {
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
                if (origin == 1) {
                    tableSearchQuery.getItems().clear();
                    busquedaB.setVisible(false);
                } else {
                    tableSearchQuery.getItems().clear();
                    return;
                }

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
                    totalVenta -= minus * quantity;
                    Platform.runLater(() -> textFieldTotalQuantity.setText(getTotalT()));
                    Platform.runLater(() -> info.setText("Producto borrado correctamente"));
                    textFieldItem.requestFocus();
                    tableView.refresh();
                } else if (quantity > 0 && quantity <= valueG) {
                    productCounts.put(producto.getId(), r);
                    totalVenta -= minus * quantity;
                    Platform.runLater(() -> info.setText(quantityStr + " producto(s) borrados correctamente"));
                    Platform.runLater(() -> textFieldTotalQuantity.setText(getTotalT()));
                    tableView.refresh();
                    textFieldItem.requestFocus();
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
                            textFieldItem.requestFocus();
                            Funtions.ChangeMessage(info,0,"Al finalizar el pedido, seleccione un tipo de pago para cerrar la venta");
                            Platform.runLater(() -> textFieldTotalQuantity.setText(getTotalT()));
                            tableView.refresh();


                            break;
                        }

                    }

                } else {

                    Platform.runLater(() -> info.setText("El producto con el código ingresado no existe, ingrese un precio"));
                    String cleanTextPrecio = precio.getText().replaceAll("'", "");
                    precio.setDisable(false);
                    precio.requestFocus();
                    if (!precio.getText().isEmpty()) {
                        tableView.getRoot().getChildren().add(new TreeItem<>(new Producto(textFieldItem.getText(), textFieldItem.getText(), cleanTextPrecio)));
                        productCounts.put(textFieldItem.getText(), 1);
                        totalVenta += Double.parseDouble(cleanTextPrecio);
                        Platform.runLater(() -> precio.clear());
                        precio.setDisable(true);
                        Funtions.ChangeMessage(info,0,"Al finalizar el pedido, seleccione un tipo de pago para cerrar la venta");
                        textFieldItem.clear();
                        textFieldItem.requestFocus();
                        Platform.runLater(() -> textFieldTotalQuantity.setText(getTotalT()));
                    }

                }


            } else {
                Funtions.ChangeMessage(info,0,"Al finalizar el pedido, seleccione un tipo de pago para cerrar la venta");
                precio.setDisable(true);
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
                precio.clear();
                Platform.runLater(() -> {
                    info.setText("Valor no númerico ingresado");
                    Funtions.ChangeMessage(info, 0, "");
                });
                e.printStackTrace();
            }
        }
        return text;
    }


    public void entregar() throws IOException, PrintException {

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
                    textFieldTotalPaidAmount.clear();
                    textFieldTotalPaidAmount.requestFocus();
                }

            } catch (NumberFormatException e) {
                // Manejar la excepción si el texto no es un número válido
                e.printStackTrace();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }


    }

    private void generarRecibo(double result) throws IOException, PrintException {
        int numeroFacturaActual = DatabaseManager.NVentas();
        int numeroFacturaSiguiente = numeroFacturaActual + 1;
        String numeroFacturaFormateado = String.format("%03d", numeroFacturaSiguiente);

        if (PagoOption == null) {
            PagoOption = "Efectivo";
        }


        StringBuilder reciboBuilder = new StringBuilder();
        reciboBuilder
                .append(" P.O.S:#").append(numeroFacturaFormateado).append("\n")
                .append(" Fecha:").append(fecha).append(" ").append(hora).append("\n")
                .append(" Atendido por:").append(NameUser).append("\n\n")
                .append(" Cliente:").append("Consumidor Final").append("\n")
                .append(" Observaciones: ").append("Copia del recibo ").append("\n").append(" " + obser.getText().toString()).append("\n")
                .append(" Productos comprados ↓\n\n")
                .append(productosArea.getText()).append("\n")
                .append(" TOTAL:$ ").append(format(String.valueOf(totalVenta))).append("\n")
                .append(" RECIBIDO:$ ").append(format(String.valueOf(totalPagado))).append("\n")
                .append(" CAMBIO:$ ").append(format(String.valueOf(result))).append("\n");

        String contenidoRecibo = reciboBuilder.toString();
//        String rutaArchivo = "/home/matt/Documents/FACTURAS/" + UUID.randomUUID();
//        FileWriter writer = new FileWriter(rutaArchivo);
//        writer.write(contenidoRecibo);
//        writer.close();
        DatabaseManager.SaveSold(numeroFacturaFormateado, fecha, hora, totalVenta, totalPagado, result, contenidoRecibo, obser.getText(), NameUser, info);
        if (!Boolean.parseBoolean(leerLineaEspecifica("PrincipalData.txt", 8).replace("\n", ""))) {
            mostrarDialogoConCheck("¿Desea imprimir el recibo de venta?", "Información");
        } else {
            Print_Ex();
        }

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

    public void cargarVistaConfig() throws IOException {

        HelloDatos.go();
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


            productosArea.append(String.format("| %-11s", "$ " + format(item.getValue().getPrice()))); // Ajusta el ancho de la columna "Precio"
            productosArea.append(String.format("| %-8s", "  x " + productCounts.get(item.getValue().getId()))); // Ajusta el ancho de la columna "Cantidad"
            productosArea.append("Ý\n");
            contando++;

            if (contando == contador) {
                productosArea.append("À---------------------------------------Ù\n");
            } else {
                productosArea.append("Ý---------------------------------------Ý\n");
            }

        }


    }


    public void Print_Ex() throws IOException, PrintException {

        String USB_PRINTER_PATH = "/dev/usb/lp0";
        OutputStream out = null;
        String os = System.getProperty("os.name").toLowerCase();
        String defaultPrinterFile = "PrincipalData.txt";
        PrintService selectedService = null;

        if (os.contains("win")) {

            File file = new File(defaultPrinterFile);
            if (file.exists()) {

                String printerName = leerLineaEspecifica("PrincipalData.txt", 6);
                PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
                for (PrintService ps : printServices) {
                    System.out.println(ps.getName());
                    if (ps.getName().equals(printerName.replace("\n", ""))) {
                        selectedService = ps;
                        break;
                    }
                }

            }

            if (selectedService == null || Boolean.parseBoolean(leerLineaEspecifica("PrincipalData.txt", 7).replace("\n", ""))) {
                PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
                selectedService = (PrintService) JOptionPane.showInputDialog(null, "Seleccione una impresora",
                        "Impresoras disponibles", JOptionPane.QUESTION_MESSAGE, null, printServices, printServices[0]);


                if (selectedService != null) {
                    int option;
                    option = JOptionPane.showConfirmDialog(null,
                            "¿Desea establecer esta impresora como predeterminada?", "Confirmación",
                            JOptionPane.YES_NO_OPTION);


                    if (option == JOptionPane.YES_OPTION) {
                        FileEditor.insertarValorEnLinea(defaultPrinterFile, 6, selectedService.getName());
                    }


                    out = new ByteArrayOutputStream();
                } else {
                    throw new IOException("No se seleccionó ninguna impresora.");

                }
            } else {
                out = new ByteArrayOutputStream();
                JOptionPane.showMessageDialog(null,"No hay ninguna impresora disponible, no se imprimirá el recibo de la compra","Alerta",JOptionPane.ERROR_MESSAGE);

            }
        } else if (os.contains("nix") || os.contains("nux")) {
            // En Linux, abrir una ventana para ingresar manualmente la ruta de la impresora
            FileDialog dialog = new FileDialog((Frame) null, "Seleccionar archivo de impresora", FileDialog.LOAD);
            dialog.setVisible(true);
            String selectedFile = dialog.getFile();
            if (selectedFile != null) {
                USB_PRINTER_PATH = dialog.getDirectory() + selectedFile;
                out = new FileOutputStream(USB_PRINTER_PATH);  // Enviar datos a la ruta en Linux
            } else {
                JOptionPane.showMessageDialog(null,"No hay ninguna impresora disponible, no se imprimirá el recibo de la compra","Alerta",JOptionPane.ERROR_MESSAGE);
                throw new IOException("No se seleccionó ninguna ruta.");

            }
        }

        // Aquí se sigue tu código original para preparar los datos a imprimir
        SimpleDateFormat formatter = new SimpleDateFormat(" yyyy/MM/dd/ HH:mm:ss ");
        Date curDate = new Date(System.currentTimeMillis());
        String str = formatter.format(curDate);
        String date = str + "\n\n\n\n\n\n";

        int numeroFacturaActual = DatabaseManager.NVentas();

        String Nfactura2 = " P.O.S:#" + String.format("%03d", numeroFacturaActual) + "\n" +
                " Fecha:" + fecha + " " + hora + "\n" +
                " Atendido por:" + NameUser + "\n\n" +
                " Cliente:Consumidor Final \n" +
                " Observaciones: Recibo original " + " " + "\n" + " " + obser.getText() + "\n\n" +
                " Productos comprados  " + "\n\n" +
                productosArea.getText() + "\n";

        // Enviar datos a la impresora o a la ruta en Linux
        if (out != null) {
            sendData(out, IniciarImpresora());
            sendData(out, SetCodePageOEM850());
            Command.ESC_Align[2] = 0x01;
            sendData(out, Command.ESC_Align);

            printImage(ImageIO.read(new File(FileEditor.leerLineaEspecifica("PrincipalData.txt",9).replace("\n",""))), out, false);
            sendData(out, setBold(true));
            sendData(out, (leerLineaEspecifica("PrincipalData.txt", 1)).getBytes());
            sendData(out, (leerLineaEspecifica("PrincipalData.txt", 2)).getBytes());
            sendData(out, (leerLineaEspecifica("PrincipalData.txt", 3)).getBytes());
            sendData(out, (leerLineaEspecifica("PrincipalData.txt", 4)).getBytes());

            sendData(out, setBold(false));

            Command.ESC_Align[2] = 0x00;
            sendData(out, Command.ESC_Align);
            sendData(out, Nfactura2.getBytes(StandardCharsets.ISO_8859_1));

            sendData(out, Objects.requireNonNull(printMixedText("TOTAL: $", format(String.valueOf(totalVenta)) + "\n")));
            sendData(out, Objects.requireNonNull(printMixedText("RECIBIDO: $", format(String.valueOf(totalPagado)) + "\n")));
            sendData(out, Objects.requireNonNull(printMixedText("CAMBIO: $", format(String.valueOf(totalPagado - totalVenta)) + "\n")));
            sendData(out, "-----------------------------------------\n\n".getBytes());
            Command.ESC_Align[2] = 0x01;
            sendData(out, Command.ESC_Align);

            byte[] code = getCodeBarCommand(String.format("%03d", numeroFacturaActual), 69, 3, 168, 1, 2);

            if (code != null) {
                sendData(out, code);
            } else {
                System.err.println("Error creando el comando de código de barras.");
            }

            sendData(out, "\nGRACIAS POR SU COMPRA!\n\n".getBytes());
            sendData(out, CTL_LF);
            sendData(out, CTL_LF);
            sendData(out, CTL_LF);
            sendData(out, CTL_LF);
            sendData(out, Command.GS_i);//Comando para cortar el papel por completo


//            ArrayList<String> Banner = new ArrayList<>();
//            Banner.add("/home/matt/Downloads/cat1.png");
//            Banner.add("/home/matt/Downloads/cats2.jpg");
//            Banner.add("/home/matt/Downloads/dogs2.jpg");
//            printImage(ImageIO.read(new File(RandomImageBannerDown(Banner))), out, true);

            out.close();  // Cerrar el flujo de salida
        }

        // Si es Windows, enviar el contenido del ByteArrayOutputStream a la impresora seleccionada
        if (os.contains("win") && out instanceof ByteArrayOutputStream) {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(((ByteArrayOutputStream) out).toByteArray());
            Doc doc = new SimpleDoc(inputStream, DocFlavor.INPUT_STREAM.AUTOSENSE, null);
            selectedService = PrintServiceLookup.lookupPrintServices(null, null)[0];
            DocPrintJob printJob = selectedService.createPrintJob();
            printJob.print(doc, new HashPrintRequestAttributeSet());
        }


    }


    public void mostrarDialogoConCheck(String mensaje, String titulo) throws PrintException, IOException {
        JCheckBox checkBoxNoMostrar = new JCheckBox("¿No volver a preguntar?");
        Object[] components = {mensaje, checkBoxNoMostrar};

        int option = JOptionPane.showConfirmDialog(null, components, titulo, JOptionPane.YES_NO_CANCEL_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            Print_Ex();
        }
        FileEditor.insertarValorEnLinea("PrincipalData.txt", 8, String.valueOf(checkBoxNoMostrar.isSelected()));

    }


    public void getLastBill() {
        String id;
        id = String.format("%03d", DatabaseManager.NVentas());

        try (Connection connection = DriverManager.getConnection(Constans.URL3);
             PreparedStatement statement = connection.prepareStatement("SELECT detallesVenta, obser FROM ventas WHERE idVenta = ?");
        ) {
            statement.setString(1, id.replace(" ", ""));
            ResultSet resultSet = statement.executeQuery();

            boolean productoEncontrado = false; // Variable para verificar si se encontró el producto

            while (resultSet.next()) {

                String detalle = resultSet.getString("detallesVenta");
                String observacion = resultSet.getString("obser");

                Platform.runLater(() -> info.setText("Factura encontrada"));
                Print_Ex_Copy(Integer.parseInt(id), detalle, observacion);


                productoEncontrado = true; // Se encontró al menos un producto
            }

            if (!productoEncontrado) {

                Platform.runLater(() -> info.setText("No hemos encontrado la factura con ID: " + id));

            }


        } catch (SQLException e) {
            e.printStackTrace();
        } catch (PrintException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void Print_Ex_Copy(int id, String detalle, String obser) throws IOException, PrintException {

        String USB_PRINTER_PATH = "/dev/usb/lp0";
        OutputStream out = null;
        String os = System.getProperty("os.name").toLowerCase();
        String defaultPrinterFile = "PrincipalData.txt";
        PrintService selectedService = null;

        if (os.contains("win")) {

            File file = new File(defaultPrinterFile);
            if (file.exists()) {

                String printerName = leerLineaEspecifica("PrincipalData.txt", 6);
                PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
                for (PrintService ps : printServices) {
                    System.out.println(ps.getName());
                    if (ps.getName().equals(printerName.replace("\n", ""))) {
                        selectedService = ps;
                        break;
                    }
                }

            }

            if (selectedService == null || Boolean.parseBoolean(leerLineaEspecifica("PrincipalData.txt", 7).replace("\n", ""))) {
                PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
                selectedService = (PrintService) JOptionPane.showInputDialog(null, "Seleccione una impresora",
                        "Impresoras disponibles", JOptionPane.QUESTION_MESSAGE, null, printServices, printServices[0]);


                if (selectedService != null) {
                    int option;
                    option = JOptionPane.showConfirmDialog(null,
                            "¿Desea establecer esta impresora como predeterminada?", "Confirmación",
                            JOptionPane.YES_NO_OPTION);


                    if (option == JOptionPane.YES_OPTION) {
                        FileEditor.insertarValorEnLinea(defaultPrinterFile, 6, selectedService.getName());
                    }


                    out = new ByteArrayOutputStream();
                } else {
                    throw new IOException("No se seleccionó ninguna impresora.");

                }
            } else {
                out = new ByteArrayOutputStream();

            }
        } else if (os.contains("nix") || os.contains("nux")) {
            // En Linux, abrir una ventana para ingresar manualmente la ruta de la impresora
            FileDialog dialog = new FileDialog((Frame) null, "Seleccionar archivo de impresora", FileDialog.LOAD);
            dialog.setVisible(true);
            String selectedFile = dialog.getFile();
            if (selectedFile != null) {
                USB_PRINTER_PATH = dialog.getDirectory() + selectedFile;
                out = new FileOutputStream(USB_PRINTER_PATH);  // Enviar datos a la ruta en Linux
            } else {
                throw new IOException("No se seleccionó ninguna ruta.");
            }
        }

        String Nfactura2 = detalle + "\n";

        // Enviar datos a la impresora o a la ruta en Linux
        if (out != null) {
            sendData(out, IniciarImpresora());
            sendData(out, SetCodePageOEM850());
            Command.ESC_Align[2] = 0x01;
            sendData(out, Command.ESC_Align);
            printImage(ImageIO.read(new File("C:\\Users\\danin\\Downloads\\logoa.jpg")), out, false);
            sendData(out, setBold(true));
            sendData(out, (leerLineaEspecifica("PrincipalData.txt", 1)).getBytes());
            sendData(out, (leerLineaEspecifica("PrincipalData.txt", 2)).getBytes());
            sendData(out, (leerLineaEspecifica("PrincipalData.txt", 3)).getBytes());
            sendData(out, (leerLineaEspecifica("PrincipalData.txt", 4)).getBytes());

            sendData(out, setBold(false));

            Command.ESC_Align[2] = 0x00;
            sendData(out, Command.ESC_Align);
            sendData(out, Nfactura2.getBytes(StandardCharsets.ISO_8859_1));
            Command.ESC_Align[2] = 0x01;
            sendData(out, Command.ESC_Align);

            byte[] code = getCodeBarCommand(String.format("%03d", id), 69, 3, 168, 1, 2);

            if (code != null) {
                sendData(out, code);
            } else {
                System.err.println("Error creando el comando de código de barras.");
            }

            sendData(out, "\nESTE RECIBO ES UNA FIEL COPIA DE SU ORIGINAL!\n\n".getBytes());
            sendData(out, CTL_LF);
            sendData(out, CTL_LF);
            sendData(out, CTL_LF);
            sendData(out, CTL_LF);
            sendData(out, Command.GS_i);//Comando para cortar el papel por completo


//            ArrayList<String> Banner = new ArrayList<>();
//            Banner.add("/home/matt/Downloads/cat1.png");
//            Banner.add("/home/matt/Downloads/cats2.jpg");
//            Banner.add("/home/matt/Downloads/dogs2.jpg");
//            printImage(ImageIO.read(new File(RandomImageBannerDown(Banner))), out, true);

            out.close();  // Cerrar el flujo de salida
        }

        // Si es Windows, enviar el contenido del ByteArrayOutputStream a la impresora seleccionada
        if (os.contains("win") && out instanceof ByteArrayOutputStream) {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(((ByteArrayOutputStream) out).toByteArray());
            Doc doc = new SimpleDoc(inputStream, DocFlavor.INPUT_STREAM.AUTOSENSE, null);
            selectedService = PrintServiceLookup.lookupPrintServices(null, null)[0];
            DocPrintJob printJob = selectedService.createPrintJob();
            printJob.print(doc, new HashPrintRequestAttributeSet());
        }


    }


}
