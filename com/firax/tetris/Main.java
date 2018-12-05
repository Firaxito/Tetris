package com.firax.tetris;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class Main extends Application {

    public static Stage primaryStage;

    public static void main(String[] args) {
        launch(args);
    }


    @Override
    public void start(Stage primaryStage) throws Exception {
        Main.primaryStage = primaryStage;

        AnchorPane root = FXMLLoader.load(getClass().getResource("layout/game_menu.fxml"));
        primaryStage.getIcons().add(new Image("/com/firax/tetris/res/icon.png"));

        primaryStage.setOnCloseRequest(event -> {
            Platform.exit();
            System.exit(0);
        });

        primaryStage.setTitle("Tetris");
        Scene scene = new Scene(root, Settings.MENU_WIDTH, Settings.MENU_HEIGHT);
        primaryStage.setScene(scene);
        primaryStage.show();
        scene.getRoot().requestFocus();
    }


}