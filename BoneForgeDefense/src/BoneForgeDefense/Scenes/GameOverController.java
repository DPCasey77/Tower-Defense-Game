package BoneForgeDefense.Scenes;

import javafx.fxml.FXML;

public class GameOverController {

    @FXML
    private void returnToMainMenu() {
        SceneSelector.setGamePaused(false);
        SceneSelector.launchMainMenuScene();
    }
}
