package BoneForgeDefense;

public class ShieldTower extends Tower {

    public ShieldTower(double x, double y) {
        super(x, y, "/BoneForgeDefense/Sprites/shieldTower.png",
              "Shield Tower", TowerType.DEFENSIVE,
              50,    // cost
              0.0,  // damage
              0.0);  // fire rate
    }

    @Override
    protected void shoot() {
        System.out.println("Shield Tower Activates!");
        // spawn projectile here later
    }
}
