package BoneForgeDefense.Scenes;

import java.util.Map;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneSelector extends Application {

    private static Stage activeStage;
    private static Scene mainMenuScene;
    private static Scene levelOneScene;
    private static Scene settingsMenuScene;

    public static LevelOneController   levelOneController;
    private static MainMenuController  mainMenuController;

    // True while a game is running but paused (player went to main menu mid-game)
    private static boolean gamePaused = false;

    @Override
    public void start(Stage stage) throws Exception {

        activeStage = stage;

        // Get launch parameters
        Parameters params = getParameters();
        Map<String, String> namedParams = params.getNamed();

        double width  = Double.parseDouble(namedParams.getOrDefault("width",  "1600"));
        double height = Double.parseDouble(namedParams.getOrDefault("height", "1000"));

        // Load MainMenu using the instance FXMLLoader so we can get the controller
        FXMLLoader mainMenuLoader = new FXMLLoader(getClass().getResource("/BoneForgeDefense/Scenes/MainMenu.fxml"));
        Parent mainMenuRoot = mainMenuLoader.load();
        mainMenuController  = mainMenuLoader.getController();
        mainMenuScene = new Scene(mainMenuRoot, width, height);

        FXMLLoader levelOneLoader = new FXMLLoader(getClass().getResource("/BoneForgeDefense/Scenes/LevelOne.fxml"));
        Parent levelOneRoot = levelOneLoader.load();
        levelOneController  = levelOneLoader.getController();
        levelOneScene = new Scene(levelOneRoot, width, height);

        Parent settingsMenuRoot = FXMLLoader.load(getClass().getResource("/BoneForgeDefense/Scenes/SettingsMenu.fxml"));
        settingsMenuScene = new Scene(settingsMenuRoot, width, height);

        stage.setTitle("Bone Forge Defense");
        stage.setScene(mainMenuScene);
        stage.show();
    }

    // Switches to the main menu and refreshes the Continue Game button
    public static void launchMainMenuScene() {
        activeStage.setScene(mainMenuScene);
        mainMenuController.refreshContinueButton();
    }

    public static void launchSettingsMenuScene() {
        activeStage.setScene(settingsMenuScene);
    }

    public static void launchLevelOneScene() {
        activeStage.setScene(levelOneScene);
    }

    public static LevelOneController getLevelOneController() {
        return levelOneController;
    }

    public static void setGamePaused(boolean paused) {
        gamePaused = paused;
    }

    public static boolean isGamePaused() {
        return gamePaused;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
