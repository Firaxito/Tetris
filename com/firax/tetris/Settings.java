package com.firax.tetris;

public final class Settings {

    private Settings() { }

    public static int GAME_WIDTH = 450;
    public static int GAME_HEIGHT = 600;

    public static int MENU_WIDTH = 750;
    public static int MENU_HEIGHT = 600;

    public static int BOARD_WIDTH = 10;
    public static int BOARD_HEIGHT = 20;

    public static int FAVOURITE_SKIN = Skin.WOOD_SKIN;

    public static boolean IS_PREVIEW_ACTIVE = true;
    public static boolean IS_ANIMATION_ENABLED = true;

    public final static String ROOT_FOLDER = "/com/firax/tetris/";


    public final class IDs {

        private IDs() { }

        public final static String GAME_CANVAS_ID = "#gameCanvas";
        public final static String CANVAS_BACKGROUND_ID = "#backgroundCanvas";
        public final static String CANVAS_NEXT_BRICK_ID = "#nextBrickCanvas";
        public final static String CANVAS_HOLD_BRICK_ID = "#holdBrickCanvas";

    }

}
