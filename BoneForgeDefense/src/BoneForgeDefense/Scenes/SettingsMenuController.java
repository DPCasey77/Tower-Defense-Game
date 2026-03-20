package BoneForgeDefense.Scenes;

import javafx.animation.AnimationTimer;


public class SettingsMenuController {
	
	private long lastNanoSecond = 0;
	private Boolean gameOver = false;
	
	public void startGameLoop() {
        new AnimationTimer() {
            @Override
            public void handle(long now) {
            	
            	// Skip check on first frame
                if (lastNanoSecond == 0) {
                		lastNanoSecond = now;
                		return; 
                }
                
                // Convert nanoseconds to seconds and calculate how many have past since 
                // last frame
                double delta = (now - lastNanoSecond) / 1_000_000_000.0;
                lastNanoSecond = now;
 
                // Stop time if Game Over bool is true
                if (!gameOver) {
                    update(delta);
                }
                
            }
        }.start();
    }
	
	private int testCounter = 0;
	private void update(double delta) {
		testCounter += 1;
		System.out.println(testCounter);
	}

}
