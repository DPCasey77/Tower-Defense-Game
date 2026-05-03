package BoneForgeDefense;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

public class BonePile {

    // The maximum number of bones that can sit on one tile
    public static final int MAX_BONES = 5;

    // Every active bone pile on the map
    public static List<BonePile> allPiles = new ArrayList<>();

    // Load image for each pile sizes
    private static final String[] SPRITE_PATHS = {
        "/BoneForgeDefense/Sprites/bonePile1.png",
        "/BoneForgeDefense/Sprites/bonePile2.png",
        "/BoneForgeDefense/Sprites/bonePile3.png",
        "/BoneForgeDefense/Sprites/bonePile4.png",
        "/BoneForgeDefense/Sprites/bonePile5.png"
    };
    private int count;
    private final int gridRow;
    private final int gridCol;
    private final ImageView sprite;

    public BonePile(int gridRow, int gridCol) {
        this.gridRow = gridRow;
        this.gridCol = gridCol;
        this.count   = 1;

        this.sprite = new ImageView();
        sprite.setPreserveRatio(false);
        // TOP_LEFT alignment lets translateX/Y position the sprite from the pane's top-left corner
        StackPane.setAlignment(sprite, Pos.TOP_LEFT);
        updateSprite();

        allPiles.add(this);
    }

    // Adds one bone to the pile (stops at MAX_BONES)
    // Returns true if the pile just reached max capacity for the first time
    public boolean addBone() {
        if (count >= MAX_BONES) return false;
        boolean wasAtMax = isAtMaxCapacity();
        count++;
        updateSprite();
        return !wasAtMax && isAtMaxCapacity();
    }

    // Removes one bone from the pile
    // Returns true if the pile just dropped below max capacity (triggers re-pathfinding)
    public boolean removeBone() {
        boolean wasAtMax = isAtMaxCapacity();
        if (count > 0) {
            count--;
            updateSprite();
        }
        return wasAtMax && !isAtMaxCapacity();
    }

    public boolean  isAtMaxCapacity() { return count >= MAX_BONES; }
    public boolean  isEmpty()         { return count <= 0; }
    public int      getCount()        { return count; }
    public int      getGridRow()      { return gridRow; }
    public int      getGridCol()      { return gridCol; }
    public ImageView getSprite()      { return sprite; }

    // Resizes and moves the sprite to fill the correct grid cell on screen
    public void updatePosition(GridPane gameGrid, int mapCols) {
        double cellSize   = gameGrid.getWidth() / mapCols;
        double gridStartX = gameGrid.getBoundsInParent().getMinX();
        double gridStartY = gameGrid.getBoundsInParent().getMinY();
        sprite.setFitWidth(cellSize);
        sprite.setFitHeight(cellSize);
        sprite.setTranslateX(gridStartX + gridCol * cellSize);
        sprite.setTranslateY(gridStartY + gridRow * cellSize);
    }

    // Returns the center X pixel of this pile's cell
    public double getPixelCenterX(GridPane gameGrid, int mapCols) {
        double cellSize   = gameGrid.getWidth() / mapCols;
        double gridStartX = gameGrid.getBoundsInParent().getMinX();
        return gridStartX + gridCol * cellSize + cellSize / 2.0;
    }

    // Returns the center Y pixel of this pile's cell 
    public double getPixelCenterY(GridPane gameGrid, int mapCols) {
        // The grid is kept square, so cell height equals cell width
        double cellSize   = gameGrid.getWidth() / mapCols;
        double gridStartY = gameGrid.getBoundsInParent().getMinY();
        return gridStartY + gridRow * cellSize + cellSize / 2.0;
    }

    // Swaps the sprite to the image matching the current bone count
    private void updateSprite() {
        int index = Math.max(0, Math.min(count - 1, SPRITE_PATHS.length - 1));
        InputStream stream = BonePile.class.getResourceAsStream(SPRITE_PATHS[index]);
        if (stream != null) {
            sprite.setImage(new Image(stream));
        }
    }
   
}
