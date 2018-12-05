package com.firax.tetris.controllers;

import com.firax.tetris.GameBoard;
import com.firax.tetris.Settings;
import com.firax.tetris.Skin;
import com.firax.tetris.bricks.Brick;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Set;

public class SettingsController implements Initializable {

    @FXML
    TextField mWidth;

    @FXML
    TextField mHeight;

    @FXML
    Button mSizeConfirm;

    @FXML
    Button mSkinNext;

    @FXML
    Button mSkinPrevious;

    @FXML
    Canvas mSkinCanvas;

    @FXML
    CheckBox mFallPreview;

    @FXML
    CheckBox mAnimations;

    Skin selectedSkin;
    int skinBrickID = Brick.J_BRICK_ID;
    int selectedWidth = Settings.BOARD_WIDTH;
    int selectedHeight = Settings.BOARD_HEIGHT;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        selectedSkin = new Skin(Settings.FAVOURITE_SKIN);
        mWidth.setText(String.valueOf(Settings.BOARD_WIDTH));
        mHeight.setText(String.valueOf(Settings.BOARD_HEIGHT));
        mFallPreview.setSelected(Settings.IS_PREVIEW_ACTIVE);
        mAnimations.setSelected(Settings.IS_ANIMATION_ENABLED);

        setupListeners();
        updateSkin();

    }

    private void changeBrickID(){
        skinBrickID++;
        if(skinBrickID > Brick.TOTAL_BRICK_COUNT) skinBrickID = 0;
    }

    private void updateSkin(){
        Settings.FAVOURITE_SKIN = selectedSkin.getSelectedSkinID();
        Brick.drawBrickOnCanvas(mSkinCanvas, skinBrickID, selectedSkin);

        if(MenuController.gameBoard != null) MenuController.gameBoard.setSkin(Settings.FAVOURITE_SKIN);

    }

    private void checkSizeButton(){
        if(mWidth.getText().equals(String.valueOf(Settings.BOARD_WIDTH))
                && mHeight.getText().equals(String.valueOf(Settings.BOARD_HEIGHT))) mSizeConfirm.setVisible(false);
        else if (hasSizeValidValues()){
            mSizeConfirm.setVisible(true);
        }
        else mSizeConfirm.setVisible(false);
    }

    private boolean hasSizeValidValues(){
        int width;
        int height;

        try{
            width = Integer.parseInt(mWidth.getText());
            height = Integer.parseInt(mHeight.getText());
        } catch (NumberFormatException e){
            return false;
        }

        if (width >= 10 && width <= 75 && height >= 20 && height <= 150){
            selectedHeight = height;
            selectedWidth = width;
            return true;
        } else return false;

    }

    private void setupListeners(){

        //SIZE STUFF
        mWidth.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                checkSizeButton();
            }
        });

        mHeight.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                checkSizeButton();
            }
        });

        mSizeConfirm.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Settings.BOARD_WIDTH = selectedWidth;
                Settings.BOARD_HEIGHT = selectedHeight;
                checkSizeButton();
                if(MenuController.gameBoard != null) MenuController.gameBoard.resizeBoard(selectedWidth, selectedHeight);
            }
        });

        //SKIN STUFF
        mSkinPrevious.setOnMouseClicked(event -> {
          selectedSkin.setSkinID(selectedSkin.getSelectedSkinID()-1);
          updateSkin();
        });

        mSkinCanvas.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
               changeBrickID();
               updateSkin();
            }
        });

        mSkinNext.setOnMouseClicked(event -> {
            selectedSkin.setSkinID(selectedSkin.getSelectedSkinID()+1);
            updateSkin();
        });

        //OTHER STUFF
        mFallPreview.setOnMouseClicked(event -> {
            Settings.IS_PREVIEW_ACTIVE = mFallPreview.isSelected();
            if(MenuController.gameBoard  != null) MenuController.gameBoard.setPreview(Settings.IS_PREVIEW_ACTIVE);
        });
        mAnimations.setOnMouseClicked(event -> {
            Settings.IS_ANIMATION_ENABLED = mAnimations.isSelected();
            if(MenuController.gameBoard  != null) MenuController.gameBoard.setAnimations(Settings.IS_ANIMATION_ENABLED);
        });
    }


}
