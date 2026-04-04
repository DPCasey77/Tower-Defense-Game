package BoneForgeDefense.Scenes;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

import BoneForgeDefense.BoneBusterTower;
import BoneForgeDefense.Node;
import BoneForgeDefense.PathFinder;
import BoneForgeDefense.ShieldTower;
import BoneForgeDefense.SlowTower;
import BoneForgeDefense.Tower;
import javafx.animation.AnimationTimer;


public class LevelOneController {
	
	private long lastNanoSecond = 0;
	private Boolean gameOver = false;

	private double bones;
	private double money;

	private List<Node> path = new ArrayList<>();
	private Pane[][] gridCells = new Pane[MAP_ROWS][MAP_COLS];
	private int pathIndex = 0;
	private double timeAccumulator = 0;
	
	@FXML private Label bonesTextbox;
    @FXML private Label moneyTextbox;

    @FXML private StackPane gameMapPane;

    @FXML private HBox offensiveTowerContainer;
    @FXML private HBox defensiveTowerContainer;
    @FXML private HBox supportTowerContainer;

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
       
	
	public void startGameLoop() {
        new AnimationTimer() {
            @Override
            public void handle(long now) {
            	
            	// Skip check on first frame
                if (lastNanoSecond == 0) {
                		lastNanoSecond = now;
                		return; 
                }
                
                // Convert nanoseconds to seconds and calculate how many have past since 
                // last frame
                double delta = (now - lastNanoSecond) / 1_000_000_000.0;
                lastNanoSecond = now;
 
                // Stop time if Game Over bool is true
                if (!gameOver) {
                    update(delta);
                }
                
            }
        }.start();
    }
	
	// Create list of towers for each category and add them to the appropriate Hbox container
	private void loadTowerCards() {
		List<Tower> offensiveTowers = List.of(new BoneBusterTower(0, 0));
	    List<Tower> defensiveTowers = List.of(new ShieldTower(0, 0));
	    List<Tower> supportTowers   = List.of(new SlowTower(0, 0));

	    for (Tower t : offensiveTowers) offensiveTowerContainer.getChildren().add(createTowerCard(t));
	    for (Tower t : defensiveTowers) defensiveTowerContainer.getChildren().add(createTowerCard(t));
	    for (Tower t : supportTowers)   supportTowerContainer.getChildren().add(createTowerCard(t));
	}
	
	private static final double CARD_WIDTH = 100;
	private static final double CARD_HEIGHT = 150;
	private static final double SPRITE_WIDTH = 64;
	private static final double SPRITE_HEIGHT = 64;
	
	// Create a VBox card for a given tower with its sprite, name, and cost
	private VBox createTowerCard(Tower tower) {
	    ImageView sprite = new ImageView(tower.getSprite().getImage());
	    sprite.setFitWidth(SPRITE_WIDTH);
	    sprite.setFitHeight(SPRITE_HEIGHT);
	    	    	    
	    Label name = new Label(tower.getName());
	    Label cost = new Label("Cost: " + tower.getCost());

	    VBox towerCard = new VBox(5, sprite, name, cost); // 
	    towerCard.setAlignment(javafx.geometry.Pos.CENTER);
	    towerCard.setPrefWidth(CARD_WIDTH);
	    towerCard.setPrefHeight(CARD_HEIGHT);
	    towerCard.setMinWidth(CARD_WIDTH);
	    towerCard.setMaxWidth(CARD_WIDTH);

	    // Add click event to the card that triggers the buyTower method
	    towerCard.setOnMouseClicked(e -> buyTower(tower));

	    return towerCard;
	}
	
	// Check if player has enough money to buy the tower, then subtract cost from money
	private void buyTower(Tower tower) {
		if (money >= tower.getCost()) {
	        money -= tower.getCost();
	        moneyTextbox.setText(String.format("%.0f", money));         
	        System.out.println("You bought the " + tower.getName() + "!");
			} else {
				System.out.println("You can't afford the " + tower.getName() + "!");
			}
	}
		
	private String getCellStyle(Node node) {
        if (node.getStart()) return "-fx-background-color: #2EA832;";
        if (node.getEnd())   return "-fx-background-color: #C43030;";
        if (node.getWall())  return "-fx-background-color: #555555;";
        return "-fx-background-color: #6B8C52;";
    }

	private void buildGameGrid() {
        PathFinder pathFinder = new PathFinder(MAP_COLS, MAP_ROWS, MAP);
        pathFinder.search();
        Node[][] nodes = pathFinder.getNodes();
        path = pathFinder.getOrderedPath();

        GridPane grid = new GridPane();
        grid.setMaxWidth(Double.MAX_VALUE);
        grid.setMaxHeight(Double.MAX_VALUE);

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
    }

	// Start a new game with given bones and money values, then start game loop and load tower cards
	public void startNewGame(double bones, double money) {
		this.bones = bones;
		this.money = money;
		bonesTextbox.setText(String.format("%.0f", bones));
		moneyTextbox.setText(String.format("%.0f", money));
		buildGameGrid();
		startGameLoop();
		loadTowerCards();
	}
	
	private void update(double delta) {
		if (pathIndex >= path.size()) return;

		timeAccumulator += delta;
		if (timeAccumulator < 1.0) return;
		timeAccumulator -= 1.0;

		// Restore the previous cell to its original color
		if (pathIndex > 0) {
			Node prev = path.get(pathIndex - 1);
			gridCells[prev.getX()][prev.getY()].setStyle(getCellStyle(prev));
		}

		// Highlight the current cell as the active position
		Node current = path.get(pathIndex);
		gridCells[current.getX()][current.getY()].setStyle("-fx-background-color: #FF6600;");
		pathIndex++;
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
