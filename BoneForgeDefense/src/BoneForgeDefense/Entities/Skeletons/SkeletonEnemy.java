package BoneForgeDefense.Entities.Skeletons;

import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;

import BoneForgeDefense.Entities.Skeleton;

public class SkeletonEnemy extends Skeleton {

    // Continuous position along the path:
    // integer part = current node index, fractional part = how far between that node and the next
    
    private static String spritePath = "/BoneForgeDefense/Sprites/skeleton.png";

    public SkeletonEnemy(double xPos, double yPos) {
        super(xPos, yPos, spritePath);
        
        // Starting health applied to every new skeleton — reduced by projectile hits
        health = 100;
        // Rate at which progress advances along the path (cells per second)
		moveSpeed=1.0;
		// Bones awarded to the player for killing this enemy type
		boneReward=10;

        // Configure the sprite inherited from Entity for overlay display
        sprite.setPreserveRatio(false);
        // TOP_LEFT alignment lets translateX/Y position the sprite from the pane's top-left corner
        StackPane.setAlignment(sprite, Pos.TOP_LEFT);
    }

	@Override
	protected void update(double delta) {
		// TODO Auto-generated method stub
		
	}


}
