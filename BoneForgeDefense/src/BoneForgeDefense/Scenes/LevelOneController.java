package BoneForgeDefense.Scenes;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.animation.AnimationTimer;


public class LevelOneController {
	
	private long lastNanoSecond = 0;
	private Boolean gameOver = false;
	
	private double bones;
	private double money;
	
	@FXML
    private Label bonesTextbox;

    @FXML
    private Label moneyTextbox;
       
	
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
	
	public void startNewGame(double bones, double money) { 		
		this.bones = bones;
		this.money = money;
		bonesTextbox.setText(String.format("%.0f", bones));
		moneyTextbox.setText(String.format("%.0f", money));
		startGameLoop();
	}
	
	//private int testCounter = 0;
	private void update(double delta) {
		//testCounter += 1;
		//System.out.println(testCounter);
	}
	
	public double getBones() {
		return bones;
	}
	
	public void setBones(double bones) {
		this.bones = bones;
	}

	public double getMoney() {
		return money;
	}

	public void setMoney(double money) {
		this.money = money;
	}

}
