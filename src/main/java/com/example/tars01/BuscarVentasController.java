package com.example.tars01;

import com.example.tars01.Database.Constans;
import com.example.tars01.Database.DatabaseManager;
import com.example.tars01.Database.Ventas;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.example.tars01.Funtions.cargarVista;

public class BuscarVentasController {




    @FXML
    TextField codeBuscar;
    @FXML
    TextArea ReciboView,infoBuscar;

    @FXML
    public TableView<Ventas> tableViewShowBuscar = new TableView<>();

    @FXML
    DatePicker FechaStart, FechaEnd;
    @FXML
    Button BuscarFilter,MostrarV;
    Double totalenCaja = 0.0;














    @FXML
    private void initialize() {

        FechaStart.setValue(LocalDate.now());
        FechaEnd.setValue(LocalDate.now());


        TableColumn<Ventas,String> idColum = new TableColumn<>("Codigo");
        idColum.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getId()));


        TableColumn<Ventas, String> nameColumn = new TableColumn<>("Fecha");
        nameColumn.setPrefWidth(200);
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().fechaProperty());

        TableColumn<Ventas, String> priceColumn = new TableColumn<>("Total vendido");
        priceColumn.setCellValueFactory(cellData -> cellData.getValue().totalVentaProperty());

        TableColumn<Ventas, String> pagadoColumn = new TableColumn<>("Pagado");
        pagadoColumn.setCellValueFactory(cellData -> cellData.getValue().pagadoProperty());

        TableColumn<Ventas, String> cambioColmn = new TableColumn<>("Cambio");
        cambioColmn.setCellValueFactory(cellData -> cellData.getValue().cambioProperty());

        TableColumn<Ventas, String> DetallesColmn = new TableColumn<>("Detalles");
        DetallesColmn.setVisible(false);
        DetallesColmn.setCellValueFactory(cellData -> cellData.getValue().cambioProperty());


        tableViewShowBuscar.getColumns().addAll(idColum,nameColumn, priceColumn,pagadoColumn,cambioColmn);



        codeBuscar.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (keyEvent.getCode() == KeyCode.ENTER) {
                    String searchText = codeBuscar.getText();
                    if (!searchText.isEmpty()) {
                        DatabaseManager.searchFactura(ReciboView,infoBuscar,searchText);
                    }else{
                        Platform.runLater(()->infoBuscar.setText("Ingrese el ID de la factura para poder buscarla"));
                    }
                }
            }
        });

        tableViewShowBuscar.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getClickCount() == 1 && tableViewShowBuscar.getSelectionModel().getSelectedItem() != null) {
                    Ventas selectedProduct = tableViewShowBuscar.getSelectionModel().getSelectedItem();
                    Platform.runLater(()->ReciboView.setText(selectedProduct.getDetalles()));
                }
            }
        });
    }









//    private void updateSaveButtonState() {
//        String codeValue = codeBuscar.getText();
//
//
//        boolean isEmpty = codeValue.isEmpty();
//
//
//        Platform.runLater(() -> btnBuscarV.setDisable(isEmpty));
//    }



    public void cargarVistaEditar() {
        cargarVista("Editar-View.fxml",codeBuscar);
    }
    public void cargarVistaCrear() {
        cargarVista("CreateProducto-View.fxml",codeBuscar);
    }

    public void cargarVistaVenta() {
        cargarVista("Main-View.fxml",codeBuscar);
    }





    public  void getFecha() {


        if (!tableViewShowBuscar.getItems().isEmpty()){
            tableViewShowBuscar.getItems().clear();
            tableViewShowBuscar.refresh();
            totalenCaja = 0.0;
        }

        LocalDate start = FechaStart.getValue();
        LocalDate end = FechaEnd.getValue();
        String formattedDateStart = start.format(DateTimeFormatter.ofPattern("yy/MM/dd"));
        String formattedDateEnd = end.format(DateTimeFormatter.ofPattern("yy/MM/dd"));
        try (Connection connection = DriverManager.getConnection(Constans.URL3);
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Ventas WHERE fecha BETWEEN ? AND ?")) {

            preparedStatement.setString(1, formattedDateStart);
            preparedStatement.setString(2, formattedDateEnd);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String idVenta = resultSet.getString("idVenta");
                String fecha = resultSet.getString("fecha");
                String totalVenta = resultSet.getString("totalVenta");
                String cantidadPagada = resultSet.getString("cantidadPagada");
                String cambio = resultSet.getString("cambio");
                String detallesVenta = resultSet.getString("detallesVenta");


                tableViewShowBuscar.getItems().add(new Ventas(idVenta,fecha,totalVenta,cantidadPagada,cambio,detallesVenta));
                tableViewShowBuscar.refresh();

                totalenCaja += Double.parseDouble(totalVenta);




            }
            Platform.runLater(()->infoBuscar.setText("El total vendido entre el día :"+formattedDateStart+" y el día: "+formattedDateEnd+ " fue de:\n "+totalenCaja));

        } catch (SQLException e) {

            System.out.println("Error al consultar las ventas: " + e.getMessage());
        }
    }


    public  void getAllVentas() {

        if (!tableViewShowBuscar.getItems().isEmpty()){
            tableViewShowBuscar.getItems().clear();
            tableViewShowBuscar.refresh();
            totalenCaja = 0.0;
        }


        try (Connection connection = DriverManager.getConnection(Constans.URL3);
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Ventas")) {

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String idVenta = resultSet.getString("idVenta");
                String fecha = resultSet.getString("fecha");
                String totalVenta = resultSet.getString("totalVenta");
                String cantidadPagada = resultSet.getString("cantidadPagada");
                String cambio = resultSet.getString("cambio");
                String detallesVenta = resultSet.getString("detallesVenta");


                tableViewShowBuscar.getItems().add(new Ventas(idVenta,fecha,totalVenta,cantidadPagada,cambio,detallesVenta));
                tableViewShowBuscar.refresh();


            }

        } catch (SQLException e) {

            System.out.println("Error al consultar las ventas: " + e.getMessage());
        }
    }




}
