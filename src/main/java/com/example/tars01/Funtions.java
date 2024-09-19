package com.example.tars01;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.util.Duration;

import java.io.*;

public class Funtions {



    public static void HideMessage(Label messageLabel,int d){
        PauseTransition pause;
        if (d==0){
            pause = new PauseTransition(Duration.seconds(5));
        }else{
            pause = new PauseTransition(Duration.seconds(d));
        }
        pause.setOnFinished(event -> {


            FadeTransition fadeOut = new FadeTransition(Duration.millis(500), messageLabel);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);
            fadeOut.play();


        });
        pause.play();

    }

    public static void ClearMessage(TextField messageLabel, int d){
        PauseTransition pause;
        if (d==0){
            pause = new PauseTransition(Duration.seconds(5));
        }else{
            pause = new PauseTransition(Duration.seconds(d));
        }
        pause.setOnFinished(event -> messageLabel.clear());
        pause.play();

    }


    public static void ChangeMessage(Label messageLabel, int d, String message) {
        PauseTransition pause;
        if (d == 0) {
            pause = new PauseTransition(Duration.seconds(5));
        } else {
            pause = new PauseTransition(Duration.seconds(d));
        }

        pause.setOnFinished(event -> {
            FadeTransition fadeOut = new FadeTransition(Duration.millis(500), messageLabel);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);
            fadeOut.setOnFinished(event2 -> {
                messageLabel.setText(message);

                FadeTransition fadeIn = new FadeTransition(Duration.millis(500), messageLabel);
                fadeIn.setFromValue(0.0);
                fadeIn.setToValue(1.0);
                fadeIn.play();
            });
            fadeOut.play();
        });

        pause.play();
    }

//    public static int obtenerNumeroFactura() {
//        int numeroFactura = 0;
//        try (BufferedReader br = new BufferedReader(new FileReader("numero_factura.txt"))) {
//            String linea;
//            while ((linea = br.readLine()) != null) {
//                numeroFactura = Integer.parseInt(linea);
//            }
//        } catch (IOException e) {
//            // Manejo de excepciones si el archivo no existe o hay un problema al leerlo
//            e.printStackTrace();
//        }
//        return numeroFactura;
//    }
//
//    // Método para guardar el número de factura actual
//    public static void guardarNumeroFactura(int numeroFactura) {
//        try (BufferedWriter bw = new BufferedWriter(new FileWriter("numero_factura.txt"))) {
//            bw.write(String.valueOf(numeroFactura));
//        } catch (IOException e) {
//            // Manejo de excepciones si hay un problema al escribir en el archivo
//            e.printStackTrace();
//        }
//    }


    public static void cargarVista(String fxmlFile,TextField textField) {
        try {

            double screenWidth = Screen.getPrimary().getVisualBounds().getWidth();
            double screenHeight = Screen.getPrimary().getVisualBounds().getHeight();
            FXMLLoader loader = new FXMLLoader(Funtions.class.getResource(fxmlFile));
            Parent root = loader.load();


            // Obtener la referencia al VBox principal en el archivo FXML principal
            VBox mainContainer = (VBox) textField.getScene().getRoot();
            mainContainer.setPrefWidth(screenWidth);
            mainContainer.setPrefHeight(screenHeight);

            // Limpiar el contenedor principal y agregar la nueva vista
            mainContainer.getChildren().clear();
            mainContainer.getChildren().add(root);

        } catch (IOException e) {
            e.printStackTrace();
            // Manejo de errores
        }
    }
}
