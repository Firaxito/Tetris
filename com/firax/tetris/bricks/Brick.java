package com.firax.tetris.bricks;

import com.firax.tetris.Skin;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;

import java.util.List;

public abstract class Brick {

    public final static int TOTAL_BRICK_COUNT= 7;

    public final static int I_BRICK_ID = 1;
    public final static int J_BRICK_ID = 2;
    public final static int L_BRICK_ID = 3;
    public final static int T_BRICK_ID = 4;
    public final static int O_BRICK_ID = 5;
    public final static int S_BRICK_ID = 6;
    public final static int Z_BRICK_ID = 7;

    private int ID;
    private boolean onGround;
    private int currentRotation;

    protected Brick(int ID){
        this.ID = ID;
        currentRotation = 0;
    }

    public boolean isOnGround(){
        return onGround;
    }

    public void setOnGround(){
        onGround = true;
    }

    public int getID(){
        return ID;
    }

    public void setCurrentRotation(int currentRotation){
        this.currentRotation = currentRotation;
    }

    public int getCurrentRotation(){
        return currentRotation;
    }

    public List<int[][]> getMatrixShapes() {
        return null;
    }

    public static Brick createBrickByID(int ID) {
        switch (ID) {
            case I_BRICK_ID:
                return new IBrick();

            case J_BRICK_ID:
                return new JBrick();

            case L_BRICK_ID:
                return new LBrick();

            case O_BRICK_ID:
                return new OBrick();

            case S_BRICK_ID:
                return new SBrick();

            case T_BRICK_ID:
                return new TBrick();

            case Z_BRICK_ID:
                return new ZBrick();
        }
        return new IBrick();
    }

    public static Color getColorByID(int ID) {
        switch (ID) {
            case Brick.I_BRICK_ID:
                return BrickColor.I_BRICK_COLOR;

            case Brick.J_BRICK_ID:
                return BrickColor.J_BRICK_COLOR;

            case Brick.L_BRICK_ID:
                return BrickColor.L_BRICK_COLOR;

            case Brick.T_BRICK_ID:
                return BrickColor.T_BRICK_COLOR;

            case Brick.O_BRICK_ID:
                return BrickColor.O_BRICK_COLOR;

            case Brick.S_BRICK_ID:
                return BrickColor.S_BRICK_COLOR;

            case Brick.Z_BRICK_ID:
                return BrickColor.Z_BRICK_COLOR;

        }
        return new Color(0, 0, 0, 0);
    }


    public static void drawBrickOnCanvas(Canvas canvas, int brickID, Skin skin){
        if (canvas == null || brickID == -1 || skin == null) return;
        canvas.getGraphicsContext2D().clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        int[][] brickMatrix;
        brickMatrix = Brick.createBrickByID(brickID).getMatrixShapes().get(0);

        int width = 0;
        int height = 0;
        int widthStart = -1;
        int heightStart = -1;

        //Realizing width
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (brickMatrix[j][i] != 0) {
                    if (widthStart < 0) widthStart = i;
                    width++;
                    break;
                }
            }
        }
        //Realizing height
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (brickMatrix[i][j] != 0) {
                    if (heightStart < 0) heightStart = i;
                    height++;
                    break;
                }
            }
        }

        double squareWidth = canvas.getWidth() / 4;
        double squareHeight = canvas.getHeight() / 4;

        double offsetX = 0;
        double offsetY = 0;

        if(height == 1) offsetY = (canvas.getHeight() - canvas.getHeight()/4) / 2;
        else if (height == 2) offsetY = canvas.getHeight()/4;
        else if (height == 3) offsetY = canvas.getHeight()/8;

        if(width == 1) offsetX = (canvas.getWidth() - canvas.getWidth()/4) / 2;
        else if (width == 2) offsetX = canvas.getWidth()/4;
        else if (width == 3) offsetX = canvas.getWidth()/8;

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (brickMatrix[i + heightStart][j + widthStart] != 0) {
                    skin.drawSquareOnCanvas(j, i, squareWidth, squareHeight, getColorByID(brickMatrix[i + heightStart][j + widthStart]), canvas, offsetX, offsetY);
                }

            }
        }
    }
}
