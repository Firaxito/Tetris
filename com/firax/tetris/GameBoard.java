package com.firax.tetris;

import com.firax.tetris.bricks.Brick;
import com.firax.tetris.bricks.BrickColor;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.util.List;


public class GameBoard {

    public final static int DEFAULT_FALL_SPEED = 100; // DEFAULT FALL VALUE FOR BRICK
    private final static int ANIMATION_DURATION = 20; //DEFAULT ANIMATION DURATION

    private GameMatrix gameMatrix; //Matrix controller
    private Timeline mainTimeline; //Here is job done (ticker)
    private AnchorPane root;
    private Label mStatistics;

    private BrickBag brickBag; //Random brick generator
    private Skin skin; //Skin for bricks

    //layout values
    private int sizeX, sizeY;
    private int paddingX, paddingY;

    private int onHoldBrickID; //ID of holding brick
    private int nextBrickID; // ID of next brick
    private int activeBrickID; // ID of active brick
    private int score;
    private int fallSpeed = DEFAULT_FALL_SPEED; //Actual brick fall speed
    private int counterToMoveDown; //Tick iterations counter... When this == fallSpeed -> Block will move down by 1 -> ( this = 0)

    private boolean canHoldBrick;
    private boolean playing;
    private boolean isAnimationPlaying;
    private boolean isPreviewActive = Settings.IS_PREVIEW_ACTIVE; //Showing where will brick fall
    private boolean isAnimationActive = Settings.IS_ANIMATION_ENABLED; //Enable animations
    private boolean isMenuOpened = true;

    public GameBoard(AnchorPane root, int blocksWidth, int blocksHeight, int sizeWidth, int sizeHeight, int paddingX, int paddingY) {

        if (blocksWidth < 8 || blocksHeight < 8) {
            throw new IllegalArgumentException(
                    "\nBoard has to be at least 8x8");
        }

        gameMatrix = new GameMatrix(blocksWidth, blocksHeight);
        this.root = root;
        brickBag = new BrickBag();
        skin = new Skin(Settings.FAVOURITE_SKIN);

        this.sizeX = sizeWidth;
        this.sizeY = sizeHeight;
        this.paddingX = paddingX;
        this.paddingY = paddingY;

        setupLayouts();
        setupMainTimeline();
        resetGame();
        reDrawAll();

    }

    public GameBoard(AnchorPane root, int blocksWidth, int blocksHeight, int paddingX, int paddingY) {
        this(root, blocksWidth, blocksHeight, 300, 600, paddingX, paddingY);
    }

    public GameBoard(AnchorPane root, int blocksWidth, int blocksHeight) {
        this(root, blocksWidth, blocksHeight, 0, 0);
    }

    public void startGame() {
        showGame();
        if (gameMatrix.getActiveBrick() == null)
            gameMatrix.addNewBrick(Brick.createBrickByID(activeBrickID));

        reDrawAll();
        playing = true;
        start();
    }

    public void pauseGame(boolean value) {
        if (!isMenuOpened) {
            playing = !value;
            if (playing) mainTimeline.play();
            else mainTimeline.pause();
        }
    }

    private void showGame() {
        isMenuOpened = false;
    }

    private void start() {
        mainTimeline.play();
    }

    public void resetGame() {
        if (!isAnimationPlaying) {
            clearBoard();
            activeBrickID = generateRandomBrickID();
            nextBrickID = generateRandomBrickID();
            canHoldBrick = true;
            onHoldBrickID = -1;
            score = 0;
            counterToMoveDown = 0;
            Platform.runLater(() -> mStatistics.setText("LINES SENT: " + score));

            if (playing) {
                gameMatrix.addNewBrick(Brick.createBrickByID(activeBrickID));
                reDrawAll();
                startGame();
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
            drawBrickOnCanvas(getCanvasByID(Settings.IDs.CANVAS_NEXT_BRICK_ID), getNextBrickID());
            drawBrickOnCanvas(getCanvasByID(Settings.IDs.CANVAS_HOLD_BRICK_ID), getOnHoldBrickID());

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
        if (playing) gameMatrix.moveBrickLeft();
    }

    public void moveBrickRight() {
        if (playing) gameMatrix.moveBrickRight();
    }

    public void moveBrickDown() {
        if (playing) gameMatrix.moveBrickDown();
    }

    public void rotateBrick() {
        if (playing) gameMatrix.rotateBrick();
    }

    public void setSkin(int skin) {
        if (!isGameEnd()) {
            this.skin.setSkinID(skin);
            reDrawAll();
        }
    }

    public int getActiveSkin() {
        return skin.getSelectedSkinID();
    }

    public void setPreview(boolean value) {
        isPreviewActive = value;
    }

    public void setFallSpeed(int fallSpeed) {
        if (fallSpeed < 10 && fallSpeed >= 0) this.fallSpeed = 10;
        else this.fallSpeed = fallSpeed;
    }

    private void animateGrayScaleEffect() {
        isAnimationPlaying = true;

        Canvas canvas = getCanvasByID(Settings.IDs.GAME_CANVAS_ID);
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
    private void animateRows(final List<Integer> rows) {
        isAnimationPlaying = true;

        Canvas canvas = getCanvasByID(Settings.IDs.GAME_CANVAS_ID);
        double squareWidth = canvas.getWidth() / gameMatrix.getWidth();
        double squareHeight = canvas.getHeight() / gameMatrix.getHeight();

        animateRowsSide(rows, canvas, squareWidth, squareHeight);

    }

    private void animateRowsSide(final List<Integer> rows, Canvas canvas, double squareWidth, double squareHeight) {
        Thread rowSideAnimation = new Thread(new Runnable() {
            int counter = 0;
            double stepX = (gameMatrix.getWidth() * squareWidth) / ANIMATION_DURATION;
            double randomStepX = stepX;

            @Override
            public void run() {
                //Waiting for main canvas to be redrawn
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    animateRowsFall(rows, canvas, squareWidth, squareHeight);
                }

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
                        animateRowsFall(rows, canvas, squareWidth, squareHeight);
                    }
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        animateRowsFall(rows, canvas, squareWidth, squareHeight);
                    }
                }
            }
        });
        rowSideAnimation.start();
    }

    private void animateRowsFall(final List<Integer> rows, Canvas canvas, double squareWidth, double squareHeight) {
        final Thread fallAnimation = new Thread(new Runnable() {

            double stepY = squareHeight / ANIMATION_DURATION;
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
                    }
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        for (int row : rows) {
                            gameMatrix.moveAllDownFromRow(row);
                        }
                        isAnimationPlaying = false;
                        e.printStackTrace();
                    }
                }
            }
        });

        fallAnimation.start();
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
        if (ID == -1) return getColorByID(activeBrickID);
        else return Brick.getColorByID(ID);
    }

    private void reDrawAll() {
        drawBackground(getCanvasByID(Settings.IDs.CANVAS_BACKGROUND_ID));
        drawBrickOnCanvas(getCanvasByID(Settings.IDs.CANVAS_NEXT_BRICK_ID), getNextBrickID());
        drawBrickOnCanvas(getCanvasByID(Settings.IDs.CANVAS_HOLD_BRICK_ID), getOnHoldBrickID());
    }

    //Redrawing game background
    private void drawBackground(Canvas canvas) {
        double squareWidth = canvas.getWidth() / gameMatrix.getWidth();
        double squareHeight = canvas.getHeight() / gameMatrix.getHeight();

        canvas.getGraphicsContext2D().setStroke(Color.BLACK);

        for (int i = 0; i < gameMatrix.getHeight(); i++) {
            for (int j = 0; j < gameMatrix.getWidth(); j++) {
                if (((i % 2) + j) % 2 == 0) canvas.getGraphicsContext2D().setFill(new Color(0.2, 0.2, 0.2, 1));
                else canvas.getGraphicsContext2D().setFill(new Color(0.15, 0.15, 0.15, 1));

                canvas.getGraphicsContext2D().fillRect(squareWidth * j, squareHeight * i, squareWidth, squareHeight); //Filling
                canvas.getGraphicsContext2D().strokeRect(squareWidth * j, squareHeight * i, squareWidth, squareHeight); //Stroking
            }
        }
    }

    //Redrawing all game blocks
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

        //Drawing preview blocks
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
        Brick.drawBrickOnCanvas(canvas, brickID, skin);
    }

    private void clearBoard() {
        gameMatrix.resetMatrix();
    }

    private Canvas getCanvasByID(String ID) {
        //This is search for layout file canvas
        if (root.lookup(ID) != null) return ((Canvas) root.lookup(ID));

        //This is search for programmatically created canvas
        for (int i = 0; i < root.getChildren().size(); i++) {
            if (root.getChildren().get(i).getId() != null && root.getChildren().get(i).getId().equals(ID))
                return ((Canvas) root.getChildren().get(i));
        }

        return new Canvas();
    }

    private void setupLayouts() {
        setupCanvases();
        mStatistics = new Label("LINES SENT:  0");
        mStatistics.setFont(new Font("Verdana", 12));
        mStatistics.setPrefWidth(150);
        mStatistics.setAlignment(Pos.CENTER);
        mStatistics.setTextFill(Color.WHITE);
        mStatistics.setLayoutY(575 + paddingY);
        mStatistics.setLayoutX(300 + paddingX);
        root.getChildren().add(mStatistics);
    }

    //This method is protection from corrupted or non-existent layout file
    //If any canvas will be missing on layout, this method will automatically
    //create canvas and set default values to it
    private void setupCanvases() {
        Canvas backgroundCanvas, gameCanvas;
        Canvas nextBrickCanvas, holdBrickCanvas;

        if (getCanvasByID(Settings.IDs.CANVAS_BACKGROUND_ID).getId() == null) {
            backgroundCanvas = new Canvas(sizeX, sizeY);
            backgroundCanvas.setId(Settings.IDs.CANVAS_BACKGROUND_ID);
        } else backgroundCanvas = getCanvasByID(Settings.IDs.CANVAS_BACKGROUND_ID);

        if (getCanvasByID(Settings.IDs.GAME_CANVAS_ID).getId() == null) {
            gameCanvas = new Canvas(sizeX, sizeY);
            gameCanvas.setId(Settings.IDs.GAME_CANVAS_ID);
        } else gameCanvas = getCanvasByID(Settings.IDs.GAME_CANVAS_ID);

        if (getCanvasByID(Settings.IDs.CANVAS_NEXT_BRICK_ID).getId() == null) {
            nextBrickCanvas = new Canvas(sizeX / 4, sizeX / 4);
            nextBrickCanvas.setLayoutX(sizeX + 35);
            nextBrickCanvas.setLayoutY(sizeY / 6);
            nextBrickCanvas.setId(Settings.IDs.CANVAS_NEXT_BRICK_ID);
        } else nextBrickCanvas = getCanvasByID(Settings.IDs.CANVAS_NEXT_BRICK_ID);

        if (getCanvasByID(Settings.IDs.CANVAS_HOLD_BRICK_ID).getId() == null) {
            holdBrickCanvas = new Canvas(sizeX / 4, sizeX / 4);
            holdBrickCanvas.setLayoutX(sizeX + 35);
            holdBrickCanvas.setLayoutY((sizeY / 6) * 4);
            holdBrickCanvas.setId(Settings.IDs.CANVAS_HOLD_BRICK_ID);
        } else holdBrickCanvas = getCanvasByID(Settings.IDs.CANVAS_HOLD_BRICK_ID);

        root.getChildren().removeAll(backgroundCanvas, gameCanvas, nextBrickCanvas, holdBrickCanvas);
        root.getChildren().add(0, backgroundCanvas);
        root.getChildren().add(1, gameCanvas);
        root.getChildren().add(2, nextBrickCanvas);
        root.getChildren().add(3, holdBrickCanvas);

        for (int i = 0; i < 4; i++) {
            root.getChildren().get(i).setLayoutX(root.getChildren().get(i).getLayoutX() + paddingX);
            root.getChildren().get(i).setLayoutY(root.getChildren().get(i).getLayoutY() + paddingY);
        }

    }

    private void ticker() {
        if (!isAnimationPlaying) {
            if (gameMatrix.isReadyForNewBrick()) {
                if (isRowFull()) {
                    score += gameMatrix.getFullRows().size();
                    Platform.runLater(() -> mStatistics.setText("LINES SENT: " + score));
                    if (isAnimationActive) {
                        //RUNNING EXPENSIVE DRAWING PROCESS
                        Platform.runLater(() -> drawBoard(getCanvasByID(Settings.IDs.GAME_CANVAS_ID)));
                        animateRows(gameMatrix.getFullRows());
                    } else {
                        for (int row : gameMatrix.getFullRows()) {
                            gameMatrix.clearRow(row);
                            gameMatrix.moveAllDownFromRow(row);
                        }
                    }
                    return;
                }
                addNewBrick();
                drawBrickOnCanvas(getCanvasByID(Settings.IDs.CANVAS_NEXT_BRICK_ID), getNextBrickID());
            }
            //RUNNING EXPENSIVE DRAWING PROCESS
            Platform.runLater(() -> drawBoard(getCanvasByID(Settings.IDs.GAME_CANVAS_ID)));

            counterToMoveDown++;
            if (playing) {
                if (counterToMoveDown > fallSpeed && fallSpeed > 0) {
                    counterToMoveDown = 0;
                    gameMatrix.moveBrickDown();
                }
            }
        }
    }

    private void setupMainTimeline() {

        mainTimeline = new Timeline();
        mainTimeline.setCycleCount(Animation.INDEFINITE);
        KeyFrame keyframe = new KeyFrame(Duration.millis(10), (ActionEvent event) -> { // 100fps

            if (playing && !gameMatrix.isMatrixFull()) ticker();

            if (gameMatrix.isMatrixFull()) {
                finishGame();
                mainTimeline.stop();
            }

        });
        mainTimeline.getKeyFrames().add(keyframe);
    }

    public boolean isGameEnd() {
        return gameMatrix.isMatrixFull();
    }

    public boolean isAnimationPlaying() {
        return isAnimationPlaying;
    }

    public void setAnimations(boolean value) {
        isAnimationActive = value;
    }

    public int getBlocksWidth() {
        return gameMatrix.getWidth();
    }

    public int getBlocksHeight() {
        return gameMatrix.getHeight();
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

    public int getActiveBrickID() {
        return activeBrickID;
    }

    public int getOnHoldBrickID() {
        return onHoldBrickID;
    }

    public int getNextBrickID() {
        return nextBrickID;
    }

    public boolean isGamePaused() {
        return !playing;
    }

    public int getWidth() {
        return sizeX;
    }

    public int getHeight() {
        return sizeY;
    }

    public void resizeBoard(int width, int height) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean finished = false;

                try {
                    //Waiting until animation is finished
                    while (!finished) {
                        if (!isAnimationPlaying) {
                            //Changing board size
                            mainTimeline.pause();
                            Thread.sleep(10);
                            gameMatrix = new GameMatrix(width, height);
                            resetGame();
                            reDrawAll();
                            mainTimeline.play();
                            finished = true;
                        } else {
                            Thread.sleep(10);
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
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
