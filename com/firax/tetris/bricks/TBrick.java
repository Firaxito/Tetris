package com.firax.tetris.bricks;

import com.firax.tetris.Variables;

import java.util.ArrayList;
import java.util.List;

public class TBrick extends Brick {

    private List<int[][]> brickMatrix = new ArrayList<>();

    public TBrick() {
        super(T_BRICK_ID);
        brickMatrix.add(new int[][]{
                {0, 0, 0, 0},
                {4, 4, 4, 0},
                {0, 4, 0, 0},
                {0, 0, 0, 0}
        });
        brickMatrix.add(new int[][]{
                {0, 4, 0, 0},
                {4, 4, 0, 0},
                {0, 4, 0, 0},
                {0, 0, 0, 0}
        });
        brickMatrix.add(new int[][]{
                {0, 4, 0, 0},
                {4, 4, 4, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 0}
        });
        brickMatrix.add(new int[][]{
                {0, 4, 0, 0},
                {0, 4, 4, 0},
                {0, 4, 0, 0},
                {0, 0, 0, 0}
        });


    }

    @Override
    public List<int[][]> getMatrixShapes() {
        return brickMatrix;
    }
}
