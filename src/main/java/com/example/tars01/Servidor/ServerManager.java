package com.example.tars01.Servidor;

import com.example.tars01.MainController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;


import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerManager {


    private static boolean serverRunning = false;
    public static final int SERVER_PORT = 8080;
    private static ServerSocket serverSocket;
    static String codigoP,mensaje;








    public static void OnServer(MenuItem see, Label info, TextField textField) throws IOException {


        if (see.getText().equals("Apagar")) {
            serverSocket.close();
            serverRunning = false;
            see.setText("Encender");
        } else if (see.getText().equals("Encender")) {
            serverSocket = new ServerSocket(SERVER_PORT);
            SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {

                    startServer(info,textField);

                    return null;
                }
            };

            worker.execute();
            see.setText("Apagar");
        }

    }


    public static void startServer(Label info,TextField textFieldItem) {
        try {

            mensaje = info.getText();
            serverRunning = true;
            Platform.runLater(() -> info.setText("Servidor iniciado. Esperando conexiones..."));



            while (serverRunning) {
                Socket clientSocket = serverSocket.accept();
                Platform.runLater(() -> info.setText("Cliente conectado desde " + clientSocket.getInetAddress()));
                System.out.println("Cliente conectado desde " + clientSocket.getInetAddress());


                try {
                    BufferedReader inputReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));


                    String message;
                    while ((message = inputReader.readLine()) != null) {
                        codigoP = message.trim();

                        Platform.runLater(() -> info.setText("Mensaje recibido"));
                        Platform.runLater(() -> textFieldItem.setText(codigoP));
                        MainController m = new MainController();
                        m.searchProduct(codigoP);
                        updateInfoExtern(textFieldItem,info);

                        System.out.println(codigoP);

                    }
                } catch (IOException e) {
                    Platform.runLater(() -> info.setText("Error al leer mensaje del cliente: " + e.getMessage()));


                }
            }
        } catch (IOException e) {
            Platform.runLater(() -> info.setText(e.getMessage()));


        }
    }


    public static void updateInfoExtern(TextField textField,Label info){
        Platform.runLater(()->textField.setText(codigoP));
        Platform.runLater(()->info.setText(mensaje));

    }


}
