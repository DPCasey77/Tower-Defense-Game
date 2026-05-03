package BoneForgeDefense.Scenes;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;

public class MainMenuController {

    @FXML private Button continueGameButton;
    @FXML private Button exitButton;
    @FXML private Button newGameButton;
    @FXML private Button settingsButton;

    // Called automatically by JavaFX after the FXML fields are injected
    @FXML
    public void initialize() {
        // No game is paused when the app first launches, so disable Continue
        continueGameButton.setDisable(true);
    }

    // Called by SceneSelector every time the main menu is shown.
    // Enables the Continue button only when a game is currently paused.
    public void refreshContinueButton() {
        continueGameButton.setDisable(!SceneSelector.isGamePaused());
    }

    @FXML
    void startNewGame(MouseEvent event) {
        // Starting a new game clears any paused game, so mark it as not paused
        SceneSelector.setGamePaused(false);
        SceneSelector.launchLevelOneScene();
        SceneSelector.getLevelOneController().startNewGame(100);
    }

    @FXML
    void continueGame(MouseEvent event) {
        SceneSelector.setGamePaused(false);
        SceneSelector.launchLevelOneScene();
        SceneSelector.getLevelOneController().resumeGame();
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
