package BoneForgeDefense.Scenes;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
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
    private void handleScreenSizeChange(ActionEvent event) {
    	// Get the current stage
    	Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    	if (smallScreenButton.isSelected()) {
    		stage.setWidth(800);
    		stage.setHeight(600);
    	} else if (mediumScreenButton.isSelected()) {
    		stage.setWidth(1200);
    		stage.setHeight(800);
    	} else if (largeScreenButton.isSelected()) {
    		stage.setWidth(1600);
    		stage.setHeight(1000);
    	} else if (extraLargeScreenButton.isSelected()) {
    		stage.setWidth(3840);
    		stage.setHeight(2160);
    	}
    	stage.centerOnScreen();
    }
    
/*    public void initialize() {
 *   	smallScreenButton.selectedProperty().addListener(new ChangeListener<Boolean>() {
 *   		@Override
 *   		public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
 *   			if (newValue) {
 *   				changeWindowSize(1366, 768);
 *   			}
 *   		}
 *   	});
 *   	mediumScreenButton.selectedProperty().addListener(new ChangeListener<Boolean>() {
 *  		@Override
 *  		public void changed(ObservableValue<? extends Boolean>observable, Boolean oldValue, Boolean newValue) {
 *  			if (newValue) {
 *   				changeWindowSize(1920, 1080);
 *   			}
 *   		}
 *   	});
 *   	largeScreenButton.selectedProperty().addListener(new ChangeListener<Boolean>() {
 *   		@Override
 *   		public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
 *   			if (newValue) {
 *   				changeWindowSize(2560, 1440);
 *   			}
 *   		}
 *   	});
 *   	extraLargeScreenButton.selectedProperty().addListener(new ChangeListener<Boolean>() {
 *   		@Override
 *   		public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
 *   			if (newValue) {
 *   				changeWindowSize(3840, 2160);
 *   			}
 *   		}
 *   	});
 *   }
 *   
 *   private Stage stage;
 *   
 *   public void setStage(Stage stage) {
 *   	this.stage = stage;
 *   }
 *   
 *   private void changeWindowSize(double width, double height) {
 *   	if (stage != null) {
 *   		stage.setWidth(width);
 *   		stage.setHeight(height);
 *   		stage.centerOnScreen();
 *   	}
 */   
    
}