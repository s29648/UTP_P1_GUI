package com.example.utpp1;

import javafx.application.Application;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.awt.*;


public class Main extends Application {

    static {
        System.loadLibrary("UTP_P1");
    }

    protected static native void resetGame();

    protected static native int dropToken(int column, Circle token);

    protected static native int getCurrentPlayer();

    protected static native int checkWinner();

    protected static native boolean isADraw();

    protected static native int getLastMoveRow();

    protected static native Point getTokenPosition(Circle token);

    public static final int ROWS = 6;
    public static final int COLS = 7;

    @Override
    public void start(Stage primaryStage) {
        MainController controller = new MainController();
        GUIManager guiManager = new GUIManager(controller);

        controller.setGUIManager(guiManager);
        guiManager.init(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}