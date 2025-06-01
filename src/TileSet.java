import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

/**
 * Class used to create and implement the attributes of individual tile sets
 */
public class TileSet {
    /**
     * Image of the tile set
     */
    private final Image image;
    /**
     * first gid attribute from tmj file
     */
    private final int firstGid;
    /**
     * width of the tile
     */
    private final int tileWidth;
    /**
     * height of the tile
     */
    private final int tileHeight;
    /**
     * amount of columns
     */
    private final int columns;
    /**
     * amount of tiles
     */
    private final int tileCount;

    /**
     * Constructor
     * @param image Image of the tile set
     * @param firstGid first gid attribute from tmj file
     * @param tileWidth width of the tile
     * @param tileHeight height of the tile
     * @param columns amount of columns
     */
    public TileSet(Image image, int firstGid, int tileWidth, int tileHeight, int columns) {
        this.image = image;
        this.firstGid = firstGid;
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        this.columns = columns;
        this.tileCount = (int) (image.getWidth() / tileWidth) * (int) (image.getHeight() / tileHeight);
    }

    /**
     * Method used to find out whether a certain tile is in a certain tile set
     * @param tileId id of the tile
     * @return is the tile in the said tile set?
     */
    public boolean containsTile(int tileId) {
        return tileId >= firstGid && tileId < firstGid + tileCount;
    }

    /**
     * Method used to draw individual tiles
     * @param gc GraphicsContext instance
     * @param tileId id of the tile
     * @param x x coordinate of the tile
     * @param y y coordinate of the tile
     */
    public void drawTile(GraphicsContext gc, int tileId, int x, int y) {
        int localId = tileId - firstGid;
        int tileX = (localId % columns) * tileWidth;
        int tileY = (localId / columns) * tileHeight;
        gc.drawImage(image,
                tileX, tileY, tileWidth, tileHeight, // source rectangle
                x, y, tileWidth, tileHeight);       // destination rectangle
    }
}