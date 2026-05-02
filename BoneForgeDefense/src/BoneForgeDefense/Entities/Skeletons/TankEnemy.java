package BoneForgeDefense.Entities.Skeletons;

import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

import java.util.List;

import BoneForgeDefense.Node;
import BoneForgeDefense.Entities.Skeleton;

public class TankEnemy extends Skeleton {

    // Continuous position along the path:
    // integer part = current node index, fractional part = how far between that node and the next
    
    private static String spritePath = "/BoneForgeDefense/Sprites/skeleton.png";

    public TankEnemy(double xPos, double yPos) {
        super(xPos, yPos, spritePath);
        
        // Starting health applied to every new skeleton — reduced by projectile hits
        health = 1000;
        // Rate at which progress advances along the path (cells per second)
		moveSpeed=0.25;
		// Bones awarded to the player for killing this enemy type
		boneReward=500;

        // Configure the sprite inherited from Entity for overlay display
        sprite.setPreserveRatio(false);
        // TOP_LEFT alignment lets translateX/Y position the sprite from the pane's top-left corner
        StackPane.setAlignment(sprite, Pos.TOP_LEFT);
    }

	@Override
	protected void update(double delta) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void updatePosition(GridPane gameGrid, int mapCols) {

        // Figure out which two path nodes the skeleton is between
        // The whole number part of progress is the index of the node the skeleton just passed
        int currentNodeIndex = (int) progress;

        // Make sure there is always a next node to move toward
        currentNodeIndex = Math.min(currentNodeIndex, path.size() - 2);

        // The decimal part is how far between the two nodes the skeleton is
        double blendFactor = progress - currentNodeIndex;

        // Blend between the two nodes to get a smooth in-between position
        Node currentNode = path.get(currentNodeIndex);
        Node nextNode = path.get(currentNodeIndex + 1);

        // Interpolate: start at currentNode and move blendFactor of the way toward nextNode
        
        // getY() = column
        yPos = currentNode.getY() + blendFactor * (nextNode.getY() - currentNode.getY());
        // getX() = row
        xPos = currentNode.getX() + blendFactor * (nextNode.getX() - currentNode.getX());

        // Convert the grid position to pixels on screen

        // Get width of grid cell in pixels
        double cellSizePixels = gameGrid.getWidth() / mapCols;

        // Resize the skeleton image to match one cell, then place it at the right spot
        double gridStartX = gameGrid.getBoundsInParent().getMinX();
        double gridStartY = gameGrid.getBoundsInParent().getMinY();

        // Resize the skeleton image to match one cell, then place it at the right spot
        sprite.setFitWidth(cellSizePixels*1.5);
        sprite.setFitHeight(cellSizePixels*1.5);
        sprite.setTranslateX(gridStartX + (yPos-.375) * cellSizePixels);
        sprite.setTranslateY(gridStartY + (xPos-.375) * cellSizePixels);
    }

	@Override
	public List<Node> getPath() {
		// TODO Auto-generated method stub
		return path;
	}


}
