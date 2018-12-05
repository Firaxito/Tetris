package com.firax.tetris;

import com.firax.tetris.bricks.Brick;
import com.firax.tetris.bricks.BrickColor;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class Skin {

    private final static int TOTAL_SKIN_COUNT = 5;

    public final static int DEFAULT_SKIN = 1;
    public final static int RETRO_SKIN = 2;
    public final static int NEON_SKIN = 3;
    public final static int WOOD_SKIN = 4;
    public final static int DOT_SKIN = 5;

    private Image woodImage;
    private int selectedSkinID;


    public Skin(int skinID) {
        woodImage = new Image(getClass().getResourceAsStream("res/wood.png"));
        setSkinID(skinID);
    }

    public void setSkinID(int skinID){
        if(skinID > TOTAL_SKIN_COUNT ) selectedSkinID = DEFAULT_SKIN;
        else if(skinID < 1) selectedSkinID = TOTAL_SKIN_COUNT;
        else selectedSkinID = skinID;
    }

    public int getSelectedSkinID(){
        return selectedSkinID;
    }

    public synchronized void drawSquareOnCanvas(double x, double y, double width, double height, Color brickColor, Canvas canvas, double offsetX, double offsetY) {
        switch (selectedSkinID){
            case DEFAULT_SKIN:
                defaultSkin(x, y, width, height, brickColor, canvas, offsetX, offsetY);
                break;
            case RETRO_SKIN:
                retroSkin(x,y,width,height,brickColor,canvas,offsetX,offsetY);
                break;
            case NEON_SKIN:
                neonSkin(x,y,width,height,brickColor,canvas,offsetX,offsetY);
                break;
            case WOOD_SKIN:
                woodSkin(x,y,width,height,brickColor,canvas,offsetX,offsetY);
                break;
            case DOT_SKIN:
                dotSkin(x,y,width,height,brickColor,canvas,offsetX,offsetY);
                break;
        }
    }

    private void defaultSkin(double x, double y, double width, double height, Color color, Canvas canvas, double offsetX, double offsetY){

        //CHECKING IF IT IS PREVIEW BLOCK
        if(color == Color.TRANSPARENT) color = Color.DARKGRAY;

        //FILLING
        canvas.getGraphicsContext2D().setFill(color);
        canvas.getGraphicsContext2D().fillRect(x * width + offsetX, y * height + offsetY, width, height);

        //INSIDE SHADOW
        canvas.getGraphicsContext2D().setFill(new Color(.1f, .1f, .1f, .1f));
        canvas.getGraphicsContext2D().fillRect((x * width + width / 4) + offsetX, (y * height + height / 4) + offsetY, width - width / 2, height - height / 2);

        //STROKING
        canvas.getGraphicsContext2D().setStroke(Color.BLACK);
        canvas.getGraphicsContext2D().strokeRect(x * width + offsetX, y * height + offsetY, width, height);
        canvas.getGraphicsContext2D().strokeRect(x * width + offsetX + 1, y * height + offsetY + 1, width - 2, height - 2);
    }

    private void retroSkin(double x, double y, double width, double height, Color color, Canvas canvas, double offsetX, double offsetY){

        //CHECKING IF IT IS PREVIEW BLOCK
        if(color == Color.TRANSPARENT) color = Color.DARKGRAY;

        //FILLING
        canvas.getGraphicsContext2D().setFill(Color.WHITE);
        canvas.getGraphicsContext2D().fillRect(x * width + offsetX, y * height + offsetY, width, height);

        //INSIDE COLOR
        canvas.getGraphicsContext2D().setFill(color);
        canvas.getGraphicsContext2D().fillRect((x * width + width / 4) + offsetX, (y * height + height / 4) + offsetY, width - width / 2, height - height / 2);

        //STROKING
        canvas.getGraphicsContext2D().setStroke(Color.BLACK);
        canvas.getGraphicsContext2D().strokeRect(x * width + offsetX, y * height + offsetY, width, height);
        canvas.getGraphicsContext2D().setStroke(color);
        canvas.getGraphicsContext2D().strokeRect(x * width + offsetX + 1, y * height + offsetY + 1, width - 2, height - 2);

    }

    private void neonSkin(double x, double y, double width, double height, Color color, Canvas canvas, double offsetX, double offsetY){

        //CHECKING IF IT IS PREVIEW BLOCK
        if(color == Color.TRANSPARENT) color = Color.DARKGRAY;

        //FILLING
        canvas.getGraphicsContext2D().setFill(Color.BLACK);
        canvas.getGraphicsContext2D().fillRect(x * width + offsetX, y * height + offsetY, width, height);

        //INSIDE COLOR
        canvas.getGraphicsContext2D().setFill(color);
        canvas.getGraphicsContext2D().fillRect((x * width + width / 4) + offsetX, (y * height + height / 4) + offsetY, width - width / 2, height - height / 2);

        //STROKING
        canvas.getGraphicsContext2D().setStroke(Color.BLACK);
        canvas.getGraphicsContext2D().strokeRect((x * width + width / 4) + offsetX, (y * height + height / 4) + offsetY, width - width / 2, height - height / 2);
        canvas.getGraphicsContext2D().strokeRect(x * width + offsetX, y * height + offsetY, width, height);
        canvas.getGraphicsContext2D().setStroke(color);
        canvas.getGraphicsContext2D().strokeRect(x * width + offsetX + 1, y * height + offsetY + 1, width - 2, height - 2);

    }

    private void woodSkin(double x, double y, double width, double height, Color color, Canvas canvas, double offsetX, double offsetY){

        //CHECKING IF IT IS PREVIEW BLOCK
        if(color == Color.TRANSPARENT) color = Color.GRAY;

        //FILLING
        canvas.getGraphicsContext2D().drawImage(woodImage,x * width + offsetX, y * height + offsetY, width, height);

        //INSIDE COLOR
        canvas.getGraphicsContext2D().setFill(new Color(color.getRed(), color.getGreen(), color.getBlue(), .3f));
        canvas.getGraphicsContext2D().fillRect(x * width + offsetX, y * height + offsetY, width, height);

        //STROKING
        canvas.getGraphicsContext2D().setStroke(Color.BLACK);
        canvas.getGraphicsContext2D().strokeRect(x * width + offsetX, y * height + offsetY, width, height);

    }

    private void dotSkin(double x, double y, double width, double height, Color color, Canvas canvas, double offsetX, double offsetY){

        //CHECKING IF IT IS PREVIEW BLOCK
        if(color == Color.TRANSPARENT) color = Color.GRAY;

        //FILLING
        canvas.getGraphicsContext2D().setFill(color);
        canvas.getGraphicsContext2D().fillOval(x * width + offsetX + width/8, y * height + offsetY + height/8, width- width/4, height-height/4);

        //STROKING
        canvas.getGraphicsContext2D().setStroke(Color.BLACK);
        canvas.getGraphicsContext2D().strokeOval(x * width + offsetX + width/8, y * height + offsetY + height/8, width- width/4, height-height/4);

    }
}
