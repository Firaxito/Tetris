package com.firax.tetris.bricks;
;

import java.util.ArrayList;
import java.util.List;

public class IBrick extends Brick {

    private List<int[][]> brickMatrix = new ArrayList<>();

    public IBrick() {
        super(I_BRICK_ID);
        brickMatrix.add(new int[][]{
                {0, 0, 0, 0},
                {1, 1, 1, 1},
                {0, 0, 0, 0},
                {0, 0, 0, 0}
        });
        brickMatrix.add(new int[][]{
                {0, 1, 0, 0},
                {0, 1, 0, 0},
                {0, 1, 0, 0},
                {0, 1, 0, 0}
        });
    }

    @Override
    public List<int[][]> getMatrixShapes() {
        return brickMatrix;
    }

}
