package com.example.tars01;

import com.example.tars01.Database.Constans;
import com.example.tars01.Database.DatabaseManager;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class LoginController {
    @FXML
    private TextField usernameField;


    @FXML
    private PasswordField passwordField;

    @FXML
    private Label messageLabel, title,R;
    @FXML
    private MFXButton I;




    @FXML
    private void initialize() {

        usernameField.textProperty().addListener((observable, oldValue, newValue) -> {
            Check();
        });
        passwordField.textProperty().addListener((observable, oldValue, newValue) -> {
            Check();
        });


        Funtions.ChangeMessage(title, 0, "TARS 01");

    }


    @FXML
    public void login() {
        String username = usernameField.getText();
        String password = passwordField.getText();


        if (!username.isEmpty() && !password.isEmpty()) {

            VerificarUser(username, password);

        }



    }




    private void Check() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        Platform.runLater(() -> I.setDisable(username.isEmpty() || password.isEmpty()));
    }

    private void VerificarUser(String user, String pass) {
        DatabaseManager.createTableUser();
        String sqlSelect = "SELECT user, pass, name, per FROM users WHERE User = ?";

        try (Connection connection = DriverManager.getConnection(Constans.URL2);
             PreparedStatement selectStatement = connection.prepareStatement(sqlSelect)) {

            selectStatement.setString(1, user);
            ResultSet resultSet = selectStatement.executeQuery();

            if (resultSet.next()) {
                String storedUser = resultSet.getString("user");
                String storedPass = resultSet.getString("pass");
                String storedName = resultSet.getString("name");
                String storedPermission = resultSet.getString("per");

                // Verificar si las contraseñas coinciden
                if (pass.equals(storedPass)) {

                    Stage stage = (Stage) I.getScene().getWindow();
                    stage.close();
                    HelloMain h = new HelloMain();
                    h.go(storedName,storedPermission);
                } else {
                    messageLabel.setText("Contraseña incorrecta");
                    Funtions.ChangeMessage(messageLabel, 0,"");
                }
            } else {
                messageLabel.setText("Usuario incorrecto");
                Funtions.ChangeMessage(messageLabel, 0,"");
            }
        } catch (SQLException e) {
            messageLabel.setText("Error al verificar el usuario");
            Funtions.ChangeMessage(messageLabel, 0,"");
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
