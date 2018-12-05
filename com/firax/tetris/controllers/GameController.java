package com.firax.tetris.controllers;

import com.firax.tetris.GameBoard;
import com.firax.tetris.Main;
import com.firax.tetris.Settings;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import com.firax.tetris.ai.AI;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class GameController implements Initializable {

    @FXML
    AnchorPane application;

    @FXML
    AnchorPane menu;

    @FXML
    Button continueButton;

    @FXML
    Button newGameButton;

    @FXML
    Button menuButton;

    private GameBoard gameBoard;
    private AI bot;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        gameBoard = new GameBoard(application,Settings.BOARD_WIDTH, Settings.BOARD_HEIGHT);
        bot = new AI(gameBoard);
        setupListeners();

        gameBoard.startGame();

    }


    private void setupListeners() {

        application.setOnKeyTyped(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {

            }
        });

        application.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                switch (event.getCode()){
                    case W:
                    case UP:
                        gameBoard.rotateBrick();
                        break;

                    case A:
                    case LEFT:
                        gameBoard.moveBrickLeft();
                        break;

                    case S:
                    case DOWN:
                        gameBoard.moveBrickDown();
                        break;

                    case D:
                    case RIGHT:
                        gameBoard.moveBrickRight();
                        break;

                    case SPACE:
                    case NUMPAD0:
                        gameBoard.makeBrickFall();
                        break;

                    case SHIFT:
                    case PLUS:
                        gameBoard.holdBrick();
                        break;

                    case ESCAPE:
                    case P:

                        pauseGame(!gameBoard.isGamePaused());

                        break;

                    case R:
                        gameBoard.resetGame();
                        break;

                    case X:
                        if(bot.isPlaying()) bot.pause();
                        else bot.play();
                        break;

                    case NUMPAD8:
                        gameBoard.setSkin(gameBoard.getActiveSkin()+1);
                        break;

                    case NUMPAD2:
                        gameBoard.setSkin(gameBoard.getActiveSkin()-1);
                        break;
                }
            }
        });

        continueButton.setOnMouseClicked(event -> pauseGame(false));

        newGameButton.setOnMouseClicked(event -> {
            pauseGame(false);
            gameBoard.resetGame();
        });

        menuButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                try {

                    AnchorPane root = FXMLLoader.load(getClass().getResource(Settings.ROOT_FOLDER + "layout/game_menu.fxml"));
                    Scene scene = new Scene(root, Settings.MENU_WIDTH, Settings.MENU_HEIGHT);
                    Main.primaryStage.setScene(scene);
                    scene.getRoot().requestFocus();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void pauseGame(boolean value){
        if(value) {
            menu.setVisible(true);
            gameBoard.pauseGame(true);
        } else {
            menu.setVisible(false);
            gameBoard.pauseGame(false);
            application.getScene().getRoot().requestFocus();
        }
    }

}
