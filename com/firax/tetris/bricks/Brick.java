package com.firax.tetris.bricks;

import com.firax.tetris.Variables;

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


}
