package com.example.tars01;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
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






        scene.setOnKeyPressed(event -> {
            try {
                BuscarProducto(event,stage,mainController);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        stage.setTitle("Bienvenido " + user);
        stage.setWidth(screenWidth);
        stage.setHeight(screenHeight);
        stage.setScene(scene);
        stage.show();
    }


    public static void BuscarProducto(KeyEvent event, Stage stage,MainController mainController) throws IOException {


        if (mainController.onTurno){
            if (event.isControlDown() && event.getCode() == KeyCode.Q) {

                if (mainController.busquedaB.isVisible()){
                    mainController.busquedaB.setVisible(false);
                }else{
                    mainController.busquedaB.setVisible(true);
                    mainController.QueryInput.requestFocus();
                }



            }
        }


    }
}
