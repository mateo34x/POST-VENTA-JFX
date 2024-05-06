package com.example.tars01;

import javafx.animation.PauseTransition;
import javafx.scene.control.Label;
import javafx.util.Duration;

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
}
