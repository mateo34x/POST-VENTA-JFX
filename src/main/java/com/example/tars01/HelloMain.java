package com.example.tars01;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloMain extends Application {

    static String User;


    @Override
    public void start(Stage stage) throws Exception {
        go("");
    }

    public static void main(String[] args) {
        launch();
    }


    public void go(String user) throws IOException {
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloMain.class.getResource("Main-View.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setResizable(true);
        double screenWidth = Screen.getPrimary().getVisualBounds().getWidth();
        double screenHeight = Screen.getPrimary().getVisualBounds().getHeight();

        MainController mainController = fxmlLoader.getController();
        mainController.setUser(user);
        System.out.println("H: " + screenHeight + "\n" +
                "W: " + screenWidth);


        stage.setTitle("Bienvenido " + user);
        stage.setWidth(screenWidth);
        stage.setHeight(screenHeight);
        stage.setScene(scene);
        stage.show();
    }

    public static String ShareData() {
        return User;
    }
}
