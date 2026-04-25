package BoneForgeDefense.Entities.Skeletons;

import BoneForgeDefense.Entities.Skeleton;
import BoneForgeDefense.Scenes.LevelOneController;

public class NecromancerEnemy extends Skeleton{
	private double castTime = 10.0;
	private double castRange = 5;
	private static String spritePath = "/BoneForgeDefense/Sprites/necromancer.png";
	
	public NecromancerEnemy(double xPos, double yPos) {
		super(xPos, yPos, spritePath);
		health = 500;
		moveSpeed=0.5;
		boneReward=100;
		
	}

	
	public void castRessurrect() {

	}
	
	private double getBonesInRange(){
		double bonesInRange = 0;
		int minX = 0;
		int minY = 0;
		int maxX = LevelOneController.MAP_COLS-1;
		int maxY = LevelOneController.MAP_ROWS-1;
		
		if(Math.floor(this.xPos - castRange) >= 0) {
			minX = (int) Math.floor(this.xPos - castRange);
		}
		if(Math.floor(this.xPos + castRange) <= LevelOneController.MAP_COLS-1) {
			maxX = (int) Math.floor(this.xPos + castRange);
		}
		if(Math.floor(this.yPos - castRange) >= 0) {
			minY = (int) Math.floor(this.yPos - castRange);
		}
		if(Math.floor(this.yPos + castRange) <= LevelOneController.MAP_ROWS-1) {
			maxY = (int) Math.floor(this.yPos + castRange);
		}
		
		
		for(int i = minY; i<=maxY; i++) {
			for(int j = minX; j<=maxX; j++) {
				if (LevelOneController.mapNodes[i][j].getRange(this.xPos,this.yPos)<=(this.castRange*this.castRange)) {
					bonesInRange+=LevelOneController.mapNodes[i][j].getBones();
				}
			}
		}
		return bonesInRange;
	}

}
