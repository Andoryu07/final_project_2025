import org.json.JSONArray;
import org.json.JSONObject;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class TiledMapLoader {
    private JSONObject mapData;
    private int tileWidth;
    private int tileHeight;

    public void loadMap(String resourcePath) throws Exception {
        // Ensure path starts with slash and is case-sensitive
        if (!resourcePath.startsWith("/")) {
            resourcePath = "/" + resourcePath;
        }

        try (InputStream is = getClass().getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new RuntimeException("Could not find resource: " + resourcePath);
            }
            String jsonContent = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            mapData = new JSONObject(jsonContent);
            tileWidth = mapData.getInt("tilewidth");
            tileHeight = mapData.getInt("tileheight");
        }
    }

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

    public JSONObject getMapData() {
        return mapData;
    }

    public int getWidth() { return mapData.getInt("width"); }
    public int getHeight() { return mapData.getInt("height"); }
    public int getTileWidth() { return tileWidth; }
    public int getTileHeight() { return tileHeight; }
}