package com.example.utpp1;

import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
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

import java.awt.*;
import java.util.Optional;

import static com.example.utpp1.Main.COLS;
import static com.example.utpp1.Main.ROWS;

public class GUIManager {

    private final MainController controller;
    private Scene scene;
    private Label statusLabel;
    private BorderPane layout;
    protected Pane gridPane;
    protected Pane tokenPane;
    private StackPane gridContainer;
    private StackPane overlay;
    private Button playAgainButton;
    private Rectangle keyboardSelectedColumn;
    private Lighting gridLighting;
    private Lighting slotLighting;
    private Lighting tokenLighting;
    private double slotSize;
    private int selectedColumn = 0;
    private boolean keybInputEnabled = true;


    public GUIManager(MainController controller) {
        this.controller = controller;
        initializeLighting();
    }

    public void init(Stage primaryStage) {
        setupGUI();

        setGridSize();

        primaryStage.setTitle("Connect Four");
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    void setupGUI() {
        // Pane allows absolute positioning
        gridPane = new Pane();
        tokenPane = new Pane();
        //StackPane lays out its children in a single stack: on top of each other
        gridContainer = new StackPane(gridPane, tokenPane);
        gridContainer.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

        statusLabel = new Label();
        statusLabel.setAlignment(Pos.CENTER);
        statusLabel.setStyle("-fx-font-family: 'Comic Sans MS'; -fx-font-weight: bold; -fx-font-size: 28px;");

        changeStatusLable(Optional.of(Main.getCurrentPlayer()), Optional.empty(), Optional.empty());

        //BorderPane automatically manages the positioning and resizing of its children to top, bottom, left, right, and center
        layout = new BorderPane();
        layout.setTop(statusLabel);
        layout.setCenter(gridContainer);
        BorderPane.setAlignment(statusLabel, Pos.CENTER);
        BorderPane.setAlignment(gridContainer, Pos.CENTER);
        layout.setStyle("-fx-background-color: #808080;");
        layout.setPadding(new Insets(20));

        scene = new Scene(layout, 700, 700);

        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.LEFT) {
                selectedColumn = (selectedColumn - 1 + COLS) % COLS;
                updateKeyboardSelectedColumn();
            } else if (event.getCode() == KeyCode.RIGHT) {
                selectedColumn = (selectedColumn + 1) % COLS;
                updateKeyboardSelectedColumn();
            } else if (event.getCode() == KeyCode.ENTER) {
                controller.handleMove(selectedColumn);
            }
        });

        scene.setOnMouseClicked(event -> {
            if (event.getButton() != MouseButton.PRIMARY) return;
            if (overlay != null && overlay.isVisible()) return;

            double boardX = gridPane.screenToLocal(event.getScreenX(), event.getScreenY()).getX();
            int column = (int) (boardX / slotSize);
            if (column >= 0 && column < COLS) {
                controller.handleMove(column);
            }
        });

        scene.widthProperty().addListener((obs, oldVal, newVal) -> setGridSize());
        scene.heightProperty().addListener((obs, oldVal, newVal) -> setGridSize());
    }

    public Circle createTokenForPlayer(int player) {
        Circle token = new Circle(slotSize / 2 - 12);
        token.setFill(player == 1
                ? Color.rgb(219, 57, 35)
                : Color.rgb(240, 233, 53));
        token.setStroke(player == 1
                ? Color.rgb(173, 41, 23)
                : Color.rgb(179, 173, 39));
        token.setEffect(tokenLighting);

        return token;
    }

    public void placeToken(Circle token, int column) {
        int row = Main.getLastMoveRow();

        token.setCenterX(column * slotSize + slotSize / 2);
        token.setCenterY(-slotSize / 2);

        tokenPane.getChildren().add(token);

        animationOfFalling(token, row);
    }

    private void animationOfFalling(Circle token, int row) {
        TranslateTransition falling = new TranslateTransition(Duration.seconds(0.4), token);

        double targetY = row * slotSize + slotSize / 2;
        falling.setToY(targetY + slotSize / 2);

        falling.setOnFinished(e -> {
            token.setCenterY(targetY);
            token.setTranslateY(0);

            int winner = Main.checkWinner();

            if (winner != 0) {
                endGame();
                changeStatusLable(Optional.of(winner), Optional.of("wins"), Optional.empty());
            } else if (Main.isADraw()) {
                endGame();
                changeStatusLable(Optional.empty(), Optional.of("It's a draw!"), Optional.empty());
            } else {
                changeStatusLable(Optional.of(Main.getCurrentPlayer()), Optional.empty(), Optional.empty());
            }
        });

        falling.play();
    }

    public void endGame() {
        keybInputEnabled = false;
        gridPane.getChildren().remove(keyboardSelectedColumn);
        showPlayAgainButton();
    }

    public void resetGUI() {
        tokenPane.getChildren().clear();

        gridPane.setEffect(null);
        tokenPane.setEffect(null);

        gridContainer.getChildren().remove(overlay);
        overlay = null;
        playAgainButton = null;

        renderGrid();

        changeStatusLable(Optional.of(Main.getCurrentPlayer()), Optional.empty(), Optional.empty());

        keybInputEnabled = true;
        selectedColumn = 0;
        updateKeyboardSelectedColumn();
    }

    private void showPlayAgainButton() {
        BoxBlur blur = new BoxBlur(10, 10, 3);
        gridPane.setEffect(blur);
        tokenPane.setEffect(blur);

        playAgainButton = new Button("Play Again");
        playAgainButton.setStyle("-fx-font-size: 30px; -fx-background-color: #cfcaca; -fx-border-radius: 15; " +
                "-fx-background-radius: 15; -fx-text-fill: #524949; -fx-padding: 10 20 10 20; " +
                "-fx-font-family: 'Arial'; -fx-font-weight: bold;");

        playAgainButton.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            e.consume();
            controller.resetGameGUI();
        });

        playAgainButton.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                e.consume();
                controller.resetGameGUI();
            }
        });

        createOverlay(playAgainButton);
    }

    private void createOverlay(Button playAgainButton) {
        Pane blockerPane = new Pane();
        blockerPane.setPrefSize(gridPane.getPrefWidth(), gridPane.getPrefHeight());
        blockerPane.setStyle("-fx-background-color: transparent;");
        blockerPane.addEventFilter(MouseEvent.ANY, e -> e.consume());

        overlay = new StackPane(blockerPane, playAgainButton);
        overlay.setPrefSize(gridPane.getPrefWidth(), gridPane.getPrefHeight());
        overlay.setAlignment(Pos.CENTER);
        gridContainer.getChildren().add(overlay);
    }

    private void setGridSize() {
        double statusLabelHeight = statusLabel.getHeight() + 10;
        double availableHeight = layout.getHeight() - statusLabelHeight - 40;
        double availableWidth = layout.getWidth() - 40;

        slotSize = Math.min(availableWidth / 7, availableHeight / 6);

        renderGrid();

        tokenPane.getChildren().forEach(node -> {
            if (node instanceof Circle token) {

                //Java receives a Point object containing only the (x: row and y: column) of the current token
                Point position = Main.getTokenPosition(token);

                if (position != null) {
                    token.setRadius(slotSize / 2 - 12);
                    token.setCenterX(position.y * slotSize + slotSize / 2);
                    token.setCenterY(position.x * slotSize + slotSize / 2);
                }
            }
        });

        updateKeyboardSelectedColumn();
    }

    void renderGrid() {
        gridPane.getChildren().clear();

        double gridWidth = COLS * slotSize;
        double gridHeight = ROWS * slotSize;

        gridPane.setPrefSize(gridWidth, gridHeight);
        tokenPane.setPrefSize(gridWidth, gridHeight);

        gridPane.setStyle("-fx-background-radius: 20; -fx-border-radius: 20; -fx-background-color: rgb(49, 69, 222);");
        gridPane.setEffect(gridLighting);

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

    private void updateKeyboardSelectedColumn() {
        if (keyboardSelectedColumn != null) gridPane.getChildren().remove(keyboardSelectedColumn);

        keyboardSelectedColumn = new Rectangle(slotSize, slotSize * 6);
        keyboardSelectedColumn.setFill(Color.rgb(59, 217, 245, 0.2));
        keyboardSelectedColumn.setX(selectedColumn * slotSize);
        gridPane.getChildren().add(keyboardSelectedColumn);
    }

    private void initializeLighting() {
        gridLighting = createLightingEffect(-90.0, 30.0, 8.0);
        slotLighting = createLightingEffect(45.0, 10.0, 5.0);
        tokenLighting = createLightingEffect(10.0, 120.0, 6.0);
    }

    private Lighting createLightingEffect(double Azimuth, double Elevation, double surfaceScale) {
        Light.Distant light = new Light.Distant();
        light.setAzimuth(Azimuth);
        light.setElevation(Elevation);

        Lighting lighting = new Lighting();
        lighting.setLight(light);
        lighting.setSurfaceScale(surfaceScale);

        return lighting;
    }

    public void changeStatusLable(Optional<Integer> player, Optional<String> event, Optional<Integer> column) {
        String currentStyle = statusLabel.getStyle();
        statusLabel.setStyle(currentStyle);
        if (player.isPresent() && event.isPresent()) {
            statusLabel.setText("Player " + player.get() + " " + event.get() + "!");
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
            statusLabel.setText("Column " + (column.get() + 1) + " is full!");
        } else if (event.isPresent()) {
            currentStyle += " -fx-text-fill: rgb(34, 34, 46); -fx-underline: true;";
            statusLabel.setStyle(currentStyle);
            statusLabel.setText(event.get());
        }
    }

}
