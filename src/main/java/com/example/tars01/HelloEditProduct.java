package com.example.tars01;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloEditProduct extends Application {



    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
        go();
    }

    public static void go() throws IOException {
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloMain.class.getResource("Editar-View.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setResizable(true);
//        double screenWidth = Screen.getPrimary().getVisualBounds().getWidth();
//        double screenHeight = Screen.getPrimary().getVisualBounds().getHeight();


//        System.out.println("H: "+screenHeight+"\n"+
//                "W: "+screenWidth);

        stage.setTitle("Editar");
        stage.setWidth(640);
        stage.setHeight(450);
        stage.setScene(scene);
        stage.show();
    }
}
