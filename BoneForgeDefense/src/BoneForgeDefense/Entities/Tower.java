package BoneForgeDefense.Entities;

public abstract class Tower extends Entity {

    public enum TowerType { OFFENSIVE, DEFENSIVE, SUPPORT }

    protected String name;
    protected TowerType towerType;
    protected int cost;
  

    public Tower(double x, double y, String spritePath, String name, TowerType towerType) {
        super(x, y, spritePath);
        this.name = name;
        this.towerType = towerType;
    }

    public String getName() { 
    	return name;
    }
    public TowerType getTowerType() { 
    	return towerType;
    }
    public int getCost() {
    	return cost;
    }

}
