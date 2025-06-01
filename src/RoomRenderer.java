import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class used to render the individual rooms
 */
public class RoomRenderer {
    /**
     * TiledMapLoader instance
     */
    private TiledMapLoader mapLoader;
    /**
     * Map used to store the individual rooms' tile sets
     */
    private Map<Integer, TileSet> tileSets = new HashMap<>();
    /**
     * List used to store the collision rectangles
     */
    private List<Rectangle2D> collisionRects = new ArrayList<>();
    /**
     * Room instance
     */
    private Room gameRoom;

    /**
     * Method used to load the room
     * @param tmjData used to access the tmj data
     * @throws Exception if the tmj data is incorrect/invalid
     */
    public void loadRoom(JSONObject tmjData) throws Exception {
        mapLoader = new TiledMapLoader();
        mapLoader.loadMap(tmjData);
        loadTilesets();
        loadCollisionObjects();

    }

    /**
     * Setter for 'gameRoom'
     * @param gameRoom what to set the value of 'gameRoom' to
     */
    public void setGameRoom(Room gameRoom) {
        this.gameRoom = gameRoom;
    }

    /**
     * Method used to load the individual collision objects
     */
    private void loadCollisionObjects() {
        collisionRects.clear();
        JSONObject collisionLayer = mapLoader.getObjectGroup("Collisions");
        if (collisionLayer != null) {
            loadCollisionsFromLayer(collisionLayer, collisionRects);
        }

    }

    /**
     * Method used to load the collision from the collision layer
     * @param layer used to access the Collisions layer
     * @param targetList used to store the coordinated of the collision objects
     */
    private void loadCollisionsFromLayer(JSONObject layer, List<Rectangle2D> targetList) {
        double layerOffsetX = layer.optDouble("offsetx", 0);
        double layerOffsetY = layer.optDouble("offsety", 0);
        JSONArray objects = layer.getJSONArray("objects");
        for (int i = 0; i < objects.length(); i++) {
            JSONObject obj = objects.getJSONObject(i);
            if (obj.optString("type", "").equals("Collisions") ||
                    obj.optString("name", "").equals("Collision")) {
                double x = obj.getDouble("x") + layerOffsetX;
                double y = obj.getDouble("y") + layerOffsetY;
                double width = obj.getDouble("width");
                double height = obj.getDouble("height");

                if (width < 1 || height < 1) continue;
                targetList.add(new Rectangle2D(x, y, width, height));
            }
        }
    }

    /**
     * Method used to load the individual tile sets from the room files
     * @throws Exception incorrect file path, incorrect tmj data, etc.
     */
    private void loadTilesets() throws Exception {
        JSONArray tilesets = mapLoader.getMapData().getJSONArray("tilesets");
        for (int i = 0; i < tilesets.length(); i++) {
            JSONObject tileset = tilesets.getJSONObject(i);
            String imagePath = tileset.getString("image");
            imagePath = imagePath.replace("..\\", "").replace("../", "");
            String filename = imagePath.substring(imagePath.lastIndexOf("/") + 1);
            String resourcePath = "/tilesets/" + filename;
            try (InputStream is = getClass().getResourceAsStream(resourcePath)) {
                if (is == null) {
                    System.err.println("Could not load tileset: " + resourcePath);
                    continue;
                }
                Image image = new Image(is);
                int firstGid = tileset.getInt("firstgid");
                int tileWidth = tileset.getInt("tilewidth");
                int tileHeight = tileset.getInt("tileheight");
                int columns = tileset.getInt("columns");

                tileSets.put(firstGid, new TileSet(image, firstGid, tileWidth, tileHeight, columns));
            }
        }
    }

    /**
     * Getter for ObjectGroup
     * @param name name of the object group
     * @return the object group
     */
    public JSONObject getObjectGroup(String name) {
        return mapLoader.getObjectGroup(name);
    }

    /**
     * Method used to render all the layers from all the tile maps
     * @param gc GraphicsContext instance
     */
    public void render(GraphicsContext gc) {
        gc.setImageSmoothing(false);
        renderLayer(gc, "Floor");
        renderLayer(gc, "Floor2");
        renderLayer(gc, "Carpets");
        renderLayer(gc, "Walls");
        renderLayer(gc, "Walls2");
        renderLayer(gc, "Repair_Tool item");
        renderLayer(gc, "WallSafe");
        renderLayer(gc, "FUSEBOX");
        renderLayer(gc, "GEARLOCKDOOR");
        renderLayer(gc, "Furniture2A");
        renderLayer(gc, "Furniture");
        renderLayer(gc, "Furniture2");
        renderLayer(gc, "Furniture3");
        renderLayer(gc, "Furniture4");
        renderLayer(gc, "Stairs2");
        renderLayer(gc, "Stairs");
        renderLayer(gc, "Tree");
        renderItems(gc);


    }

    /**
     * Method used to render the individual layers from the maps
     * @param gc GraphicsContext instance
     * @param layerName name of the layer
     */
    private void renderLayer(GraphicsContext gc, String layerName) {
        int[] layerData = mapLoader.getLayerData(layerName);
        if (layerData == null) return;
        int width = mapLoader.getWidth();
        int height = mapLoader.getHeight();
        int tileWidth = mapLoader.getTileWidth();
        int tileHeight = mapLoader.getTileHeight();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int tileId = layerData[y * width + x];
                if (tileId != 0) {
                    renderTile(gc, tileId, x * tileWidth, y * tileHeight);
                }
            }
        }
    }

    /**
     * Method used to render the individual tiles from the maps
     * @param gc GraphicsContext instance
     * @param tileId id of the tile
     * @param x x coordinate of the tile
     * @param y y coordinate of the tile
     */
    private void renderTile(GraphicsContext gc, int tileId, int x, int y) {
        for (Map.Entry<Integer, TileSet> entry : tileSets.entrySet()) {
            int firstGid = entry.getKey();
            if (tileId >= firstGid) {
                TileSet tileset = entry.getValue();
                if (tileset.containsTile(tileId)) {
                    tileset.drawTile(gc, tileId, x, y);
                    break;
                }
            }
        }
    }

    /**
     * Method used to render individual items in the room
     * @param gc GraphicsContext instance
     */
    private void renderItems(GraphicsContext gc) {
        if (gameRoom == null) return;
        // Track positions to avoid overlap
        Map<Point2D, Integer> positionCounts = new HashMap<>();
        for (Item item : gameRoom.getItems()) {
            Point2D pos = gameRoom.getItemPosition(item);
            if (pos != null) {
                // Calculate position offset
                int count = positionCounts.getOrDefault(pos, 0);
                double offsetX = count * 5; // 5 pixels offset per item
                double offsetY = count * 5;
                Image image = loadItemImage(item);
                if (image != null) {
                    double x = pos.getX() * getTileWidth() + 8 + offsetX;
                    double y = pos.getY() * getTileHeight() + 8 + offsetY;
                    double size = Math.min(getTileWidth(), getTileHeight()) * 0.5;
                    gc.drawImage(image, x, y, size, size);
                }
                positionCounts.put(pos, count + 1);
            }
        }
    }

    /**
     * Method used to load the individual items' images
     * @param item which item's image to load
     * @return the image of the selected item
     */
    private Image loadItemImage(Item item) {
        String path = "/sprites/items/" + item.getName().toUpperCase().replace(" ", "_") + ".png";
        try (InputStream is = getClass().getResourceAsStream(path)) {
            return is != null ? new Image(is) : null;
        } catch (Exception e) {
            System.err.println("Error loading item image: " + e.getMessage());
            return null;
        }
    }

    /**
     * Getter for width of the room in pixels
     * @return width of the room in pixels
     */
    public int getWidthInPixels() {
        return mapLoader.getWidth() * mapLoader.getTileWidth();
    }
    /**
     * Getter for height of the room in pixels
     * @return height of the room in pixels
     */
    public int getHeightInPixels() {
        return mapLoader.getHeight() * mapLoader.getTileHeight();
    }

    /**
     * Getter for width of the tile
     * @return width of the tile
     */
    public int getTileWidth() {
        return mapLoader.getTileWidth();
    }
    /**
     * Getter for height of the tile
     * @return height of the tile
     */
    public int getTileHeight() {
        return mapLoader.getTileHeight();
    }

    /**
     * Method used to get all the collisions in the room
     * @return list containing all the collisions
     */
    public List<Rectangle2D> getCollisions() {
        List<Rectangle2D> allCollisions = new ArrayList<>(collisionRects);
        return allCollisions;
    }

}