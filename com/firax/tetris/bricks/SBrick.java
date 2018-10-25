package com.firax.tetris.bricks;

import com.firax.tetris.Variables;

import java.util.ArrayList;
import java.util.List;

public class SBrick extends Brick {

    private List<int[][]> brickMatrix = new ArrayList<>();

    public SBrick() {
        super(S_BRICK_ID);
        brickMatrix.add(new int[][]{
                {0, 0, 0, 0},
                {0, 6, 6, 0},
                {6, 6, 0, 0},
                {0, 0, 0, 0}
        });
        brickMatrix.add(new int[][]{
                {6, 0, 0, 0},
                {6, 6, 0, 0},
                {0, 6, 0, 0},
                {0, 0, 0, 0}
        });
    }

    @Override
    public List<int[][]> getMatrixShapes() {
        return brickMatrix;
    }
}
