package com.example.utpp1;

import javafx.application.Platform;
import javafx.scene.shape.Circle;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
import javafx.scene.Node;


import static com.example.utpp1.Main.COLS;
import static com.example.utpp1.Main.ROWS;

public class MainTest {
    private MainController controller;
    private GUIManager guiManager;

    @BeforeEach
    void setUp() {
        new Main();
        Main.resetGame();

    }

    @Test
    //test if grid is initialized properly: has empty slots and does not have any tokens placed yet
    void testGridInit() throws InterruptedException {

        // to ensure that the JavaFX environment is started before running the tests
        Platform.startup(() -> {
            controller = new MainController();
            guiManager = new GUIManager(controller);
        });

        //wait until the JavaFX environment is loaded properly
        Platform.runLater(() -> {
            guiManager.renderGrid();

            // gridPane has the given number of slots
            int gridSize = guiManager.gridPane.getChildren().size();
            Assertions.assertEquals(ROWS * COLS, gridSize);

            // slots are initialized properly
            for (Node node : guiManager.gridPane.getChildren()) {
                Assertions.assertInstanceOf(Circle.class, node);
            }

            // no tokens in slots when initialized
            guiManager.tokenPane.getChildren().forEach(node -> {
                if (node instanceof Circle token) {
                    Point position = Main.getTokenPosition(token);
                    Assertions.assertNull(position);
                }
            });
        });
    }

    @Test
    //test if move has been performed and token position returns expected values
    void testValidMove() {
        Circle token = new Circle();
        int moveSuccess = Main.dropToken(0, token);
        Assertions.assertEquals(1, moveSuccess);

        Point position = Main.getTokenPosition(token);
        Assertions.assertEquals(5, position.x);
        Assertions.assertEquals(0, position.y);
    }

    @Test
    //test move if the column is already filled
    void testInvalidMove() {
        //fill the column
        for (int i = 0; i < ROWS; i++) {
            Circle token = new Circle();
            Main.dropToken(0, token);
        }

        //try to add one more token
        Circle token = new Circle();
        int moveSuccess = Main.dropToken(0, token);
        Assertions.assertEquals(2, moveSuccess);
    }

    @Test
    //test if players id switches with every move from 1 to 2
    void testSwitchingPlayers() {
        int firstMovePlayer = Main.getCurrentPlayer();
        Assertions.assertEquals(1, firstMovePlayer);
        Circle token = new Circle();

        Main.dropToken(0, token);
        int secondMovePlayer = Main.getCurrentPlayer();
        Assertions.assertEquals(2, secondMovePlayer);

        Assertions.assertNotEquals(firstMovePlayer, secondMovePlayer);

        Main.dropToken(0, token);
        int thirdMovePlayer = Main.getCurrentPlayer();
        Assertions.assertEquals(firstMovePlayer, thirdMovePlayer);
    }

    @Test
    // test if player who connected 4 in horizontal direction wins
    void testWinHorizontally() {
        for (int col = 0; col < 4; col++) {
            Circle token = new Circle();
            //player 1
            Main.dropToken(col, token);
            //player 2
            Main.dropToken(col, token);
        }

        int winner = Main.checkWinner();
        Assertions.assertEquals(1, winner);
    }

    @Test
    // test if player who connected 4 in vertical direction wins
    void testWinVertically() {
        for (int row = 0; row < 4; row++) {
            //player 1 drops in column 0
            Circle tokenPlayer1 = new Circle();
            Main.dropToken(0, tokenPlayer1);
            //player 2 drops in column 1
            Circle tokenPlayer2 = new Circle();
            Main.dropToken(1, tokenPlayer2);
        }

        int winner = Main.checkWinner();
        Assertions.assertEquals(1, winner);
    }

    // 6| [ ][ ][ ][ ][ ][ ][ ]
    // 5| [ ][ ][ ][ ][ ][ ][ ]
    // 4| [ ][ ][ ][1][ ][ ][ ]
    // 3| [ ][ ][1][2][ ][ ][ ]
    // 2| [2][1][1][2][ ][ ][ ]
    // 1| [1][2][2][1][ ][ ][ ]
    //_________________________
    //     1  2  3  4  5  6  7


    @Test
    // test if player who connected 4 in diagonal direction wins
    void testWinDiagonally() {
        // 1
        Main.dropToken(0, new Circle());
        // 2
        Main.dropToken(1, new Circle());
        // 1
        Main.dropToken(1, new Circle());
        // 2
        Main.dropToken(2, new Circle());
        // 1
        Main.dropToken(2, new Circle());
        // 2
        Main.dropToken(0, new Circle());
        // 1
        Main.dropToken(2, new Circle());
        // 2
        Main.dropToken(3, new Circle());
        // 1
        Main.dropToken(3, new Circle());
        // 2
        Main.dropToken(3, new Circle());
        // 1
        Main.dropToken(3, new Circle());

        int winner = Main.checkWinner();
        Assertions.assertEquals(1, winner);
    }
    // 6| [ ][ ][ ][ ][ ][ ][ ]
    // 5| [ ][ ][ ][ ][ ][ ][ ]
    // 4| [ ][ ][ ][1][ ][ ][ ]
    // 3| [ ][ ][ ][2][1][ ][ ]
    // 2| [ ][ ][ ][1][2][1][2]
    // 1| [ ][ ][ ][2][1][2][1]
    //_________________________
    //     1  2  3  4  5  6  7

    @Test
    void testWinDiagonallyBackwards() {
        // row 1
        Main.dropToken(3, new Circle());
        Main.dropToken(4, new Circle());
        Main.dropToken(5, new Circle());
        Main.dropToken(6, new Circle());
        // row 2
        Main.dropToken(3, new Circle());
        Main.dropToken(4, new Circle());
        Main.dropToken(5, new Circle());
        Main.dropToken(6, new Circle());
        // row 3
        Main.dropToken(3, new Circle());
        Main.dropToken(4, new Circle());
        // row 4
        Main.dropToken(3, new Circle());

        int winner = Main.checkWinner();
        Assertions.assertEquals(1, winner);
    }


    // 6| [1][1][1][2][1][1][1]
    // 5| [1][1][1][2][1][1][1]
    // 4| [2][2][2][1][2][2][2]
    // 3| [1][1][1][2][1][1][1]
    // 2| [2][2][2][1][2][2][2]
    // 1| [1][1][1][2][1][1][1]
    //_________________________
    //     1  2  3  4  5  6  7


    @Test
    void testADraw() {
        // column 1
        for (int row = 0; row < 6; row++) {
            Main.dropToken(0, new Circle());
        }
        // column 3
        for (int row = 0; row < 6; row++) {
            Main.dropToken(2, new Circle());
        }
        // column 5
        for (int row = 0; row < 6; row++) {
            Main.dropToken(4, new Circle());
        }
        // column 7
        for (int row = 0; row < 6; row++) {
            Main.dropToken(6, new Circle());
        }
        // col 6
        Main.dropToken(5, new Circle());
        //col 4
        for (int row = 0; row < 6; row++) {
            Main.dropToken(3, new Circle());
        }
        // col 6
        for (int row = 0; row < 5; row++) {
            Main.dropToken(5, new Circle());
        }
        //col 2
        for (int row = 0; row < 6; row++) {
            Main.dropToken(1, new Circle());
        }


        boolean draw = Main.isADraw();
        Assertions.assertTrue(draw);
    }



    @AfterEach
    void reset() {
        Main.resetGame();
    }
}
