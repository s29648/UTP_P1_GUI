//package com.example.utpp1;
//
//import javafx.scene.shape.Circle;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import java.awt.*;
//
//public class MainTest {
//    private Main game;
//
//    @BeforeEach
//    void setUp() {
//        game = new Main();
//        game.resetGame();
//    }
//
//
//    @Test
//    //test if grid is initialized properly: has empty slots and does not have any tokens placed yet
//    void testGridInit() {
//        game.renderGrid();
//
//        game.gridPane.getChildren().forEach(node -> {
//            Assertions.assertNotNull(node);
//        });
//
//        game.tokenPane.getChildren().forEach(node -> {
//            if (node instanceof Circle token) {
//                Point position = game.getTokenPosition(token);
//                Assertions.assertNull(position);
//            }
//        });
//    }
//
//    @Test
//    //test if move has been performed and token position returns expected values
//    void testValidMove() {
//        Circle token = new Circle();
//        boolean moveSuccess = game.dropToken(0, token);
//        Assertions.assertTrue(moveSuccess);
//
//        Point position = game.getTokenPosition(token);
//        Assertions.assertEquals(5, position.x);
//        Assertions.assertEquals(0, position.y);
//    }
//
//    @Test
//    //test move if the column is already filled
//    void testInvalidMove() {
//        //fill the column
//        for (int i = 0; i < Main.ROWS; i++) {
//            Circle token = new Circle();
//            game.dropToken(0, token);
//        }
//
//        //try to add one more token
//        Circle token = new Circle();
//        boolean moveSuccess = game.dropToken(0, token);
//        Assertions.assertFalse(moveSuccess);
//    }
//
//    @Test
//    //test if players id switches with every move from 1 to 2
//    void testSwitchingPlayers() {
//        int firstMovePlayer = game.getCurrentPlayer();
//        Assertions.assertEquals(1, firstMovePlayer);
//        Circle token = new Circle();
//
//        game.dropToken(0, token);
//        int secondMovePlayer = game.getCurrentPlayer();
//        Assertions.assertEquals(2, secondMovePlayer);
//
//        Assertions.assertNotEquals(firstMovePlayer, secondMovePlayer);
//
//        game.dropToken(0, token);
//        int thirdMovePlayer = game.getCurrentPlayer();
//        Assertions.assertEquals(firstMovePlayer, thirdMovePlayer);
//    }
//
//    @Test
//    // test if player who connected 4 in horizontal direction wins
//    void testWinHorizontally() {
//        for (int col = 0; col < 4; col++) {
//            Circle token = new Circle();
//            //player 1
//            game.dropToken(col, token);
//            //player 2
//            game.dropToken(col, token);
//        }
//
//        boolean winner = game.checkWinner();
//        Assertions.assertTrue(winner);
//    }
//
//    @Test
//    void testWinConditionVertical() {
//
//        for (int row = 0; row < 4; row++) {
//            Circle tokenPlayer1 = new Circle();
//            game.dropToken(0, tokenPlayer1); // Player 1 drops in column 0
//
//            // Since the player automatically switches, Player 2 has the next move.
//            // To avoid Player 2 interfering in the same column, we let Player 2 drop in another column.
//            Circle tokenPlayer2 = new Circle();
//            game.dropToken(1, tokenPlayer2); // Player 2 drops in column 1
//        }
//
//        // After 4 drops by Player 1 in column 0, check if Player 1 has won
//        boolean winner = game.checkWinner();
//        Assertions.assertTrue(winner);
//    }
//
//
//    @AfterEach
//    void reset() {
//        game.resetGame();
//    }
//}
