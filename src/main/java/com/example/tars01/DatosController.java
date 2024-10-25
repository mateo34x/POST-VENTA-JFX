package com.example.tars01;

import com.example.tars01.Utils.FileEditor;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class DatosController {

    @FXML
    TextField NameL,NitL,DirL,TelL,LogoL,ImpreL;
    @FXML
    ImageView chooseLogo;
    @FXML
    CheckBox checkImpre;
    Boolean isChecked;






    @FXML
    private void initialize() {

        NameL.setText(FileEditor.leerLineaEspecifica("PrincipalData.txt",1));
        ImpreL.setText(FileEditor.leerLineaEspecifica("PrincipalData.txt",6));

        if (FileEditor.leerLineaEspecifica("PrincipalData.txt",7)!=null){
            checkImpre.setSelected(Boolean.parseBoolean(FileEditor.leerLineaEspecifica("PrincipalData.txt",7).replace("\n","")));
        }




        checkImpre.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) {
                System.out.println("Check: "+t1);
                FileEditor.insertarValorEnLinea("PrincipalData.txt",7, String.valueOf(t1));
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
}
