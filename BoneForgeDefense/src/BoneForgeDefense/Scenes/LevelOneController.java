package BoneForgeDefense.Scenes;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.animation.AnimationTimer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import BoneForgeDefense.Node;
import BoneForgeDefense.PathFinder;
import BoneForgeDefense.Entities.Skeleton;
import BoneForgeDefense.Entities.Tower;
import BoneForgeDefense.Entities.Skeletons.SkeletonEnemy;
import BoneForgeDefense.Entities.OffensiveTowers.BoneBusterTower;
import BoneForgeDefense.Entities.DefensiveTowers.ShieldTower;
import BoneForgeDefense.Entities.SupportTowers.SlowTower;


public class LevelOneController {

	// --- Game state ---
	private long lastNanoSecond = 0;
	private Boolean gameOver = false;

	private double bones;
	private double money;

	// --- Enemy spawning ---
	// Seconds between each skeleton spawn (chosen randomly within this range)
	private static final double SPAWN_INTERVAL_MIN = 3.0;
	private static final double SPAWN_INTERVAL_MAX = 8.0;

	// --- Map / grid ---
	private List<Node> path = new ArrayList<>();   // ordered list of nodes from start to end
	private Pane[][] gridCells = new Pane[MAP_ROWS][MAP_COLS]; // visual cell references
	private GridPane gameGrid;

	// --- Skeleton tracking ---
	private final List<SkeletonEnemy> activeSkeletons = new ArrayList<>();
	private double spawnAccumulator = 0;  // time elapsed since the last spawn
	private double nextSpawnTime = 0;     // seconds until the next spawn (re-randomized after each spawn)
	private final Random random = new Random();

	// --- FXML bindings ---
	@FXML private Label bonesTextbox;
    @FXML private Label moneyTextbox;

    @FXML private StackPane gameMapPane;

    @FXML private HBox offensiveTowerContainer;
    @FXML private HBox defensiveTowerContainer;
    @FXML private HBox supportTowerContainer;

	// --- Map definition ---
	// 0 = open (tower-placeable), 1 = wall, 2 = start, 3 = end
    private static final int MAP_ROWS = 50;
    private static final int MAP_COLS = 50;
    private static final int[][] MAP = {
        {2,0,0,0,1,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,1,0,0,0,0,0},
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
        {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,3}
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
	private void loadTowerCards() {
		List<Tower> offensiveTowers = List.of(new BoneBusterTower(0, 0));
	    List<Tower> defensiveTowers = List.of(new ShieldTower(0, 0));
	    List<Tower> supportTowers   = List.of(new SlowTower(0, 0));

	    for (Tower t : offensiveTowers) offensiveTowerContainer.getChildren().add(createTowerCard(t));
	    for (Tower t : defensiveTowers) defensiveTowerContainer.getChildren().add(createTowerCard(t));
	    for (Tower t : supportTowers)   supportTowerContainer.getChildren().add(createTowerCard(t));
	}

	// Tower card display constants
	private static final double CARD_WIDTH = 100;
	private static final double CARD_HEIGHT = 150;
	private static final double SPRITE_WIDTH = 64;
	private static final double SPRITE_HEIGHT = 64;

	// Creates a VBox card for a given tower containing its sprite, name, and cost
	private VBox createTowerCard(Tower tower) {
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

	    // Clicking the card attempts to purchase the tower
	    towerCard.setOnMouseClicked(e -> buyTower(tower));

	    return towerCard;
	}

	// Deducts the tower's cost if the player has enough money
	private void buyTower(Tower tower) {
		if (money >= tower.getCost()) {
	        money -= tower.getCost();
	        moneyTextbox.setText(String.format("%.0f", money));
	        System.out.println("You bought the " + tower.getName() + "!");
			} else {
				System.out.println("You can't afford the " + tower.getName() + "!");
			}
	}

	// Returns the CSS background color for a cell based on its node type
	private String getCellStyle(Node node) {
        if (node.getStart()) return "-fx-background-color: #2EA832;";
        if (node.getEnd())   return "-fx-background-color: #C43030;";
        if (node.getWall())  return "-fx-background-color: #555555;";
        return "-fx-background-color: #6B8C52;";
    }

	// Runs PathFinder on the map, then builds and populates the visual GridPane
	private void buildGameGrid() {
        PathFinder pathFinder = new PathFinder(MAP_COLS, MAP_ROWS, MAP);
        pathFinder.search();
        Node[][] nodes = pathFinder.getNodes();
        path = pathFinder.getOrderedPath();

        // Constrain the grid to a square (min of pane width and height) so cells are always square
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

        // Create a colored Pane for each cell and store a reference for later use
        for (int row = 0; row < MAP_ROWS; row++) {
            for (int col = 0; col < MAP_COLS; col++) {
                Pane cell = new Pane();
                cell.setMaxWidth(Double.MAX_VALUE);
                cell.setMaxHeight(Double.MAX_VALUE);
                cell.setStyle(getCellStyle(nodes[row][col]));
                gridCells[row][col] = cell;
                grid.add(cell, col, row);
            }
        }

        gameMapPane.getChildren().add(grid);

        // Reposition all active skeletons whenever the pane is resized
        gameMapPane.widthProperty().addListener((obs, old, newVal) ->
            activeSkeletons.forEach(this::positionSkeleton));
        gameMapPane.heightProperty().addListener((obs, old, newVal) ->
            activeSkeletons.forEach(this::positionSkeleton));
    }

	// Creates a new SkeletonEnemy, adds its sprite to the map, and places it at the start of the path
    private void spawnSkeleton() {
        SkeletonEnemy skeleton = new SkeletonEnemy(0, 0, "/BoneForgeDefense/Sprites/skeleton.png");

        // Configure the sprite inherited from Entity for overlay display
        skeleton.getSprite().setPreserveRatio(false);
        // TOP_LEFT alignment lets translateX/Y position the sprite from the pane's top-left corner
        StackPane.setAlignment(skeleton.getSprite(), Pos.TOP_LEFT);
        gameMapPane.getChildren().add(skeleton.getSprite());

        activeSkeletons.add(skeleton);
        positionSkeleton(skeleton);
    }

    // Moves the skeleton's image to the correct pixel position on screen.
    // Because progress is a decimal (e.g. 3.75), the skeleton sits between two path
    // nodes and we blend between them so the movement looks smooth.
    private void positionSkeleton(SkeletonEnemy skeleton) {

        // --- Step 1: Figure out which two path nodes the skeleton is between ---

        // The whole number part of progress is the index of the node the skeleton just passed.
        // Example: progress = 3.75  →  currentNodeIndex = 3
        int currentNodeIndex = (int) skeleton.getProgress();

        // Safety check: make sure there is always a "next" node to move toward.
        // Without this, we could crash when the skeleton is near the very end.
        currentNodeIndex = Math.min(currentNodeIndex, path.size() - 2);

        // The decimal part (0.0 to 1.0) tells us how far between the two nodes we are.
        // Example: progress = 3.75  →  blendFactor = 0.75  (75% of the way to the next node)
        double blendFactor = skeleton.getProgress() - currentNodeIndex;

        // --- Step 2: Blend between the two nodes to get a smooth in-between position ---

        Node currentNode = path.get(currentNodeIndex);
        Node nextNode    = path.get(currentNodeIndex + 1);

        // Interpolate: start at currentNode and move blendFactor of the way toward nextNode.
        // Note: getY() = column, getX() = row (this is how PathFinder stores coordinates)
        double blendedColumn = currentNode.getY() + blendFactor * (nextNode.getY() - currentNode.getY());
        double blendedRow    = currentNode.getX() + blendFactor * (nextNode.getX() - currentNode.getX());

        // --- Step 3: Convert the grid position to pixels on screen ---

        // How wide/tall is one grid cell in pixels?
        double cellSizePixels = gameGrid.getWidth() / MAP_COLS;

        // The grid is centered inside the pane, so we need to know how far from the
        // pane's top-left corner the grid actually starts.
        double gridStartX = gameGrid.getBoundsInParent().getMinX();
        double gridStartY = gameGrid.getBoundsInParent().getMinY();

        // Resize the skeleton image to match one cell, then place it at the right spot
        skeleton.getSprite().setFitWidth(cellSizePixels);
        skeleton.getSprite().setFitHeight(cellSizePixels);
        skeleton.getSprite().setTranslateX(gridStartX + blendedColumn * cellSizePixels);
        skeleton.getSprite().setTranslateY(gridStartY + blendedRow    * cellSizePixels);
    }

	// Initializes resources and starts the game
	public void startNewGame(double bones, double money) {
		this.bones = bones;
		this.money = money;
		bonesTextbox.setText(String.format("%.0f", bones));
		moneyTextbox.setText(String.format("%.0f", money));
		buildGameGrid();
		startGameLoop();
		loadTowerCards();
	}

	// Delegates each frame to the spawner and skeleton movement logic
	private void update(double delta) {
		updateSpawner(delta);
		updateSkeletons(delta);
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

	// Advances every active skeleton along the path and removes any that have reached the end
	private void updateSkeletons(double delta) {
		List<SkeletonEnemy> toRemove = new ArrayList<>();
		for (SkeletonEnemy skeleton : activeSkeletons) {
			skeleton.setProgress(skeleton.getProgress() + delta); // progress advances at 1 cell per second
			if (skeleton.getProgress() >= path.size() - 1) {
				toRemove.add(skeleton);
				continue;
			}
			positionSkeleton(skeleton);
		}
		// Remove finished skeletons from the scene, the enemy list, and the active list
		toRemove.forEach(s -> {
			gameMapPane.getChildren().remove(s.getSprite());
			Skeleton.enemyList.remove(s);
			activeSkeletons.remove(s);
		});
	}

	public double getBones() {
		return bones;
	}

	public void setBones(double bones) {
		this.bones = bones;
	}

	public double getMoney() {
		return money;
	}

	public void setMoney(double money) {
		this.money = money;
	}

}
