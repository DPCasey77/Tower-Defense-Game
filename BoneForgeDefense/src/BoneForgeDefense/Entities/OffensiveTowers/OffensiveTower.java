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
	
	public abstract void shoot();
	public abstract int getTarget();

}
