package BoneForgeDefense.Entities.SupportTowers;


public class SlowTower extends SupportTower {
	
  public SlowTower(double x, double y) {	
	super(x, y,											//position X and Y
			"/BoneForgeDefense/Sprites/slowTower.png",	//Sprite Location
            "Slow Tower",								//Tower Name
            TowerType.SUPPORT,							//Tower Type
            30);										//Tower Range
	
	
  }

  
}