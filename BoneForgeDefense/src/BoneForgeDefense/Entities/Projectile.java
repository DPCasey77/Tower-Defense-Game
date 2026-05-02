package BoneForgeDefense.Entities;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Projectile {

    // How close the center of the projectile must get to count as a hit
    private static final double HIT_DISTANCE = 8.0;

    private double x;
    private double y;
    private final Skeleton target;
    private final double speed;     // pixels per second
    private final double damage;    // health subtracted from the target on hit
    private final Circle shape;     // JavaFX Circle used to draw the projectile

    public Projectile(double startX, double startY,
                      Skeleton target, double speed, double damage) {
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
        double dx = target.getSpriteCenterX() - x;
        double dy = target.getSpriteCenterY() - y;
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
        double dx = target.getSpriteCenterX() - x;
        double dy = target.getSpriteCenterY() - y;
        return Math.sqrt(dx * dx + dy * dy) <= HIT_DISTANCE;
    }

    // Returns true when the target has already been removed from the game
    public boolean isTargetGone() {
        return !Skeleton.enemyList.contains(target);
    }

    public Skeleton getTarget()  { return target; }
    public double        getDamage()  { return damage; }

    // The JavaFX circle shape
    public Circle getShape() { return shape; }

    // Moves every active projectile toward its target and resolves hits.
    // onKilled is called for any skeleton that reaches zero health, so the caller
    // can handle scene and list cleanup without Projectile needing a Pane reference.
    // Returns the list of spent projectiles for the caller to remove from the scene.
    public static List<Projectile> updateAll(double delta, List<Projectile> activeProjectiles,
                                             Consumer<Skeleton> onKilled) {
        List<Projectile> spent = new ArrayList<>();

        for (Projectile proj : activeProjectiles) {
            // Discard the projectile if the target was killed by another shot first
            if (proj.isTargetGone()) {
                spent.add(proj);
                continue;
            }

            proj.moveTowardsTarget(delta);

            if (proj.hasHitTarget()) {
                Skeleton target = proj.getTarget();
                target.setHealth(target.getHealth() - proj.getDamage());

                // Kill the skeleton when it runs out of health
                if (target.getHealth() <= 0) {
                    onKilled.accept(target);
                }
                spent.add(proj);
            }
        }

        return spent;
    }
}
