package BoneForgeDefense.Entities;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.layout.GridPane;

import BoneForgeDefense.Node;
import BoneForgeDefense.Entities.Skeletons.SkeletonEnemy;

public abstract class Skeleton extends Entity{
	private double health;
	public static List<Skeleton> enemyList = new ArrayList<Skeleton>();
	public Skeleton(double xPos, double yPos, String spritePath) {
		super(xPos, yPos, spritePath);
		enemyList.add(this);
	}

	public double getRange(double otherX, double otherY) {
		//does not sqrt the result to avoid costly calculations when called every frame. square other range values to compare to this value

		// Use the sprite's pixel center — xPos/yPos are not updated after spawn
		double centerX = sprite.getTranslateX() + sprite.getFitWidth()  / 2.0;
		double centerY = sprite.getTranslateY() + sprite.getFitHeight() / 2.0;
    	double distance = Math.pow(centerX - otherX, 2) + Math.pow(centerY - otherY, 2);
    	return distance;
    }

	public double getHealth() {
		return health;
	}

	public void setHealth(double health) {
		this.health = health;
	}

	// Bones awarded to the player when this skeleton is killed
	public abstract int getBoneReward();

	// Advances every active skeleton along the path and returns a list of those
	// that have reached the end so the caller can remove them from the scene.
	public static List<SkeletonEnemy> updateAll(double delta, List<Node> path,
	                                            GridPane gameGrid, int mapCols) {
		List<SkeletonEnemy> reachedEnd = new ArrayList<>();
		for (Skeleton s : enemyList) {
			SkeletonEnemy skeleton = (SkeletonEnemy) s;
			skeleton.advance(delta);
			if (skeleton.hasReachedEnd(path)) {
				reachedEnd.add(skeleton);
				continue;
			}
			skeleton.updatePosition(path, gameGrid, mapCols);
		}
		return reachedEnd;
	}

}
