package BoneForgeDefense.Scenes;

import java.util.Map;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneSelector extends Application{

	@Override
	public void start(Stage stage) throws Exception {
		// TODO Auto-generated method stub
		
		//get launch parameters
		Parameters params = getParameters();
		Map<String, String> namedParams = params.getNamed();
		
		
		double width = Double.parseDouble(namedParams.getOrDefault("width", "800"));
	    double height = Double.parseDouble(namedParams.getOrDefault("height", "600"));
	    
	    
	    Parent mainMenuRoot = FXMLLoader.load(getClass().getResource("/BoneForgeDefense/Scenes/MainMenu.fxml"));
		Scene mainMenuScene = new Scene(mainMenuRoot, width, height);
		
		Parent levelOneRoot = FXMLLoader.load(getClass().getResource("/BoneForgeDefense/Scenes/LevelOne.fxml"));
		Scene levelOne = new Scene(levelOneRoot, width, height);
		
		stage.setTitle("Bone Forge Defense");
		stage.setScene(mainMenuScene);
		stage.show();
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		launch(args);
	}
}
