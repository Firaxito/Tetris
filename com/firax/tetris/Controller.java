package com.firax.tetris;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import com.firax.tetris.ai.AI;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    AnchorPane application;

    //RECOMMENDED SIZE RATIO: 1:2
    private final static int WIDTH = 10;
    private final static int HEIGHT = 20;
    private GameBoard gameBoard;
    private AI bot;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        gameBoard = new GameBoard(WIDTH, HEIGHT, application);
        bot = new AI(gameBoard, WIDTH, HEIGHT);
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
                        gameBoard.pauseGame(!gameBoard.isGamePaused());
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
    }
}
