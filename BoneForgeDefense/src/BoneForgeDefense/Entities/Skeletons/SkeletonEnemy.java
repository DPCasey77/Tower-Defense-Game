package BoneForgeDefense.Entities.Skeletons;

import BoneForgeDefense.Entities.Skeleton;

public class SkeletonEnemy extends Skeleton {

    // Continuous position along the path:
    // integer part = current node index, fractional part = how far between that node and the next
    private double progress = 0.0;

    public SkeletonEnemy(double xPos, double yPos, String spritePath) {
        super(xPos, yPos, spritePath);
    }

    public double getProgress() {
        return progress;
    }

    public void setProgress(double progress) {
        this.progress = progress;
    }
}
