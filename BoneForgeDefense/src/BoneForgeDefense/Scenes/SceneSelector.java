package BoneForgeDefense.Scenes;

import java.util.Map;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneSelector extends Application{

	private static Stage activeStage;
	private static Scene mainMenuScene;
	private static Scene levelOneScene;
	
	@Override
	public void start(Stage stage) throws Exception {
		// TODO Auto-generated method stub
		
		activeStage = stage;
		
		//get launch parameters
		Parameters params = getParameters();
		Map<String, String> namedParams = params.getNamed();
		
		
		double width = Double.parseDouble(namedParams.getOrDefault("width", "800"));
	    double height = Double.parseDouble(namedParams.getOrDefault("height", "600"));
	    
	    
	    Parent mainMenuRoot = FXMLLoader.load(getClass().getResource("/BoneForgeDefense/Scenes/MainMenu.fxml"));
		mainMenuScene = new Scene(mainMenuRoot, width, height);
		
		Parent levelOneRoot = FXMLLoader.load(getClass().getResource("/BoneForgeDefense/Scenes/LevelOne.fxml"));
		levelOneScene = new Scene(levelOneRoot, width, height);
		
		stage.setTitle("Bone Forge Defense");
		stage.setScene(mainMenuScene);
		stage.show();
	}
	
	public static void launchLevelOneScene() {
		activeStage.setScene(levelOneScene);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		launch(args);
	}
}
