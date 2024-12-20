package com.example.tars01;

import com.example.tars01.Utils.FileEditor;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class DatosController {

    @FXML
    TextField NameL,NitL,DirL,TelL,LogoL,ImpreL;
    @FXML
    ImageView chooseLogo;
    @FXML
    CheckBox checkImpre,checkRecibo;
    String Config = "PrincipalData.txt";
    Boolean isChecked;






    @FXML
    private void initialize() {

        NameL.setText(FileEditor.leerLineaEspecifica(Config,1));
        NitL.setText(FileEditor.leerLineaEspecifica(Config,2));
        DirL.setText(FileEditor.leerLineaEspecifica(Config,3));
        TelL.setText(FileEditor.leerLineaEspecifica(Config,4));
        ImpreL.setText(FileEditor.leerLineaEspecifica(Config,6));
        LogoL.setText(FileEditor.leerLineaEspecifica(Config,9));

        if (FileEditor.leerLineaEspecifica(Config,7)!=null){
            checkImpre.setSelected(Boolean.parseBoolean(FileEditor.leerLineaEspecifica(Config,7).replace("\n","")));
        }
        if (FileEditor.leerLineaEspecifica(Config,8)!=null){
            checkRecibo.setSelected(Boolean.parseBoolean(FileEditor.leerLineaEspecifica(Config,8).replace("\n","")));

        }




        checkImpre.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) {
                System.out.println("Check7: "+t1);
                FileEditor.insertarValorEnLinea(Config,7, String.valueOf(t1));
            }
        });

        checkRecibo.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) {
                System.out.println("Check8: "+t1);
                FileEditor.insertarValorEnLinea(Config,8, String.valueOf(t1));
            }
        });






        NameL.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (keyEvent.getCode()== KeyCode.ENTER){
                    if (!NameL.getText().isEmpty()){
                        FileEditor.insertarValorEnLinea("PrincipalData.txt",1,NameL.getText());
                    }
                }
            }
        });

        NitL.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (keyEvent.getCode()== KeyCode.ENTER){
                    if (!NitL.getText().isEmpty()){
                        FileEditor.insertarValorEnLinea("PrincipalData.txt",2,"NIT:"+NitL.getText());
                    }
                }
            }
        });

        DirL.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (keyEvent.getCode()== KeyCode.ENTER){
                    if (!DirL.getText().isEmpty()){
                        FileEditor.insertarValorEnLinea("PrincipalData.txt",3,"DIR:"+DirL.getText());
                    }
                }
            }
        });

        TelL.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (keyEvent.getCode()== KeyCode.ENTER){
                    if (!TelL.getText().isEmpty()){
                        FileEditor.insertarValorEnLinea("PrincipalData.txt",4,"TEL:"+TelL.getText());
                    }
                }
            }
        });
    }


    public void chooseLogo(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccione una imagen");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg")
        );

        File selectedFile = fileChooser.showOpenDialog(new Stage());
        if (selectedFile != null) {
            try {
                // Intentamos cargar la imagen
                Image image = new Image(new FileInputStream(selectedFile));

                // Verificamos las dimensiones
                if (image.getWidth() == 236 && image.getHeight() == 236) {
                    String imagePath = selectedFile.getAbsolutePath();
                    LogoL.setText(imagePath);
                    System.out.println("Ruta de la imagen seleccionada: " + imagePath);
                    FileEditor.insertarValorEnLinea("PrincipalData.txt",9,imagePath);

                    // Aquí podrías usar imagePath según lo necesites en tu aplicación

                } else {
                    // Mostramos una alerta si las dimensiones no son correctas
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Dimensiones incorrectas");
                    alert.setHeaderText(null);
                    alert.setContentText("La imagen debe tener dimensiones de 236 x 236 píxeles.");
                    alert.showAndWait();
                }
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

    }
}
