package com.example.tars01;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class MainController {



    private boolean serverRunning = false;



    public static final int SERVER_PORT = 8080;
    private ServerSocket serverSocket = new ServerSocket(SERVER_PORT);

    @FXML
    public Label logArea;

    @FXML
    public TextField textFieldItem;

    @FXML
    public Button logout;

    @FXML
    public MenuItem see;

    public MainController() throws IOException {
    }


    @FXML
    private void OnServer() throws IOException {

        if (see.getText().equals("Apagar")){
            serverSocket.close();
            serverRunning = false;
            see.setText("Encender");
        } else if (see.getText().equals("Encender")) {
            SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {

                    if (serverSocket.isClosed()){
                        serverSocket = new ServerSocket(SERVER_PORT);
                    }
                    startServer();

                    return null;
                }
            };

            worker.execute();
            see.setText("Apagar");
        }

    }


    public void startServer() {
        try {



            serverRunning = true;
            Platform.runLater(() -> logArea.setText("Servidor iniciado. Esperando conexiones..."));


            while (serverRunning) {
                Socket clientSocket = serverSocket.accept();
                Platform.runLater(() -> logArea.setText("Cliente conectado desde "+clientSocket.getInetAddress()));
                System.out.println("Cliente conectado desde "+clientSocket.getInetAddress());




                try {
                    BufferedReader inputReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));


                    String message;
                    while ((message = inputReader.readLine()) != null) {
                        String codigoP = message.trim();

                        Platform.runLater(() -> logArea.setText("Mensaje recibido"));
                        Platform.runLater(() -> textFieldItem.setText(codigoP));
                        System.out.println(codigoP);

                    }
                } catch (IOException e) {
                    Platform.runLater(() ->logArea.setText("Error al leer mensaje del cliente: "+e.getMessage()));


                }
            }
        } catch (IOException e) {
            Platform.runLater(() ->logArea.setText(e.getMessage()));


        }
    }

    public void Logout() throws IOException {
        if (!see.getText().equals("Encender")){
            serverSocket.close();
        }

        Stage stage = (Stage) logout.getScene().getWindow();
        stage.close();
        HelloApplication.go();
    }



}
