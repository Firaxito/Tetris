package com.firax.tetris.bricks;

import java.util.ArrayList;
import java.util.List;

public class OBrick extends Brick {

    private List<int[][]> brickMatrix = new ArrayList<>();

    public OBrick() {
        super(O_BRICK_ID);
        brickMatrix.add(new int[][]{
                {0, 0, 0, 0},
                {0, 5, 5, 0},
                {0, 5, 5, 0},
                {0, 0, 0, 0}
        });
    }

    @Override
    public List<int[][]> getMatrixShapes() {
        return brickMatrix;
    }
}
