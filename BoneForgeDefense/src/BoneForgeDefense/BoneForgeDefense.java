package BoneForgeDefense;

import BoneForgeDefense.Scenes.SceneSelector;
import javafx.application.Application;

public class BoneForgeDefense {

	public static void main(String[] args) {
		// TODO create the whole ass game
		System.out.println("Please work!!!");
		System.out.println("Lets hope this push works!");
		//TODO Create settings file
		
		StartUp(args);
		
	}

	
	//Startup Sequence
	private static void StartUp(String[] args) {
		//Create Game Settings
		GameSettings gameSettings = new GameSettings();
		//gather needed info to start the MainMenu Scene
		int height = (int) gameSettings.getSetting("screenHeight");
		int width = (int) gameSettings.getSetting("screenWidth");
		Application.launch(SceneSelector.class, "--height=" + height, "--width=" + width);
		
	}

}
