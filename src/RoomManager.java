import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RoomManager {
    private Map<String, RoomRenderer> rooms = new HashMap<>();
    private RoomRenderer currentRoom;
    private Player player;
    private String currentRoomName;
    private Runnable onRoomChanged;
    private long lastTransitionTime = 0;
    private static final long TRANSITION_COOLDOWN = 500; // milliseconds
    private boolean isShowingPrompt = false;
    private double renderScale = 1.0;
    private Prompt prompt = new Prompt();
    private boolean wasInPromptArea = false;
    private GameGUI gameGUI;

    public boolean isShowingPrompt() {
        return isShowingPrompt;
    }

    public void setRenderScale(double scale) {
        this.renderScale = scale;
    }

    public double getRenderScale() {
        return renderScale;
    }

    public RoomManager(Player player, GameGUI gameGUI) {
        this.player = player;
        this.gameGUI = gameGUI;

    }

    public void loadRoom(String roomName) throws Exception {
        if (!rooms.containsKey(roomName)) {
            RoomRenderer room = new RoomRenderer();
            room.loadRoom("/maps/" + roomName + ".tmj");
            rooms.put(roomName, room);
        }

        // Set as current room if this is the first room being loaded
        if (currentRoom == null) {
            currentRoom = rooms.get(roomName);
            currentRoomName = roomName;
            positionPlayerAtSpawn();
        }
    }

    private String normalizeRoomName(String roomName) {
        // Convert to lowercase and replace spaces with underscores
        return roomName.trim()
                .toLowerCase()
                .replace(" ", "_");
    }

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

    public void transitionToRoom(String roomName) {
        player.setMovementEnabled(false); // Add this
        player.setTransitioning(true);
        try {
            Platform.runLater(() -> {
                player.setSpeed(0, 0);
                player.setTransitioning(true);

            });

            String normalizedName = normalizeRoomName(roomName);
            if (!rooms.containsKey(normalizedName)) {
                loadRoom(normalizedName);
            }

            if (rooms.containsKey(normalizedName)) {
                String previousRoomName = currentRoomName;
                currentRoom = rooms.get(normalizedName);
                currentRoomName = normalizedName;

                positionPlayerAtEntrance(previousRoomName);

                if (onRoomChanged != null) {
                    onRoomChanged.run();
                }

                // Add a small delay before re-enabling controls
                new Thread(() -> {
                    try {
                        Thread.sleep(100);
                        Platform.runLater(() -> {
                            player.setTransitioning(false);
                            player.setMovementEnabled(true); // Re-enable
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

                    // Ensure valid position
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

    private boolean isPlayerInExit(JSONObject exit) {
        double exitX = exit.getDouble("x");
        double exitY = exit.getDouble("y");
        double exitWidth = exit.getDouble("width");
        double exitHeight = exit.getDouble("height");

        // Convert player position to pixels
        double playerX = player.getX() * currentRoom.getTileWidth();
        double playerY = player.getY() * currentRoom.getTileHeight();
        double playerSize = 20; // Player circle diameter

        // Calculate player bounds
        double playerLeft = playerX - playerSize / 2;
        double playerRight = playerX + playerSize / 2;
        double playerTop = playerY - playerSize / 2;
        double playerBottom = playerY + playerSize / 2;

        // Calculate exit bounds
        double exitLeft = exitX;
        double exitRight = exitX + exitWidth;
        double exitTop = exitY;
        double exitBottom = exitY + exitHeight;

        // Check for overlap with more generous bounds for small exits
        boolean xOverlap = playerRight > exitLeft && playerLeft < exitRight;
        boolean yOverlap = playerBottom > exitTop && playerTop < exitBottom;

        // For very small exits (like in caravan), make the check more generous
        if (exitWidth < 20 || exitHeight < 20) {
            double expandedWidth = Math.max(20, exitWidth);
            double expandedHeight = Math.max(20, exitHeight);
            double centerX = exitX + exitWidth/2;
            double centerY = exitY + exitHeight/2;

            return Math.abs(playerX - centerX) < expandedWidth/2 &&
                    Math.abs(playerY - centerY) < expandedHeight/2;
        }

        return xOverlap && yOverlap;
    }

    public void checkAllTransitions() {
        if (System.currentTimeMillis() - lastTransitionTime < TRANSITION_COOLDOWN) {
            return;
        }
        checkPromptTransitions();
        checkRegularExits();
    }

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

    public void checkPromptTransitions() {
        boolean currentlyInPromptArea = false;

        JSONObject prompts = currentRoom.getObjectGroup("RoomTransitionPrompts");
        if (prompts == null) {
            prompts = currentRoom.getObjectGroup("RoomTransitionPrompt"); // Try alternative name
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


    public void renderPrompt(GraphicsContext gc, double screenWidth, double screenHeight) {
        prompt.render(gc, screenWidth, screenHeight);
    }

    public boolean tryConfirmPrompt() {
        if (prompt.isActive()) {
            gameGUI.performTransition(prompt.getTargetRoom());
            prompt.hide();
            return true;
        }
        return false;
    }

    private void showTransitionPrompt(String targetRoom) {
        prompt.show("Enter " + targetRoom.replace("_", " "), targetRoom);
    }

    public RoomRenderer getCurrentRoom() {
        return currentRoom;
    }

    public void setOnRoomChanged(Runnable callback) {
        this.onRoomChanged = callback;
    }

    public String getCurrentRoomName() {
        return currentRoomName;
    }
}