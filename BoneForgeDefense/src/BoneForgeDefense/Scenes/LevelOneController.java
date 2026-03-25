package BoneForgeDefense.Scenes;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;

import BoneForgeDefense.BoneBusterTower;
import BoneForgeDefense.ShieldTower;
import BoneForgeDefense.SlowTower;
import BoneForgeDefense.Tower;
import javafx.animation.AnimationTimer;


public class LevelOneController {
	
	private long lastNanoSecond = 0;
	private Boolean gameOver = false;
	
	private double bones;
	private double money;
	
	@FXML private Label bonesTextbox;
    @FXML private Label moneyTextbox;    
    
    @FXML private HBox offensiveTowerContainer;
    @FXML private HBox defensiveTowerContainer;
    @FXML private HBox supportTowerContainer;
       
	
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
		
	// Start a new game with given bones and money values, then start game loop and load tower cards
	public void startNewGame(double bones, double money) { 		
		this.bones = bones;
		this.money = money;
		bonesTextbox.setText(String.format("%.0f", bones));
		moneyTextbox.setText(String.format("%.0f", money));
		startGameLoop();
		loadTowerCards();
	}
	
	//private int testCounter = 0;
	private void update(double delta) {
		//testCounter += 1;
		//System.out.println(testCounter);
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
