import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Class used to load the tiled maps
 */
public class TiledMapLoader {
    /**
     * map data from the tmj files
     */
    private JSONObject mapData;
    /**
     * width of a tile
     */
    private int tileWidth;
    /**
     * height of a tile
     */
    private int tileHeight;

    /**
     * Method used to load the map
     * @param mapData which data to use
     * @throws Exception incorrect json data
     */
    public void loadMap(JSONObject mapData) throws Exception {
        this.mapData = mapData;
        tileWidth = mapData.getInt("tilewidth");
        tileHeight = mapData.getInt("tileheight");
    }

    /**
     * Getter to access the object group
     * @param name name
     * @return the object layer
     */
    public JSONObject getObjectGroup(String name) {
        JSONArray layers = mapData.getJSONArray("layers");
        for (int i = 0; i < layers.length(); i++) {
            JSONObject layer = layers.getJSONObject(i);
            if (layer.getString("type").equals("objectgroup") &&
                    layer.getString("name").equals(name)) {
                return layer;
            }
        }
        return null;
    }

    /**
     * Method used to get a layer
     * @param layerName name of the layer
     * @return the layer, if it exists
     */
    public JSONObject getLayer(String layerName) {
        JSONArray layers = mapData.getJSONArray("layers");
        for (int i = 0; i < layers.length(); i++) {
            JSONObject layer = layers.getJSONObject(i);
            if (layer.getString("name").equals(layerName)) {
                return layer;
            }
        }
        return null;
    }

    /**
     * Method used to get the layer data
     * @param layerName name of the layer to access
     * @return the data from the layer
     */
    public int[] getLayerData(String layerName) {
        JSONObject layer = getLayer(layerName);
        if (layer != null) {
            JSONArray data = layer.getJSONArray("data");
            int[] result = new int[data.length()];
            for (int i = 0; i < data.length(); i++) {
                result[i] = data.getInt(i);
            }
            return result;
        }
        return null;
    }

    /**
     * Getter for 'mapData'
     * @return value of 'mapData'
     */
    public JSONObject getMapData() {
        return mapData;
    }

    /**
     * Getter for width
     * @return value of width
     */
    public int getWidth() { return mapData.getInt("width"); }

    /**
     * Getter for height
     * @return height
     */
    public int getHeight() { return mapData.getInt("height"); }

    /**
     * Getter for 'tileWidth'
     * @return value of 'tileWidth'
     */
    public int getTileWidth() { return tileWidth; }

    /**
     * Getter for 'tileHeight'
     * @return value of 'tileHeight'
     */
    public int getTileHeight() { return tileHeight; }
}