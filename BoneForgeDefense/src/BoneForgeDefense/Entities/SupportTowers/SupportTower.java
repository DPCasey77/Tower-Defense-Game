package BoneForgeDefense.Entities.SupportTowers;

import BoneForgeDefense.Entities.Tower;

public abstract class SupportTower  extends Tower{
	protected double range;



	public SupportTower(double x, double y, String spritePath, String name, TowerType towerType, double range) {
		super(x, y, spritePath, name, towerType);
		this.range=range;
	}

	@Override
	public double getRange() {
		return range;
	}

	// Applies this support tower's effect to in-range entities each frame.
	// Returns true if something changed that requires enemy paths to be recalculated.
	public abstract boolean update(double delta, double towerPixelX, double towerPixelY);

}
