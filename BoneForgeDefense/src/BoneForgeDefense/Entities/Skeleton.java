package BoneForgeDefense.Entities;

public abstract class Skeleton extends Entity{
	private double health;
	public Skeleton(double xPos, double yPos, String spritePath) {
		super(xPos, yPos, spritePath);
		this.setHealth(100);
	}
	
	public double getRange(double otherX, double otherY) {
    	double distance = Math.sqrt(Math.pow(this.xPos-otherX, 2) + Math.pow(this.yPos-otherY, 2));
    	return distance;
    }

	public double getHealth() {
		return health;
	}

	public void setHealth(double health) {
		this.health = health;
	}

}
