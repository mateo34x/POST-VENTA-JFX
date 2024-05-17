package com.example.tars01;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloCreateProduct extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        go();
    }

    public static void main(String[] args) {
        launch();
    }

    public static void go() throws IOException {
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloMain.class.getResource("CreateProducto-View.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setResizable(true);
        double screenWidth = Screen.getPrimary().getVisualBounds().getWidth();
        double screenHeight = Screen.getPrimary().getVisualBounds().getHeight();
        stage.setTitle("Crear producto");
        stage.setWidth(screenWidth);
        stage.setHeight(screenHeight);
        stage.setScene(scene);
        stage.show();
    }
}
