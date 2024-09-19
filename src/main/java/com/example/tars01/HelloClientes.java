package com.example.tars01;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;

import static javafx.application.Application.launch;

public class HelloClientes extends Application {


    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
        go();
    }

    public static void go() throws IOException {
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloClientes.class.getResource("Clientes.fxml"));

        Scene scene = new Scene(fxmlLoader.load());
        stage.setResizable(true);
        double screenWidth = Screen.getPrimary().getVisualBounds().getWidth();
        double screenHeight = Screen.getPrimary().getVisualBounds().getHeight();

        //MainController mainController = fxmlLoader.getController();


//        scene.setOnKeyPressed(event -> {
//            try {
//                EventosC(event,stage,mainController);
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        });

        stage.setTitle("Crear nuevo cliente");
        stage.setWidth(screenWidth);
        stage.setHeight(screenHeight);
        stage.setScene(scene);
        stage.show();
    }

}
