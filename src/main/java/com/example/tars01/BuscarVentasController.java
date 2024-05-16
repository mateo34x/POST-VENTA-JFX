package com.example.tars01;

import com.example.tars01.Database.DatabaseManager;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import static com.example.tars01.Funtions.cargarVista;

public class BuscarVentasController {




    @FXML
    TextField codeBuscar;
    @FXML
    TextArea ReciboView,infoBuscar;
    @FXML
    TableView tableViewShowBuscar;
    @FXML
    DatePicker FechaStart, FechaEnd;
    @FXML
    Button btnBuscarV,MostrarV;














    @FXML
    private void initialize() {



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



}
