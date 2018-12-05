package com.firax.tetris;

import com.firax.tetris.bricks.Brick;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GameMatrix {

    private final static int ACTIVEBRICK = -1;

    private boolean isMatrixFull;
    private int matrixHeight;
    private int matrixWidth;
    private int[][] matrix;
    private Brick activeBrick;

    public GameMatrix(int width, int height) {
        matrixHeight = height;
        matrixWidth = width;
        matrix = new int[height][width];
    }

    public void resetMatrix() {
        for (int[] row : matrix) {
            Arrays.fill(row, 0);
        }
        isMatrixFull = false;
    }

    public void addNewBrick(Brick brick) {
        activeBrick = brick;
        addBrickToMatrix();
    }

    public void replaceBrick(Brick newBrick) {
        changeActiveBrickValue(0);
        addNewBrick(newBrick);
    }

    public void moveBrickDown() {
        List<Point> points = getActiveBrickPoints();

        //Checking if there is space under brick
        for (Point point : points) {
            if (point.y >= matrixHeight - 1 || (matrix[point.y + 1][point.x] != 0 && matrix[point.y + 1][point.x] != -1)) {
                changeActiveBrickValue(activeBrick.getID());
                activeBrick.setOnGround();
                return;
            }
        }
        editMatrix(points, 0);

        for (Point point : points) {
            point.y += 1;
        }

        editMatrix(points, ACTIVEBRICK);
    }

    public void moveBrickLeft() {
        moveBrickSide(-1);
    }

    public void moveBrickRight() {
        moveBrickSide(1);
    }

    public Brick getActiveBrick() {
        return activeBrick;
    }

    public List<Point> getFinalPosition() {
        List<Point> points = getActiveBrickPoints();
        for (int i = 0; i < matrixHeight; i++) {
            for (Point point : points) {
                if (point.y >= matrixHeight - 1 || ((matrix[point.y + 1][point.x] != 0 && matrix[point.y + 1][point.x] != -1))) {
                    return points;
                }
            }
            for (Point point : points) {
                point.y += 1;
            }
        }

        return points;
    }

    public synchronized void moveAllDownFromRow(int row) {
      editMatrix(null, row);
    }

    public void clearRow(int row) {
        List<Point> points = new ArrayList<>();
        for (int i = 0; i < matrixWidth; i++) {
            points.add(new Point(i, row));
        }
        editMatrix(points, 0);
    }

    public List<Integer> getFullRows() {
        List<Integer> rows = new ArrayList<>();
        int counter;

        for (int i = 0; i < matrixHeight; i++) {
            counter = 0;
            for (int j = 0; j < matrixWidth; j++) {
                if (matrix[i][j] != 0 && matrix[i][j] != -1) {
                    counter++;
                    if (counter == matrixWidth) rows.add(i);
                }
            }
        }
        return rows;
    }

    private void addBrickToMatrix() {
        if (isSpaceOnTop()) {
            List<Point> points = new ArrayList<>();
            int xStart = matrixWidth / 2 - 1;

            for (int i = 0; i < 2; i++) {
                for (int j = 0; j < 4; j++) {
                    if (activeBrick.getMatrixShapes().get(0)[i + 1][j] != 0)
                        points.add(new Point(j+xStart, i));
                }
            }
            editMatrix(points, ACTIVEBRICK);
        } else {
            System.out.println("FULL");
            isMatrixFull = true;
        }
    }

    private boolean isSpaceOnTop() {
        for (int j = 0; j < 2; j++)
            for (int i = 0; i < matrixWidth; i++) {
                if (matrix[j][i] != 0) return false;
            }
        return true;
    }

    private void changeActiveBrickValue(int newBrickValue) {
        List<Point> points = new ArrayList<>();
        for (int i = 0; i < matrixHeight; i++) {
            for (int j = 0; j < matrixWidth; j++) {
                if (matrix[i][j] == ACTIVEBRICK)
                    points.add(new Point(j, i));
            }
        }
        editMatrix(points, newBrickValue);
    }

    //Only this method can touch matrix directly.
    //It's prevention from conflicting edit
    private synchronized void editMatrix(List<Point> points, int value){
        //Special occasion when list is null
        //Moving matrix down from value (row index)
        if(points == null){
            for (int i = value - 1; i >= 0; i--) {
                for (int j = 0; j < matrixWidth; j++) {
                    matrix[i + 1][j] = matrix[i][j];
                }
            }
        } else {
            for (Point point : points)
                matrix[point.y][point.x] = value;
        }
    }

    private void moveBrickSide(int side) {
        List<Point> points = getActiveBrickPoints();

        if (isSpaceOnSide(points, side)) {
            editMatrix(points, 0);

            for (Point point : points) {
                point.x += side;
            }

            editMatrix(points, ACTIVEBRICK);
        }
    }

    private List<Point> getActiveBrickPoints() {
        List<Point> points = new ArrayList<>();
        for (int i = 0; i < matrixHeight; i++) {
            for (int j = 0; j < matrixWidth; j++) {
                if (matrix[i][j] == ACTIVEBRICK) {
                    points.add(new Point(j, i));
                    if (points.size() == 4) break;
                }
            }
        }
        return points;
    }

    private boolean isSpaceOnSide(List<Point> points, int side) {
        for (Point point : points) {
            if (point.x + side > matrixWidth - 1 || point.x + side < 0)
                return false;

            if (matrix[point.y][point.x + side] != ACTIVEBRICK && matrix[point.y][point.x + side] != 0)
                return false;
        }
        return true;
    }

    public int getValue(int x, int y) {
        return matrix[y][x];
    }

    public boolean isReadyForNewBrick() {
        return (activeBrick.isOnGround() && !isMatrixFull);
    }

    public boolean isMatrixFull() {
        return isMatrixFull;
    }

    public int getHeight() {
        return matrixHeight;
    }

    public int getWidth() {
        return matrixWidth;
    }

    public void rotateBrick() {
        int previousRotation = activeBrick.getCurrentRotation();
        int newRotation = getNewBrickRotation();

        Point distance = new Point(0, 0);

        //Calculate distance between first block and left corner
        start:
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 4; j++) {
                if (activeBrick.getMatrixShapes().get(previousRotation)[i][j] != 0) {
                    distance.y = i;
                    distance.x = j;
                    break start;
                }
            }
        }

        List<Point> points = getActiveBrickPoints();
        if (points.size() <= 0) return;

        //Getting grid position of brick's corner
        int startX = points.get(0).x - distance.x;
        int startY = points.get(0).y - distance.y;

        //Checking if there is no collision in matrix
        List<Point> newPoints = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (activeBrick.getMatrixShapes().get(newRotation)[i][j] != 0) {
                    //Checking walls on top, left and right
                    //If there is collision with wall while rotating, trying to move block on opposite direction
                    if (startY + i < 0) {
                        moveBrickDown();
                        rotateBrick();
                        return;
                    } else if (startX + j < 0) {
                        List<Point> location = getActiveBrickPoints();
                        if (isSpaceOnSide(location, 1)) {
                            moveBrickRight();
                            rotateBrick();
                        }
                        return;
                    } else if (startX + j > matrixWidth - 1) {
                        List<Point> location = getActiveBrickPoints();
                        if (isSpaceOnSide(location, -1)) {
                            moveBrickLeft();
                            rotateBrick();
                        }
                        return;
                    }

                    if (startY + i > matrixHeight - 1) return;
                    else if (matrix[startY + i][startX + j] != 0 && matrix[startY + i][startX + j] != ACTIVEBRICK)
                        return;
                    else newPoints.add(new Point(startX + j, startY + i));

                }
            }
        } // No collision detected
        activeBrick.setCurrentRotation(newRotation);
        changeActiveBrickValue(0);
        editMatrix(newPoints, ACTIVEBRICK);

    }

    private int getNewBrickRotation() {
        int currentRotation = activeBrick.getCurrentRotation() + 1;
        int maxRotations = activeBrick.getMatrixShapes().size() - 1;

        if (currentRotation > maxRotations) currentRotation = 0;

        return currentRotation;
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();

        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 10; j++) {
                output.append(matrix[i][j]);
            }
            output.append("\n");
        }

        return output.toString();
    }

}
