package BoneForgeDefense.Entities.OffensiveTowers;

import java.util.Comparator;
import java.util.List;

import BoneForgeDefense.Entities.Skeletons.Skeleton;


public class BoneBusterTower extends OffensiveTower {
	
	private enum AttackPriority {FIRST,LAST,CLOSEST,FARTHEST,LOWEST_HEALTH,HIGHEST_HEALTH};
	private AttackPriority priority;
	
    public BoneBusterTower(double x, double y) {
        super(x, y, "/BoneForgeDefense/Sprites/boneBusterTower.png",
              "Bone Buster Tower",
              75,    // cost
              15.0,  // damage
              1.0);  // fire rate (shots per second)
        priority = AttackPriority.FIRST;
    }

    @Override
	public void shoot() {
        System.out.println("Bone Buster Tower fires!");
        // spawn projectile here later
    }

	@Override
	public Skeleton getTarget(List<Skeleton> enemyList) {
		for(Skeleton e:enemyList) {
			if (e.getRange(xPos, yPos) > this.range) {
				enemyList.remove(e);
			}
		}
		
		if (!enemyList.isEmpty()) {
			switch(priority) {
				case CLOSEST:
					enemyList.sort(Comparator.comparingDouble(enemy -> enemy.getRange(this.xPos, this.yPos)));
					return enemyList.get(0);
				case FARTHEST:
					enemyList.sort(Comparator.comparingDouble(enemy -> enemy.getRange(this.xPos, this.yPos)));
					return enemyList.reversed().get(0);
				case FIRST:
					return enemyList.get(0);
				case LAST:
					return enemyList.reversed().get(0);
				case LOWEST_HEALTH:
					enemyList.sort(Comparator.comparingDouble(enemy -> enemy.getHealth()));
					return enemyList.get(0);
				case HIGHEST_HEALTH:
					enemyList.sort(Comparator.comparingDouble(enemy ->  enemy.getHealth()));
					return enemyList.reversed().get(0);
				default:
					return null;		
			}	
		}
		
		else {
			return null;
		}
	}

}
