/**
 * 
 */
/**
 * 
 */
module BoneForgeDefense {
	requires javafx.graphics;
	requires javafx.fxml;
	requires javafx.controls;
	requires javafx.base;
	requires com.fasterxml.jackson.databind;
	opens BoneForgeDefense.Scenes to javafx.fxml, javafx.graphics, com.fasterxml.jackson.databind;
	opens BoneForgeDefense.Entities to javafx.fxml, javafx.graphics;
	opens BoneForgeDefense.Entities.Skeletons to javafx.fxml, javafx.graphics;
	opens BoneForgeDefense.Entities.OffensiveTowers to javafx.fxml, javafx.graphics;
	opens BoneForgeDefense.Entities.DefensiveTowers to javafx.fxml, javafx.graphics;
	opens BoneForgeDefense.Entities.SupportTowers to javafx.fxml, javafx.graphics;
}