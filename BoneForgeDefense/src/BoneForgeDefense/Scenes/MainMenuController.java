package BoneForgeDefense.Scenes;

import java.io.File;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;

public class MainMenuController {

    @FXML private Button continueGameButton;
    @FXML private Button exitButton;
    @FXML private Button newGameButton;
    @FXML private Button settingsButton;

    
    @FXML
    public void initialize() {
        continueGameButton.setDisable(!new File("SaveGameData.json").exists());
    }

    // Enables the Continue button whenever a save file is present
    public void refreshContinueButton() {
        continueGameButton.setDisable(!new File("SaveGameData.json").exists());
    }

    @FXML
    void startNewGame(MouseEvent event) {
        SceneSelector.setGamePaused(false);
        SceneSelector.launchLevelOneScene();
        SceneSelector.getLevelOneController().startNewGame(100);
    }

    @FXML
    void continueGame(MouseEvent event) {
        SceneSelector.setGamePaused(false);
        SceneSelector.launchLevelOneScene();
        SceneSelector.getLevelOneController().startLoadedGame();
    }

    @FXML
    void openSettings(MouseEvent event) {
        SceneSelector.launchSettingsMenuScene();
    }

    @FXML
    void exitProgram(MouseEvent event) {
        System.exit(0);
    }

}
