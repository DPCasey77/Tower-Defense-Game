package BoneForgeDefense.Scenes;

import java.util.List;

public class GameState {
	
	public double bones;
	public double kills;
	public int lives;
	public int wave;
	public int mapId;
	
    public List<TowerData> towers;
    
    public GameState() {}

	public GameState(double bones, int kills, int wave, int lives) {
		 this.bones = bones;
		 this.kills = kills;
		 this.wave = wave;
		 this.lives = lives;
	}

}
