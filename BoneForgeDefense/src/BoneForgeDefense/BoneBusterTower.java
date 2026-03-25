package BoneForgeDefense;

public class BoneBusterTower extends Tower {

    public BoneBusterTower(double x, double y) {
        super(x, y, "/BoneForgeDefense/Sprites/boneBusterTower.png",
              "Bone Buster Tower", TowerType.OFFENSIVE,
              75,    // cost
              15.0,  // damage
              1.0);  // fire rate (shots per second)
    }

    @Override
    protected void shoot() {
        System.out.println("Bone Buster Tower fires!");
        // spawn projectile here later
    }
}
