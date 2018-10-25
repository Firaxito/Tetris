package com.firax.tetris;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Main extends Application {


    public static void main(String[] args) {
        launch(args);
    }


    @Override
    public void start(Stage primaryStage) throws Exception {
        AnchorPane root = FXMLLoader.load(getClass().getResource("game_singleplayer.fxml"));
        primaryStage.getIcons().add(new Image("file:src/sample/res/icon.png"));


        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                Platform.exit();
                System.exit(0);
            }
        });


        primaryStage.setTitle("Tetris");
        Scene scene = new Scene(root, 450, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
        scene.getRoot().requestFocus();
    }


}