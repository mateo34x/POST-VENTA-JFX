package com.example.tars01;

import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.logging.Handler;

public class LoginController {
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label messageLabel;
    @FXML
    private Button R;



    @FXML
    public void login() {
        String username = usernameField.getText();
        String password = passwordField.getText();


        if (!username.isEmpty() && !password.isEmpty()) {
            messageLabel.setText("Login successful");
        } else {
            messageLabel.setText("Username and password are required");

        }

       Funtions.HideMessage(messageLabel,0);

    }

    @FXML
    public void Registro() throws IOException {

        Stage stage = (Stage) R.getScene().getWindow();
        stage.close();
        messageLabel.setText("Yendo al registro");
        HelloRegistro.go();



    }


}
