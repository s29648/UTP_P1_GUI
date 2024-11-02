package com.example.utpp1;

import javafx.scene.shape.Circle;

import java.util.Optional;

public class MainController {

    private GUIManager guiManager;

    public void setGUIManager(GUIManager guiManager) {
        this.guiManager = guiManager;
    }

    public void handleMove(int column) {
        int player = Main.getCurrentPlayer();
        Circle token = guiManager.createTokenForPlayer(player);

        int res = Main.dropToken(column, token);
        if (res == 1) {
            guiManager.placeToken(token, column);
        } else if (res == 2) {
            guiManager.changeStatusLable(Optional.empty(), Optional.empty(), Optional.of(column));
        }
    }

    public void resetGameGUI() {
        guiManager.resetGUI();
        Main.resetGame();
    }
}
