import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HidingSpotManager {
    private Player player;
    private RoomManager roomManager;
    private boolean isHiding = false;
    private double originalX, originalY;
    private long lastHideTime = 0;
    private static final long HIDE_COOLDOWN = 1000; // 1 second cooldown
    private List<Rectangle2D> hidingSpots = new ArrayList<>();
    private String currentHidingSpotName = "";
    private boolean showHidePrompt = false;
    private boolean showExitPrompt = false;

    public HidingSpotManager(Player player, RoomManager roomManager) {
        this.player = player;
        this.roomManager = roomManager;
    }

    public void loadHidingSpots() {
        hidingSpots.clear();
        JSONObject hidingLayer = roomManager.getCurrentRoom().getObjectGroup("HidingSpotPrompt");
        if (hidingLayer != null) {
            JSONArray objects = hidingLayer.getJSONArray("objects");
            for (int i = 0; i < objects.length(); i++) {
                JSONObject obj = objects.getJSONObject(i);
                double x = obj.getDouble("x");
                double y = obj.getDouble("y");
                double width = obj.getDouble("width");
                double height = obj.getDouble("height");
                hidingSpots.add(new Rectangle2D(x, y, width, height));
            }
        }
    }

    public void update() {
        if (isHiding) {
            // Player is already hiding - check for exit
            if (System.currentTimeMillis() - lastHideTime > HIDE_COOLDOWN) {
                showExitPrompt = true;
                showHidePrompt = false;
            }
            return;
        }

        // Check if player is near a hiding spot
        showHidePrompt = false;
        RoomRenderer room = roomManager.getCurrentRoom();
        if (room == null) return;

        double playerX = player.getX() * room.getTileWidth();
        double playerY = player.getY() * room.getTileHeight();
        Rectangle2D playerBounds = new Rectangle2D(playerX - 10, playerY - 10, 20, 20);

        for (Rectangle2D spot : hidingSpots) {
            if (spot.intersects(playerBounds)) {
                JSONObject hidingLayer = roomManager.getCurrentRoom().getObjectGroup("HidingSpotPrompt");
                if (hidingLayer != null) {
                    JSONArray objects = hidingLayer.getJSONArray("objects");
                    for (int i = 0; i < objects.length(); i++) {
                        JSONObject obj = objects.getJSONObject(i);
                        if (obj.getDouble("x") == spot.getMinX() && obj.getDouble("y") == spot.getMinY()) {
                            currentHidingSpotName = obj.getString("name");
                            showHidePrompt = true;
                            break;
                        }
                    }
                }
                break;
            }
        }
    }

    public boolean tryHide() {
        if (isHiding) {
            // Exit hiding spot
            if (System.currentTimeMillis() - lastHideTime > HIDE_COOLDOWN) {
                player.setPosition(originalX, originalY);
                player.setMovementEnabled(true);
                isHiding = false;
                showExitPrompt = false;
                lastHideTime = System.currentTimeMillis();
                return true;
            }
            return false;
        }

        // Enter hiding spot
        if (showHidePrompt) {
            originalX = player.getX();
            originalY = player.getY();

            // Position player at the center of the hiding spot
            RoomRenderer room = roomManager.getCurrentRoom();
            for (Rectangle2D spot : hidingSpots) {
                double playerX = player.getX() * room.getTileWidth();
                double playerY = player.getY() * room.getTileHeight();
                Rectangle2D playerBounds = new Rectangle2D(playerX - 10, playerY - 10, 20, 20);

                if (spot.intersects(playerBounds)) {
                    double centerX = (spot.getMinX() + spot.getWidth() / 2) / room.getTileWidth();
                    double centerY = (spot.getMinY() + spot.getHeight() / 2) / room.getTileHeight();
                    player.setPosition(centerX, centerY);
                    break;
                }
            }

            player.setMovementEnabled(false);
            isHiding = true;
            showHidePrompt = false;
            lastHideTime = System.currentTimeMillis();
            return true;
        }
        return false;
    }

    public void render(GraphicsContext gc, double screenWidth, double screenHeight) {
        if (isHiding) {
            return;
        }
        if (!showHidePrompt && !showExitPrompt) return;

        double width = 300;
        double height = 60;
        double x = (screenWidth - width) / 2;
        double y = screenHeight - height - 20;

        gc.save();
        gc.setTransform(1, 0, 0, 1, 0, 0);

        // Draw background
        gc.setFill(Color.rgb(0, 0, 0, 0.7));
        gc.fillRoundRect(x, y, width, height, 10, 10);

        // Draw border
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(2);
        gc.strokeRoundRect(x, y, width, height, 10, 10);

        // Draw text
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", 16));

        if (showHidePrompt) {
            // Draw key prompt (left-aligned)
            gc.setTextAlign(TextAlignment.LEFT);
            gc.fillText("[F]", x + 20, y + height / 2 + 5);

            // Draw message (centered)
            gc.setTextAlign(TextAlignment.CENTER);
            gc.fillText(currentHidingSpotName, x + width / 2 + 20, y + height / 2 + 5);
        } else if (showExitPrompt) {
            // Draw exit prompt
            gc.setTextAlign(TextAlignment.LEFT);
            gc.fillText("[F]", x + 20, y + height / 2 + 5);

            gc.setTextAlign(TextAlignment.CENTER);
            gc.fillText("Exit hiding spot", x + width / 2 + 20, y + height / 2 + 5);
        }

        gc.restore();
    }

    public boolean isHiding() {
        return isHiding;
    }
}