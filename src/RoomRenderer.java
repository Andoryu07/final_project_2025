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

public class RoomRenderer {
    private TiledMapLoader mapLoader;
    private Map<Integer, TileSet> tileSets = new HashMap<>();
    private List<Rectangle2D> collisionRects = new ArrayList<>();
    private List<Rectangle2D> gardenLockCollisions = new ArrayList<>();
    private List<Rectangle2D> doorLockCollisions = new ArrayList<>();
    private boolean gardenLockActive = true;
    private boolean doorLockActive = true;
    public void loadRoom(String mapPath) throws Exception {
        mapLoader = new TiledMapLoader();
        mapLoader.loadMap(mapPath);
        loadTilesets();
        loadCollisionObjects();
        System.out.println("Loaded " + collisionRects.size() + " collision objects");
    }

    private void loadCollisionObjects() {
        collisionRects.clear();
        gardenLockCollisions.clear();
        doorLockCollisions.clear();

        // Regular collisions
        JSONObject collisionLayer = mapLoader.getObjectGroup("Collisions");
        if (collisionLayer != null) {
            loadCollisionsFromLayer(collisionLayer, collisionRects);
        }

        // Garden lock collisions
        JSONObject gardenLockLayer = mapLoader.getObjectGroup("GARDEN_LOCK");
        if (gardenLockLayer != null) {
            loadCollisionsFromLayer(gardenLockLayer, gardenLockCollisions);
        }

        // Door lock collisions
        JSONObject doorLockLayer = mapLoader.getObjectGroup("DOOR_LOCK_COLLISION");
        if (doorLockLayer != null) {
            loadCollisionsFromLayer(doorLockLayer, doorLockCollisions);
        }
    }
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

    private void loadTilesets() throws Exception {
        JSONArray tilesets = mapLoader.getMapData().getJSONArray("tilesets");
        for (int i = 0; i < tilesets.length(); i++) {
            JSONObject tileset = tilesets.getJSONObject(i);
            String imagePath = tileset.getString("image");

            imagePath = imagePath.replace("..\\", "").replace("../", "");
            String filename = imagePath.substring(imagePath.lastIndexOf("/") + 1);
            String resourcePath = "resources/tilesets/" + filename;

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

    public JSONObject getObjectGroup(String name) {
        return mapLoader.getObjectGroup(name);
    }

    public void render(GraphicsContext gc) {
        gc.setImageSmoothing(false);
        renderLayer(gc, "Floor");
        renderLayer(gc, "Floor2");
        renderLayer(gc, "Tree");
        renderLayer(gc, "Repair_Tool item");
        renderLayer(gc, "Carpets");
        renderLayer(gc, "Walls");
        renderLayer(gc, "Walls2");
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

    }

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

    public int getWidthInPixels() {
        return mapLoader.getWidth() * mapLoader.getTileWidth();
    }

    public int getHeightInPixels() {
        return mapLoader.getHeight() * mapLoader.getTileHeight();
    }

    public int getTileWidth() {
        return mapLoader.getTileWidth();
    }

    public int getTileHeight() {
        return mapLoader.getTileHeight();
    }
    public void setGardenLockActive(boolean active) {
        this.gardenLockActive = active;
    }

    public void setDoorLockActive(boolean active) {
        this.doorLockActive = active;
    }
    public List<Rectangle2D> getCollisions() {
        List<Rectangle2D> allCollisions = new ArrayList<>(collisionRects);
        if (gardenLockActive) {
            allCollisions.addAll(gardenLockCollisions);
        }
        if (doorLockActive) {
            allCollisions.addAll(doorLockCollisions);
        }
        return allCollisions;
    }
}