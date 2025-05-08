import javafx.animation.AnimationTimer;
import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GameGUI extends Application {
    private final double MAX_WALK_SPEED = 0.008;  // Reduced from 0.02 (very slow)
    private RoomManager roomManager;
    private Player player;
    private Canvas canvas;
    private Set<KeyCode> pressedKeys = new HashSet<>();
    private Set<KeyCode> keysToRemove = new HashSet<>();
    private AnimationTimer gameLoop;
    private final int TRANSITION_DURATION = 100;
    private boolean shiftPressed = false;
    private long lastUpdateTime = System.nanoTime();
    private InventoryGUI inventoryGUI;
    private StackPane rootPane;
    private List<SearchSpot> activeSearchSpots = new ArrayList<>();
    private int selectedSpotIndex = 0;
    private World world;
    private Game game;
    @Override
    public void start(Stage primaryStage) {
        try {
            primaryStage.setMinWidth(1024);
            primaryStage.setMinHeight(768);
            canvas = new Canvas(); // Initialize canvas first
            setupStage(primaryStage);
            primaryStage.setOnShown(e -> updateCanvasSize());
            // Set initial window size
            primaryStage.setWidth(1280);
            primaryStage.setHeight(720);
            player = new Player("Player", 100, null, "Enter_Hall");
            game = new Game();
            world = new World(player,game);
            game.setWorld(world);
            inventoryGUI = new InventoryGUI(player);
            rootPane.getChildren().add(inventoryGUI); // Add to StackPane
            roomManager = new RoomManager(player, this);
            loadRooms(roomManager);
            roomManager.setOnRoomChanged(() -> {
                primaryStage.setTitle("Your Game - " + roomManager.getCurrentRoomName());
                updateCanvasSize();
                render();
            });

            startGameLoop();
            primaryStage.setTitle("Your Game - " + roomManager.getCurrentRoomName());
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to start game: " + e.getMessage());
        }
    }

    private void loadRooms(RoomManager roomManager) {
        try {
            roomManager.loadRoom("enter_hall");
            roomManager.loadRoom("library");
            roomManager.loadRoom("living_room");
            roomManager.loadRoom("garden");
            roomManager.loadRoom("dining_room");
            roomManager.loadRoom("bathroom");
            roomManager.loadRoom("secret_chamber");
            roomManager.loadRoom("main_bedroom");
            roomManager.loadRoom("laboratory");
            roomManager.loadRoom("garden_house");
            roomManager.loadRoom("cellar");
            roomManager.loadRoom("caravan");
            roomManager.loadRoom("cemetery_ending");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to start game: " + e.getMessage());
        }
    }

    private void setupStage(Stage primaryStage) {
        rootPane = new StackPane();
        rootPane.setStyle("-fx-background-color: black;");
        rootPane.getChildren().add(canvas);

        Scene scene = new Scene(rootPane, Color.BLACK);
        // Correct path if CSS is in src/main/resources/resources/
//        scene.getStylesheets().add(getClass().getResource("/inventory.css").toExternalForm());
        // In setupStage() method:
        scene.getStylesheets().add(getClass().getResource("/inventory.css").toExternalForm());
        primaryStage.setScene(scene);

        scene.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if (e.getCode() == KeyCode.I) {
                if (!inventoryGUI.isInventoryVisible()) {
                    // Pause game
                    player.setMovementEnabled(false);
                    gameLoop.stop();
                } else {
                    // Resume game
                    player.setMovementEnabled(true);
                    gameLoop.start();
                }
                inventoryGUI.toggle();
                e.consume();
            } else if (e.getCode() == KeyCode.ESCAPE && inventoryGUI.isInventoryVisible()) {
                inventoryGUI.toggle();
                player.setMovementEnabled(true);
                gameLoop.start();
                e.consume();
            }
        });
        // Enhanced input handling
        scene.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            KeyCode code = e.getCode();
            if (player.isTransitioning() || roomManager.isShowingPrompt()) {
                e.consume(); // Block input during transitions/prompts
                if (isMovementKey(code)) {
                    pressedKeys.remove(code);
                }
            } else if (isMovementKey(code)) {
                pressedKeys.add(code);
            }
        });
        scene.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            KeyCode code = e.getCode();
            if (code == KeyCode.SHIFT) {
                shiftPressed = true;
                if (player.getCurrentStamina() > 0) {
                    player.setSprinting(true);
                }
            }
        });
        scene.addEventFilter(KeyEvent.KEY_RELEASED, e -> {
            KeyCode code = e.getCode();
            if (code == KeyCode.SHIFT) {
                shiftPressed = false;
                player.setSprinting(false);
            }
        });

        scene.addEventFilter(KeyEvent.KEY_RELEASED, e -> {
            KeyCode code = e.getCode();
            pressedKeys.remove(code);
            if (isMovementKey(code)) {
                // Immediately stop movement in this direction
                if (code == KeyCode.W || code == KeyCode.UP ||
                        code == KeyCode.S || code == KeyCode.DOWN) {
                    player.setSpeed(player.getSpeedX(), 0);
                } else {
                    player.setSpeed(0, player.getSpeedY());
                }
            }
        });

        // Fullscreen toggle
        scene.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.F11) {
                primaryStage.setFullScreen(!primaryStage.isFullScreen());
                updateCanvasSize();
            }
        });
        scene.addEventFilter(KeyEvent.ANY, event -> {
            if (player.isTransitioning() || roomManager.isShowingPrompt()) {
                if (isMovementKey(event.getCode())) {
                    event.consume(); // Block all movement input
                    clearMovementInputs();
                    pressedKeys.remove(event.getCode());
                }
            }
        });
        scene.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if (e.getCode() == KeyCode.F) {
                if (roomManager.tryConfirmPrompt()) {
                    e.consume();
                }
            }
        });
        scene.setOnScroll(e -> {
            if (!activeSearchSpots.isEmpty()) {
                double delta = e.getDeltaY();
                selectedSpotIndex = (int) (selectedSpotIndex - Math.signum(delta)) % activeSearchSpots.size();
                if (selectedSpotIndex < 0) selectedSpotIndex = activeSearchSpots.size() - 1;
            }
        });

// Handle interaction (e.g., pressing 'F')
        scene.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if (e.getCode() == KeyCode.F && !activeSearchSpots.isEmpty()) {
                SearchSpot spot = activeSearchSpots.get(selectedSpotIndex);
                if (!spot.isSearched()) {
                    List<Item> items = spot.search();
                    // Add items to inventory
                    items.forEach(player.getInventory()::addItem);
                }
            }
        });
    }

    // Helper method to check if a key is a movement key
    private boolean isMovementKey(KeyCode code) {
        return code == KeyCode.W || code == KeyCode.UP ||
                code == KeyCode.S || code == KeyCode.DOWN ||
                code == KeyCode.A || code == KeyCode.LEFT ||
                code == KeyCode.D || code == KeyCode.RIGHT;
    }

    private void updateCanvasSize() {
        if (roomManager.getCurrentRoom() == null) return;

        RoomRenderer room = roomManager.getCurrentRoom();

        // Calculate scale factors
        double scaleX = canvas.getScene().getWidth() / room.getWidthInPixels();
        double scaleY = canvas.getScene().getHeight() / room.getHeightInPixels();
        double scale = Math.min(scaleX, scaleY);

        // Set canvas size to integer multiples for crisp rendering
        double renderWidth = room.getWidthInPixels() * Math.floor(scale);
        double renderHeight = room.getHeightInPixels() * Math.floor(scale);

        canvas.setWidth(renderWidth);
        canvas.setHeight(renderHeight);

        // Center the canvas
        canvas.setLayoutX((canvas.getScene().getWidth() - renderWidth) / 2);
        canvas.setLayoutY((canvas.getScene().getHeight() - renderHeight) / 2);

        roomManager.setRenderScale(Math.floor(scale));
    }

    private void startGameLoop() {
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                update();
                render();
            }
        };
        gameLoop.start();
    }

    private void update() {
        // Clear any keys pressed during transitions/prompts
        keysToRemove.forEach(pressedKeys::remove);
        keysToRemove.clear();
        if (world == null || world.getCurrentRoom() == null) {
            return;
        }
        List<SearchSpot> activeSpots = new ArrayList<>();

        activeSearchSpots.clear();
        Room currentRoom = world.getCurrentRoom();
        if (world != null && world.getCurrentRoom() != null) {
            for (SearchSpot spot : currentRoom.getSearchSpots()) {
                if (isPlayerOverlapping(spot, player.getX(), player.getY())) {
                    activeSearchSpots.add(spot);
                }
            }
        }
        for (SearchSpot spot : currentRoom.getSearchSpots()) {
            if (isPlayerOverlapping(spot, player.getX(), player.getY())) {
                activeSpots.add(spot);
            }
        }
        boolean isMoving = pressedKeys.stream().anyMatch(this::isMovementKey);
        player.updateWalkCycle(isMoving);
        player.updatePosition();
        handlePlayerMovement();
        roomManager.checkAllTransitions();
        constrainPlayerToRoom();
        long currentTime = System.nanoTime();
        long deltaTimeNanos = currentTime - lastUpdateTime;
        double deltaTimeSeconds = deltaTimeNanos / 1_000_000_000.0;
        lastUpdateTime = currentTime;
        // Update stamina - now passing isMoving parameter
        player.updateStamina(deltaTimeSeconds, isMoving);

        // If shift is held but stamina ran out, stop sprinting
        if (shiftPressed && player.getCurrentStamina() <= 0) {
            player.setSprinting(false);
        }
    }
    private boolean isPlayerOverlapping(SearchSpot spot, double playerX, double playerY) {
        RoomRenderer room = roomManager.getCurrentRoom();
        if (room == null) return false;

        // Convert player position to pixels
        double px = playerX * room.getTileWidth();
        double py = playerY * room.getTileHeight();

        // Search spot coordinates are already in pixels from Tiled
        double spotX = spot.getX();
        double spotY = spot.getY();
        double spotWidth = spot.getWidth();
        double spotHeight = spot.getHeight();

        // Debug positions
        System.out.printf("Player at (%.1f, %.1f), Spot %s at (%.1f, %.1f)%n",
                px, py, spot.getName(), spotX, spotY);

        // Check overlap with player's approximate bounds (20x20 pixels)
        return px + 10 >= spotX && px - 10 <= spotX + spotWidth &&
                py + 10 >= spotY && py - 10 <= spotY + spotHeight;
    }
    private void constrainPlayerToRoom() {
        if (roomManager.getCurrentRoom() == null) return;

        RoomRenderer room = roomManager.getCurrentRoom();
        double roomWidth = room.getWidthInPixels() / room.getTileWidth();
        double roomHeight = room.getHeightInPixels() / room.getTileHeight();

        // Player dimensions in tile units
        double playerWidth = 0.6;
        double playerHeight = 0.6;

        double minX = playerWidth / 2;
        double maxX = roomWidth - playerWidth / 2;
        double minY = playerHeight / 2;
        double maxY = roomHeight - playerHeight / 2;

        double newX = Math.max(minX, Math.min(maxX, player.getX()));
        double newY = Math.max(minY, Math.min(maxY, player.getY()));

        if (newX != player.getX() || newY != player.getY()) {
            player.setPosition(newX, newY);
            player.setSpeed(0, 0);
        }
    }

    private void handlePlayerMovement() {
        if (player.isTransitioning() || !player.isMovementEnabled()) return;
        // Calculate base speed (walking or sprinting)
        double speedMultiplier = player.isSprinting() ? player.getSPRINT_SPEED_MULTIPLIER() : 1.0;
        double currentMaxSpeed = MAX_WALK_SPEED * speedMultiplier;
        // Reset speeds first
        double targetSpeedX = 0;
        double targetSpeedY = 0;

        // Handle movement input
        boolean movingUp = pressedKeys.contains(KeyCode.W) || pressedKeys.contains(KeyCode.UP);
        boolean movingDown = pressedKeys.contains(KeyCode.S) || pressedKeys.contains(KeyCode.DOWN);
        boolean movingLeft = pressedKeys.contains(KeyCode.A) || pressedKeys.contains(KeyCode.LEFT);
        boolean movingRight = pressedKeys.contains(KeyCode.D) || pressedKeys.contains(KeyCode.RIGHT);

        // Calculate base movement direction
        if (movingUp && !movingDown) targetSpeedY = -currentMaxSpeed;
        if (movingDown && !movingUp) targetSpeedY = currentMaxSpeed;
        if (movingLeft && !movingRight) targetSpeedX = -currentMaxSpeed;
        if (movingRight && !movingLeft) targetSpeedX = currentMaxSpeed;

        // Normalize diagonal movement
        if (targetSpeedX != 0 && targetSpeedY != 0) {
            targetSpeedX *= 0.7071;
            targetSpeedY *= 0.7071;
        }

        // Check collisions BEFORE moving
        double newX = player.getX();
        double newY = player.getY();

        // First check X movement
        if (targetSpeedX != 0) {
            double testX = player.getX() + targetSpeedX;
            if (!checkCollision(testX, player.getY())) {
                newX = testX;
            } else {
                targetSpeedX = 0; // Stop X movement if collision detected
            }
        }

        // Then check Y movement
        if (targetSpeedY != 0) {
            double testY = player.getY() + targetSpeedY;
            if (!checkCollision(player.getX(), testY)) {
                newY = testY;
            } else {
                targetSpeedY = 0; // Stop Y movement if collision detected
            }
        }

        // Update position and speed
        player.setPosition(newX, newY);
        player.setSpeed(targetSpeedX, targetSpeedY);
    }

    private void renderStaminaBar(GraphicsContext gc) {
        double staminaPercentage = player.getCurrentStamina() / player.getMaxStamina();
        double barWidth = 200;
        double barHeight = 10;
        double x = 20;
        double y = 20;

        // Background
        gc.setFill(Color.rgb(50, 50, 50, 0.7));
        gc.fillRect(x, y, barWidth, barHeight);

        // Foreground (stamina level)
        Color staminaColor = player.isSprinting() ? Color.LIMEGREEN : Color.DARKGREEN;
        gc.setFill(staminaColor);
        gc.fillRect(x, y, barWidth * staminaPercentage, barHeight);

        // Border
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(1);
        gc.strokeRect(x, y, barWidth, barHeight);

        // Tex
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", 12));
        gc.fillText("Stamina: " + (int) player.getCurrentStamina() + "%", x, y - 5);
    }

    private boolean checkCollision(double tileX, double tileY) {
        RoomRenderer room = roomManager.getCurrentRoom();
        if (room == null) return false;

        double playerX = tileX * room.getTileWidth();
        double playerY = tileY * room.getTileHeight();

        // Tight collision box (12x12 pixels centered on player)
        Rectangle2D playerHitbox = new Rectangle2D(
                playerX - 6, playerY - 6, 12, 12
        );
        // Simply check for intersection without any bounce effect
        return room.getCollisions().stream()
                .anyMatch(rect -> rect.intersects(playerHitbox));
    }

    public void clearMovementInputs() {
        pressedKeys.removeIf(code ->
                code == KeyCode.W || code == KeyCode.A || code == KeyCode.S || code == KeyCode.D ||
                        code == KeyCode.UP || code == KeyCode.DOWN || code == KeyCode.LEFT || code == KeyCode.RIGHT
        );
        keysToRemove.clear();
    }

    private void render() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.save();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        if (roomManager.getCurrentRoom() != null) {
            gc.save();
            gc.scale(roomManager.getRenderScale(), roomManager.getRenderScale());
            RoomRenderer room = roomManager.getCurrentRoom();
            room.render(gc);
            gc.setStroke(Color.RED);
            gc.setLineWidth(1);
            for (Rectangle2D rect : room.getCollisions()) {
                gc.strokeRect(
                        rect.getMinX(),
                        rect.getMinY(),
                        rect.getWidth(),
                        rect.getHeight()
                );
            }
            room.render(gc);
            renderExitAreas(gc, room);
            double playerX = player.getX() * room.getTileWidth();
            double playerY = player.getY() * room.getTileHeight();
            if (player.getSpeedX() != 0 || player.getSpeedY() != 0) {
                playerY += Math.sin(player.getWalkCyclePosition()) * 0.5;
            }
            gc.setFill(Color.BLUE);
            gc.fillOval(playerX - 10, playerY - 10, 20, 20);
            gc.restore();
        }
        // Render prompt AFTER everything else, in screen coordinates
        roomManager.renderPrompt(gc, canvas.getWidth(), canvas.getHeight());
        if (player.getCurrentStamina() < player.getMaxStamina() || player.isSprinting()) {
            renderStaminaBar(gc);
        }
        if (!activeSearchSpots.isEmpty()) {

            RoomRenderer room = roomManager.getCurrentRoom();
            double playerScreenX = (player.getX() * room.getTileWidth()) * roomManager.getRenderScale();
            double playerScreenY = (player.getY() * room.getTileHeight()) * roomManager.getRenderScale();
            double menuX = playerScreenX + 30;
            double menuY = playerScreenY - 50;
            menuX = Math.min(menuX, canvas.getWidth() - 210);
            menuY = Math.max(menuY, 10);
            gc.setFill(Color.rgb(0, 0, 0, 0.7));
            gc.fillRect(menuX, menuY, 200, 30 * activeSearchSpots.size());
            gc.setFont(Font.font("Arial", 16));
            for (int i = 0; i < activeSearchSpots.size(); i++) {
                SearchSpot spot = activeSearchSpots.get(i);
                gc.setFill(i == selectedSpotIndex ? Color.YELLOW : Color.WHITE);
                gc.fillText("Search " + spot.getName(),
                        menuX + 10,
                        menuY + 20 + i * 30);
            }
        }
        gc.restore();
    }

    private void renderExitAreas(GraphicsContext gc, RoomRenderer room) {
        JSONObject objects = room.getObjectGroup("GameObjects");
        if (objects != null) {
            JSONArray exits = objects.getJSONArray("objects");
            gc.setStroke(Color.RED);
            gc.setLineWidth(2);

            for (int i = 0; i < exits.length(); i++) {
                JSONObject exit = exits.getJSONObject(i);
                if (exit.getString("name").startsWith("EXIT TO")) {
                    gc.strokeRect(
                            exit.getDouble("x"),
                            exit.getDouble("y"),
                            exit.getDouble("width"),
                            exit.getDouble("height")
                    );
                }
            }
        }
    }

    public void performTransition(String targetRoom) {
        clearMovementInputs();
        FadeTransition fadeOut = new FadeTransition(Duration.millis(TRANSITION_DURATION), canvas);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.4);
        fadeOut.setOnFinished(e -> {
            roomManager.transitionToRoom(targetRoom);
            FadeTransition fadeIn = new FadeTransition(Duration.millis(TRANSITION_DURATION), canvas);
            fadeIn.setFromValue(0.4);
            fadeIn.setToValue(1.0);
            fadeIn.play();
        });
        fadeOut.play();
    }

    public World getWorld() {
        return world;
    }

    public static void main(String[] args) {
        launch(args);
    }
}