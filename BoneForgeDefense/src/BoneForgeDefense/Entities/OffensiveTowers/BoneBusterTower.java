package BoneForgeDefense.Entities.OffensiveTowers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import BoneForgeDefense.Entities.Skeleton;


public class BoneBusterTower extends OffensiveTower {
	
	private enum AttackPriority {FIRST,LAST,CLOSEST,FARTHEST,LOWEST_HEALTH,HIGHEST_HEALTH};
	private AttackPriority priority;
	private record InRangePair(double rangeToTurret, int enemyListIndex) {};
	
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
	public int getTarget() {
		List<InRangePair>inRangeEnemies = new ArrayList<InRangePair>();
		int counter = 0;
		for(Skeleton e : Skeleton.enemyList) {
			double rangeToEnemy = e.getRange(xPos, yPos);
			if (rangeToEnemy <= (this.range * this.range)) {
				inRangeEnemies.add(new InRangePair(rangeToEnemy,counter));
			}
			counter++;
		}
		
		if (!inRangeEnemies.isEmpty()) {
			switch(priority) {
				case CLOSEST:
					return Collections.min(inRangeEnemies,Comparator.comparingDouble(InRangePair::rangeToTurret)).enemyListIndex();
				case FARTHEST:
					return Collections.max(inRangeEnemies,Comparator.comparingDouble(InRangePair::rangeToTurret)).enemyListIndex();
				case FIRST:
					return inRangeEnemies.get(0).enemyListIndex();
				case LAST:
					return inRangeEnemies.get(inRangeEnemies.size()-1).enemyListIndex();
				case LOWEST_HEALTH:
					return Collections.min(inRangeEnemies,Comparator.comparingDouble(enemy -> Skeleton.enemyList.get(enemy.enemyListIndex()).getHealth())).enemyListIndex();
				case HIGHEST_HEALTH:
					return Collections.max(inRangeEnemies,Comparator.comparingDouble(enemy -> Skeleton.enemyList.get(enemy.enemyListIndex()).getHealth())).enemyListIndex();
				default:
					return -1;		
			}
		}
		
		else {
			return -1;
		}
	}

}
