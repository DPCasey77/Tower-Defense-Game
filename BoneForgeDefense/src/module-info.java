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
	opens BoneForgeDefense.Scenes to javafx.fxml, javafx.graphics;
	opens BoneForgeDefense.Entities to javafx.fxml, javafx.graphics;
	opens BoneForgeDefense.Entities.Skeletons to javafx.fxml, javafx.graphics;
	opens BoneForgeDefense.Entities.OffensiveTowers to javafx.fxml, javafx.graphics;
	opens BoneForgeDefense.Entities.DefensiveTowers to javafx.fxml, javafx.graphics;
	opens BoneForgeDefense.Entities.SupportTowers to javafx.fxml, javafx.graphics;
}