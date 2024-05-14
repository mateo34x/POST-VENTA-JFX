package com.example.tars01;

import javafx.animation.PauseTransition;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
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
        pause.setOnFinished(event -> messageLabel.setText(""));
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

    public static int obtenerNumeroFactura() {
        int numeroFactura = 0;
        try (BufferedReader br = new BufferedReader(new FileReader("numero_factura.txt"))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                numeroFactura = Integer.parseInt(linea);
            }
        } catch (IOException e) {
            // Manejo de excepciones si el archivo no existe o hay un problema al leerlo
            e.printStackTrace();
        }
        return numeroFactura;
    }

    // Método para guardar el número de factura actual
    public static void guardarNumeroFactura(int numeroFactura) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("numero_factura.txt"))) {
            bw.write(String.valueOf(numeroFactura));
        } catch (IOException e) {
            // Manejo de excepciones si hay un problema al escribir en el archivo
            e.printStackTrace();
        }
    }
}
