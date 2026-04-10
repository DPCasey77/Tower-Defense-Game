package BoneForgeDefense.Entities.DefensiveTowers;

public class ShieldTower extends DefensiveTower {

    public ShieldTower(double x, double y) {
        super(x, y,
        	"/BoneForgeDefense/Sprites/shieldTower.png",	//Sprite Location
            "Shield Tower",									//Tower Name
            TowerType.DEFENSIVE,							//Tower Type
            50);											//Tower Range
    }

}
