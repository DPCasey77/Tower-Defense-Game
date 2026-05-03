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
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;

import BoneForgeDefense.Maps;
import BoneForgeDefense.Node;
import BoneForgeDefense.PathFinder;
import BoneForgeDefense.Entities.Skeleton;
import BoneForgeDefense.Entities.Tower;
import BoneForgeDefense.Entities.Projectile;
import BoneForgeDefense.Entities.Skeletons.NecromancerEnemy;
import BoneForgeDefense.Entities.Skeletons.SkeletonEnemy;
import BoneForgeDefense.Entities.Skeletons.TankEnemy;
import BoneForgeDefense.Entities.OffensiveTowers.BoneBusterTower;
import BoneForgeDefense.Entities.OffensiveTowers.OffensiveTower;
import BoneForgeDefense.Entities.DefensiveTowers.ShieldTower;
import BoneForgeDefense.BonePile;
import BoneForgeDefense.Entities.SupportTowers.BoneHarvesterTower;
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
    private static final double SPAWN_INTERVAL_MIN = 1.0;
    private static final double SPAWN_INTERVAL_MAX = 2.0;

    // Map / grid variables
    private List<Node> path = new ArrayList<>();   // ordered list of nodes from start to end
    public static Node[][] mapNodes;                     // full grid of node data (used for cell styles and hover highlights)
    private Pane[][] gridCells = new Pane[MAP_ROWS][MAP_COLS]; // references to each visual grid cell
    private GridPane gameGrid;

    // Start tile 2 and end tile 3 positions that read from the map at start
    private int startRow, startCol, endRow, endCol;
    private static GridPane staticGameGrid;

    // Bone pile tracking
    private BonePile[][] bonePiles = new BonePile[MAP_ROWS][MAP_COLS];

    // Skeleton tracking variables
    private List<Skeleton> activeSkeletons = new CopyOnWriteArrayList<>();

    // Projectile tracking variables
    private final List<Projectile> activeProjectiles = new ArrayList<>();
    private double spawnAccumulator = 0;  
    private double nextSpawnTime = 0;    
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

    // Reference to the running game loop so it can be stopped when a new game starts
    private AnimationTimer gameLoop = null;

    // FXML bindings
    @FXML private Label bonesTextbox;
    @FXML private Label killsTextbox;
    @FXML private Label livesTextbox;
    // Shows instructions while in tower placement mode
    @FXML private Label placementStatusLabel;

    @FXML private StackPane gameMapPane;

    @FXML private HBox offensiveTowerContainer;
    @FXML private HBox defensiveTowerContainer;
    @FXML private HBox supportTowerContainer;

    // Grid dimensions for the level. The map data itself lives in the Maps class
    // so multiple levels can share the same controller logic.
    public static final int MAP_ROWS = Maps.LEVEL_ONE.length;
    public static final int MAP_COLS = Maps.LEVEL_ONE[0].length;

    // Starts the JavaFX AnimationTimer, which calls update() every frame
    public void startGameLoop() {
        gameLoop = new AnimationTimer() {
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
        };
        gameLoop.start();
    }

    // Builds one shop card per tower and adds it to the correct tab container.
    // Containers are cleared first so restarting the game does not duplicate the cards.
    // Each tower is paired with a Supplier so a new instance can be created when placed on the grid.
    private void loadTowerCards() {
        offensiveTowerContainer.getChildren().clear();
        defensiveTowerContainer.getChildren().clear();
        supportTowerContainer.getChildren().clear();

        offensiveTowerContainer.getChildren().add(
            createTowerCard(new BoneBusterTower(0, 0), () -> new BoneBusterTower(0, 0)));
        defensiveTowerContainer.getChildren().add(
            createTowerCard(new ShieldTower(0, 0), () -> new ShieldTower(0, 0)));
        supportTowerContainer.getChildren().add(
            createTowerCard(new SlowTower(0, 0), () -> new SlowTower(0, 0)));
        supportTowerContainer.getChildren().add(
            createTowerCard(new BoneHarvesterTower(0, 0), () -> new BoneHarvesterTower(0, 0)));
    }

    // Tower card display constants
    private static final double CARD_WIDTH  = 100;
    private static final double CARD_HEIGHT = 150;
    private static final double SPRITE_WIDTH  = 64;
    private static final double SPRITE_HEIGHT = 64;

   
    // Clicking the card enters placement mode for that tower type.
    // Factory parameter creates a new tower instance 
    private VBox createTowerCard(Tower tower, Supplier<Tower> factory) {
        ImageView sprite = new ImageView(tower.getSprite().getImage());
        sprite.setFitWidth(SPRITE_WIDTH);
        sprite.setFitHeight(SPRITE_HEIGHT);

        Label name = new Label(tower.getName());
        Label cost = new Label("Cost: " + tower.getCost());

        // Creates a VBox card showing the tower's sprite, name, and cost
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
        int cellType = Maps.LEVEL_ONE[row][col];
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

        // Update routing now that this cell is blocked
        repathAfterTowerPlaced(row, col);

        cancelPlacementMode();
    }

    // Returns a copy of MAP with every cell that holds a placed tower marked as a wall
    private int[][] buildMapWithTowers() {
        int[][] mapWithTowers = new int[MAP_ROWS][MAP_COLS];
        for (int r = 0; r < MAP_ROWS; r++) {
            for (int c = 0; c < MAP_COLS; c++) {
                mapWithTowers[r][c] = (placedTowers[r][c] != null) ? 1 : Maps.LEVEL_ONE[r][c];
            }
        }
        return mapWithTowers;
    }

    // After a tower is placed at the default path is rebuilt
    // Any active skeleton with a route through the tower is redirected
    private void repathAfterTowerPlaced(int towerRow, int towerCol) {
        int[][] baseNavMesh = buildMapWithTowers();

        // Refresh the default path used by future spawns so they avoid every placed tower
        List<Node> newDefaultPath = computePath(baseNavMesh, 0, 0);
        if (newDefaultPath != null) {
            path = newDefaultPath;
        }

        // Re-route any active skeleton whose remaining path passes through this cell
        for (Skeleton s : Skeleton.enemyList) {
            List<Node> currentPath = s.getPath();
            if (currentPath == null) continue;

            int currentNodeIndex = (int) s.getProgress();
            // Cap to the last walkable index in the path
            currentNodeIndex = Math.min(currentNodeIndex, currentPath.size() - 1);

            // Search for the tower in the remaining portion of this skeleton's path
            boolean towerInRemainingPath = false;
            for (int i = currentNodeIndex; i < currentPath.size(); i++) {
                Node n = currentPath.get(i);
                if (n.getX() == towerRow && n.getY() == towerCol) {
                    towerInRemainingPath = true;
                    break;
                }
            }
            if (!towerInRemainingPath) continue;

            Node currentNode = currentPath.get(currentNodeIndex);
            int row = currentNode.getX();
            int col = currentNode.getY();

            // Build a navmesh for this specific skeleton.
            // If it is standing on a max-capacity pile, adjacent walls are walkable for it.
            int[][] navMesh = buildNavMeshForSkeleton(row, col);
            
            // Skip if Skeleton's current tile is blocked
            if (navMesh[row][col] == 1) continue; 

            List<Node> newPath = computePath(navMesh, row, col);
            if (newPath == null) continue;

            s.setPath(newPath);
            // Drop the integer portion of progress
            s.setProgress(s.getProgress() - currentNodeIndex);
        }
    }

    // Runs PathFinder from (startRow, startCol) to wherever the tile-3 end is on the map   
    private List<Node> computePath(int[][] map, int startRow, int startCol) {
        try {
            PathFinder pf = new PathFinder(MAP_COLS, MAP_ROWS, map, startRow, startCol,
                                            endRow, endCol);
            return pf.getOrderedPath();
        } catch (Exception e) {
        	// Returns null if no route exists
            return null;
        }
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
        
        // Subtract the radius of the circle to put it at (centerX, centerY)
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
        else if (node.getEnd())   return "-fx-background-color: #C43030;";
        else if (node.getWall())  return "-fx-background-color: #555555;";
        if(node.getBones()>=100) return "-fx-background-color: #D40202";
        return "-fx-background-color: #6B8C52;";
    }

    // Runs PathFinder on the map, then builds and populates the visual GridPane.
    // Handle per-cell click and hover handlers for tower placement.
    private void buildGameGrid() {
        // Scan the map to find where the start tile (2) and end tile (3) are placed
        for (int r = 0; r < MAP_ROWS; r++) {
            for (int c = 0; c < MAP_COLS; c++) {
                if (Maps.LEVEL_ONE[r][c] == 2) { startRow = r; startCol = c; }
                if (Maps.LEVEL_ONE[r][c] == 3) { endRow   = r; endCol   = c; }
            }
        }

        PathFinder pathFinder = new PathFinder(MAP_COLS, MAP_ROWS, Maps.LEVEL_ONE, startRow, startCol, endRow, endCol);
        
        mapNodes = pathFinder.getNodes();
        path = pathFinder.getOrderedPath();

        // Constrain the grid to a square so cells are always square
        GridPane grid = new GridPane();
        gameGrid = grid;
        staticGameGrid = grid; 
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
                        boolean isValidSpot = (Maps.LEVEL_ONE[r][c] == 0 || Maps.LEVEL_ONE[r][c] == 1)
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

        // Reposition all active skeletons and bone piles whenever the pane is resized
        gameMapPane.widthProperty().addListener((obs, old, newVal) -> {
            activeSkeletons.forEach(s -> s.updatePosition(gameGrid, MAP_COLS));
            BonePile.allPiles.forEach(p -> p.updatePosition(gameGrid, MAP_COLS));
        });
        gameMapPane.heightProperty().addListener((obs, old, newVal) -> {
            activeSkeletons.forEach(s -> s.updatePosition(gameGrid, MAP_COLS));
            BonePile.allPiles.forEach(p -> p.updatePosition(gameGrid, MAP_COLS));
        });

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
    public void spawnSkeleton(int type) {
    	Skeleton spawnSkeleton;
    	switch(type){
    	case 0:
    		spawnSkeleton = new SkeletonEnemy(0, 0);
    		break;
    	case 1:
    		spawnSkeleton = new SkeletonEnemy(0, 0);
    		break;
    	case 2:
    		spawnSkeleton = new NecromancerEnemy(0, 0);
    		break;
    	default:
    		spawnSkeleton  = new TankEnemy(0, 0);
    		break;
    	}
        
        gameMapPane.getChildren().add(spawnSkeleton.getSprite());

        // Each skeleton gets its own copy of the current path so re-routes affect only this one
        spawnSkeleton.setPath(new ArrayList<>(path));

        activeSkeletons.add(spawnSkeleton);
        spawnSkeleton.updatePosition(gameGrid, MAP_COLS);
    }

    // Resets game state so a fresh game can be started
    private void resetGameState() {
        // Stop the old game loop so it does not keep firing alongside the new one
        if (gameLoop != null) {
            gameLoop.stop();
            gameLoop = null;
        }

        // Remove the game grid from the screen
        if (gameGrid != null) {
            gameMapPane.getChildren().remove(gameGrid);
            gameGrid      = null;
            staticGameGrid = null;
        }

        // Remove every skeleton's sprite and clear both tracking lists
        for (Skeleton s : activeSkeletons) {
            gameMapPane.getChildren().remove(s.getSprite());
        }
        activeSkeletons.clear();
        Skeleton.enemyList.clear();

        // Remove every projectile shape and clear the tracking list
        for (Projectile p : activeProjectiles) {
            gameMapPane.getChildren().remove(p.getShape());
        }
        activeProjectiles.clear();

        // Remove every bone pile sprite and reset the tracking structures
        for (BonePile pile : BonePile.allPiles) {
            gameMapPane.getChildren().remove(pile.getSprite());
        }
        BonePile.allPiles.clear();
        bonePiles = new BonePile[MAP_ROWS][MAP_COLS];

        // Hide the range indicator if one is currently showing
        hideRangeCircle();

        // Clear all placed towers
        placedTowers = new Tower[MAP_ROWS][MAP_COLS];

        // Reset spawner timing and misc game state
        spawnAccumulator = 0;
        nextSpawnTime    = 0;
        lastNanoSecond   = 0;
        gameOver         = false;

        // Exit placement mode in case the player was mid-placement when they quit
        cancelPlacementMode();
    }

    // Resets all state and starts a fresh game
    public void startNewGame(double bones) {
        resetGameState();

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
        // Inside your controller's update method
        List<Skeleton> skeletonsToCleanup = Skeleton.updateAll(activeSkeletons, delta, gameGrid, MAP_COLS);

        // Check if any skeleton stepped onto a new tile that has a 5 bone pile
        checkSkeletonsOnPiles();

        // Reset every skeleton's speed mod so slow towers can re-apply their effect this frame
        Skeleton.resetSpeedMods();

        // Iterate every placed tower and apply the appropriate per-frame logic
        double cellSize   = gameGrid.getWidth() / MAP_COLS;
        double gridStartX = gameGrid.getBoundsInParent().getMinX();
        double gridStartY = gameGrid.getBoundsInParent().getMinY();
        boolean repathNeeded = false; // set to true if a bone pile drops below max capacity this frame
        for (int row = 0; row < MAP_ROWS; row++) {
            for (int col = 0; col < MAP_COLS; col++) {
                Tower tower = placedTowers[row][col];
                if (tower == null) continue;

                // The pixel center of this tower's grid cell
                double towerPixelX = gridStartX + col * cellSize + cellSize / 2.0;
                double towerPixelY = gridStartY + row * cellSize + cellSize / 2.0;

                if (tower instanceof OffensiveTower) {
                    Projectile proj = ((OffensiveTower) tower).update(delta, towerPixelX, towerPixelY);
                    if (proj == null) continue;

                    // Overlay the new projectile on the game map
                    StackPane.setAlignment(proj.getShape(), Pos.TOP_LEFT);
                    activeProjectiles.add(proj);
                    gameMapPane.getChildren().add(proj.getShape());
                } else if (tower instanceof SupportTower) {
                    // Support towers apply effects to in-range entities.
                    // 0 = nothing, 1 = bone harvested, 2 = harvested + pile dropped below max
                    int result = ((SupportTower) tower).update(delta, towerPixelX, towerPixelY);
                    if (result > 0) {
                        // Award the player bones when harvested
                        bones += 10;
                        bonesTextbox.setText(String.format("%.0f", bones));
                    }
                    if (result == 2) {
                        repathNeeded = true;
                    }
                }
            }
        }

        // clean up empty piles and recalculate all skeleton paths
        if (repathNeeded) {
            cleanupEmptyPiles();
            repathAllSkeletons();
        }

        // Move projectiles and apply damage on hit        
        Projectile.updateAll(delta, activeProjectiles, this::killSkeleton)
            .forEach(p -> {
                gameMapPane.getChildren().remove(p.getShape());
                // Remove spent projectiles from the scene
                activeProjectiles.remove(p);
            });

        // Advance every skeleton
        Skeleton.updateAll(skeletonsToCleanup, delta, gameGrid, MAP_COLS).forEach(this::escapeSkeleton);
    }

    // Award bones, increments the kill counter, and leave a bone at the tile
    private void killSkeleton(Skeleton skeleton) {
        // skeleton.getX() = row, skeleton.getY() = col — clamped to valid grid bounds
        int row = Math.max(0, Math.min((int) Math.floor(skeleton.getX()), MAP_ROWS - 1));
        int col = Math.max(0, Math.min((int) Math.floor(skeleton.getY()), MAP_COLS - 1));

        // Add a bone to the pile at the kill location
        // If the pile just hit max capacity, reroute any skeleton already standing on that tile
        boolean pileReachedMax = addBoneToPile(row, col);
        if (pileReachedMax) {
            rerouteSkeletonsOnPile(row, col);
        }

        bones += skeleton.getBoneReward();
        bonesTextbox.setText(String.format("%.0f", bones));
        kills++;
        killsTextbox.setText(String.valueOf(kills));
        skeleton.removeFromScene(gameMapPane);
        activeSkeletons.remove(skeleton);
    }

    // Creatw a new bone pile at (row, col) if one doesn't exist or add bone to the existing pile
    // Returns true if the pile  reached max capacity for the first time
    private boolean addBoneToPile(int row, int col) {
        if (bonePiles[row][col] == null) {
            // No pile here yet, so create one and add sprite above the grid
            BonePile newPile = new BonePile(row, col);
            bonePiles[row][col] = newPile;
            newPile.updatePosition(gameGrid, MAP_COLS);
            StackPane.setAlignment(newPile.getSprite(), Pos.TOP_LEFT);
            gameMapPane.getChildren().add(newPile.getSprite());
            return false;
        } else {
            // Pile already exists so add one bone and check if it just hit max capacity
            return bonePiles[row][col].addBone();
        }
    }

    // Removes any bone piles that have been fully harvested
    private void cleanupEmptyPiles() {
        for (int r = 0; r < MAP_ROWS; r++) {
            for (int c = 0; c < MAP_COLS; c++) {
                BonePile pile = bonePiles[r][c];
                if (pile != null && pile.isEmpty()) {
                    gameMapPane.getChildren().remove(pile.getSprite());
                    BonePile.allPiles.remove(pile);
                    bonePiles[r][c] = null;
                }
            }
        }
    }

    // Builds a navmesh for a single skeleton based on where it is standing
    // If the skeleton is standing on a 5 bone pile, wall tiles next to that pile become walkable
    private int[][] buildNavMeshForSkeleton(int skeletonRow, int skeletonCol) {
        int[][] navMesh = buildMapWithTowers();
        BonePile pile = bonePiles[skeletonRow][skeletonCol];
        if (pile != null && pile.isAtMaxCapacity()) {
            openAdjacentWalls(navMesh, skeletonRow, skeletonCol);
        }
        return navMesh;
    }

    // When a pile just reached max capacity, reroute any skeleton on that tile so they can use the newly accessible wall tiles
    private void rerouteSkeletonsOnPile(int pileRow, int pileCol) {
        int[][] navMesh = buildNavMeshForSkeleton(pileRow, pileCol);

        for (Skeleton s : Skeleton.enemyList) {
            List<Node> currentPath = s.getPath();
            if (currentPath == null) continue;

            int nodeIndex = (int) s.getProgress();
            nodeIndex = Math.min(nodeIndex, currentPath.size() - 1);
            Node currentNode = currentPath.get(nodeIndex);

            // Only reroute skeletons that are currently on this specific pile tile
            if (currentNode.getX() != pileRow || currentNode.getY() != pileCol) continue;

            List<Node> newPath = computePath(navMesh, pileRow, pileCol);
            if (newPath == null) continue;

            s.setPath(newPath);
            s.setProgress(s.getProgress() - nodeIndex);
        }
    }

    // Detects when a skeleton steps onto a new tile if that tile holds a 5 bone pile
    private void checkSkeletonsOnPiles() {
        for (Skeleton s : activeSkeletons) {
            List<Node> currentPath = s.getPath();
            if (currentPath == null) continue;

            int nodeIndex = (int) s.getProgress();
            if (nodeIndex == s.getLastNodeIndex()) continue; // still on the same tile as last frame
            s.setLastNodeIndex(nodeIndex);

            if (nodeIndex >= currentPath.size()) continue;
            Node currentNode = currentPath.get(nodeIndex);
            int row = currentNode.getX();
            int col = currentNode.getY();

            // Only reroute if there is a 5 bone pile on this tile
            BonePile pile = bonePiles[row][col];
            if (pile == null || !pile.isAtMaxCapacity()) continue;

            // Open up the adjacent walls for this skeleton's path
            int[][] navMesh = buildNavMeshForSkeleton(row, col);
            List<Node> newPath = computePath(navMesh, row, col);
            if (newPath == null) continue;

            s.setPath(newPath);
            s.setProgress(s.getProgress() - nodeIndex);
        }
    }

    // Marks the four tiles directly next to a full bone pile as walkable
    private void openAdjacentWalls(int[][] navMesh, int pileRow, int pileCol) {
        int[][] neighbors = {
            {pileRow - 1, pileCol},
            {pileRow + 1, pileCol},
            {pileRow, pileCol - 1},
            {pileRow, pileCol + 1}
        };
        for (int[] neighbor : neighbors) {
            int r = neighbor[0];
            int c = neighbor[1];
            if (r >= 0 && r < MAP_ROWS && c >= 0 && c < MAP_COLS && navMesh[r][c] == 1) {
                navMesh[r][c] = 0;
            }
        }
    }

    // Rebuilds every active skeleton's remaining path from its current position
    private void repathAllSkeletons() {
        // Base navmesh has no pile effects; pile effects are applied per-skeleton below
        int[][] baseNavMesh = buildMapWithTowers();

        // Update the default path so newly spawned skeletons use the latest layout
        List<Node> newDefault = computePath(baseNavMesh, 0, 0);
        if (newDefault != null) path = newDefault;

        // Re-route each skeleton from its current grid node to the end
        for (Skeleton s : Skeleton.enemyList) {
            List<Node> currentPath = s.getPath();
            if (currentPath == null) continue;

            int nodeIndex = (int) s.getProgress();
            nodeIndex = Math.min(nodeIndex, currentPath.size() - 1);
            Node currentNode = currentPath.get(nodeIndex);
            int row = currentNode.getX();
            int col = currentNode.getY();

            // Build a navmesh for this specific skeleton
            // If standing on a 5 bone pile, adjacent walls are walkable for it
            int[][] navMesh = buildNavMeshForSkeleton(row, col);

            // Skip skeletons on wall tiles
            if (navMesh[row][col] == 1) continue;

            List<Node> newPath = computePath(navMesh, row, col);
            if (newPath == null) continue;

            s.setPath(newPath);
            // Drop the integer portion of progress
            s.setProgress(s.getProgress() - nodeIndex);
        }
    }

    // Decrements lives and gives no bone reward
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
            Random random = new Random();
            spawnSkeleton(random.nextInt(3));
        }
    }

    public double getBones() {
        return bones;
    }

    public void setBones(double bones) {
        this.bones = bones;
    }

    // Returns the game grid so support towers can compute pixel positions for range checks
    public static GridPane getGameGrid() {
        return staticGameGrid;
    }

    // Pause the game without resetting anything
    @FXML
    private void returnToMainMenu() {
        if (gameLoop != null) {
            gameLoop.stop();
        }
        // Reset the timer
        lastNanoSecond = 0;
        SceneSelector.setGamePaused(true);
        SceneSelector.launchMainMenuScene();
    }

    // Resume from where game was paused
    public void resumeGame() {
        if (gameLoop != null) {
            gameLoop.start();
        }
    }

}
