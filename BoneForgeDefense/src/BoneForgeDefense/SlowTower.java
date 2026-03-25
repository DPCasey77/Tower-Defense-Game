package BoneForgeDefense;

public class SlowTower extends Tower {
	
  public SlowTower(double x, double y) {	
	super(x, y, "/BoneForgeDefense/Sprites/slowTower.png",
            "Slow Tower", TowerType.SUPPORT,
            30,    // cost
            15.0,  // damage
            1.0);  // fire rate (shots per second)
  }

  @Override
  protected void shoot() {
      System.out.println("Slow Tower Fires!");
      // spawn projectile here later
  }
}