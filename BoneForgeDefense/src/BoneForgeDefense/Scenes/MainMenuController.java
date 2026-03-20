package BoneForgeDefense.Scenes;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;

public class MainMenuController {

    @FXML
    private Button continueGameButton;

    @FXML
    private Button exitButton;

    @FXML
    private Button newGameButton;

    @FXML
    private Button settingsButton;
    
    SceneSelector sceneSelector = new SceneSelector();

    @FXML
    void startNewGame(MouseEvent event) {
    	SceneSelector.launchLevelOneScene();
    	System.out.println("New game started!");
    }

}

