/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.jplayer;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Objects;

/**
 *
 * @author James.J
 */
public class JVplayer extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(JVplayer.class.getResource("player.fxml"));
        Parent root = fxmlLoader.load();
        
        Scene scene = new Scene(root);
        PauseTransition pt= new PauseTransition(Duration.seconds(5));
        pt.setOnFinished(e-> scene.setCursor(Cursor.NONE));
        scene.addEventHandler(Event.ANY, e->{
            pt.playFromStart();
            scene.setCursor(Cursor.DEFAULT);
        });


        stage.getIcons().add(new Image(Objects.requireNonNull(JVplayer.class.getResource("assets/Icons.png")).openStream()));
        stage.setTitle("JVplayer");
        stage.setMinHeight(120);
        stage.setMinWidth(550);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
