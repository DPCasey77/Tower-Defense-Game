package BoneForgeDefense.Entities;

import java.util.ArrayList;
import java.util.List;

public abstract class Skeleton extends Entity{
	private double health;
	public static List<Skeleton> enemyList = new ArrayList<Skeleton>();
	public Skeleton(double xPos, double yPos, String spritePath) {
		super(xPos, yPos, spritePath);
		enemyList.add(this);
	}
	
	public double getRange(double otherX, double otherY) {
		//does not sqrt the result to avoid costly calculations when called every frame. square other range values to compare to this value
		
    	double distance = Math.pow(this.xPos-otherX, 2) + Math.pow(this.yPos-otherY, 2);
    	return distance;
    }

	public double getHealth() {
		return health;
	}

	public void setHealth(double health) {
		this.health = health;
	}

}
