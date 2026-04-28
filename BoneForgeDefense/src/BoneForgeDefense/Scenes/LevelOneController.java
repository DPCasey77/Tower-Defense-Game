package BoneForgeDefense.Scenes;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.animation.AnimationTimer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

import BoneForgeDefense.Node;
import BoneForgeDefense.PathFinder;
import BoneForgeDefense.Entities.Skeleton;
import BoneForgeDefense.Entities.Tower;
import BoneForgeDefense.Entities.Projectile;
import BoneForgeDefense.Entities.Skeletons.SkeletonEnemy;
import BoneForgeDefense.Entities.OffensiveTowers.BoneBusterTower;
import BoneForgeDefense.Entities.OffensiveTowers.OffensiveTower;
import BoneForgeDefense.Entities.DefensiveTowers.ShieldTower;
import BoneForgeDefense.Entities.SupportTowers.SlowTower;
import BoneForgeDefense.Entities.SupportTowers.SupportTower;


public class LevelOneController {

	// Game state variables
    private long lastNanoSecond = 0;
    private Boolean gameOver = false;

    private double bones;
    private int kills;
    private int lives;

    // Starting lives for a new game
    private static final int STARTING_LIVES = 20;

    // Seconds between each skeleton spawn (chosen randomly within this range)
    private static final double SPAWN_INTERVAL_MIN = 3.0;
    private static final double SPAWN_INTERVAL_MAX = 8.0;

    // Map / grid variables
    private List<Node> path = new ArrayList<>();   // ordered list of nodes from start to end
    public static Node[][] mapNodes;                     // full grid of node data (used for cell styles and hover highlights)
    private Pane[][] gridCells = new Pane[MAP_ROWS][MAP_COLS]; // references to each visual grid cell
    private GridPane gameGrid;

    // Skeleton tracking variables
    private final List<Skeleton> activeSkeletons  = new ArrayList<>();

    // Projectile tracking variables
    private final List<Projectile> activeProjectiles = new ArrayList<>();
    private double spawnAccumulator = 0;  // time elapsed since the last spawn
    private double nextSpawnTime = 0;     // seconds until the next spawn (re-randomized after each spawn)
    private final Random random = new Random();

    // Tower placement variables
    // When the player clicks a tower card, pendingTower holds the selected tower
    // Pressing Esc cancels
    private Tower pendingTower = null;
    // A factory that creates a fresh instance of the selected tower type on placement
    private Supplier<Tower> pendingTowerSupplier = null;
    // Tracks which tower occupies each grid cell (null = empty)
    private Tower[][] placedTowers = new Tower[MAP_ROWS][MAP_COLS];

    // Range indicator drawn while the cursor is over a placed tower; null when nothing is hovered
    private Circle hoverRangeCircle = null;

    // FXML bindings
    @FXML private Label bonesTextbox;
    @FXML private Label killsTextbox;
    @FXML private Label livesTextbox;
    @FXML private Label placementStatusLabel; // shows instructions while in tower placement mode

    @FXML private StackPane gameMapPane;

    @FXML private HBox offensiveTowerContainer;
    @FXML private HBox defensiveTowerContainer;
    @FXML private HBox supportTowerContainer;

    // Map definition 
    // 0 = open (tower-placeable), 1 = wall (tower-placeable), 2 = start, 3 = end
    public static final int MAP_ROWS = 50;
    public static final int MAP_COLS = 50;
    private static final int[][] MAP = {
        {0,0,0,0,1,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,1,0,0,0,0,0},
        {0,1,1,0,1,0,1,1,1,1,1,0,1,0,1,1,1,1,1,0,1,0,1,1,1,1,1,0,1,0,1,1,1,1,1,0,1,0,1,1,1,1,1,0,1,0,1,1,1,0},
        {0,0,0,0,1,0,0,0,1,0,0,0,1,0,0,0,1,0,0,0,1,0,0,0,1,0,0,0,1,0,0,0,1,0,0,0,1,0,0,0,1,0,0,0,1,0,0,0,1,0},
        {1,1,1,0,1,1,1,0,1,0,1,1,1,1,1,0,1,0,1,1,1,1,1,0,1,0,1,1,1,1,1,0,1,0,1,1,1,1,1,0,1,0,1,1,1,1,1,0,1,0},
        {0,0,0,0,0,0,1,0,0,0,1,0,0,0,0,0,1,0,0,0,0,0,1,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,1,0,0,0},
        {0,1,1,1,1,0,1,1,1,0,1,0,1,1,1,1,1,1,1,1,1,0,1,1,1,1,1,1,1,0,1,1,1,1,1,1,1,0,1,1,1,1,1,1,1,0,1,1,1,0},
        {0,1,0,0,0,0,0,0,1,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,1,0,0,0,0,0,1,0},
        {0,1,0,1,1,1,1,1,1,1,1,1,1,1,1,0,1,0,1,1,1,1,1,1,1,0,1,0,1,1,1,1,1,0,1,0,1,1,1,1,1,0,1,0,1,1,1,0,1,0},
        {0,1,0,0,0,0,1,0,0,0,0,0,1,0,0,0,1,0,0,0,1,0,0,0,1,0,0,0,0,0,1,0,0,0,1,0,0,0,1,0,0,0,1,0,0,0,1,0,1,0},
        {0,1,1,1,1,0,1,0,1,1,1,0,1,0,1,1,1,1,1,0,1,0,1,0,1,1,1,1,1,0,1,0,1,0,1,1,1,0,1,0,1,1,1,1,1,0,1,0,1,0},
        {0,0,0,0,1,0,0,0,1,0,0,0,0,0,0,0,0,0,1,0,0,0,1,0,0,0,0,0,1,0,0,0,1,0,0,0,0,0,1,0,0,0,0,0,1,0,0,0,0,0},
        {1,1,1,0,1,1,1,1,1,1,1,1,1,1,1,1,1,0,1,1,1,0,1,1,1,1,1,0,1,1,1,0,1,1,1,1,1,0,1,1,1,1,1,0,1,1,1,1,1,0},
        {0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,1,0,0,0,1,0,0,0,0,0,1,0,0,0,1,0,0,0,0,0,1,0,0,0,0,0,1,0,0,0,0,0,1,0},
        {0,1,1,1,1,1,1,0,1,0,1,1,1,1,1,0,1,1,1,0,1,1,1,1,1,0,1,0,1,1,1,1,1,1,1,0,1,1,1,1,1,0,1,1,1,1,1,0,1,0},
        {0,1,0,0,0,0,1,0,0,0,1,0,0,0,1,0,0,0,1,0,0,0,0,0,1,0,0,0,1,0,0,0,1,0,0,0,0,0,1,0,0,0,1,0,0,0,1,0,0,0},
        {0,1,0,1,1,0,1,1,1,0,1,0,1,0,1,1,1,0,1,1,1,1,1,0,1,0,1,1,1,0,1,0,1,0,1,1,1,0,1,0,1,0,1,0,1,0,1,1,1,0},
        {0,0,0,1,0,0,0,0,1,0,0,0,1,0,0,0,1,0,0,0,1,0,0,0,1,0,0,0,1,0,0,0,1,0,0,0,1,0,0,0,1,0,0,0,1,0,0,0,1,0},
        {1,1,1,1,1,1,1,0,1,1,1,1,1,1,1,0,1,1,1,0,1,0,1,1,1,1,1,0,1,1,1,0,1,1,1,0,1,1,1,1,1,1,1,0,1,1,1,0,1,0},
        {0,0,0,0,0,0,1,0,0,0,0,0,0,0,1,0,0,0,1,0,0,0,1,0,0,0,1,0,0,0,1,0,0,0,1,0,0,0,0,0,0,0,1,0,0,0,1,0,1,0},
        {0,1,1,1,1,0,1,1,1,1,1,1,1,0,1,1,1,0,1,1,1,1,1,0,1,0,1,0,1,0,1,1,1,0,1,1,1,1,1,1,1,0,1,1,1,0,1,0,1,0},
        {0,1,0,0,1,0,0,0,0,0,0,0,1,0,0,0,1,0,0,0,0,0,0,0,1,0,0,0,1,0,0,0,1,0,0,0,0,0,0,0,1,0,0,0,1,0,0,0,1,0},
        {0,1,0,1,1,1,1,1,1,1,1,0,1,1,1,0,1,1,1,1,1,1,1,1,1,1,1,0,1,1,1,0,1,1,1,1,1,1,1,0,1,1,1,0,1,1,1,0,1,0},
        {0,1,0,0,0,0,1,0,0,0,1,0,0,0,1,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,1,0,0,0,0,0,1,0,0,0,1,0,0,0,0,0},
        {0,1,1,1,1,0,1,0,1,0,1,1,1,0,1,1,1,0,1,0,1,1,1,1,1,1,1,1,1,0,1,1,1,0,1,0,1,1,1,1,1,0,1,1,1,1,1,1,1,0},
        {0,0,0,0,1,0,1,0,1,0,0,0,1,0,0,0,1,0,0,0,1,0,0,0,1,0,0,0,1,0,0,0,1,0,1,0,0,0,1,0,0,0,1,0,0,0,0,0,0,0},
        {1,1,1,0,1,0,1,0,1,1,1,0,1,1,1,1,1,1,1,1,1,0,1,0,1,0,1,0,1,1,1,0,1,0,1,1,1,0,1,0,1,1,1,1,1,1,1,1,1,0},
        {0,0,1,0,1,0,1,0,0,0,1,0,0,0,1,0,0,0,0,0,0,0,1,0,1,0,1,0,0,0,1,0,1,0,0,0,1,0,1,0,0,0,0,0,0,0,0,0,1,0},
        {0,1,1,0,1,0,1,1,1,0,1,1,1,0,1,0,1,1,1,1,1,1,1,0,1,0,1,1,1,0,1,0,1,1,1,0,1,0,1,1,1,1,1,1,1,1,1,0,1,0},
        {0,0,0,0,1,0,0,0,1,0,0,0,1,0,0,0,1,0,0,0,0,0,0,0,1,0,0,0,1,0,0,0,0,0,1,0,0,0,1,0,0,0,0,0,0,0,1,0,1,0},
        {1,1,1,1,1,1,1,0,1,1,1,0,1,1,1,1,1,0,1,1,1,1,1,1,1,1,1,0,1,1,1,1,1,0,1,1,1,1,1,0,1,1,1,1,1,0,1,0,1,0},
        {0,0,0,0,0,0,0,0,1,0,0,0,0,0,1,0,0,0,1,0,0,0,1,0,0,0,1,0,0,0,1,0,0,0,0,0,1,0,0,0,0,0,1,0,0,0,1,0,1,0},
        {0,1,1,1,1,1,1,1,1,0,1,1,1,0,1,0,1,1,1,0,1,0,1,0,1,0,1,1,1,0,1,1,1,1,1,0,1,0,1,1,1,0,1,0,1,1,1,0,1,0},
        {0,1,0,0,0,0,1,0,0,0,1,0,0,0,1,0,0,0,0,0,1,0,0,0,1,0,0,0,1,0,0,0,0,0,1,0,0,0,1,0,0,0,1,0,0,0,1,0,1,0},
        {0,1,0,1,1,0,1,1,1,1,1,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,1,1,1,1,1,0,1,1,1,1,1,0,1,1,1,1,1,0,1,0,1,0},
        {0,1,0,1,0,0,0,0,1,0,0,0,1,0,0,0,0,0,1,0,0,0,0,0,0,0,1,0,0,0,1,0,0,0,0,0,1,0,0,0,1,0,0,0,1,0,0,0,1,0},
        {0,1,0,1,0,1,1,0,1,0,1,1,1,0,1,1,1,0,1,0,1,1,1,1,1,0,1,1,1,0,1,0,1,1,1,0,1,0,1,1,1,0,1,0,1,1,1,1,1,0},
        {0,1,0,0,0,1,0,0,0,0,1,0,0,0,1,0,0,0,1,0,0,0,1,0,0,0,0,0,1,0,0,0,1,0,0,0,1,0,0,0,1,0,0,0,0,0,0,0,1,0},
        {0,1,1,1,1,1,1,1,1,1,1,0,1,1,1,0,1,1,1,1,1,0,1,1,1,1,1,0,1,1,1,1,1,0,1,1,1,1,1,0,1,1,1,1,1,1,1,0,1,0},
        {0,0,0,0,1,0,0,0,0,0,0,0,1,0,0,0,1,0,0,0,1,0,0,0,1,0,0,0,0,0,1,0,0,0,1,0,0,0,0,0,0,0,0,0,1,0,0,0,1,0},
        {1,1,1,0,1,0,1,1,1,1,1,1,1,0,1,1,1,0,1,0,1,1,1,0,1,1,1,1,1,0,1,1,1,0,1,1,1,1,1,1,1,1,1,0,1,0,1,1,1,0},
        {0,0,1,0,1,0,1,0,0,0,1,0,0,0,1,0,0,0,1,0,0,0,1,0,0,0,1,0,0,0,0,0,1,0,0,0,0,0,0,0,1,0,0,0,1,0,1,0,0,0},
        {0,0,1,0,1,0,1,0,1,0,1,0,1,1,1,0,1,1,1,1,1,0,1,1,1,0,1,1,1,1,1,1,1,1,1,1,1,1,1,0,1,0,1,1,1,0,1,1,1,1},
        {0,0,1,0,1,0,0,0,1,0,0,0,1,0,0,0,1,0,0,0,1,0,0,0,1,0,0,0,0,0,0,0,1,0,0,0,1,0,0,0,1,0,1,0,0,0,1,0,0,0},
        {1,1,1,0,1,1,1,1,1,1,1,1,1,0,1,1,1,0,1,0,1,1,1,0,1,1,1,1,1,1,1,0,1,1,1,0,1,0,1,1,1,0,1,0,1,1,1,1,1,0},
        {0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,1,0,0,0,1,0,0,0,1,0,0,0,1,0,0,0,1,0,0,0,1,0,0,0,1,0,0,0,0,0,1,0},
        {0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,1,1,1,1,1,0,1,1,1,0,1,0,1,0,1,1,1,0,1,1,1,0,1,1,1,1,1,1,1,1,1,0,1,0},
        {0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,1,0,0,0,1,0,0,0,1,0,0,0,1,0,0,0,1,0,0,0,0,0,0,0,1,0,0,0,1,0},
        {1,1,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,1,0,1,1,1,0,1,1,1,1,1,1,1,0,1,1,1,0,1,1,1,1,1,1,1,0,1,0,1,1,1,0},
        {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
        {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0}
    };

    // Starts the JavaFX AnimationTimer, which calls update() every frame
    public void startGameLoop() {
        new AnimationTimer() {
            @Override
            public void handle(long now) {

                // Skip the first frame so delta is not calculated from time 0
                if (lastNanoSecond == 0) {
                    lastNanoSecond = now;
                    return;
                }

                // Convert nanoseconds to seconds elapsed since the last frame
                double delta = (now - lastNanoSecond) / 1_000_000_000.0;
                lastNanoSecond = now;

                // Pause all updates while the game is over
                if (!gameOver) {
                    update(delta);
                }

            }
        }.start();
    }

    // Builds one shop card per tower and adds it to the correct tab container
    // Each tower is paired with a Supplier so a new instance can be created when placed on the grid
    private void loadTowerCards() {
        offensiveTowerContainer.getChildren().add(
            createTowerCard(new BoneBusterTower(0, 0), () -> new BoneBusterTower(0, 0)));
        defensiveTowerContainer.getChildren().add(
            createTowerCard(new ShieldTower(0, 0), () -> new ShieldTower(0, 0)));
        supportTowerContainer.getChildren().add(
            createTowerCard(new SlowTower(0, 0), () -> new SlowTower(0, 0)));
    }

    // Tower card display constants
    private static final double CARD_WIDTH  = 100;
    private static final double CARD_HEIGHT = 150;
    private static final double SPRITE_WIDTH  = 64;
    private static final double SPRITE_HEIGHT = 64;

    // Creates a VBox card showing the tower's sprite, name, and cost.
    // Clicking the card enters placement mode for that tower type.
    // The 'factory' parameter is a function that creates a brand-new tower
    // instance when the player confirms placement on the grid.
    private VBox createTowerCard(Tower tower, Supplier<Tower> factory) {
        ImageView sprite = new ImageView(tower.getSprite().getImage());
        sprite.setFitWidth(SPRITE_WIDTH);
        sprite.setFitHeight(SPRITE_HEIGHT);

        Label name = new Label(tower.getName());
        Label cost = new Label("Cost: " + tower.getCost());

        VBox towerCard = new VBox(5, sprite, name, cost);
        towerCard.setAlignment(javafx.geometry.Pos.CENTER);
        towerCard.setPrefWidth(CARD_WIDTH);
        towerCard.setPrefHeight(CARD_HEIGHT);
        towerCard.setMinWidth(CARD_WIDTH);
        towerCard.setMaxWidth(CARD_WIDTH);

        // Clicking the card begins the placement process
        towerCard.setOnMouseClicked(e -> startPlacementMode(tower, factory));

        return towerCard;
    }

    // Enters tower placement mode for the given tower type
    // Pressing Esc cancels
    private void startPlacementMode(Tower tower, Supplier<Tower> factory) {
        if (bones < tower.getCost()) {
            placementStatusLabel.setText("Not enough bones for " + tower.getName() + "!");
            return;
        }
        pendingTower = tower;
        pendingTowerSupplier = factory;
        placementStatusLabel.setText(
            "Placing: " + tower.getName() + " (cost: " + tower.getCost() + ")"
            + "  |  Esc to cancel");
        // Change the cursor to a crosshair to signal placement mode is active
        gameMapPane.setCursor(Cursor.CROSSHAIR);
    }

    // Exits placement mode without placing a tower
    private void cancelPlacementMode() {
        pendingTower = null;
        pendingTowerSupplier = null;
        placementStatusLabel.setText("");
        gameMapPane.setCursor(Cursor.DEFAULT);
    }

    // Called when a grid cell is clicked
    private void onCellClicked(int row, int col) {
        // Ignore clicks when not in placement mode
        if (pendingTower == null) return;

        // Only cells marked 0 (open) or 1 (wall) accept towers
        int cellType = MAP[row][col];
        if (cellType != 0 && cellType != 1) return;

        // Block stacking a second tower on the same cell
        if (placedTowers[row][col] != null) return;

        // Re-check if player can afford tower
        if (bones < pendingTower.getCost()) {
            placementStatusLabel.setText("Not enough bones!");
            cancelPlacementMode();
            return;
        }

        placeTowerAt(row, col);
    }

    // Creates a fresh tower instance, renders it in the cell and deducts the cost
    private void placeTowerAt(int row, int col) {
        // Instantiate a tower
        Tower newTower = pendingTowerSupplier.get();
        placedTowers[row][col] = newTower;

        // Bind the tower sprite size to the cell so it scales with window resizes
        Pane targetCell = gridCells[row][col];
        ImageView towerSprite = newTower.getSprite();
        towerSprite.setPreserveRatio(false);
        towerSprite.fitWidthProperty().bind(targetCell.widthProperty());
        towerSprite.fitHeightProperty().bind(targetCell.heightProperty());
        targetCell.getChildren().add(towerSprite);

        // Show the tower's range indicator while the cursor hovers over its sprite
        if (newTower.getRange() > 0) {
            towerSprite.setOnMouseEntered(e -> showRangeCircle(row, col, newTower));
            towerSprite.setOnMouseExited(e -> hideRangeCircle());
        }

        // Deduct bones only now that the tower is placed
        bones -= pendingTower.getCost();
        bonesTextbox.setText(String.format("%.0f", bones));

        cancelPlacementMode();
    }

    // Adds a circle range indicator centered on the given tower
    private void showRangeCircle(int row, int col, Tower tower) {
        hideRangeCircle();

        // Get the tower's pixel center each time since the grid scales with the window
        double cellSize   = gameGrid.getWidth() / MAP_COLS;
        double gridStartX = gameGrid.getBoundsInParent().getMinX();
        double gridStartY = gameGrid.getBoundsInParent().getMinY();
        double centerX = gridStartX + col * cellSize + cellSize / 2.0;
        double centerY = gridStartY + row * cellSize + cellSize / 2.0;

        double range = tower.getRange();
        Circle circle = new Circle(range);
        circle.setFill(Color.TRANSPARENT);
        circle.setStroke(Color.WHITE);
        circle.setStrokeWidth(2);
        // Don't let the circle steal mouse events from the tower or cells underneath
        circle.setMouseTransparent(true);
        
        // Subtract the radius of the circle to put it at (centerX, centerY).
        circle.setTranslateX(centerX - range);
        circle.setTranslateY(centerY - range);
        
        // Shift the circle's bounds to make the top left corner at (0,0)
        StackPane.setAlignment(circle, Pos.TOP_LEFT);
        gameMapPane.getChildren().add(circle);
        hoverRangeCircle = circle;
    }

    // Removes the currently displayed range indicator if one is active
    private void hideRangeCircle() {
        if (hoverRangeCircle != null) {
            gameMapPane.getChildren().remove(hoverRangeCircle);
            hoverRangeCircle = null;
        }
    }

    // Returns the CSS background color for a cell based on its node type
    private String getCellStyle(Node node) {
        if (node.getStart()) return "-fx-background-color: #2EA832;";
        if (node.getEnd())   return "-fx-background-color: #C43030;";
        if (node.getWall())  return "-fx-background-color: #555555;";
        return "-fx-background-color: #6B8C52;";
    }

    // Runs PathFinder on the map, then builds and populates the visual GridPane.
    // Handle per-cell click and hover handlers for tower placement.
    private void buildGameGrid() {
        PathFinder pathFinder = new PathFinder(MAP_COLS, MAP_ROWS, MAP,0,0,MAP_COLS-1,MAP_ROWS-1);
        
        mapNodes = pathFinder.getNodes();
        path = pathFinder.getOrderedPath();

        // Constrain the grid to a square so cells are always square
        GridPane grid = new GridPane();
        gameGrid = grid;
        var squareSize = Bindings.min(gameMapPane.widthProperty(), gameMapPane.heightProperty());
        grid.maxWidthProperty().bind(squareSize);
        grid.maxHeightProperty().bind(squareSize);

        // Divide the grid evenly across all columns and rows using percentage constraints
        for (int col = 0; col < MAP_COLS; col++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setPercentWidth(100.0 / MAP_COLS);
            grid.getColumnConstraints().add(cc);
        }
        for (int row = 0; row < MAP_ROWS; row++) {
            RowConstraints rc = new RowConstraints();
            rc.setPercentHeight(100.0 / MAP_ROWS);
            grid.getRowConstraints().add(rc);
        }

        // Create a colored Pane for each cell, wire placement handlers, and store a reference
        for (int row = 0; row < MAP_ROWS; row++) {
            for (int col = 0; col < MAP_COLS; col++) {
                Pane cell = new Pane();
                cell.setMaxWidth(Double.MAX_VALUE);
                cell.setMaxHeight(Double.MAX_VALUE);
                cell.setStyle(getCellStyle(mapNodes[row][col]));
                gridCells[row][col] = cell;
                grid.add(cell, col, row);

                // Capture row/col in final variables for use inside the lambda closures
                final int r = row, c = col;

                // While in placement mode, highlight the cell green (valid) or red (invalid) on hover
                cell.setOnMouseEntered(e -> {
                    if (pendingTower != null) {
                        boolean isValidSpot = (MAP[r][c] == 0 || MAP[r][c] == 1)
                                              && placedTowers[r][c] == null;
                        cell.setStyle(isValidSpot
                            ? "-fx-background-color: #90EE90;"   // light green = can place here
                            : "-fx-background-color: #FF6B6B;"); // light red   = cannot place here
                    }
                });

                // Always restore the natural cell color when the mouse leaves
                cell.setOnMouseExited(e -> cell.setStyle(getCellStyle(mapNodes[r][c])));

                // Attempt to place the pending tower when the player clicks this cell
                cell.setOnMouseClicked(e -> onCellClicked(r, c));
            }
        }

        gameMapPane.getChildren().add(grid);

        // Reposition all active skeletons whenever the pane is resized
        gameMapPane.widthProperty().addListener((obs, old, newVal) ->
            activeSkeletons.forEach(s -> s.updatePosition(path, gameGrid, MAP_COLS)));
        gameMapPane.heightProperty().addListener((obs, old, newVal) ->
            activeSkeletons.forEach(s -> s.updatePosition(path, gameGrid, MAP_COLS)));

        // Register the Escape key to cancel placement mode.
        // Wait until the scene is attached because the scene is not yet
        // available when buildGameGrid() runs
        gameMapPane.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.setOnKeyPressed(e -> {
                    if (e.getCode() == KeyCode.ESCAPE) {
                        cancelPlacementMode();
                    }
                });
            }
        });
    }

    // Creates a new SkeletonEnemy, adds its sprite to the map, and places it at the start of the path
    private void spawnSkeleton() {
        SkeletonEnemy skeleton = new SkeletonEnemy(0, 0);
        gameMapPane.getChildren().add(skeleton.getSprite());

        activeSkeletons.add(skeleton);
        skeleton.updatePosition(path, gameGrid, MAP_COLS);
    }

    // Initializes resources and starts the game
    public void startNewGame(double bones) {
        this.bones = bones;
        this.kills = 0;
        this.lives = STARTING_LIVES;
        bonesTextbox.setText(String.format("%.0f", bones));
        killsTextbox.setText(String.valueOf(kills));
        livesTextbox.setText(String.valueOf(lives));
        buildGameGrid();
        startGameLoop();
        loadTowerCards();
    }

    // Delegates each frame to all game subsystems
    private void update(double delta) {
        updateSpawner(delta);

        // Reset every skeleton's speed mod so slow towers can re-apply their effect this frame
        Skeleton.resetSpeedMods();

        // Iterate every placed tower and apply the appropriate per-frame logic
        // Pixel dimensions are calculated here because the controller controls the grid layout
        double cellSize   = gameGrid.getWidth() / MAP_COLS;
        double gridStartX = gameGrid.getBoundsInParent().getMinX();
        double gridStartY = gameGrid.getBoundsInParent().getMinY();
        for (int row = 0; row < MAP_ROWS; row++) {
            for (int col = 0; col < MAP_COLS; col++) {
                Tower tower = placedTowers[row][col];
                if (tower == null) continue;

                // The pixel center of this tower's grid cell
                double towerPixelX = gridStartX + col * cellSize + cellSize / 2.0;
                double towerPixelY = gridStartY + row * cellSize + cellSize / 2.0;

                if (tower instanceof OffensiveTower) {
                    // Offensive towers handle targeting, timer, and projectile creation
                    Projectile proj = ((OffensiveTower) tower).update(delta, towerPixelX, towerPixelY);
                    if (proj == null) continue;

                    // Overlay the new projectile on the game map
                    StackPane.setAlignment(proj.getShape(), Pos.TOP_LEFT);
                    activeProjectiles.add(proj);
                    gameMapPane.getChildren().add(proj.getShape());
                } else if (tower instanceof SupportTower) {
                    // Support towers apply effects to in-range entities
                    ((SupportTower) tower).update(delta, towerPixelX, towerPixelY);
                }
            }
        }

        // Move projectiles and apply damage on hit
        // Remove spent ones from the scene and tracking list
        Projectile.updateAll(delta, activeProjectiles, this::killSkeleton)
            .forEach(p -> {
                gameMapPane.getChildren().remove(p.getShape());
                activeProjectiles.remove(p);
            });

        // Advance every skeleton
        // escapeSkeleton handles cleanup and life deduction for those that finish
        Skeleton.updateAll(delta, path, gameGrid, MAP_COLS).forEach(this::escapeSkeleton);
    }

    // Called when a skeleton is destroyed by a tower
    // Awards bones and increments kill counter.
    private void killSkeleton(SkeletonEnemy skeleton) {
        skeleton.removeFromScene(gameMapPane);
        activeSkeletons.remove(skeleton);
        bones += skeleton.getBoneReward();
        bonesTextbox.setText(String.format("%.0f", bones));
        kills++;
        killsTextbox.setText(String.valueOf(kills));
    }

    // Called when a skeleton reaches the path end
    // Decrements lives and gives no bone reward.
    private void escapeSkeleton(Skeleton skeleton) {
        skeleton.removeFromScene(gameMapPane);
        activeSkeletons.remove(skeleton);
        lives--;
        livesTextbox.setText(String.valueOf(lives));
    }

    // Tracks elapsed time and spawns a new skeleton when the random interval expires
    private void updateSpawner(double delta) {
        spawnAccumulator += delta;
        if (spawnAccumulator >= nextSpawnTime) {
            spawnAccumulator = 0;
            // Pick a new random delay for the next spawn
            nextSpawnTime = SPAWN_INTERVAL_MIN + random.nextDouble() * (SPAWN_INTERVAL_MAX - SPAWN_INTERVAL_MIN);
            spawnSkeleton();
        }
    }

    public double getBones() {
        return bones;
    }

    public void setBones(double bones) {
        this.bones = bones;
    }

}
