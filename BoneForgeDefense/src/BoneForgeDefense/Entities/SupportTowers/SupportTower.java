package BoneForgeDefense.Entities.SupportTowers;

import BoneForgeDefense.Entities.Tower;

public abstract class SupportTower  extends Tower{
	protected double range;

	

	public SupportTower(double x, double y, String spritePath, String name, TowerType towerType, double range) {
		super(x, y, spritePath, name, towerType);
		this.range=range;
	}


}
