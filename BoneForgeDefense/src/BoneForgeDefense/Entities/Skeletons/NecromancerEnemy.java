package BoneForgeDefense.Entities.Skeletons;

import java.util.List;

import BoneForgeDefense.Node;
import BoneForgeDefense.Entities.Skeleton;
import BoneForgeDefense.Scenes.LevelOneController;
import BoneForgeDefense.Scenes.SceneSelector;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;

public class NecromancerEnemy extends Skeleton{
	private double castTimeCoolDown = 10.0;
	private double castTimer = 2.0;
	private double castTimeCounter = 0;
	private double coolDown = 0;
	private double castRange = 5;
	private boolean isCasting = false;
	private int enemiesToSpawn =0;
	private static String spritePath = "/BoneForgeDefense/Sprites/necromancer.png";
	private double defualtMoveSpeed = 0.5;
	
	public NecromancerEnemy(double xPos, double yPos) {
		super(xPos, yPos, spritePath);
		health = 500;
		moveSpeed=defualtMoveSpeed;
		boneReward=200;
		// Configure the sprite inherited from Entity for overlay display
		sprite.setPreserveRatio(false);
        // TOP_LEFT alignment lets translateX/Y position the sprite from the pane's top-left corner
        StackPane.setAlignment(sprite, Pos.TOP_LEFT);
	}
	
	public void castRessurrect(int enemiesToSpawn, double delta) {
		System.out.println("Casting Timer: " + castTimeCounter);
		if (castTimeCounter>=castTimer) {
			for(int i=0;i<enemiesToSpawn;i++) {
				SceneSelector.levelOneController.spawnSkeleton(5);
			}
			isCasting=false;
			this.enemiesToSpawn=0;
			castTimeCounter=0;
		}
		castTimeCounter+=delta;
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
		if(bonesInRange>=100) {
			for(int i = minY; i<=maxY; i++) {
				for(int j = minX; j<=maxX; j++) {
					if (LevelOneController.mapNodes[i][j].getRange(this.xPos,this.yPos)<=(this.castRange*this.castRange)) {
						LevelOneController.mapNodes[i][j].setBones(0);
					}
				}
			}
		}
		return bonesInRange;
	}
	private void removeBonesInRange(){
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
					LevelOneController.mapNodes[i][j].setBones(0);
				}
			}
		}
	}

	@Override
	protected void update(double delta) {
		
		if(isCasting) {
			moveSpeed=0;
			castRessurrect(enemiesToSpawn,delta);
		}
		else {
			canCast(delta);
			moveSpeed=defualtMoveSpeed;
		}
		
	}

	private void canCast(double delta) {
		if (coolDown<=0) {
			enemiesToSpawn = (int)getBonesInRange()/100-(int)getBonesInRange()%100;
			if(enemiesToSpawn>=1) {
				isCasting=true;
				removeBonesInRange();
				castRessurrect(enemiesToSpawn,delta);
				System.out.println("Casting Resurrect at x:" + xPos + " y:" + yPos);
				coolDown=castTimeCoolDown;
			}
		}
		else {
			coolDown--;
		}
	}

	@Override
	public List<Node> getPath() {
		// TODO Auto-generated method stub
		return path;
	}

}
