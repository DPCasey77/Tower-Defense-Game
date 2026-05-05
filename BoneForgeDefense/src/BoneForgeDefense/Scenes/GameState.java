package BoneForgeDefense.Scenes;

import java.util.List;

public class GameState {
	
	public double bones;
	public int kills;
	public int lives;
	public int mapId;
	
    public List<TowerData> towers;
    
    public GameState() {}

	public GameState(double bones, int kills, int lives) {
		 this.bones = bones;
		 this.kills = kills;
		 this.lives = lives;
	}

}
