package com.example.utpp1;

//MAIN HEADER FILE:
//javac --module-path ~/Downloads/javafx-sdk-23.0.1/lib/ --add-modules javafx.controls,javafx.fxml -h . Main.java

import java.awt.*;
import java.util.Optional;

import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.BoxBlur;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;



public class Main extends Application {
    static {
        System.loadLibrary("UTP_P1"); // Ensure this matches the name of Darya's C++ library
    }

    protected native void resetGame();
    protected native boolean dropToken(int column, Circle token);
    protected native int getCurrentPlayer();
    protected native int checkWinner();
    protected native boolean isADraw();
    protected native int getLastMoveRow();
    protected native Point getTokenPosition(Circle token);

    public static final int ROWS = 6;
    public static final int COLS = 7;

    private Label statusLabel;
    private int selectedColumn = 0;
    private boolean keybInputEnabled = true;
    // Pane - allows absolute positioning
    Pane gridPane = new Pane();;
    Pane tokenPane = new Pane();;
    private Rectangle keyboardSelectedColumn;
    private BorderPane root;
    private double slotSize;
    private StackPane overlay;
    private Button playAgainButton;
    private Lighting gridLighting;
    private Lighting slotLighting;
    private Lighting tokenLighting;
    private StackPane gridContainer;

    //TODO:
    //revise project structure
    //rewrite junit

    @Override
    public void start(Stage primaryStage) {


        //StackPane lays out its children in a single stack: on top of each other (Ideal for overlay effects)
        gridContainer = new StackPane(gridPane, tokenPane);
        gridContainer.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

        statusLabel = new Label();
        statusLabel.setAlignment(Pos.CENTER);
        statusLabel.setStyle("-fx-font-family: 'Comic Sans MS'; -fx-font-weight: bold; -fx-font-size: 28px;");

        changeStatusLable(Optional.of(getCurrentPlayer()),Optional.empty(), Optional.empty());

        //layout container
        //BorderPane automatically manages the positioning and resizing of its children
        //five distinct regions: top, bottom, left, right, and center
        root = new BorderPane();
        root.setTop(statusLabel);
        root.setCenter(gridContainer);
        BorderPane.setAlignment(statusLabel, Pos.CENTER);
        BorderPane.setAlignment(gridContainer, Pos.CENTER);
        root.setStyle("-fx-background-color: #808080;");
        root.setPadding(new Insets(20));

        //create new window and place the layout container inside
        Scene scene = new Scene(root, 700, 700);

        statusLabel.prefWidthProperty().bind(scene.widthProperty().multiply(0.5)); // 50% of the Scene width
        statusLabel.prefHeightProperty().bind(scene.heightProperty().multiply(0.05));

        scene.widthProperty().addListener((obs, oldVal, newVal) -> setGridSize());
        scene.heightProperty().addListener((obs, oldVal, newVal) -> setGridSize());

        gridLighting = applyLighting(-90.0, 30.0, 8.0);
        slotLighting = applyLighting(45.0, 10.0, 5.0);
        tokenLighting = applyLighting(10.0, 120.0, 6);

        scene.setOnMouseClicked(event -> {
            if (event.getButton() != MouseButton.PRIMARY) return;
            if (overlay != null && overlay.isVisible()) return;

            double boardX = gridPane.screenToLocal(event.getScreenX(), event.getScreenY()).getX();
            int column = (int) (boardX / slotSize);
            if (column >= 0 && column < COLS) {
                handleMove(column);
            }
        });

        scene.setOnKeyPressed(event -> {
            if (!keybInputEnabled) return;
            if (event.getCode() == KeyCode.LEFT) {
                selectedColumn = (selectedColumn - 1 + COLS) % COLS;
                updateKSelectedColumn();
            } else if (event.getCode() == KeyCode.RIGHT) {
                selectedColumn = (selectedColumn + 1) % COLS;
                updateKSelectedColumn();
            } else if (event.getCode() == KeyCode.ENTER) {
                handleMove(selectedColumn);
            }
        });

        setGridSize();

        primaryStage.setTitle("Connect Four");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void setGridSize() {
        double statusLabelHeight = statusLabel.getHeight() + 10;
        double availableHeight = root.getHeight() - statusLabelHeight - 40;
        double availableWidth = root.getWidth() - 40;

        slotSize = Math.min(availableWidth / COLS, availableHeight / ROWS);

        renderGrid();

        tokenPane.getChildren().forEach(node -> {
            if (node instanceof Circle token) {

                //Java receives a Point object containing only the (x: row and y: column) of the current token
                Point position = getTokenPosition(token);

                if (position != null) {
                    token.setRadius(slotSize / 2 - 12);
                    token.setCenterX(position.y * slotSize + slotSize / 2);
                    token.setCenterY(position.x * slotSize + slotSize / 2);
                };
            }
        });

        updateKSelectedColumn();
    }

    void renderGrid() {
        double gridWidth = COLS * slotSize;
        double gridHeight = ROWS * slotSize;

        gridPane.setPrefSize(gridWidth, gridHeight);
        tokenPane.setPrefSize(gridWidth, gridHeight);

        gridPane.getChildren().clear();
        gridPane.setEffect(gridLighting);
        gridPane.setStyle( "-fx-background-radius: 20; -fx-border-radius: 20; -fx-background-color: rgb(49, 69, 222);");

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                Circle slot = new Circle(slotSize / 2 - 10);
                slot.setCenterX(col * slotSize + slotSize / 2);
                slot.setCenterY(row * slotSize + slotSize / 2);
                slot.setFill(Color.WHITE);
                slot.setEffect(slotLighting);

                gridPane.getChildren().add(slot);
            }
        }
    }

    private Lighting applyLighting(double Azimuth, double Elevation, double surfaceScale) {
        //similar to the sun's light, parallel rays
        Light.Distant light = new Light.Distant();
        light.setAzimuth(Azimuth);
        light.setElevation(Elevation);

        Lighting lighting = new Lighting();
        lighting.setLight(light);
        lighting.setSurfaceScale(surfaceScale);

        return lighting;
    }

    private void updateKSelectedColumn() {
        if (keyboardSelectedColumn != null) {
            gridPane.getChildren().remove(keyboardSelectedColumn);
        }

        keyboardSelectedColumn = new Rectangle(slotSize, slotSize * ROWS);
        keyboardSelectedColumn.setFill(Color.rgb(59, 217, 245, 0.2));
        keyboardSelectedColumn.setX(selectedColumn * slotSize);
        gridPane.getChildren().add(keyboardSelectedColumn);
    }

    private void handleMove(int column) {
        int player = getCurrentPlayer();
        Circle token = new Circle(slotSize / 2 - 12);

        if (dropToken(column, token)) {
            int row = getLastMoveRow();

            token.setCenterX(column * slotSize + slotSize / 2);
            token.setFill(player == 1
                    ? Color.rgb(219, 57, 35)
                    : Color.rgb(240, 233, 53));
            token.setStroke(player == 1
                    ? Color.rgb(173, 41, 23)
                    : Color.rgb(179, 173, 39));
            token.setEffect(tokenLighting);
            token.setCenterY(-slotSize / 2);


            tokenPane.getChildren().add(token);
            animationOfFalling(token, row, column);

        } else {
            changeStatusLable(Optional.empty(), Optional.empty(), Optional.of(column));
        }
    }

    private void animationOfFalling(Circle token, int row, int column) {
        TranslateTransition falling = new TranslateTransition(Duration.seconds(0.4), token);

        double targetY = row * slotSize + slotSize / 2;
        falling.setToY(targetY + slotSize / 2);

        falling.setOnFinished(e -> {
            token.setCenterY(targetY);
            token.setTranslateY(0);

            int winner = checkWinner();

            if (winner != 0) {
                endGame();
                changeStatusLable(Optional.of(winner), Optional.of("wins"), Optional.empty());
            } else if (isADraw()) {
                endGame();
                changeStatusLable(Optional.empty(), Optional.of("It's a draw!"), Optional.empty());
            } else {
                changeStatusLable(Optional.of(getCurrentPlayer()), Optional.empty(), Optional.empty());
            }
        });

        falling.play();
    }

    private void endGame() {
        keybInputEnabled = false;
        gridPane.getChildren().remove(keyboardSelectedColumn);

        BoxBlur blur = new BoxBlur(10, 10, 3);
        gridPane.setEffect(blur);
        tokenPane.setEffect(blur);

        playAgainButton = new Button("Play Again");
        playAgainButton.setStyle("-fx-font-size: 30px; -fx-background-color: #cfcaca; -fx-border-radius: 15; " +
                "-fx-background-radius: 15; -fx-text-fill: #524949; -fx-padding: 10 20 10 20; " +
                "-fx-font-family: 'Arial'; -fx-font-weight: bold;");
        playAgainButton.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            e.consume();
            resetGame();
            resetGUI();
        });

        playAgainButton.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                e.consume();
                resetGame();
                resetGUI();
            }
        });

        // Create a blocker pane that covers the board and consumes events
        Pane blockerPane = new Pane();
        blockerPane.setPrefSize(gridPane.getPrefWidth(), gridPane.getPrefHeight());
        blockerPane.setStyle("-fx-background-color: transparent;");
        blockerPane.addEventFilter(MouseEvent.ANY, e -> e.consume());

        //play again overlay
        overlay = new StackPane(blockerPane, playAgainButton);
        overlay.setPrefSize(gridPane.getPrefWidth(), gridPane.getPrefHeight());
        overlay.setAlignment(Pos.CENTER);

        gridContainer.getChildren().add(overlay);
    }

    private void resetGUI() {
        //remove old
        tokenPane.getChildren().clear();
        gridPane.setEffect(null);
        tokenPane.setEffect(null);
        gridContainer.getChildren().remove(overlay);
        overlay = null;
        playAgainButton = null;

        //add new
        renderGrid();
        changeStatusLable(Optional.of(getCurrentPlayer()), Optional.empty(), Optional.empty());
        keybInputEnabled = true;
        selectedColumn = 0;
        updateKSelectedColumn();
    }

    private void changeStatusLable(Optional<Integer> player, Optional<String> event, Optional<Integer> column) {
        String currentStyle = statusLabel.getStyle();
        statusLabel.setStyle(currentStyle);
        if (player.isPresent() && event.isPresent()) {
            statusLabel.setText("Player "+ player.get() + " " + event.get() +"!");
            currentStyle += " -fx-text-fill: #6b0404; -fx-underline: true;";
            statusLabel.setStyle(currentStyle);

        } else if (player.isPresent()) {
            String color = (player.get() == 1) ? "rgb(158, 38, 27)" : "rgb(235, 212, 12)";
            currentStyle += "; -fx-text-fill: " + color + "; -fx-underline: false;";
            statusLabel.setStyle(currentStyle);
            statusLabel.setText("Player " + player.get() + " moves");


        } else if (column.isPresent()) {
            currentStyle += " -fx-text-fill: rgb(45, 45, 56); -fx-underline: false;";
            statusLabel.setStyle(currentStyle);
            statusLabel.setText("Column " + (column.get() +1)+ " is full!");
        } else if (event.isPresent()) {
            currentStyle += " -fx-text-fill: rgb(34, 34, 46); -fx-underline: true;";
            statusLabel.setStyle(currentStyle);
            statusLabel.setText(event.get());
        }

        return;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
