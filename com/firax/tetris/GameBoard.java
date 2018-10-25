package com.firax.tetris;

import com.firax.tetris.bricks.Brick;
import com.firax.tetris.bricks.BrickColor;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.List;


public class GameBoard {

    public final static int DEFAULT_FALL_SPEED = 200; // DEFAULT SPEED VALUE FOR BRICK

    private final static int ANIMATION_DURATION = 20; //DEFAULT ANIMATION DURATION

    private GameMatrix gameMatrix; //Matrix controller
    private AnchorPane root;
    private Label mStatistics;
    private BrickBag brickBag; //Random brick generator
    private Skin skin; //Skin for bricks
    private int onHoldBrickID; //ID of holding brick
    private int nextBrickID; // ID of next brick
    private int activeBrickID; // ID of active brick
    private int score;
    private int fallSpeed = DEFAULT_FALL_SPEED; //Actual brick fall speed
    private int counterToMoveDown; //On certain value, block will move down
    private boolean canHoldBrick;
    private boolean playing;
    private boolean isAnimationPlaying;
    private boolean isPreviewActive = true; //Showing where will brick fall
    private boolean isAnimationActive = true; //Enable animations

    private Thread mainThread; //Here is job done

    public GameBoard(int width, int height, AnchorPane root) {

        if (height < 8 || width < 8) {
            throw new IllegalArgumentException(
                    "\nBoard has to be at least 8x8");
        }

        gameMatrix = new GameMatrix(width, height);
        this.root = root;
        brickBag = new BrickBag();
        skin = new Skin(Skin.WOOD_SKIN);
        setupLayouts();
        setupMainThread();
        resetGame();
        reDrawAll();

    }

    public void startGame() {
        if (gameMatrix.getActiveBrick() == null)
            gameMatrix.addNewBrick(Brick.createBrickByID(activeBrickID));
        playing = true;
        start();
    }

    public void pauseGame(boolean value) {
        playing = value;
        if (playing) {
            mainThread.interrupt();
            start();
        }
    }

    private void start() {
        if(mainThread == null || !mainThread.isAlive()) {
            setupMainThread();
            mainThread.start();
        }
    }

    public void resetGame() {
        if (!isAnimationPlaying) {
            clearBoard();
            activeBrickID = generateRandomBrickID();
            nextBrickID = generateRandomBrickID();
            canHoldBrick = true;
            onHoldBrickID = -1;
            score = 0;
            Platform.runLater(() -> mStatistics.setText("LINES SENT: " + score));

            if (playing) {
                gameMatrix.addNewBrick(Brick.createBrickByID(activeBrickID));
                reDrawAll();
                if (!mainThread.isAlive()) startGame();
            }
        }
    }

    public void holdBrick() {
        if (canHoldBrick) {
            canHoldBrick = false;
            //if (Empty brick holder)
            if (onHoldBrickID == -1) {
                onHoldBrickID = activeBrickID;
                activeBrickID = nextBrickID;
                nextBrickID = generateRandomBrickID();
            } else {
                int holdBrickCacheID = onHoldBrickID;
                onHoldBrickID = activeBrickID;
                activeBrickID = holdBrickCacheID;
            }
            gameMatrix.replaceBrick(Brick.createBrickByID(activeBrickID));
            drawBrickOnCanvas(getCanvasByID(Variables.CANVAS_NEXT_BRICK_ID), getNextBrickID());
            drawBrickOnCanvas(getCanvasByID(Variables.CANVAS_HOLD_BRICK_ID), getOnHoldBrickID());

        } else {
            //TODO NOTIFY PLAYER
        }
    }

    //Instantly place a brick at the lowest point
    public void makeBrickFall() {
        if (playing && !isAnimationPlaying)
            while (!gameMatrix.isReadyForNewBrick() && !gameMatrix.isMatrixFull()) {
                gameMatrix.moveBrickDown();
            }
    }

    public void moveBrickLeft() {
        gameMatrix.moveBrickLeft();
    }

    public void moveBrickRight() {
        gameMatrix.moveBrickRight();
    }

    public void moveBrickDown() {
        gameMatrix.moveBrickDown();
    }

    public void rotateBrick() {
        gameMatrix.rotateBrick();
    }

    public void setSkin(int skin){
        if(!isAnimationPlaying) {
            this.skin.setSkinID(skin);
            reDrawAll();
        }
    }

    public int getActiveSkin(){
        return skin.getSelectedSkinID();
    }

    public void setPreview(boolean value) {
        isPreviewActive = value;
    }

    public void setFallSpeed(int fallSpeed){
        this.fallSpeed = fallSpeed;
    }

    private void animateGrayScaleEffect() {
        isAnimationPlaying = true;

        Canvas canvas = getCanvasByID(Variables.GAME_CANVAS_ID);
        double squareWidth = canvas.getWidth() / gameMatrix.getWidth();
        double squareHeight = canvas.getHeight() / gameMatrix.getHeight();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    for (int i = gameMatrix.getHeight() - 1; i >= 0; i--) {
                        for (int j = 0; j < gameMatrix.getWidth(); j++) {
                            if (gameMatrix.getValue(j, i) != 0) {
                                skin.drawSquareOnCanvas(j, i, squareWidth, squareHeight, Color.DARKGRAY, canvas, 0, 0);
                                Thread.sleep(5);
                            }
                        }
                        Thread.sleep(40);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    isAnimationPlaying = false;
                }
                isAnimationPlaying = false;
            }
        }).start();
    }

    //Input is list of rows, which will be animated and taken off from matrix
    private void animateRowsFall(final List<Integer> rows) {
        isAnimationPlaying = true;

        Canvas canvas = getCanvasByID(Variables.GAME_CANVAS_ID);
        double squareWidth = canvas.getWidth() / gameMatrix.getWidth();
        double squareHeight = canvas.getHeight() / gameMatrix.getHeight();
        double stepY = squareHeight / ANIMATION_DURATION;

        Thread fallAnimation = new Thread(new Runnable() {

            int counter = 0;
            int multiplier = 0;

            @Override
            public void run() {
                while (counter != ANIMATION_DURATION) {
                    canvas.getGraphicsContext2D().clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

                    //Starting with bottom row
                    for (int i = gameMatrix.getHeight() - 1; i >= 0; i--) {

                        multiplier = 0;
                        //Counting how fast will blocks fall (multiplier)
                        for (int k = rows.size() - 1; k >= 0; k--) {
                            if (rows.get(k) > i) {
                                if (k - 1 >= 0) {
                                    if (rows.get(k) - rows.get(k - 1) != 1)
                                        multiplier = rows.size() - k;
                                } else multiplier = rows.size() - k;
                            }
                        }

                        //Drawing row
                        for (int j = 0; j < gameMatrix.getWidth(); j++) {
                            if (gameMatrix.getValue(j, i) != 0) {
                                skin.drawSquareOnCanvas(j, i, squareWidth, squareHeight, getColorByID(gameMatrix.getValue(j, i)),
                                        canvas, 0, (stepY * multiplier) * counter);
                            }
                        }
                    }

                    //Animation is finished
                    if (++counter == ANIMATION_DURATION) {
                        for (int row : rows) {
                            gameMatrix.moveAllDownFromRow(row);
                        }
                        isAnimationPlaying = false;
                        start();
                    }
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        for (int row : rows) {
                            gameMatrix.moveAllDownFromRow(row);
                        }
                        isAnimationPlaying = false;
                        start();
                        e.printStackTrace();
                    }
                }
            }
        });

        Thread rowSideAnimation = new Thread(new Runnable() {
            int counter = 0;
            double stepX = (gameMatrix.getWidth() * squareWidth) / ANIMATION_DURATION;
            double randomStepX = stepX;

            @Override
            public void run() {
                while (counter != ANIMATION_DURATION) {
                    for (int row : rows) {

                        //Every row will travel to opposite direction
                        if (row % 2 == 0) randomStepX = -stepX;
                        else randomStepX = stepX;

                        canvas.getGraphicsContext2D().clearRect(0, row * squareHeight, canvas.getWidth(), squareHeight);
                        for (int i = 0; i < gameMatrix.getWidth(); i++) {
                            skin.drawSquareOnCanvas(i, row, squareWidth, squareHeight, getColorByID(gameMatrix.getValue(i, row)), canvas, counter * randomStepX, 0);
                        }
                    }

                    if (++counter == ANIMATION_DURATION) {
                        for (int row : rows) gameMatrix.clearRow(row);
                        fallAnimation.start();
                    }
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        fallAnimation.start();
                    }
                }
            }
        });

        rowSideAnimation.start();
    }

    private void finishGame() {
        animateGrayScaleEffect();
    }

    private void addNewBrick() {
        activeBrickID = nextBrickID;
        nextBrickID = generateRandomBrickID();
        gameMatrix.addNewBrick(Brick.createBrickByID(activeBrickID));
        canHoldBrick = true;
    }

    private boolean isRowFull() {
        return gameMatrix.getFullRows().size() > 0;
    }

    private int generateRandomBrickID() {
        if (brickBag.getRemainingBrickCount() == 0) brickBag.refillBag();
        return brickBag.getRandomBrick();
    }

    private Color getColorByID(int ID) {
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

            case -1:
                return getColorByID(activeBrickID);

        }
        return new Color(0, 0, 0, 0);
    }

    private void reDrawAll() {
        drawBackground(getCanvasByID(Variables.CANVAS_BACKGROUND_ID));
        drawMatrix(getCanvasByID(Variables.CANVAS_BACKGROUND_ID));
        drawBrickOnCanvas(getCanvasByID(Variables.CANVAS_NEXT_BRICK_ID), getNextBrickID());
        drawBrickOnCanvas(getCanvasByID(Variables.CANVAS_HOLD_BRICK_ID), getOnHoldBrickID());
    }

    private void drawBackground(Canvas canvas) {
        double squareWidth = canvas.getWidth() / gameMatrix.getWidth();
        double squareHeight = canvas.getHeight() / gameMatrix.getHeight();

        canvas.getGraphicsContext2D().setFill(new Color(0.2, 0.2, 0.2, 1));
        canvas.getGraphicsContext2D().setFill(new Color(0.35, 0.35, 0.35, 1));

        for (int i = 0; i < gameMatrix.getHeight(); i++) {
            for (int j = 0; j < gameMatrix.getWidth(); j++) {
                if (((i % 2) + j) % 2 == 0) canvas.getGraphicsContext2D().setFill(new Color(0.2, 0.2, 0.2, 1));
                else canvas.getGraphicsContext2D().setFill(new Color(0.15, 0.15, 0.15, 1));
                canvas.getGraphicsContext2D().fillRect(squareWidth * j, squareHeight * i, squareWidth, squareHeight);
            }
        }
    }

    private void drawMatrix(Canvas canvas) {
        double squareWidth = canvas.getWidth() / gameMatrix.getWidth();
        double squareHeight = canvas.getHeight() / gameMatrix.getHeight();

        canvas.getGraphicsContext2D().setStroke(Color.BLACK);

        for (int i = 0; i < gameMatrix.getHeight(); i++) {
            for (int j = 0; j < gameMatrix.getWidth(); j++) {
                canvas.getGraphicsContext2D().strokeRect(squareWidth * j, squareHeight * i, squareWidth, squareHeight);
            }
        }
    }

    private void drawBoard(Canvas canvas) {
        canvas.getGraphicsContext2D().clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        double squareWidth = canvas.getWidth() / gameMatrix.getWidth();
        double squareHeight = canvas.getHeight() / gameMatrix.getHeight();

        for (int i = 0; i < gameMatrix.getHeight(); i++) {
            for (int j = 0; j < gameMatrix.getWidth(); j++) {
                if (gameMatrix.getValue(j, i) != 0) {
                    skin.drawSquareOnCanvas(j, i, squareWidth, squareHeight, getColorByID(gameMatrix.getValue(j, i)), canvas, 0, 0);
                }
            }
        }

        if (isPreviewActive) {
            List<Point> position = gameMatrix.getFinalPosition();

            for (Point point : position) {
                if (gameMatrix.getValue(point.x, point.y) != -1) {
                    skin.drawSquareOnCanvas(point.x, point.y, squareWidth, squareHeight, Color.TRANSPARENT, canvas, 0, 0);
                }
            }
        }

    }

    private void drawBrickOnCanvas(Canvas canvas, int brickID) {
        canvas.getGraphicsContext2D().clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        int[][] brickMatrix;
        if (brickID == -1) return;
        else brickMatrix = Brick.createBrickByID(brickID).getMatrixShapes().get(0);

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

        int higher = height > width ? height : width;
        double offset;
        if (higher == 4) offset = 0;
        else if (higher == 3) offset = canvas.getWidth()/16;
        else if (higher == 2) offset = canvas.getWidth()/4.5;
        else offset = 0;

        double squareWidth = canvas.getWidth() / higher - offset;
        double squareHeight = canvas.getHeight() / higher - offset;

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (brickMatrix[i + heightStart][j + widthStart] != 0) {
                    skin.drawSquareOnCanvas(j , i, squareWidth, squareHeight, getColorByID(brickMatrix[i + heightStart][j + widthStart]), canvas, offset, 0);
                }

            }
        }

    }

    public int[][] getGameMatrixCopy() {
        int[][] matrixCopy = new int[gameMatrix.getHeight()][gameMatrix.getWidth()];
        for (int i = 0; i < gameMatrix.getHeight(); i++) {
            for (int j = 0; j < gameMatrix.getWidth(); j++) {
                matrixCopy[i][j] = gameMatrix.getValue(j, i);
            }
        }
        return matrixCopy;
    }

    private void clearBoard() {
        gameMatrix.resetMatrix();
    }

    public int getActiveBrickID() {
        return activeBrickID;
    }

    public int getOnHoldBrickID() {
        return onHoldBrickID;
    }

    public int getNextBrickID() {
        return nextBrickID;
    }

    public boolean isGamePaused(){
        return playing;
    }

    private Canvas getCanvasByID(String ID) {
        //This is search for fxml file canvas
        if (root.lookup(ID) != null) return ((Canvas) root.lookup(ID));

        //This is search for programmatically created canvas
        for (int i = 0; i < root.getChildren().size(); i++) {
            if (root.getChildren().get(i).getId() != null && root.getChildren().get(i).getId().equals(ID))
                return ((Canvas) root.getChildren().get(i));
        }

        return new Canvas();
    }


    private void setupLayouts(){
        setupCanvases();
        mStatistics = new Label("LINES SENT:  0");
        mStatistics.setFont(new Font("Verdana", 12));
        mStatistics.setPrefWidth(150);
        mStatistics.setAlignment(Pos.CENTER);
        mStatistics.setTextFill(Color.WHITE);
        mStatistics.setLayoutY(575);
        mStatistics.setLayoutX(300);
        root.getChildren().add(mStatistics);
    }

    //This method is protection from interrupted or non-existent fxml file
    //If any canvas will be missing on fxml, this method will automatically
    //create canvas and set default values to it
    private void setupCanvases() {
        Canvas backgroundCanvas, gameCanvas, gridCanvas;
        Canvas nextBrickCanvas, holdBrickCanvas;

        if (getCanvasByID(Variables.CANVAS_BACKGROUND_ID).getId() == null) {
            backgroundCanvas = new Canvas(300, 600);
            backgroundCanvas.setId(Variables.CANVAS_BACKGROUND_ID);
        } else backgroundCanvas = getCanvasByID(Variables.CANVAS_BACKGROUND_ID);

        if (getCanvasByID(Variables.GAME_CANVAS_ID).getId() == null) {
            gameCanvas = new Canvas(300, 600);
            gameCanvas.setId(Variables.GAME_CANVAS_ID);
        } else gameCanvas = getCanvasByID(Variables.GAME_CANVAS_ID);

        if (getCanvasByID(Variables.CANVAS_NEXT_BRICK_ID).getId() == null) {
            nextBrickCanvas = new Canvas(75, 75);
            nextBrickCanvas.setLayoutX(335);
            nextBrickCanvas.setLayoutY(100);
            nextBrickCanvas.setId(Variables.CANVAS_NEXT_BRICK_ID);
        } else nextBrickCanvas = getCanvasByID(Variables.CANVAS_NEXT_BRICK_ID);

        if (getCanvasByID(Variables.CANVAS_HOLD_BRICK_ID).getId() == null) {
            holdBrickCanvas = new Canvas(75, 75);
            holdBrickCanvas.setLayoutX(335);
            holdBrickCanvas.setLayoutY(400);
            holdBrickCanvas.setId(Variables.CANVAS_HOLD_BRICK_ID);
        } else holdBrickCanvas = getCanvasByID(Variables.CANVAS_HOLD_BRICK_ID);

        root.getChildren().removeAll(backgroundCanvas, gameCanvas, nextBrickCanvas, holdBrickCanvas);
        root.getChildren().add(0, backgroundCanvas);
        root.getChildren().add(1, gameCanvas);
        root.getChildren().add(2, nextBrickCanvas);
        root.getChildren().add(3, holdBrickCanvas);

    }

    private void ticker(){
        if (!isAnimationPlaying) {
            if (gameMatrix.isReadyForNewBrick()) {
                if (isRowFull()) {
                    score += gameMatrix.getFullRows().size();
                    Platform.runLater(() -> mStatistics.setText("LINES SENT: " + score));
                    if (isAnimationActive) {
                        animateRowsFall(gameMatrix.getFullRows());
                    } else {
                        for (int row : gameMatrix.getFullRows()) {
                            gameMatrix.clearRow(row);
                            gameMatrix.moveAllDownFromRow(row);
                        }
                    }
                    return;
                }
                addNewBrick();
                drawBrickOnCanvas(getCanvasByID(Variables.CANVAS_NEXT_BRICK_ID), getNextBrickID());
            }
            //RUNNING EXPENSIVE DRAWING PROCESS
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    drawBoard(getCanvasByID(Variables.GAME_CANVAS_ID));
                }
            });
            counterToMoveDown++;
            if (playing) {
                if (counterToMoveDown > fallSpeed && fallSpeed > 0) {
                    counterToMoveDown = 0;
                    gameMatrix.moveBrickDown();
                }
            }
        }
    }

    private void setupMainThread() {
        mainThread = new Thread(() -> {
            while (playing && !gameMatrix.isMatrixFull()) {
                try {
                    Thread.sleep(5);
                    ticker();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (gameMatrix.isMatrixFull())
                finishGame();
        });
    }

    public boolean isGameEnd() {
        return gameMatrix.isMatrixFull();
    }

    public boolean isAnimationPlaying(){
        return isAnimationPlaying;
    }

    public void setAnimations(boolean value) {
        isAnimationActive = value;
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();

        for (int i = 0; i < gameMatrix.getHeight(); i++) {
            for (int j = 0; j < gameMatrix.getWidth(); j++) {
                output.append(gameMatrix.getValue(j, i));
            }
            output.append("\n");
        }

        return output.toString();
    }


}
