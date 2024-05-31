package com.example.tars01;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        go();


    }

    public static void main(String[] args) {
        launch();
    }


    public static void go() throws IOException {
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(),675,412);

        stage.setResizable(false);

        scene.setOnKeyPressed(event -> {
            try {
                Registro(event,stage);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        double screenWidth = Screen.getPrimary().getVisualBounds().getWidth();
        double screenHeight = Screen.getPrimary().getVisualBounds().getHeight();
//        stage.setWidth(screenWidth);
//        stage.setHeight(screenHeight);
        stage.setScene(scene);
        stage.show();


    }

    public static void Registro(KeyEvent event,Stage stage) throws IOException {

        if (event.isControlDown() && event.getCode() == KeyCode.K ) {
            stage.toBack();
            HelloRegistro.go();
        }



    }
}