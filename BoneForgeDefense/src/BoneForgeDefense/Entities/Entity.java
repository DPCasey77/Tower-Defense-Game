package BoneForgeDefense.Entities;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public abstract class Entity {

    protected double xPos;
    protected double yPos;
    protected ImageView sprite;

    public Entity(double x, double y, String spritePath) {
        this.xPos = x;
        this.yPos = y;
        this.sprite = new ImageView(new Image(getClass().getResourceAsStream(spritePath)));
        this.sprite.setX(x);
        this.sprite.setY(y);
    }

    public double getX() {
    	return xPos; 
    }
    
    public double getY() { 
    	return yPos; 
    }

    public void setPosition(double x, double y) {
        this.xPos = x;
        this.yPos = y;
        sprite.setX(x);
        sprite.setY(y);
    }

    public ImageView getSprite() { 
    	return sprite;
    }
    
    

}