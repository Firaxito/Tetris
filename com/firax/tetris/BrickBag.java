package com.firax.tetris;

import com.firax.tetris.bricks.Brick;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BrickBag {

    private final static int BRICKS_COUNT_MULTIPLIER = 2; //How many times will be bag filled with each brick
    private List<Integer> bricksID;

    public BrickBag() {
        bricksID = new ArrayList<>();
        refillBag();
    }

    public int getRemainingBrickCount(){
        return bricksID.size();
    }

    int getRandomBrick(){
        Random random = new Random();
        int selectedIndex = random.nextInt(bricksID.size());
        int returnValue = bricksID.get(selectedIndex);
        bricksID.remove(selectedIndex);
        return returnValue;
    }

    public void refillBag(){
        bricksID.clear();

        for(int i = 1; i <= Brick.TOTAL_BRICK_COUNT; i++){
            for(int j = 0; j < BRICKS_COUNT_MULTIPLIER; j++){
                bricksID.add(i);
            }
        }
    }

}
