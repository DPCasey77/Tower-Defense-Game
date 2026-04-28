package BoneForgeDefense.Entities.DefensiveTowers;

import BoneForgeDefense.Entities.Tower;

public abstract class DefensiveTower  extends Tower{
	protected double range;

	

	public DefensiveTower(double x, double y, String spritePath, String name, TowerType towerType, double range) {
		super(x, y, spritePath, name, towerType);
		this.range=range;
	}

	@Override
	public double getRange() {
		return range;
	}

}
