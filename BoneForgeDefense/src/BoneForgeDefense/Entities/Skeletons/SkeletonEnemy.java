package BoneForgeDefense.Entities.Skeletons;

import java.util.List;

import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import BoneForgeDefense.Node;
import BoneForgeDefense.Entities.Skeleton;

public class SkeletonEnemy extends Skeleton {

    // Starting health applied to every new skeleton — reduced by projectile hits
    private static final double DEFAULT_HEALTH = 100.0;

    // Bones awarded to the player for killing this enemy type
    private static final int BONE_REWARD = 10;

    // Rate at which progress advances along the path (cells per second)
    private static final double MOVE_SPEED = 1.0;

    // Continuous position along the path:
    // integer part = current node index, fractional part = how far between that node and the next
    private double progress = 0.0;

    public SkeletonEnemy(double xPos, double yPos, String spritePath) {
        super(xPos, yPos, spritePath);
        setHealth(DEFAULT_HEALTH);

        // Configure the sprite inherited from Entity for overlay display
        sprite.setPreserveRatio(false);
        // TOP_LEFT alignment lets translateX/Y position the sprite from the pane's top-left corner
        StackPane.setAlignment(sprite, Pos.TOP_LEFT);
    }

    public double getProgress() {
        return progress;
    }

    public void setProgress(double progress) {
        this.progress = progress;
    }

    // Advances progress along the path by delta seconds
    public void advance(double delta) {
        progress += delta * MOVE_SPEED;
    }

    // Returns true when the skeleton has reached the end node of the path
    public boolean hasReachedEnd(List<Node> path) {
        return progress >= path.size() - 1;
    }

    // Returns the X pixel coordinate of the sprite's center within the map pane
    public double getSpriteCenterX() {
        return sprite.getTranslateX() + sprite.getFitWidth() / 2.0;
    }

    // Returns the Y pixel coordinate of the sprite's center within the map pane
    public double getSpriteCenterY() {
        return sprite.getTranslateY() + sprite.getFitHeight() / 2.0;
    }

    // Moves the skeleton's image to the correct pixel position on screen.
    // Because progress is a decimal the skeleton sits between two path nodes and blends between them
    public void updatePosition(List<Node> path, GridPane gameGrid, int mapCols) {

        // Figure out which two path nodes the skeleton is between
        // The whole number part of progress is the index of the node the skeleton just passed
        int currentNodeIndex = (int) progress;

        // Make sure there is always a next node to move toward
        currentNodeIndex = Math.min(currentNodeIndex, path.size() - 2);

        // The decimal part is how far between the two nodes the skeleton is
        double blendFactor = progress - currentNodeIndex;

        // Blend between the two nodes to get a smooth in-between position
        Node currentNode = path.get(currentNodeIndex);
        Node nextNode    = path.get(currentNodeIndex + 1);

        // Interpolate: start at currentNode and move blendFactor of the way toward nextNode.
        // Note: getY() = column
        double blendedColumn = currentNode.getY() + blendFactor * (nextNode.getY() - currentNode.getY());
        // getX() = row
        double blendedRow    = currentNode.getX() + blendFactor * (nextNode.getX() - currentNode.getX());

        // Convert the grid position to pixels on screen

        // Get width of grid cell in pixels
        double cellSizePixels = gameGrid.getWidth() / mapCols;

        // Resize the skeleton image to match one cell, then place it at the right spot
        double gridStartX = gameGrid.getBoundsInParent().getMinX();
        double gridStartY = gameGrid.getBoundsInParent().getMinY();

        // Resize the skeleton image to match one cell, then place it at the right spot
        sprite.setFitWidth(cellSizePixels);
        sprite.setFitHeight(cellSizePixels);
        sprite.setTranslateX(gridStartX + blendedColumn * cellSizePixels);
        sprite.setTranslateY(gridStartY + blendedRow    * cellSizePixels);
    }

    // Removes this skeleton's sprite from the map pane and from the shared enemy list
    public void removeFromScene(Pane gameMapPane) {
        gameMapPane.getChildren().remove(sprite);
        Skeleton.enemyList.remove(this);
    }

    @Override
    public int getBoneReward() {
        return BONE_REWARD;
    }
}
