package BoneForgeDefense.Entities.OffensiveTowers;

import BoneForgeDefense.Entities.Tower;


public abstract class OffensiveTower extends Tower{
	protected double range;
	protected double damage;
	protected double fireRate;
	protected double fireTick;
	

	public OffensiveTower(double x, double y, String spritePath, String name, double range, double damage, double fireRate) {
		super(x, y, spritePath, name, TowerType.OFFENSIVE);
		this.range=range;
		this.damage=damage;
		this.fireRate=fireRate;
	}
	
	// Counts down the fire timer by delta seconds.
	// Returns true (and resets the timer) when it is time to fire the next shot.
	// The timer only advances while a target is available, so the tower fires
	// immediately when an enemy first enters range.
	public boolean advanceFireTimer(double delta) {
		fireTick -= delta;
		if (fireTick <= 0) {
			fireTick = 1.0 / fireRate;
			return true;
		}
		return false;
	}

	public double getRange()    { return range; }
	public double getDamage()   { return damage; }
	public double getFireRate() { return fireRate; }

	public abstract void shoot();
	public abstract int getTarget();

}
