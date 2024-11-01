package com.example.utpp1;

import javafx.scene.shape.Circle;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;

public class MainTest {
    private Main game;

    @BeforeEach
    void setUp() {
        game = new Main();
        game.resetGame();
    }


    @Test
    //test if grid is initialized properly: has empty slots and does not have any tokens placed yet
    void testGridInit() {
        game.renderGrid();

        game.gridPane.getChildren().forEach(node -> {
            Assertions.assertNotNull(node);
        });

        game.tokenPane.getChildren().forEach(node -> {
            if (node instanceof Circle token) {
                Point position = game.getTokenPosition(token);
                Assertions.assertNull(position);
            }
        });
    }

    @Test
    //test if move has been performed and token position returns expected values
    void testValidMove() {
        Circle token = new Circle();
        boolean moveSuccess = game.dropToken(0, token);
        Assertions.assertTrue(moveSuccess);

        Point position = game.getTokenPosition(token);
        Assertions.assertEquals(5, position.x);
        Assertions.assertEquals(0, position.y);
    }

    @Test
    //test move if the column is already filled
    void testInvalidMove() {
        //fill the column
        for (int i = 0; i < Main.ROWS; i++) {
            Circle token = new Circle();
            game.dropToken(0, token);
        }

        //try to add one more token
        Circle token = new Circle();
        boolean moveSuccess = game.dropToken(0, token);
        Assertions.assertFalse(moveSuccess);
    }

    @Test
    //test if players id switches with every move from 1 to 2
    void testSwitchingPlayers() {
        int firstMovePlayer = game.getCurrentPlayer();
        Assertions.assertEquals(1, firstMovePlayer);
        Circle token = new Circle();

        game.dropToken(0, token);
        int secondMovePlayer = game.getCurrentPlayer();
        Assertions.assertEquals(2, secondMovePlayer);

        Assertions.assertNotEquals(firstMovePlayer, secondMovePlayer);

        game.dropToken(0, token);
        int thirdMovePlayer = game.getCurrentPlayer();
        Assertions.assertEquals(firstMovePlayer, thirdMovePlayer);
    }

    @Test
    // test if player who connected 4 in horizontal direction wins
    void testWinHorizontally() {
        for (int col = 0; col < 4; col++) {
            Circle token = new Circle();
            //player 1
            game.dropToken(col, token);
            //player 2
            game.dropToken(col, token);
        }

        int winner = game.checkWinner();
        Assertions.assertEquals(1, winner);
    }

    @Test
    // test if player who connected 4 in vertical direction wins
    void testWinVertically() {
        for (int row = 0; row < 4; row++) {
            //player 1 drops in column 0
            Circle tokenPlayer1 = new Circle();
            game.dropToken(0, tokenPlayer1);
            //player 2 drops in column 1
            Circle tokenPlayer2 = new Circle();
            game.dropToken(1, tokenPlayer2);
        }

        int winner = game.checkWinner();
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
        game.dropToken(0, new Circle());
        // 2
        game.dropToken(1, new Circle());
        // 1
        game.dropToken(1, new Circle());
        // 2
        game.dropToken(2, new Circle());
        // 1
        game.dropToken(2, new Circle());
        // 2
        game.dropToken(0, new Circle());
        // 1
        game.dropToken(2, new Circle());
        // 2
        game.dropToken(3, new Circle());
        // 1
        game.dropToken(3, new Circle());
        // 2
        game.dropToken(3, new Circle());
        // 1
        game.dropToken(3, new Circle());

        int winner = game.checkWinner();
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
        game.dropToken(3, new Circle());
        game.dropToken(4, new Circle());
        game.dropToken(5, new Circle());
        game.dropToken(6, new Circle());
        // row 2
        game.dropToken(3, new Circle());
        game.dropToken(4, new Circle());
        game.dropToken(5, new Circle());
        game.dropToken(6, new Circle());
        // row 3
        game.dropToken(3, new Circle());
        game.dropToken(4, new Circle());
        // row 4
        game.dropToken(3, new Circle());

        int winner = game.checkWinner();
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
            game.dropToken(0, new Circle());
        }
        // column 3
        for (int row = 0; row < 6; row++) {
            game.dropToken(2, new Circle());
        }
        // column 5
        for (int row = 0; row < 6; row++) {
            game.dropToken(4, new Circle());
        }
        // column 7
        for (int row = 0; row < 6; row++) {
            game.dropToken(6, new Circle());
        }
        // col 6
        game.dropToken(5, new Circle());
        //col 4
        for (int row = 0; row < 6; row++) {
            game.dropToken(3, new Circle());
        }
        // col 6
        for (int row = 0; row < 5; row++) {
            game.dropToken(5, new Circle());
        }
        //col 2
        for (int row = 0; row < 6; row++) {
            game.dropToken(1, new Circle());
        }


        boolean draw = game.isADraw();
        Assertions.assertTrue(draw);
    }



    @AfterEach
    void reset() {
        game.resetGame();
    }
}
