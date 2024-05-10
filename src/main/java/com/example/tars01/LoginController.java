package com.example.tars01;

import com.example.tars01.Database.Constans;
import com.example.tars01.Database.DatabaseManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

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

            VerificarUser(username,password);

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



    private void VerificarUser(String user, String pass) {
        DatabaseManager.createTableUser();
        String sqlSelect = "SELECT user, pass FROM users WHERE user = ?";

        try (Connection connection = DriverManager.getConnection(Constans.URL2);
             PreparedStatement selectStatement = connection.prepareStatement(sqlSelect)) {

            selectStatement.setString(1, user);
            ResultSet resultSet = selectStatement.executeQuery();

            if (resultSet.next()) {
                String storedUser = resultSet.getString("user");
                String storedPass = resultSet.getString("pass");

                // Verificar si las contraseñas coinciden
                if (pass.equals(storedPass)) {

                    Stage stage = (Stage) R.getScene().getWindow();
                    stage.close();
                    HelloMain h = new HelloMain();
                    h.go();
                } else {
                    messageLabel.setText("Contraseña incorrecta");
                    Funtions.HideMessage(messageLabel, 0);
                }
            } else {
                messageLabel.setText("Usuario incorrecto");
                Funtions.HideMessage(messageLabel, 0);
            }
        } catch (SQLException e) {
            messageLabel.setText("Error al verificar el usuario");
            Funtions.HideMessage(messageLabel, 0);
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
