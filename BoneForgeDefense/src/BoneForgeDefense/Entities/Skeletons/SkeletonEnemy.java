package BoneForgeDefense.Entities.Skeletons;

import javafx.scene.image.ImageView;

	public class SkeletonEnemy extends Skeleton {
		// Continuous position along the path:
		// integer part = current node index
		// fractional part = how far between that node and the next
		private double progress = 0.0;
		private final ImageView view;
		SkeletonEnemy(double xPos, double yPos, String spritePath, ImageView view) { 
			super(xPos,yPos,spritePath);
			this.view = view; 
		}
		public ImageView getView() {
			return view;
		}
		public double getProgress() {
			return progress;
		}
		public void setProgress(double progress) {
			this.progress = progress;
		}
}
