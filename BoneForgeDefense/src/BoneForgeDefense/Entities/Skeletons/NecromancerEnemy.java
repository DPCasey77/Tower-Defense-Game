package BoneForgeDefense.Entities.Skeletons;

import BoneForgeDefense.Entities.Skeleton;
import BoneForgeDefense.Scenes.LevelOneController;
import BoneForgeDefense.Scenes.SceneSelector;

public class NecromancerEnemy extends Skeleton{
	private double castTimeCoolDown = 10.0*60;
	private double castTimer = 2.0*60;
	private double castTimeCounter = 0;
	private double coolDown = 0;
	private double castRange = 5;
	private boolean isCasting = false;
	private static String spritePath = "/BoneForgeDefense/Sprites/necromancer.png";
	private double defualtMoveSpeed = 0.5;
	
	public NecromancerEnemy(double xPos, double yPos) {
		super(xPos, yPos, spritePath);
		health = 500;
		moveSpeed=defualtMoveSpeed;
		boneReward=100;
	}
	
	public void castRessurrect(int enemiesToSpawn, double delta) {
		castTimeCounter++;
		if (castTimeCounter>=castTimer) {
			for(int i=0;i<enemiesToSpawn;i++) {
				SceneSelector.levelOneController.spawnSkeleton(0);
			}
			isCasting=false;
		}
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

	@Override
	protected void update(double delta) {
		canCast(delta);
		if(isCasting) {
			moveSpeed=0;
		}
		else {
			moveSpeed=defualtMoveSpeed;
		}
		
	}

	private void canCast(double delta) {
		if (coolDown<=0) {
			int enemiesToSpawn = (int)getBonesInRange()%100;
			if(enemiesToSpawn>=1) {
				isCasting=true;
				castRessurrect(enemiesToSpawn,delta);
				coolDown=castTimeCoolDown;
			}
		}
		else {
			coolDown--;
		}
	}

}
