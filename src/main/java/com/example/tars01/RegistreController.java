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
import java.util.UUID;

public class RegistreController {

    @FXML
    private TextField nombreVerdaderoField;

    @FXML
    private TextField nombreUsuarioField;

    @FXML
    private PasswordField passwordField, MasterField;

    @FXML
    private Label messageLabel;

    @FXML
    private MFXButton saveUser;

    String permisos;

    @FXML
    private ComboBox<String> optionsPer;

    @FXML
    Button Volver;


    @FXML
    private void initialize() {
        optionsPer.getItems().addAll(
                "Administrador",
                "Trabajador",
                "Invitado"
        );
        optionsPer.getSelectionModel().selectedItemProperty().addListener((ov, t, t1) -> {


            switch (t1){
                case "Administrador":
                    permisos = "ADMIN";
                break;

                case "Trabajador":
                    permisos = "EMPLOYED";
                break;

                case "Invitado":
                    permisos = "GUEST";
                break;
            }

            Platform.runLater(() -> MasterField.setVisible(t1.equals("Administrador")));


        });


        TextField[] clients = {nombreVerdaderoField, nombreUsuarioField, passwordField, MasterField};

        for (TextField client : clients) {
            client.textProperty().addListener((observable, oldValue, newValue) -> updateSaveButtonState(permisos));

        }


    }


    @FXML
    public void guardar() {
        String nombreVerdadero = nombreVerdaderoField.getText();
        String nombreUsuario = nombreUsuarioField.getText();
        String password = passwordField.getText();
        String Master = MasterField.getText();




        if (!nombreVerdadero.isEmpty() && !nombreUsuario.isEmpty() && !password.isEmpty()) {
            registrar(nombreVerdadero, nombreUsuario, password, Master);
        } else {
            messageLabel.setText("Todos los campos son requeridos");
        }

        Funtions.HideMessage(messageLabel, 0);
    }

    @FXML
    public void volver() throws IOException {

        Stage stage = (Stage) Volver.getScene().getWindow();
        stage.close();
        HelloApplication.go();
    }


    @FXML
    public void registrar(String name, String user, String pass,String Master) {
        DatabaseManager.createTableUser();
        String sqlSelect = "SELECT COUNT(*) AS count FROM users WHERE User = ?";
        String sqlInsert = "INSERT INTO users (code, name, user, per, pass, passM) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(Constans.URL2);
             PreparedStatement selectStatement = connection.prepareStatement(sqlSelect);
             PreparedStatement insertStatement = connection.prepareStatement(sqlInsert)) {

            selectStatement.setString(1, user);
            ResultSet resultSet = selectStatement.executeQuery();
            resultSet.next();
            int count = resultSet.getInt("count");

            if (count > 0) {
                String sqlCheckPass = "SELECT pass FROM users WHERE User = ?";
                PreparedStatement checkPassStatement = connection.prepareStatement(sqlCheckPass);
                checkPassStatement.setString(1, user);

                messageLabel.setText("Usuario existente, inicie sesión");

            } else {

                UUID uuid = UUID.randomUUID();
                String randomUUIDString = uuid.toString();
                insertStatement.setString(1, randomUUIDString);
                insertStatement.setString(2, name);
                insertStatement.setString(3, user);
                insertStatement.setString(4, permisos);
                insertStatement.setString(5, pass);
                insertStatement.setString(6, Master);
                insertStatement.executeUpdate();

                messageLabel.setText("Usuario creado correctamente");
                Funtions.ChangeMessage(messageLabel, 0, "Cierre esta venta he inicie sesion");

            }
        } catch (SQLException e) {
            messageLabel.setText("Error al crear el usuario: " + e.getMessage());
            System.out.println(e.getMessage());
        }

    }

    private void updateSaveButtonState(String permisosSeleccionado) {

        if (permisosSeleccionado != null) {
            switch (permisosSeleccionado) {
                case "ADMIN":
                    if (nombreUsuarioField.getText().isEmpty() || nombreVerdaderoField.getText().isEmpty() ||
                            passwordField.getText().isEmpty() || MasterField.getText().isEmpty()) {
                        Platform.runLater(() -> saveUser.setDisable(true));
                    } else {
                        Platform.runLater(() -> saveUser.setDisable(false));
                    }
                    break;

                case "EMPLOYED", "GUEST":

                    if (nombreUsuarioField.getText().isEmpty() || nombreVerdaderoField.getText().isEmpty() ||
                            passwordField.getText().isEmpty()) {

                        Platform.runLater(() -> saveUser.setDisable(true));

                    } else {
                        Platform.runLater(() -> saveUser.setDisable(false));
                    }

                    break;


            }
        }

    }
}
