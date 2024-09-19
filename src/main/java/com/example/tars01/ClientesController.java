package com.example.tars01;

import com.example.tars01.Database.Cliente;
import com.example.tars01.Database.DatabaseManager;
import com.example.tars01.Utils.EmailValidator;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class ClientesController {
    String nameValue;
    String LastNvalue;
    String IDvalue;
    String gmailvalue;
    String Phonevalue;
    String Advalue;


    @FXML
    public Label messageClient;
    public TextField NClient, LClient, IClient, GClient, TClient, AClient;
    public MFXButton saveClient;


    @FXML
    private void initialize() {
        TextField[] clients = {NClient, LClient, IClient, GClient, TClient, AClient};

        for (TextField client : clients) {
            client.textProperty().addListener((observable, oldValue, newValue) -> updateSaveButtonState());

            if (client == IClient || client == TClient) {
                client.textProperty().addListener((observable, oldValue, newValue) -> {
                    if (!newValue.matches("\\d*")) {
                        client.setText(newValue.replaceAll("\\D", ""));
                    }
                });
            }
        }


    }


    private void updateSaveButtonState() {
        nameValue = NClient.getText();
        LastNvalue = LClient.getText();
        IDvalue = IClient.getText();
        gmailvalue = GClient.getText();
        Phonevalue = TClient.getText();
        Advalue = AClient.getText();

        boolean isEmpty = nameValue.isEmpty() || LastNvalue.isEmpty() || IDvalue.isEmpty() ||
                gmailvalue.isEmpty() || Phonevalue.isEmpty() || Advalue.isEmpty() || !EmailValidator.isValid(gmailvalue) || Phonevalue.length() > 10;


        if (isEmpty) {

            if (!EmailValidator.isValid(gmailvalue)) {
                Platform.runLater(() -> messageClient.setText("El correo digitado parece no ser válido"));
            } else if (Phonevalue.length() > 10) {
                Platform.runLater(() -> messageClient.setText("El número de teléfono solo puede tener 10 digitos"));

            } else {
                Platform.runLater(() -> messageClient.setText("Hay campos vacios, complételos"));
            }
        } else {
            Platform.runLater(() -> messageClient.setText(""));
        }

        Platform.runLater(() -> saveClient.setDisable(isEmpty));
    }



    public void SaveCliente(){
        Cliente cliente = new Cliente(nameValue,LastNvalue,IDvalue,Phonevalue,gmailvalue,Advalue);
        DatabaseManager.insertarCliente(cliente,messageClient,NClient,LClient,IClient,NClient,GClient,AClient);
    }

}
