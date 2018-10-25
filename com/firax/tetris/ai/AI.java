package com.firax.tetris.ai;

import com.firax.tetris.GameBoard;
import com.firax.tetris.Point;
import com.firax.tetris.bricks.Brick;

import java.util.ArrayList;
import java.util.List;

public class AI {

    private final static int BRICK_CONSTANT = 10000; //BRICK VALUE WHEN ADDED INTO MATRIX
    private final static int ROTATION_DELAY = 20;
    private final static int MOVE_DELAY = 20;
    private final static double DELAY_SCALE = 0.25;

    private GameBoard game;
    private Thread mainThread;

    private int width;
    private int height;

    private boolean playing;

    public AI(GameBoard game, int width, int height) {
        this.game = game;
        this.width = width;
        this.height = height;
        game.resetGame();
        setupThread();
    }

    public void play() {
        playing = true;
        setupThread();
        game.setAnimations(false);
        game.setFallSpeed(-1);
        game.setPreview(false);
        mainThread.start();
    }

    public void pause() {
        game.setAnimations(true);
        game.setFallSpeed(GameBoard.DEFAULT_FALL_SPEED);
        game.setPreview(true);
        playing = false;
    }

    public boolean isPlaying() {
        return playing;
    }

    private int countHolesInColumn(int column, int lowestY, int[][] matrix) {
        int sum = 0;

        for (int i = lowestY + 1; i < height; i++) {
            if (matrix[i][column] == 0) sum++;
            else break;
        }
        return sum;
    }

    private int countRowBlocks(int row, int[][] matrix) {
        int sum = 0;
        for (int i = 0; i < matrix[0].length; i++) {
            if (matrix[row][i] > 0) sum++;
        }
        return sum;
    }

    //squares on bottom has higher value then squares on top
    private double getSquareValue(int y) {
        return height - (height - y);
    }

    private ScoreAI getBlockScore(int matrix[][], int x, int y) {
        ScoreAI score = new ScoreAI();
        int width = matrix[0].length;

        //LEFT
        if (x - 1 < 0) score.wallTouch++;
        else if (matrix[y][x - 1] > 0) score.blockTouch++;

        //RIGHT
        if (x + 1 >= width) score.wallTouch++;
        else if (matrix[y][x + 1] > 0) score.blockTouch++;

        //BOTTOM
        if (y + 1 >= matrix.length) score.groundTouch++;
        else if (matrix[y + 1][x] > 0) score.blockTouch++;

        return score;
    }

    private int[][] getSmallerShape(int[][] defaultShape) {
        int width = 0;
        int height = 0;
        int widthStart = -1;
        int heightStart = -1;

        //Realizing width
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (defaultShape[j][i] != 0) {
                    if (widthStart < 0) widthStart = i;
                    width++;
                    break;
                }
            }
        }
        //Realizing height
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (defaultShape[i][j] != 0) {
                    if (heightStart < 0) heightStart = i;
                    height++;
                    break;
                }
            }
        }

        int[][] shape = new int[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                shape[i][j] = defaultShape[i + heightStart][j + widthStart];
                if (shape[i][j] > 0) shape[i][j] = BRICK_CONSTANT;
            }
        }
        return shape;
    }

    private ScoreAI calculateBestMove() {
        int[][] matrix = game.getGameMatrixCopy();
        Brick activeBrick = Brick.createBrickByID(game.getActiveBrickID());

        int[] highestBrick = new int[width];
        for (int i = 0; i < width; i++) highestBrick[i] = height;

        //FINDING HIGHEST BRICK IN EACH COLUMN
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (matrix[j][i] > 0) {
                    highestBrick[i] = j;
                    break;
                }
            }
        }

        int matrixShapes = activeBrick.getMatrixShapes().size();

        ScoreAI[][] values = new ScoreAI[matrixShapes][width];
        for (int i = 0; i < matrixShapes; i++) for (int j = 0; j < width; j++) values[i][j] = new ScoreAI();

        //FOR EACH BRICK SHAPE
        for (int shapeCounter = 0; shapeCounter < matrixShapes; shapeCounter++) {
            int defaultShape[][] = activeBrick.getMatrixShapes().get(shapeCounter);

            //GET THE SMALLEST POSSIBLE SHAPE
            int[][] shape = getSmallerShape(defaultShape);
            int shapeHeight = shape.length;
            int shapeWidth = shape[0].length;


            //FOR EACH POSSIBLE X
            for (int i = 0; i <= width - shapeWidth; i++) {

                //FOR EACH IMPOSSIBLE X
                for (int x = width - shapeWidth + 1; x < width; x++) {
                    values[shapeCounter][x].value = -1000; //IMPOSSIBRU TO DO PLACE IT THERE
                }

                int shapeSquareCount = 0;
                for (int j = 0; j < shapeHeight; j++) {
                    for (int k = 0; k < shapeWidth; k++) {
                        if (shape[j][k] > 0) shapeSquareCount++;
                    }
                }

                //CHECKING SPACE IN MATRIX
                List<Point> points = new ArrayList<>();
                boolean isOk = false;
                int offset = -shapeHeight + 1;

                int highestPosY = i;
                for(int y = 0; y < shapeWidth; y++){
                    if(highestBrick[y+i] < highestBrick[highestPosY]) {
                        highestPosY = y + i;
                    }
                }

                while (!isOk) {
                    check:
                    for (int j = i; j < i + shapeWidth; j++) {
                        for (int k = highestBrick[highestPosY] - shapeHeight - offset; k < highestBrick[highestPosY] - offset; k++) {
                            if(k < 0) { game.resetGame(); return new ScoreAI(); }
                                if (k >= height || (matrix[k][j] > 0 && shape[k-(highestBrick[highestPosY] - shapeHeight - offset)][j- i] > 0)) {
                                points.clear();
                                offset++;
                                break check;
                            } else {
                                if(shape[k-(highestBrick[highestPosY] - shapeHeight - offset)][j- i] > 0)
                                    points.add(new Point(j, k));
                                if (points.size() == shapeSquareCount) isOk = true;
                            }
                        }
                    }
                }



                //ADDING SHAPE INTO MATRIX
                for (Point point : points) {
                    matrix[point.y][point.x] = BRICK_CONSTANT;
                }

                //COUNTING HOLES
                for (int j = i; j < i + shapeWidth; j++) {
                    int lowestY = 0;
                    for (Point point : points) {
                        if (point.x == j && point.y > lowestY) lowestY = point.y;
                    }
                    values[shapeCounter][i].holes += countHolesInColumn(j, lowestY, matrix);
                }

                for (Point p : points)
                    values[shapeCounter][i].value = getSquareValue(p.y);

                //REMOVING BRICKS FROM MATRIX
                for (Point point : points) {
                    matrix[point.y][point.x] = 0;
                }

                //ADDING INFORMATION OF EACH BLOCK
                for (Point p : points) {
                    ScoreAI partScore = getBlockScore(matrix, p.x, p.y);
                    values[shapeCounter][i].blockTouch += partScore.blockTouch;
                    values[shapeCounter][i].groundTouch += partScore.groundTouch;
                    values[shapeCounter][i].wallTouch += partScore.wallTouch;
                }
            }
        }

        //FINDING BEST MOVE FROM ALL POSSIBLE MOVES
        ScoreAI bestScore = new ScoreAI();
        for (int i = 0; i < matrixShapes; i++) {
            for (int j = 0; j < width; j++) {
                if (getScoreAIValue(values[i][j]) > getScoreAIValue(bestScore)) {
                    bestScore = values[i][j];
                    bestScore.rotation = i;
                    bestScore.posX = j;
                }
            }
        }
        return bestScore;
    }

    //Calculate score of AI move with specific values
    private double getScoreAIValue(ScoreAI score) {
        double value = score.value;
        value -= score.holes * 8.8;
        value += score.blockTouch * 4.8;
        value += score.wallTouch * 3.2;
        value += score.groundTouch * 6.5;

        return value;
    }

    //Find most left X of active brick
    private int getMostLeftX() {
        int mostLeft = width - 1;
        int[][] matrix = game.getGameMatrixCopy();
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (matrix[i][j] == -1)
                    if (mostLeft > j) mostLeft = j;
            }
        }
        return mostLeft;
    }

    private void tick() throws InterruptedException {
        ScoreAI move = calculateBestMove(); //Calculate best move to make

        //If move is bad (has low value), hold brick and calculate best move with new one
        if (getScoreAIValue(move) - move.value < 20) {
            game.holdBrick();
            move = calculateBestMove();
        }

        //ROTATING BRICK
        int rotation = 0;
        while (rotation != move.rotation) {
            game.rotateBrick();
            rotation++;
            Thread.sleep((int) (ROTATION_DELAY * DELAY_SCALE));
        }
        Thread.sleep((int) (50 * DELAY_SCALE));

        //MOVING BRICK TO SIDE
        int xPos = getMostLeftX(); //Most left x position of active block
        while (xPos > move.posX) {
            game.moveBrickLeft();
            xPos--;
            Thread.sleep((int) (MOVE_DELAY * DELAY_SCALE));
        }
        while (xPos < move.posX) {
            game.moveBrickRight();
            xPos++;
            Thread.sleep((int) (MOVE_DELAY * DELAY_SCALE));
        }
        Thread.sleep((int) (100 * DELAY_SCALE));
        game.makeBrickFall();
    }

    private void setupThread() {
        mainThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (playing) {
                    try {
                        Thread.sleep((int) (50 * DELAY_SCALE));
                        if (!game.isAnimationPlaying())
                            tick();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}


