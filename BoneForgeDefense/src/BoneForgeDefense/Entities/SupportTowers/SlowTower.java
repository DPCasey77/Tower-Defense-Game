package BoneForgeDefense.Entities.SupportTowers;

import BoneForgeDefense.Entities.Skeleton;


public class SlowTower extends SupportTower {

	// Multiplier for enemy movement speed while inside the slow radius
	private static final double SLOW_FACTOR = 0.5;

  public SlowTower(double x, double y) {
	super(x, y,											//position X and Y
			"/BoneForgeDefense/Sprites/slowTower.png",	//Sprite Location
            "Slow Tower",								//Tower Name
            TowerType.SUPPORT,							//Tower Type
            30);										//Tower Range
    this.cost = 50;
  }

  // Slows every skeleton within the tower's range
  @Override
  public void update(double delta, double towerPixelX, double towerPixelY) {
      double rangeSquared = range * range;
      for (Skeleton s : Skeleton.enemyList) {
          double dx = s.getSpriteCenterX() - towerPixelX;
          double dy = s.getSpriteCenterY() - towerPixelY;
          
          // Square both sides to avoid the cost of a square root each check
          if (dx * dx + dy * dy <= rangeSquared) {
              s.setMoveSpeedMod(SLOW_FACTOR);
          }
      }
  }


}
