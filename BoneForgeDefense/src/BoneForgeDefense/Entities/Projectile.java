package BoneForgeDefense.Entities;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import BoneForgeDefense.Entities.Skeletons.SkeletonEnemy;

public class Projectile {

    // How close the center of the projectile must get to count as a hit
    private static final double HIT_DISTANCE = 8.0;

    private double x;               
    private double y;               
    private final SkeletonEnemy target;
    private final double speed;     // pixels per second
    private final double damage;    // health subtracted from the target on hit
    private final Circle shape;     // JavaFX Circle used to draw the projectile

    public Projectile(double startX, double startY,
                      SkeletonEnemy target, double speed, double damage) {
        this.x      = startX;
        this.y      = startY;
        this.target = target;
        this.speed  = speed;
        this.damage = damage;

        // Draw as a small yellow-orange circle — no sprite image file required
        this.shape = new Circle(4.0);
        this.shape.setFill(Color.LIGHTYELLOW);
        this.shape.setStroke(Color.DARKORANGE);
        this.shape.setStrokeWidth(1.5);
        this.shape.setTranslateX(startX);
        this.shape.setTranslateY(startY);
    }

    // Moves one step toward the target's current pixel position
    // Uses delta time to keep  speed is frame-rate independent
    public void moveTowardsTarget(double delta) {
        // Aim at the center of the target's sprite
        double targetCenterX = target.getSprite().getTranslateX()
                               + target.getSprite().getFitWidth()  / 2.0;
        double targetCenterY = target.getSprite().getTranslateY()
                               + target.getSprite().getFitHeight() / 2.0;

        double dx = targetCenterX - x;
        double dy = targetCenterY - y;
        double distance = Math.sqrt(dx * dx + dy * dy);

        if (distance > 0) {
            // Move toward the target, but never travel further than the remaining distance
            double step = Math.min(speed * delta, distance);
            x += (dx / distance) * step;
            y += (dy / distance) * step;
        }

        shape.setTranslateX(x);
        shape.setTranslateY(y);
    }

    // Returns true when the projectile center is within HIT_DISTANCE pixels of the target center
    public boolean hasHitTarget() {
        double targetCenterX = target.getSprite().getTranslateX()
                               + target.getSprite().getFitWidth()  / 2.0;
        double targetCenterY = target.getSprite().getTranslateY()
                               + target.getSprite().getFitHeight() / 2.0;
        double dx = targetCenterX - x;
        double dy = targetCenterY - y;
        return Math.sqrt(dx * dx + dy * dy) <= HIT_DISTANCE;
    }

    // Returns true when the target has already been removed from the game
    public boolean isTargetGone() {
        return !Skeleton.enemyList.contains(target);
    }

    public SkeletonEnemy getTarget()  { return target; }
    public double        getDamage()  { return damage; }

    // The JavaFX circle shape
    public Circle getShape() { return shape; }
}
