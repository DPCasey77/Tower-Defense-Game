package BoneForgeDefense.Entities.Skeletons;

import BoneForgeDefense.Entities.Skeleton;

public class NecromancerEnemy extends Skeleton{

	public NecromancerEnemy(double xPos, double yPos, String spritePath) {
		super(xPos, yPos, spritePath);
		// TODO Auto-generated constructor stub
	}

	@Override
	public int getBoneReward() {
		// TODO Auto-generated method stub
		return 20;
	}

}
