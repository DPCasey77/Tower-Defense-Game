package BoneForgeDefense.Scenes;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.stage.Stage;

public class SettingsMenuController {

    @FXML
    private RadioButton extraLargeScreenButton;

    @FXML
    private RadioButton largeScreenButton;

    @FXML
    private RadioButton mediumScreenButton;

    @FXML
    private RadioButton smallScreenButton;

    @FXML
    private Slider volumeSlider;

    @FXML
    void handleScreenSizeChange(ActionEvent event) {

    }
    
    public void initialize() {
    	smallScreenButton.selectedProperty().addListener(new ChangeListener<Boolean>() {
    		@Override
    		public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
    			if (newValue) {
    				changeWindowSize(1366, 768);
    			}
    		}
    	});
    	mediumScreenButton.selectedProperty().addListener(new ChangeListener<Boolean>() {
    		@Override
    		public void changed(ObservableValue<? extends Boolean>observable, Boolean oldValue, Boolean newValue) {
    			if (newValue) {
    				changeWindowSize(1920, 1080);
    			}
    		}
    	});
    	largeScreenButton.selectedProperty().addListener(new ChangeListener<Boolean>() {
    		@Override
    		public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
    			if (newValue) {
    				changeWindowSize(2560, 1440);
    			}
    		}
    	});
    	extraLargeScreenButton.selectedProperty().addListener(new ChangeListener<Boolean>() {
    		@Override
    		public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
    			if (newValue) {
    				changeWindowSize(3840, 2160);
    			}
    		}
    	});
    }
    
    private Stage stage;
    
    public void setStage(Stage stage) {
    	this.stage = stage;
    }
    
    private void changeWindowSize(double width, double height) {
    	if (stage != null) {
    		stage.setWidth(width);
    		stage.setHeight(height);
    		stage.centerOnScreen();
    	}
    }
    
}