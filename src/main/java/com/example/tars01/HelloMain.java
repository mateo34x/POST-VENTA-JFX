package com.example.tars01;

import com.example.tars01.Printer.PrinterCommandsAct;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Screen;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.io.*;

import static com.example.tars01.Printer.PrinterCommandsAct.SetCodePageOEM850;
import static com.example.tars01.Printer.PrinterCommandsAct.printImage;

public class HelloMain extends Application {

    static String User;


    @Override
    public void start(Stage stage) throws Exception {
        go("","","");
    }

    public static void main(String[] args) {
        launch();
    }


    public void go(String user,String permision,String passM) throws IOException {
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloMain.class.getResource("Main-View.fxml"));

        Scene scene = new Scene(fxmlLoader.load());
        stage.setResizable(true);
        double screenWidth = Screen.getPrimary().getVisualBounds().getWidth();
        double screenHeight = Screen.getPrimary().getVisualBounds().getHeight();

        MainController mainController = fxmlLoader.getController();
        mainController.setUser(user,permision,passM);






        scene.setOnKeyPressed(event -> {
            try {
                EventosC(event,stage,mainController);
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


    public static void EventosC(KeyEvent event, Stage stage,MainController mainController) throws IOException {


        if (mainController.onTurno){
            if (event.isControlDown() && event.getCode() == KeyCode.Q) {

                if (mainController.busquedaB.isVisible()){
                    mainController.busquedaB.setVisible(false);
                    mainController.textFieldItem.requestFocus();
                }else{
                    mainController.busquedaB.setVisible(true);
                    mainController.QueryInput.requestFocus();
                }

            }


            if (event.isControlDown() && event.getCode() == KeyCode.O){
                if (mainController.obser.isVisible()){
                    mainController.obser.setVisible(false);
                    mainController.labelItem121.setVisible(false);

                }else{
                    mainController.obser.setVisible(true);
                    mainController.labelItem121.setVisible(true);
                    mainController.obser.requestFocus();
                }
            }

            if (event.getCode() == KeyCode.F){
                if (mainController.ReciboViewVenta.isVisible()){
                    mainController.ReciboViewVenta.setVisible(false);
                }
            }


            if (event.isControlDown() && event.getCode() == KeyCode.L){
                mainController.getLastBill();
            }

            if (event.isControlDown() && event.getCode()== KeyCode.P){
                PrinterCommandsAct.IniciarImpresora();
                OutputStream out = new FileOutputStream("/dev/usb/lp0");
                PrinterCommandsAct.sendData(out, SetCodePageOEM850());
                printImage(ImageIO.read(new File("/home/tars/Downloads/barcode.jpg")), out, false);
                out.close();

            }

        }


    }
}
