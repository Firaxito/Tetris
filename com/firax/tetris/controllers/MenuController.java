package com.firax.tetris.controllers;

import com.firax.tetris.GameBoard;
import com.firax.tetris.Main;
import com.firax.tetris.Settings;
import com.firax.tetris.ai.AI;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;

public class MenuController implements Initializable {

    public static GameBoard gameBoard;

    @FXML
    AnchorPane menu;

    @FXML
    private Button singlePlayerButton;

    @FXML
    private Button multiPlayerButton;

    @FXML
    private Button settingsButton;

    @FXML
    private Button leaderBoardButton;

    @FXML
    private Button helpButton;

    @FXML
    private Button exitButton;

    private Pane infoLayout;
    private boolean isInfoVisible;
    private boolean isInfoAnimationPlaying;
    String[] highScores = new String[10];
    private AI ai;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        gameBoard = new GameBoard(menu, Settings.BOARD_WIDTH, Settings.BOARD_HEIGHT, 300, 0);
        ai = new AI(gameBoard);
        gameBoard.startGame();
        ai.setSpeedScale(2);
        ai.play(Settings.IS_ANIMATION_ENABLED, Settings.IS_PREVIEW_ACTIVE, 10);
        setupListeners();
        setupInfoLayout();
        loadHighScores();
    }

    private void loadHighScores(){
        try {
        BufferedReader bufRead = new BufferedReader(new FileReader("highscores"));
        StringBuilder builder = new StringBuilder();
        String line;
            int counter = 0;
            while ((line = bufRead.readLine()) != null) {
                builder.append(line).append("\n");
                highScores[counter++] = line;
                if(counter > 9) return;
            }
        } catch (IOException e){
            System.out.println("highscore file doesn't exist");
        }
    }

    private void setupListeners() {
        singlePlayerButton.setOnMouseClicked(event -> startSinglePlayer());
        multiPlayerButton.setOnMouseClicked(event -> startMultiPlayer());
        settingsButton.setOnMouseClicked(event -> showSettings());
        leaderBoardButton.setOnMouseClicked(event -> showLeaderboard());
        helpButton.setOnMouseClicked(event -> showHelp());
        exitButton.setOnMouseClicked(event -> exit());

        menu.setOnKeyPressed(event -> {
            if(event.getCode() == KeyCode.ESCAPE){
                hideInfo();
            }
        });

    }

    private void setupInfoLayout() {
        infoLayout = new Pane();
        infoLayout.setLayoutX(300);
        Pane background = new Pane();
        background.setPrefHeight(600);
        background.setPrefWidth(450);
        background.setStyle("-fx-background-color: black; -fx-opacity: 0.85;");
        infoLayout.getChildren().add(background);
        Pane temp = new Pane();
        temp.setId("NULL");
        infoLayout.getChildren().add(temp);
        infoLayout.setTranslateX(450);
        menu.getChildren().add(infoLayout);
    }

    private void startSinglePlayer() {

        ai.pause();
        gameBoard.pauseGame(true);

        try {
            AnchorPane root = FXMLLoader.load(getClass().getResource(Settings.ROOT_FOLDER + "layout/game_singleplayer.fxml"));
            Scene scene = new Scene(root, Settings.GAME_WIDTH, Settings.GAME_HEIGHT);
            Main.primaryStage.setScene(scene);
            scene.getRoot().requestFocus();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startMultiPlayer() {

    }

    private void showSettings() {
        if (!isInfoAnimationPlaying) {
            if (infoLayout.getChildren().get(infoLayout.getChildren().size() - 1).getId().equals("SETTINGS")) {
                if (isInfoVisible) hideInfo();
                else showInfo();
            } else {
                if (isInfoVisible) animateInfo("SETTINGS");
                else {
                    infoLayout.getChildren().remove(infoLayout.getChildren().size() - 1);
                    infoLayout.getChildren().add(getSettingsPane());
                    showInfo();
                }
            }
        }
    }

    private void showLeaderboard() {
        if (!isInfoAnimationPlaying) {
            if (infoLayout.getChildren().get(infoLayout.getChildren().size() - 1).getId().equals("LEADERBOARD")) {
                if (isInfoVisible) hideInfo();
                else showInfo();
            } else {
                if (isInfoVisible) animateInfo("LEADERBOARD");
                else {
                    infoLayout.getChildren().remove(infoLayout.getChildren().size() - 1);
                    infoLayout.getChildren().add(getLeaderBoardPane());
                    showInfo();
                }
            }
        }
    }

    private void showHelp() {
        if (!isInfoAnimationPlaying) {
            if (infoLayout.getChildren().get(infoLayout.getChildren().size() - 1).getId().equals("HELP")) {
                if (isInfoVisible) hideInfo();
                else showInfo();
            } else {
                if (isInfoVisible) animateInfo("HELP");
                else {
                    try {
                        System.out.println();
                        AnchorPane root = FXMLLoader.load(getClass().getResource(Settings.ROOT_FOLDER + "layout/help_layout.fxml"));
                        root.setId("HELP");
                        root.setLayoutX(-10);
                        infoLayout.getChildren().remove(infoLayout.getChildren().size() - 1);
                        infoLayout.getChildren().add(root);
                        showInfo();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void animateInfo(String type) {
        isInfoAnimationPlaying = true;

        //Animation
        Timeline timeline = new Timeline();
        timeline.setCycleCount(200);
        KeyFrame keyframe = new KeyFrame(Duration.millis(4), (ActionEvent event) -> {
            //Hiding
            if (infoLayout.getTranslateX() < 450) {
                isInfoVisible = false;
                infoLayout.setTranslateX(infoLayout.getTranslateX() + 4.5);
                System.out.println(infoLayout.getTranslateX());
            } else {
                isInfoAnimationPlaying = false;
                switch (type) {
                    case "HELP":
                        showHelp();
                        break;
                    case "LEADERBOARD":
                        showLeaderboard();
                        break;
                    case "SETTINGS":
                        showSettings();
                }
                timeline.stop();
            }
        });
        timeline.getKeyFrames().add(keyframe);
        timeline.play();

    }

    public void hideInfo() {
        if (!isInfoAnimationPlaying) {
            isInfoVisible = false;
            //Animation
            Timeline timeline = new Timeline();
            timeline.setCycleCount(100);
            KeyFrame keyframe = new KeyFrame(Duration.millis(4), (ActionEvent event) -> {
                if (isInfoVisible) timeline.stop();

                if (infoLayout.getTranslateX() < 450)
                    infoLayout.setTranslateX(infoLayout.getTranslateX() + 4.5);
            });
            timeline.getKeyFrames().add(keyframe);
            timeline.play();
        }
    }

    public void showInfo() {
        if (!isInfoAnimationPlaying) {
            isInfoVisible = true;
            //Animation
            Timeline timeline = new Timeline();
            timeline.setCycleCount(100);
            KeyFrame keyframe = new KeyFrame(Duration.millis(4), (ActionEvent event) -> {
                if (!isInfoVisible) timeline.stop();

                if (infoLayout.getTranslateX() > 0)
                    infoLayout.setTranslateX(infoLayout.getTranslateX() - 4.5);
            });
            timeline.getKeyFrames().add(keyframe);
            timeline.play();
        }
    }

    private Pane getSettingsPane() {
        Pane pane = new Pane();
        pane.setId("SETTINGS");
        AnchorPane root;

        try {
            root = FXMLLoader.load(getClass().getResource(Settings.ROOT_FOLDER + "layout/settings_layout.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
            return pane;
        }

        pane.getChildren().add(root);

        return pane;
    }

    private Pane getLeaderBoardPane() {
        Pane pane = new Pane();
        pane.setId("LEADERBOARD");

        Label title = new Label("NO HIGH SCORES");
        title.setPrefWidth(450);
        title.setPrefHeight(600);
        title.setAlignment(Pos.CENTER);
        title.setTextFill(Color.WHITE);
        title.setFont(new Font("Verdana", 20));
        pane.getChildren().add(title);

        if(highScores[0] != null){
            title.setText("TOP 10 PLAYERS");
            title.setAlignment(Pos.TOP_CENTER);
            title.setLayoutY(35);

            for(int i = 0; i < highScores.length; i++){

                Label label = new Label();
                label.setLayoutY(i*50 + 100);
                label.setLayoutX(50);
                label.setFont(new Font("Verdana", 16));
                label.setTextFill(Color.WHITE);
                label.setText(i+1 + ".    ");

                if(highScores[i] != null) {

                    String[] splitted = highScores[i].split("\\s+");
                    label.setText(label.getText() + splitted[0]);
                    if(splitted.length > 1) {

                        Label score = new Label();
                        score.setLayoutY(i * 50 + 100);
                        score.setPrefWidth(450);
                        score.setLayoutX(-35);
                        score.setAlignment(Pos.CENTER_RIGHT);
                        score.setFont(new Font("Verdana", 16));
                        score.setTextFill(Color.WHITE);
                        score.setText(splitted[1] + " points");
                        pane.getChildren().add(score);

                    }
                }
                pane.getChildren().add(label);
            }
        }
        return pane;
    }


    private void exit() {
        Platform.exit();
        System.exit(0);
    }


}
