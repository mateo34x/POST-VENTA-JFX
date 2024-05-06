package com.example.tars01;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class RegistreController {

    @FXML
    private TextField nombreVerdaderoField;

    @FXML
    private TextField nombreUsuarioField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label messageLabel;

    @FXML
    Button Volver;

    @FXML
    public void guardar() {
        String nombreVerdadero = nombreVerdaderoField.getText();
        String nombreUsuario = nombreUsuarioField.getText();
        String password = passwordField.getText();

        // Aquí puedes realizar la tarea de guardar el nombre verdadero, nombre de usuario y contraseña
        // Por simplicidad, este ejemplo simplemente muestra un mensaje de éxito
        if (!nombreVerdadero.isEmpty() && !nombreUsuario.isEmpty() && !password.isEmpty()) {
            messageLabel.setText("Registro exitoso");
        } else {
            messageLabel.setText("Todos los campos son requeridos");
        }

        Funtions.HideMessage(messageLabel,0);
    }

    @FXML
    public void volver() throws IOException {

        Stage stage = (Stage) Volver.getScene().getWindow();
        stage.close();
        HelloApplication.go();
    }
}
