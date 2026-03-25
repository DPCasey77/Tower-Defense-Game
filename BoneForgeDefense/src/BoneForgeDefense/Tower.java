package BoneForgeDefense;

public abstract class Tower extends Entity {

    public enum TowerType { OFFENSIVE, DEFENSIVE, SUPPORT }

    protected String name;
    protected TowerType towerType;
    protected int cost;
    protected double damage;
    protected double fireRate;       
    protected double fireRateTimer;  

    public Tower(double x, double y, String spritePath, String name, TowerType towerType,
                 int cost, double damage, double fireRate) {
        super(x, y, spritePath);
        this.name = name;
        this.towerType = towerType;
        this.cost = cost;
        this.damage = damage;
        this.fireRate = fireRate;
        this.fireRateTimer = 0;
    }

    protected abstract void shoot();

    
    public String getName() { 
    	return name;
    }
    public TowerType getTowerType() { 
    	return towerType;
    }
    public int getCost() {
    	return cost;
    }
    public double getDamage() {
    	return damage;
    }
    public double getFireRate()
    { 
    	return fireRate;
   }
}
