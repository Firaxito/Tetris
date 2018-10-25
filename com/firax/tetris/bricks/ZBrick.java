package com.firax.tetris.bricks;

import com.firax.tetris.Variables;

import java.util.ArrayList;
import java.util.List;

public class ZBrick extends Brick {

    private List<int[][]> brickMatrix = new ArrayList<>();

    public ZBrick() {
        super(Z_BRICK_ID);
        brickMatrix.add(new int[][]{
                {0, 0, 0, 0},
                {7, 7, 0, 0},
                {0, 7, 7, 0},
                {0, 0, 0, 0}
        });
        brickMatrix.add(new int[][]{
                {0, 7, 0, 0},
                {7, 7, 0, 0},
                {7, 0, 0, 0},
                {0, 0, 0, 0}
        });

    }

    @Override
    public List<int[][]> getMatrixShapes() {
        return brickMatrix;
    }
}
