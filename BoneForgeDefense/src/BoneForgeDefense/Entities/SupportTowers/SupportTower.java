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
	// Returns: 0 = nothing happened, 1 = bone harvested (award bones),
	//          2 = bone harvested AND pile dropped below max capacity (award bones + repath).
	public abstract int update(double delta, double towerPixelX, double towerPixelY);

}
