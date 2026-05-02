package BoneForgeDefense.Entities.OffensiveTowers;

import BoneForgeDefense.Entities.Projectile;
import BoneForgeDefense.Entities.Skeleton;
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
	
	// Counts down the fire timer by delta seconds	
	public boolean advanceFireTimer(double delta) {
		fireTick -= delta;
		
		// Return true when it is time to fire the next shot
		if (fireTick <= 0) {
			fireTick = 1.0 / fireRate;
			return true;
		}
		return false;
	}

	@Override
	public double getRange()    { return range; }
	public double getDamage()   { return damage; }
	public double getFireRate() { return fireRate; }

	public abstract void shoot();

	// Returns the index into Skeleton.enemyList of the best target within range or -1 if no enemy is in range
	public abstract int getTarget(double towerPixelX, double towerPixelY);

	// Speed of projectiles fired by this tower, in pixels per second
	private static final double PROJECTILE_SPEED = 400.0;

	// Advances the fire timer, selects a target, and returns a Projectile ready to
	// be added to the scene, or null if the tower is cooling down or out of range.
	public Projectile update(double delta, double towerPixelX, double towerPixelY) {
		// Timer only advances when a target is in range, so the first shot is immediate
		int targetIndex = getTarget(towerPixelX, towerPixelY);
		if (targetIndex < 0) return null;

		if (!advanceFireTimer(delta)) return null;

		// Place to add sound effects
		shoot();

		// Resolve the index to the actual skeleton and create the projectile
		Skeleton target = (Skeleton) Skeleton.enemyList.get(targetIndex);
		return new Projectile(towerPixelX, towerPixelY, target, PROJECTILE_SPEED, damage);
	}

}
