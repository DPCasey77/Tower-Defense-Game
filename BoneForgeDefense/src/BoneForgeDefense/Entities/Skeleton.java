package BoneForgeDefense.Entities;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import BoneForgeDefense.Node;

public abstract class Skeleton extends Entity{
	protected double health;
	protected double moveSpeed;
	protected double boneReward;
	
	//stat modifiers for effects such as slow towers
	private double moveSpeedMod = 1.0;
	private double healthMod = 1.0;
	private double boneRewardMod = 1.0;
	
	private double progress = 0.0;
	
	public static List<Skeleton> enemyList = new ArrayList<Skeleton>();
	public Skeleton(double xPos, double yPos, String spritePath) {
		super(xPos, yPos, spritePath);
		enemyList.add(this);
	}

	public double getRange(double otherX, double otherY) {
		//does not sqrt the result to avoid costly calculations when called every frame. square other range values to compare to this value

		// Use the sprite's pixel center because xPos/yPos are not updated after spawn
		double centerX = sprite.getTranslateX() + sprite.getFitWidth()  / 2.0;
		double centerY = sprite.getTranslateY() + sprite.getFitHeight() / 2.0;
    	double distance = Math.pow(centerX - otherX, 2) + Math.pow(centerY - otherY, 2);
    	return distance;
    }

	public double getHealth() {
		return health*healthMod;
	}

	public void setHealth(double health) {
		this.health = health;
	}
	public void setHealthMod(double mod) {
		this.healthMod = mod;
	}
	
	public double getSpeed() {
		return moveSpeed*moveSpeedMod;
	}
	public void setMoveSpeed(double speed) {
		this.moveSpeed=speed;
	}
	public void setMoveSpeedMod(double mod) {
		this.moveSpeedMod=mod;
	}

	// Resets every active skeleton's movement speed modifier to 1.0
	public static void resetSpeedMods() {
		for (Skeleton s : enemyList) {
			s.setMoveSpeedMod(1.0);
		}
	}
	
	public double getBoneReward() {
		return boneReward*boneRewardMod;
	}
	public void setBoneReward(double reward) {
		this.boneReward=reward;
	}
	public void setBoneRewardMod(double mod) {
		this.boneReward=mod;
	}

	// Advances every active skeleton along the path and returns a list of skeletons that have reached the end
	public static List<Skeleton> updateAll(double delta, List<Node> path, GridPane gameGrid, int mapCols) {
		List<Skeleton> reachedEnd = new ArrayList<>();
		for (Skeleton s : enemyList) {
			
			s.advance(delta);
			if (s.hasReachedEnd(path)) {
				reachedEnd.add(s);
				continue;
			}
			s.updatePosition(path, gameGrid, mapCols);
			
		}
		return reachedEnd;
	}
	
	// Moves the skeleton's image to the correct pixel position on screen.
    // Progress is a decimal so the skeleton sits between two path nodes and blends between them
    public void updatePosition(List<Node> path, GridPane gameGrid, int mapCols) {

        // Figure out which two path nodes the skeleton is between
        // The whole number part of progress is the index of the node the skeleton just passed
        int currentNodeIndex = (int) progress;

        // Make sure there is always a next node to move toward
        currentNodeIndex = Math.min(currentNodeIndex, path.size() - 2);

        // The decimal part is how far between the two nodes the skeleton is
        double blendFactor = progress - currentNodeIndex;

        // Blend between the two nodes to get a smooth in-between position
        Node currentNode = path.get(currentNodeIndex);
        Node nextNode = path.get(currentNodeIndex + 1);

        // Interpolate: start at currentNode and move blendFactor of the way toward nextNode
        
        // getY() = column
        double blendedColumn = currentNode.getY() + blendFactor * (nextNode.getY() - currentNode.getY());
        // getX() = row
        double blendedRow = currentNode.getX() + blendFactor * (nextNode.getX() - currentNode.getX());

        // Convert the grid position to pixels on screen

        // Get width of grid cell in pixels
        double cellSizePixels = gameGrid.getWidth() / mapCols;

        // Resize the skeleton image to match one cell, then place it at the right spot
        double gridStartX = gameGrid.getBoundsInParent().getMinX();
        double gridStartY = gameGrid.getBoundsInParent().getMinY();

        // Resize the skeleton image to match one cell, then place it at the right spot
        sprite.setFitWidth(cellSizePixels);
        sprite.setFitHeight(cellSizePixels);
        sprite.setTranslateX(gridStartX + blendedColumn * cellSizePixels);
        sprite.setTranslateY(gridStartY + blendedRow    * cellSizePixels);
    }
    // Advances progress along the path by delta seconds
    public void advance(double delta) {
        progress += delta * this.getSpeed();
    }
    // Returns true when the skeleton has reached the end node of the path
    public boolean hasReachedEnd(List<Node> path) {
        return progress >= path.size() - 1;
    }
    // Removes this skeleton's sprite from the map pane and the shared enemy list
    public void removeFromScene(Pane gameMapPane) {
        gameMapPane.getChildren().remove(sprite);
        Skeleton.enemyList.remove(this);
    }
    public double getProgress() {
        return progress;
    }

    public void setProgress(double progress) {
        this.progress = progress;
    }
    // Returns the X pixel coordinate of the sprite's center within the map pane
    public double getSpriteCenterX() {
        return sprite.getTranslateX() + sprite.getFitWidth() / 2.0;
    }

    // Returns the Y pixel coordinate of the sprite's center within the map pane
    public double getSpriteCenterY() {
        return sprite.getTranslateY() + sprite.getFitHeight() / 2.0;
    }

}
