import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Class used to manage the individual rooms
 */
public class RoomManager {
    /**
     * Map used to store the rooms
     */
    private Map<String, RoomRenderer> rooms = new HashMap<>();
    /**
     * Current room of the room renderer
     */
    private RoomRenderer currentRoom;
    /**
     * Player instance
     */
    private Player player;
    /**
     * Name of the current room
     */
    private String currentRoomName;
    /**
     * Runnable used to set what happens when the room changes
     */
    private Runnable onRoomChanged;
    /**
     * Time since last room transition
     */
    private long lastTransitionTime = 0;
    /**
     * Cooldown between individual transitions
     */
    private static final long TRANSITION_COOLDOWN = 500; // milliseconds
    /**
     * Is there a prompt showing currently?
     */
    private boolean isShowingPrompt = false;
    /**
     * Render scale of the room
     */
    private double renderScale = 1.0;
    /**
     * Prompts
     */
    private Prompt prompt = new Prompt();
    /**
     * Was the player in prompt area?
     */
    private boolean wasInPromptArea = false;
    /**
     * GameGUI instance
     */
    private GameGUI gameGUI;

    /**
     * Getter for 'isShowingPrompt'
     * @return value of 'isShowingPrompt'
     */
    public boolean isShowingPrompt() {
        return isShowingPrompt;
    }

    /**
     * Setter for 'renderScale'
     * @param scale what to set the value of 'renderScale' to
     */
    public void setRenderScale(double scale) {
        this.renderScale = scale;
    }

    /**
     * Getter for 'renderScale'
     * @return value of 'renderScale'
     */
    public double getRenderScale() {
        return renderScale;
    }

    /**
     * Constructor
     * @param player player instance
     * @param gameGUI gameGUI instance
     */
    public RoomManager(Player player, GameGUI gameGUI) {
        this.player = player;
        this.gameGUI = gameGUI;

    }

    /**
     * Method used to create/Get the room upon a transition
     * @param roomName which room to create
     * @return the room which is supposed to be created/obtained
     */
    private Room getOrCreateGameRoom(String roomName) {
        Room gameRoom = gameGUI.getWorld().getRoomByName(roomName);
        if (gameRoom == null) {
            gameRoom = new Room(roomName, false);
            gameGUI.getWorld().addRoom(gameRoom);
        }
        return gameRoom;
    }

    /**
     * Method used to load the room
     * @param roomName name of the room which is supposed to be loaded
     * @throws Exception when the room doesn't exist/the file path is incorrect,etc.
     */
    public void loadRoom(String roomName) throws Exception {
        if (!rooms.containsKey(roomName)) {
            JSONObject tmjData = loadTMJData("/maps/" + roomName + ".tmj");
            RoomRenderer renderer = new RoomRenderer();
            renderer.loadRoom(tmjData);
            rooms.put(roomName, renderer);
            // Get or create the game room
            Room gameRoom = getOrCreateGameRoom(roomName);
            renderer.setGameRoom(gameRoom);
            gameGUI.getWorld().loadSearchSpotsFromTMJ(gameRoom, tmjData);
            loadCassettePlayers(gameRoom, tmjData);
            // Set current room if this is the first room
            if (currentRoom == null) {
                currentRoom = renderer;
                currentRoomName = roomName;
                gameGUI.getWorld().setCurrentRoom(gameRoom);
                positionPlayerAtSpawn();
            }
        }
    }

    /**
     * Method used to load the .tmj date from the file
     * @param path path of the file
     * @return json object from the file
     * @throws Exception file path doesn't exist/is empty
     */
    private JSONObject loadTMJData(String path) throws Exception {
        try (InputStream is = getClass().getResourceAsStream(path)) {
            assert is != null;
            return new JSONObject(new String(is.readAllBytes()));
        }
    }

    /**
     * Method used to 'normalize' the room names
     * @param roomName which room name to normalize
     * @return the normalized room name
     */
    private String normalizeRoomName(String roomName) {
        // Convert to lowercase and replace spaces with underscores
        return roomName.trim()
                .toLowerCase()
                .replace(" ", "_");
    }

    /**
     * Method used to position the player at the SPAWNPOINT object
     */
    private void positionPlayerAtSpawn() {
        JSONObject objects = currentRoom.getObjectGroup("GameObjects");
        if (objects != null) {
            JSONArray objectArray = objects.getJSONArray("objects");
            for (int i = 0; i < objectArray.length(); i++) {
                JSONObject obj = objectArray.getJSONObject(i);
                if (obj.has("name") && obj.getString("name").equals("SPAWNPOINT")) {
                    player.setPosition(
                            obj.getDouble("x") / currentRoom.getTileWidth(),
                            obj.getDouble("y") / currentRoom.getTileHeight()
                    );
                    player.setCurrentRoom(currentRoomName);
                    break;
                }
            }
        }
    }

    /**
     * Method used to transition from room to room
     * @param roomName name of the room to transition to
     * @param useSavedPosition should the saved position be used or not?
     * @param savedX the x coordinate saved
     * @param savedY the y coordinate saved
     */
    public void transitionToRoom(String roomName, boolean useSavedPosition, double savedX, double savedY) {
        player.setMovementEnabled(false);
        player.setTransitioning(true);
        try {
            Platform.runLater(() -> {
                player.setSpeed(0, 0);
                player.setTransitioning(true);
            });
            if (currentRoom == null) {
                // Handle initial room setup
                loadRoom(roomName.toLowerCase());
                positionPlayerAtSpawn();
                return;
            }
            String normalizedName = normalizeRoomName(roomName);
            if (!rooms.containsKey(normalizedName)) {
                loadRoom(normalizedName);
            }
            if (rooms.containsKey(normalizedName)) {
                String previousRoomName = currentRoomName;
                currentRoom = rooms.get(normalizedName);
                currentRoomName = normalizedName;
                Room gameRoom = gameGUI.getWorld().getRoomByName(normalizedName);
                gameGUI.getWorld().setCurrentRoom(gameRoom);
                if (useSavedPosition) {
                    player.setPosition(savedX, savedY);
                } else {
                    positionPlayerAtEntrance(previousRoomName);
                }
                if (onRoomChanged != null) {
                    onRoomChanged.run();
                }
                // Adds a small delay before re-enabling controls
                new Thread(() -> {
                    try {
                        Thread.sleep(100);
                        Platform.runLater(() -> {
                            player.setTransitioning(false);
                            player.setMovementEnabled(true);
                        });
                    } catch (InterruptedException e) { /* ... */ }
                }).start();
            }
        } catch (Exception e) {
            System.err.println("Transition failed: " + e.getMessage());
            Platform.runLater(() -> {
                player.setTransitioning(false);
            });
        }
    }

    /**
     * Method used to position the player at an entrance(ENTER FROM [Name of the previous room]) object of the room transitioned into
     * @param previousRoom room the player had transitioned into
     */
    private void positionPlayerAtEntrance(String previousRoom) {
        JSONObject objects = currentRoom.getObjectGroup("GameObjects");
        if (objects != null) {
            JSONArray objectArray = objects.getJSONArray("objects");
            String entranceName = "ENTER FROM " + previousRoom.toUpperCase().replace("_", " ");
            for (int i = 0; i < objectArray.length(); i++) {
                JSONObject obj = objectArray.getJSONObject(i);
                if (obj.getString("name").equalsIgnoreCase(entranceName)) {
                    double entranceX = obj.getDouble("x");
                    double entranceY = obj.getDouble("y");
                    double entranceWidth = obj.getDouble("width");
                    double entranceHeight = obj.getDouble("height");
                    double tileWidth = currentRoom.getTileWidth();
                    double tileHeight = currentRoom.getTileHeight();
                    // Calculate entrance center in tile units
                    double centerX = (entranceX + entranceWidth / 2) / tileWidth;
                    double centerY = (entranceY + entranceHeight / 2) / tileHeight;
                    // Determine direction based on entrance dimensions
                    if (entranceWidth > entranceHeight) { // Horizontal entrance (top/bottom)
                        if (entranceY < currentRoom.getHeightInPixels() / 2) {
                            // Top entrance: place player below
                            centerY += (entranceHeight / tileHeight) / 2 + 0.2;
                        } else {
                            // Bottom entrance: place player above
                            centerY -= (entranceHeight / tileHeight) / 2 + 0.2;
                        }
                    } else { // Vertical entrance (left/right)
                        if (entranceX < currentRoom.getWidthInPixels() / 2) {
                            // Left entrance: place player to the right
                            centerX += (entranceWidth / tileWidth) / 2 + 0.2;
                        } else {
                            // Right entrance: place player to the left
                            centerX -= (entranceWidth / tileWidth) / 2 + 0.2;
                        }
                    }
                    int attempts = 0;
                    while (checkCollision(centerX, centerY) && attempts < 5) {
                        centerX += 0.2;
                        centerY += 0.2;
                        attempts++;
                    }
                    player.setPosition(centerX, centerY);
                    return;
                }
            }
        }
    }

    /**
     * Method used to check for any collision in a certain tile
     * @param tileX x coordinate of the tile
     * @param tileY y coordinate of the tile
     * @return is there a collision in these coordinates?
     */
    private boolean checkCollision(double tileX, double tileY) {
        RoomRenderer room = getCurrentRoom();
        if (room == null) return false;
        double playerX = tileX * room.getTileWidth();
        double playerY = tileY * room.getTileHeight();
        Rectangle2D hitbox = new Rectangle2D(
                playerX - 10, playerY - 10, 20, 20);
        return room.getCollisions().stream()
                .anyMatch(rect -> rect.intersects(hitbox));
    }

    /**
     * Method used to determine whether the player is located in an exit(EXIT TO [Name of the next room]) object
     * @param exit which EXIT TO object are we checking for
     * @return is the player in an exit area?
     */
    private boolean isPlayerInExit(JSONObject exit) {
        double exitX = exit.getDouble("x");
        double exitY = exit.getDouble("y");
        double exitWidth = exit.getDouble("width");
        double exitHeight = exit.getDouble("height");
        // Convert player position to pixels
        double playerX = player.getX() * currentRoom.getTileWidth();
        double playerY = player.getY() * currentRoom.getTileHeight();
        double playerSize = 20; // Player circle
        // Calculate player bounds
        double playerLeft = playerX - playerSize / 2;
        double playerRight = playerX + playerSize / 2;
        double playerTop = playerY - playerSize / 2;
        double playerBottom = playerY + playerSize / 2;
        double exitLeft = exitX;
        double exitRight = exitX + exitWidth;
        double exitTop = exitY;
        double exitBottom = exitY + exitHeight;
        boolean xOverlap = playerRight > exitLeft && playerLeft < exitRight;
        boolean yOverlap = playerBottom > exitTop && playerTop < exitBottom;
        if (exitWidth < 20 || exitHeight < 20) {
            double expandedWidth = Math.max(20, exitWidth);
            double expandedHeight = Math.max(20, exitHeight);
            double centerX = exitX + exitWidth / 2;
            double centerY = exitY + exitHeight / 2;
            return Math.abs(playerX - centerX) < expandedWidth / 2 &&
                    Math.abs(playerY - centerY) < expandedHeight / 2;
        }
        return xOverlap && yOverlap;
    }

    /**
     * Method used to check for all transition types
     */
    public void checkAllTransitions() {
        if (System.currentTimeMillis() - lastTransitionTime < TRANSITION_COOLDOWN) {
            return;
        }
        checkPromptTransitions();
        checkRegularExits();
    }

    /**
     * Method used to check if the player is located in one of the EXIT TO[Name of the next room] objects
     * @return is the player located in one of the EXIT TO[Name of the next room] objects?
     */
    private boolean checkRegularExits() {
        JSONObject exits = currentRoom.getObjectGroup("GameObjects");
        if (exits != null) {
            JSONArray objects = exits.getJSONArray("objects");
            for (int i = 0; i < objects.length(); i++) {
                JSONObject obj = objects.getJSONObject(i);
                if (obj.has("name") && obj.getString("name").startsWith("EXIT TO")) {
                    if (isPlayerInExit(obj)) {
                        String targetRoom = obj.getString("name").substring(7).trim()
                                .toUpperCase().replace(" ", "_");
                        gameGUI.performTransition(targetRoom);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Method used to check whether a prompt transition is available
     */
    public void checkPromptTransitions() {
        boolean currentlyInPromptArea = false;

        JSONObject prompts = currentRoom.getObjectGroup("RoomTransitionPrompts");
        if (prompts == null) {
            prompts = currentRoom.getObjectGroup("RoomTransitionPrompt");
        }

        if (prompts != null) {
            JSONArray objects = prompts.getJSONArray("objects");
            for (int i = 0; i < objects.length(); i++) {
                JSONObject obj = objects.getJSONObject(i);
                if (isPlayerInExit(obj)) {
                    currentlyInPromptArea = true;
                    if (!prompt.isActive()) {
                        String targetRoom = obj.getString("name").substring(7).trim()
                                .toUpperCase().replace(" ", "_");
                        showTransitionPrompt(targetRoom);
                    }
                    break; // Only show one prompt at a time
                }
            }
        }

        // Only hide if we left a prompt area after being in one
        if (wasInPromptArea && !currentlyInPromptArea) {
            prompt.hide();
        }
        wasInPromptArea = currentlyInPromptArea;
    }

    /**
     * Method used to render the transition prompts
     * @param gc GraphicsContext instance
     * @param screenWidth width of the screen
     * @param screenHeight height of the screen
     */
    public void renderPrompt(GraphicsContext gc, double screenWidth, double screenHeight) {
        prompt.render(gc, screenWidth, screenHeight);
    }

    /**
     * Method used to try and confirm the transition prompts
     * @return has the transition occurred successfully?
     */
    public boolean tryConfirmPrompt() {
        if (prompt.isActive()) {
            gameGUI.performTransition(prompt.getTargetRoom());
            prompt.hide();
            return true;
        }
        return false;
    }

    /**
     * Method used to load the CassettePlayer objects
     * @param gameRoom which rooms to add the objects into
     * @param tmjData used to access the tmj date
     */
    private void loadCassettePlayers(Room gameRoom, JSONObject tmjData) {
        if (tmjData.has("layers")) {
            JSONArray layers = tmjData.getJSONArray("layers");
            for (int i = 0; i < layers.length(); i++) {
                JSONObject layer = layers.getJSONObject(i);
                if ("CassettePlayer".equals(layer.optString("name", ""))) {
                    if (layer.has("objects")) {
                        JSONArray objects = layer.getJSONArray("objects");
                        for (int j = 0; j < objects.length(); j++) {
                            JSONObject obj = objects.getJSONObject(j);
                            gameRoom.addCassettePlayerPosition(
                                    obj.getDouble("x"),
                                    obj.getDouble("y")
                            );
                        }
                    }
                    break;
                }
            }
        }
    }

    /**
     * Method used to show the transition prompt
     * @param targetRoom which room would the transition occur into
     */
    private void showTransitionPrompt(String targetRoom) {
        prompt.show("Enter " + targetRoom.replace("_", " "), targetRoom);
    }

    /**
     * Setter for 'currentRoom'
     * @param currentRoom what to set the value of 'currentRoom' to
     */
    public void setCurrentRoom(RoomRenderer currentRoom) {
        this.currentRoom = currentRoom;
    }

    /**
     * Getter for 'currentRoom'
     * @return value of 'currentRoom'
     */
    public RoomRenderer getCurrentRoom() {
        return currentRoom;
    }

    /**
     * Setter for 'onRoomChanged'
     * @param callback what to set the value of 'onRoomChanged' to
     */
    public void setOnRoomChanged(Runnable callback) {
        this.onRoomChanged = callback;
    }

    /**
     * Getter for 'currentRoomName'
     * @return value of 'currentRoomName'
     */
    public String getCurrentRoomName() {
        return currentRoomName;
    }
}