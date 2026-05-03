package BoneForgeDefense.Entities.SupportTowers;

import BoneForgeDefense.BonePile;
import BoneForgeDefense.Scenes.LevelOneController;

public class BoneHarvesterTower extends SupportTower {

    // How many seconds to wait between each harvest action
    private static final double HARVEST_INTERVAL = 10.0;

    // Tracks how much time has passed since the last harvest
    private double harvestTimer = 0;

    public BoneHarvesterTower(double x, double y) {
        super(x, y,
              "/BoneForgeDefense/Sprites/boneHarvesterTower.png",
              "Bone Harvester",
              TowerType.SUPPORT,
              150); // range in pixels
        this.cost = 75;
    }

    // Every 10 seconds remove one bone from the nearest pile within range
    @Override
    public int update(double delta, double towerPixelX, double towerPixelY) {
        harvestTimer += delta;
        if (harvestTimer < HARVEST_INTERVAL) return 0;
        harvestTimer = 0;

        BonePile target = findNearestPile(towerPixelX, towerPixelY);
        if (target == null) return 0;

        // removeBone() returns true only when the pile drops below max capacity
        boolean droppedBelowMax = target.removeBone();
        return droppedBelowMax ? 2 : 1;
    }

    // Finds the closest bone pile within this tower's range
    private BonePile findNearestPile(double towerPixelX, double towerPixelY) {
        double rangeSquared = range * range;
        BonePile closest   = null;
        double closestDist = Double.MAX_VALUE;

        for (BonePile pile : BonePile.allPiles) {
            double pileX = pile.getPixelCenterX(LevelOneController.getGameGrid(), LevelOneController.MAP_COLS);
            double pileY = pile.getPixelCenterY(LevelOneController.getGameGrid(), LevelOneController.MAP_COLS);
            double dx    = pileX - towerPixelX;
            double dy    = pileY - towerPixelY;
            double dist  = dx * dx + dy * dy;

            if (dist <= rangeSquared && dist < closestDist) {
                closestDist = dist;
                closest     = pile;
            }
        }
        return closest;
    }
}
