import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class TileSet {
    private final Image image;
    private final int firstGid;
    private final int tileWidth;
    private final int tileHeight;
    private final int columns;
    private final int tileCount;

    public TileSet(Image image, int firstGid, int tileWidth, int tileHeight, int columns) {
        this.image = image;
        this.firstGid = firstGid;
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        this.columns = columns;
        this.tileCount = (int) (image.getWidth() / tileWidth) * (int) (image.getHeight() / tileHeight);
    }

    public boolean containsTile(int tileId) {
        return tileId >= firstGid && tileId < firstGid + tileCount;
    }

    public void drawTile(GraphicsContext gc, int tileId, int x, int y) {
        int localId = tileId - firstGid;
        int tileX = (localId % columns) * tileWidth;
        int tileY = (localId / columns) * tileHeight;

        gc.drawImage(image,
                tileX, tileY, tileWidth, tileHeight, // source rectangle
                x, y, tileWidth, tileHeight);       // destination rectangle
    }
}