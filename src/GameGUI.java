import javafx.animation.AnimationTimer;
import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.*;
import java.util.stream.Collectors;

public class GameGUI extends Application {
    private final double MAX_WALK_SPEED = 0.008;
    private RoomManager roomManager;
    private Player player;
    private Canvas canvas;
    private Set<KeyCode> pressedKeys = new HashSet<>();
    private Set<KeyCode> keysToRemove = new HashSet<>();
    public AnimationTimer gameLoop;
    private final int TRANSITION_DURATION = 100;
    private boolean shiftPressed = false;
    private long lastUpdateTime = System.nanoTime();
    private InventoryGUI inventoryGUI;
    private StackPane rootPane;
    private List<SearchSpot> activeSearchSpots = new ArrayList<>();
    private int selectedSpotIndex = 0;
    private World world;
    private Game game;
    private List<Item> recentFoundItems = new ArrayList<>();
    private long itemNotificationEndTime = 0;
    private HidingSpotManager hidingSpotManager;
    private final TextArea console = new TextArea();
    public boolean consoleVisible = false;
    private StackPane consoleContainer;
    private Scene scene;
    private Item currentNearbyItem;
    private Prompt itemPickupPrompt = new Prompt();
    private boolean nearCassettePlayer = false;
    private Prompt cassettePrompt = new Prompt();
    private GameStateGUI loadedState;
    private File saveFileToLoad;
    private Prompt stalkerClawPrompt = new Prompt();
    private boolean nearStalkerClaw = false;
    private boolean isPaused = false;
    private StackPane pauseButtonContainer;
    private ImageView pauseButton;
    public void loadGame(File saveFile) {
        this.saveFileToLoad = saveFile;
    }
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
            world = new World(player);
            player.setWorld(world);
            player.setGameGUI(this);
            game = new Game(world);
            world.setGame(game);
            world.loadRoomLayout("src/fileImports/game_layout.txt");
            world.loadSearchSpots("src/fileImports/search_spots.txt");
            inventoryGUI = new InventoryGUI(player, this);
            rootPane.getChildren().add(inventoryGUI);
            roomManager = new RoomManager(player, this);
            hidingSpotManager = new HidingSpotManager(player, roomManager);
            loadRooms(roomManager);
            if (saveFileToLoad != null) {
                loadGameState(saveFileToLoad);
            }
            if (loadedState != null) {
                roomManager.transitionToRoom(loadedState.getCurrentRoomName(),true,loadedState.getPlayerX(),loadedState.getPlayerY());
                player.setPosition(loadedState.getPlayerX(), loadedState.getPlayerY());
            }
            roomManager.setOnRoomChanged(() -> {
                primaryStage.setTitle("The Game - " + roomManager.getCurrentRoomName());
                updateCanvasSize();
                render();
            });

            startGameLoop();
            primaryStage.setTitle("The Game - " + roomManager.getCurrentRoomName());
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to start game: " + e.getMessage());
        }
    }

    private void loadRooms(RoomManager roomManager) {
        try {
            if (loadedState != null) {
                roomManager.loadRoom(loadedState.getCurrentRoomName().toLowerCase());
            }
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
        Image pauseImage = new Image("/ui/pause_button.png");
        pauseButton = new ImageView(pauseImage);
        // Keep original image size
        pauseButton.setPreserveRatio(true);
        pauseButton.setFitWidth(175);
        pauseButton.setFitHeight(88);
        pauseButton.setCursor(Cursor.HAND);
        StackPane.setAlignment(pauseButton, Pos.TOP_LEFT);
        StackPane.setMargin(pauseButton, new Insets(10));
        pauseButton.setOnMouseClicked(e -> showPauseMenu(primaryStage));
        rootPane.getChildren().add(pauseButton);
        scene = new Scene(rootPane, Color.BLACK);
        setupConsole(scene);
        scene.getStylesheets().add(getClass().getResource("/inventory.css").toExternalForm());
        primaryStage.setScene(scene);

        scene.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if (e.getCode() == KeyCode.I) {
                if (!inventoryGUI.isInventoryVisible()) {
                    scene.setCursor(Cursor.DEFAULT);
                    player.setMovementEnabled(false);
                    gameLoop.stop();
                } else {
                    // Resume game
                    scene.setCursor(Cursor.NONE);
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
        scene.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ALT) {
                scene.setCursor(Cursor.DEFAULT);
            }

            if (event.getCode() == KeyCode.ESCAPE && !inventoryGUI.isInventoryVisible() && !consoleVisible) {
                showPauseMenu(primaryStage);
                event.consume();
            }
        });

        scene.addEventHandler(KeyEvent.KEY_RELEASED, event -> {
            if (event.getCode() == KeyCode.ALT && !isPaused) {
                scene.setCursor(Cursor.NONE);
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
                if (code == KeyCode.W || code == KeyCode.UP ||
                        code == KeyCode.S || code == KeyCode.DOWN) {
                    player.setSpeed(player.getSpeedX(), 0);
                } else {
                    player.setSpeed(0, player.getSpeedY());
                }
            }
        });
        scene.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.F11) {
                primaryStage.setFullScreen(!primaryStage.isFullScreen());
                updateCanvasSize();
            }
        });
        scene.addEventFilter(KeyEvent.ANY, event -> {
            if (player.isTransitioning() || roomManager.isShowingPrompt()) {
                if (isMovementKey(event.getCode())) {
                    event.consume();
                    clearMovementInputs();
                    pressedKeys.remove(event.getCode());
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
        scene.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if (e.getCode() == KeyCode.E && !activeSearchSpots.isEmpty()) {
                SearchSpot spot = activeSearchSpots.get(selectedSpotIndex);
                if (!spot.isSearched()) {
                    List<Item> items = spot.getItems().stream()
                            .map(item -> {
                                try {
                                    return item.getClass().getDeclaredConstructor().newInstance();
                                } catch (Exception exception) {
                                    return item; // Fallback for non-cloneable items
                                }
                            })
                            .collect(Collectors.toList());
                    recentFoundItems.clear();
                    List<Item> overflowItems = new ArrayList<>();

                    items.forEach(item -> {
                        if (player.getInventory().addItem(item)) {
                            recentFoundItems.add(item);
                            System.out.println("Added item: " + item.getName());
                        } else {
                            overflowItems.add(item);
                        }
                    });

                    // Add overflow items to the room with offset positions
                    if (!overflowItems.isEmpty()) {
                        Room currentRoom = world.getCurrentRoom();
                        final double[] offset = {0}; // Track position offset
                        overflowItems.forEach(item -> {
                            // Create a new instance to avoid reference sharing
                            Item newItem = createNewItemInstance(item);
                            // Calculate offset position
                            double x = player.getX() + offset[0] * 0.1;
                            double y = player.getY() + offset[0] * 0.1;
                            currentRoom.addItem(newItem, x, y);
                            offset[0] += 1;
                            System.out.println("Dropped item: " + newItem.getName());
                        });
                    }

                    if (!recentFoundItems.isEmpty() || !overflowItems.isEmpty()) {
                        itemNotificationEndTime = System.currentTimeMillis() + 3000;
                    }

                    spot.setSearched(true);
                    activeSearchSpots.removeIf(SearchSpot::isSearched);
                    if (!activeSearchSpots.isEmpty()) {
                        selectedSpotIndex = Math.min(selectedSpotIndex, activeSearchSpots.size() - 1);
                    }
                    e.consume();
                }
            }
        });
        scene.setOnScroll(e -> {
            if (!activeSearchSpots.isEmpty()) {
                double delta = e.getDeltaY();
                int direction = (int) Math.signum(delta);
                selectedSpotIndex = (selectedSpotIndex - direction + activeSearchSpots.size()) % activeSearchSpots.size();
                e.consume();
            }
        });
        scene.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if (e.getCode() == KeyCode.F) {
                if (nearCassettePlayer) {
                    openSaveMenu();
                    e.consume();
                }
                if (nearStalkerClaw && stalkerClawPrompt.isActive()) {
                    triggerEndingSequence(primaryStage);
                    e.consume();
                }
                boolean actionTaken = false;
                if (currentNearbyItem != null && itemPickupPrompt.isActive()) {
                    System.out.println("actionTaken");
                    new TakeCommand(player, currentNearbyItem).execute();
                    currentNearbyItem = null;
                    itemPickupPrompt.hide();
                    actionTaken = true;
                }
                if (!actionTaken) {
                    actionTaken = hidingSpotManager.tryHide();
                }
                if (!actionTaken) {
                    actionTaken = roomManager.tryConfirmPrompt();
                }
                if (actionTaken) {
                    e.consume();
                }
            }

        });
        primaryStage.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                scene.setCursor(inventoryGUI.isInventoryVisible() ? Cursor.DEFAULT : Cursor.NONE);
            } else {
                scene.setCursor(Cursor.DEFAULT); // Show cursor when window loses focus
                inventoryGUI.hideAllPopups();
            }
        });
        scene.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if (e.getCode() == KeyCode.BACK_QUOTE) {
                toggleConsole();
                e.consume();
            }
        });

    }

    private Item createNewItemInstance(Item original) {
        if (original instanceof Bandage) return new Bandage();
        if (original instanceof PistolAmmo) return new PistolAmmo(((PistolAmmo) original).getAmount());
        if (original instanceof HealingSerum) return new HealingSerum();
        if (original instanceof ShotgunShells) return new ShotgunShells(((ShotgunShells) original).getAmount());
        if (original instanceof Batteries) return new Batteries();
        if (original instanceof Cassette) return new Cassette();
        // Add other item types as needed
        return original; // fallback for non-cloneable items
    }

    private void setupConsole(Scene scene) {
        console.setWrapText(true);
        console.setEditable(false);
        console.getStyleClass().add("console-text-area");
        console.setPrefRowCount(8);
        ScrollPane scrollPane = new ScrollPane(console);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefSize(400, 200);
        scrollPane.setMaxSize(400, 200); // Add constraint
        scrollPane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7); " +
                "-fx-border-color: #666; -fx-border-width: 2; " +
                "-fx-border-radius: 5; -fx-background-radius: 5;");
        consoleContainer = new StackPane(scrollPane);
        consoleContainer.setAlignment(Pos.TOP_LEFT);
        consoleContainer.setVisible(false);
        StackPane.setMargin(consoleContainer, new Insets(20, 0, 0, 20));
        scrollPane.prefWidthProperty().bind(consoleContainer.widthProperty());
        scrollPane.prefHeightProperty().bind(consoleContainer.heightProperty());
        rootPane.getChildren().add(consoleContainer);
    }

    public void addConsoleMessage(String message) {
        Platform.runLater(() -> {
            console.appendText(message + "\n");
            console.setScrollTop(Double.MAX_VALUE);
        });
    }

    // Helper method to check if a key is a movement key
    private boolean isMovementKey(KeyCode code) {
        return code == KeyCode.W || code == KeyCode.UP ||
                code == KeyCode.S || code == KeyCode.DOWN ||
                code == KeyCode.A || code == KeyCode.LEFT ||
                code == KeyCode.D || code == KeyCode.RIGHT;
    }

    public void toggleConsole() {
        consoleVisible = !consoleVisible;
        Scene scene = rootPane.getScene();
        scene.setCursor(consoleVisible ? Cursor.DEFAULT : Cursor.NONE);
        consoleContainer.setVisible(consoleVisible);
        // Close inventory if opening console
        if (consoleVisible) {
            // Close inventory properly if it's open
            if (inventoryGUI.isInventoryVisible()) {
                inventoryGUI.toggle();
            }
            Platform.runLater(() -> {
                console.requestFocus();
                consoleContainer.requestFocus();
            });
        } else {
            if (!inventoryGUI.isInventoryVisible()) {
                scene.setCursor(Cursor.NONE);
                player.setMovementEnabled(true);
                gameLoop.start();
            }
        }
    }

    private void updateCanvasSize() {
        if (roomManager.getCurrentRoom() == null) return;

        RoomRenderer room = roomManager.getCurrentRoom();

        double scaleX = canvas.getScene().getWidth() / room.getWidthInPixels();
        double scaleY = canvas.getScene().getHeight() / room.getHeightInPixels();
        double scale = Math.min(scaleX, scaleY);

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
        if (hidingSpotManager.isHiding()) {
            activeSearchSpots.clear();
            currentNearbyItem = null;
            return;
        }
        checkNearbyItems();
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
                if (!spot.isSearched() && isPlayerOverlapping(spot, player.getX(), player.getY())) {
                    activeSearchSpots.add(spot);
                }
            }
        }
        // Ensure selected index is valid
        if (!activeSearchSpots.isEmpty()) {
            selectedSpotIndex = Math.min(selectedSpotIndex, activeSearchSpots.size() - 1);
        } else {
            selectedSpotIndex = 0;
        }

        for (SearchSpot spot : currentRoom.getSearchSpots()) {
            if (isPlayerOverlapping(spot, player.getX(), player.getY())) {
                activeSpots.add(spot);
            }
        }
        boolean isMoving = pressedKeys.stream().anyMatch(this::isMovementKey);
        checkCassettePlayer();
        checkEndingObject();
        player.updateWalkCycle(isMoving);
        player.updatePosition();
        hidingSpotManager.loadHidingSpots();
        hidingSpotManager.update();
        handlePlayerMovement();
        roomManager.checkAllTransitions();
        constrainPlayerToRoom();
        long currentTime = System.nanoTime();
        long deltaTimeNanos = currentTime - lastUpdateTime;
        double deltaTimeSeconds = deltaTimeNanos / 1_000_000_000.0;
        lastUpdateTime = currentTime;
        // Updated stamina - now passing isMoving parameter
        player.updateStamina(deltaTimeSeconds, isMoving);

        // If shift is held but stamina ran out, stop sprinting
        if (shiftPressed && player.getCurrentStamina() <= 0) {
            player.setSprinting(false);
        }
    }

    private void checkNearbyItems() {
        if (hidingSpotManager.isHiding()) {
            hidePickupPrompt();
            return;
        }
        Room currentRoom = world.getCurrentRoom();
        currentNearbyItem = null;
        if (currentRoom != null) {
            List<Item> roomItems = new ArrayList<>(currentRoom.getItems());
            for (Item item : roomItems) {
                Point2D pos = currentRoom.getItemPosition(item);
                if (pos != null && isPlayerNear(pos.getX(), pos.getY())) {
                    currentNearbyItem = item;
                    break;
                }
            }
        }
        if (currentNearbyItem != null) {
            showPickupPrompt(currentNearbyItem.getName());
        } else {
            hidePickupPrompt();
        }
    }

    private boolean isPlayerNear(double itemX, double itemY) {
        RoomRenderer room = roomManager.getCurrentRoom();
        if (room == null) return false;

        // Convert to pixel coordinates
        double playerX = player.getX() * room.getTileWidth();
        double playerY = player.getY() * room.getTileHeight();
        double itemPixelX = itemX * room.getTileWidth();
        double itemPixelY = itemY * room.getTileHeight();


        return Math.hypot(playerX - itemPixelX, playerY - itemPixelY) < 32;
    }

    private void showPickupPrompt(String itemName) {
        itemPickupPrompt.show("Pick up " + itemName, "");
    }

    private void hidePickupPrompt() {
        itemPickupPrompt.hide();
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
        if (movingUp && !movingDown) targetSpeedY = -currentMaxSpeed;
        if (movingDown && !movingUp) targetSpeedY = currentMaxSpeed;
        if (movingLeft && !movingRight) targetSpeedX = -currentMaxSpeed;
        if (movingRight && !movingLeft) targetSpeedX = currentMaxSpeed;
        if (targetSpeedX != 0 && targetSpeedY != 0) {
            targetSpeedX *= 0.7071;
            targetSpeedY *= 0.7071;
        }
        double newX = player.getX();
        double newY = player.getY();
        if (targetSpeedX != 0) {
            double testX = player.getX() + targetSpeedX;
            if (!checkCollision(testX, player.getY())) {
                newX = testX;
            } else {
                targetSpeedX = 0;
            }
        }

        if (targetSpeedY != 0) {
            double testY = player.getY() + targetSpeedY;
            if (!checkCollision(player.getX(), testY)) {
                newY = testY;
            } else {
                targetSpeedY = 0;
            }
        }
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
        // Text
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", 12));
        gc.fillText("Stamina: " + (int) player.getCurrentStamina() + "%", x, y - 5);
    }

    private boolean checkCollision(double tileX, double tileY) {
        if (hidingSpotManager.isHiding()) {
            return false; // No collisions while hiding
        }
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
        }
        if (!hidingSpotManager.isHiding()) {
            RoomRenderer room = roomManager.getCurrentRoom();
            double playerX = player.getX() * room.getTileWidth();
            double playerY = player.getY() * room.getTileHeight();
            if (player.getSpeedX() != 0 || player.getSpeedY() != 0) {
                playerY += Math.sin(player.getWalkCyclePosition()) * 0.5;
            }
            gc.setFill(Color.BLUE);
            gc.fillOval(playerX - 10, playerY - 10, 20, 20);
        }
        gc.restore();
        stalkerClawPrompt.render(gc, canvas.getWidth(), canvas.getHeight());
        cassettePrompt.render(gc, canvas.getWidth(), canvas.getHeight());
        hidingSpotManager.render(gc, canvas.getWidth(), canvas.getHeight());
        roomManager.renderPrompt(gc, canvas.getWidth(), canvas.getHeight());
        itemPickupPrompt.render(gc, canvas.getWidth(), canvas.getHeight());
        if (player.getCurrentStamina() < player.getMaxStamina() || player.isSprinting()) {
            renderStaminaBar(gc);
        }
        if (!activeSearchSpots.isEmpty()) {
            RoomRenderer room = roomManager.getCurrentRoom();
            double playerScreenX = (player.getX() * room.getTileWidth()) * roomManager.getRenderScale();
            double playerScreenY = (player.getY() * room.getTileHeight()) * roomManager.getRenderScale();

            // Position menu relative to player but constrained to screen
            double menuX = playerScreenX + 30;
            double menuY = playerScreenY - 50;
            menuX = Math.min(menuX, canvas.getWidth() - 250); // Keep on screen
            menuY = Math.max(menuY, 20); // Keep on screen

            // Menu background with rounded corners
            gc.setFill(Color.rgb(30, 30, 40, 0.85));
            gc.fillRoundRect(menuX, menuY, 230, 40 + 35 * activeSearchSpots.size(), 15, 15);

            // Border
            gc.setStroke(Color.rgb(100, 150, 255));
            gc.setLineWidth(2);
            gc.strokeRoundRect(menuX, menuY, 230, 40 + 35 * activeSearchSpots.size(), 15, 15);

            // Title
            gc.setFill(Color.WHITE);
            gc.setFont(Font.font("Arial", FontWeight.BOLD, 18));
            gc.fillText("Searchable Objects", menuX + 15, menuY + 25);

            // Separator line
            gc.setStroke(Color.rgb(100, 150, 255, 0.5));
            gc.setLineWidth(1);
            gc.strokeLine(menuX + 10, menuY + 35, menuX + 220, menuY + 35);

            // Items
            gc.setFont(Font.font("Arial", 16));
            for (int i = 0; i < activeSearchSpots.size(); i++) {
                SearchSpot spot = activeSearchSpots.get(i);

                // Highlight selected item
                if (i == selectedSpotIndex) {
                    gc.setFill(Color.rgb(100, 150, 255, 0.3));
                    gc.fillRoundRect(menuX + 10, menuY + 40 + i * 35, 210, 30, 5, 5);

                    gc.setStroke(Color.rgb(100, 150, 255));
                    gc.setLineWidth(1.5);
                    gc.strokeRoundRect(menuX + 10, menuY + 40 + i * 35, 210, 30, 5, 5);
                }

                // Item text
                gc.setFill(i == selectedSpotIndex ? Color.rgb(200, 230, 255) : Color.WHITE);
                gc.fillText(spot.getName(), menuX + 20, menuY + 60 + i * 35);

                // Search status indicator
                gc.setFill(spot.isSearched() ? Color.RED : Color.GREEN);
                gc.fillOval(menuX + 190, menuY + 47 + i * 35, 8, 8);
            }

            // Instruction text
            gc.setFill(Color.rgb(200, 200, 200));
            gc.setFont(Font.font("Arial", FontPosture.ITALIC, 12));
            gc.fillText("Press E to search", menuX + 15, menuY + 55 + activeSearchSpots.size() * 35);
        }
        if (System.currentTimeMillis() < itemNotificationEndTime) {
            renderItemNotification(gc);
        }

    }


    private void renderItemNotification(GraphicsContext gc) {
        if (recentFoundItems.isEmpty()) return;

        double width = 300;
        double height = 50 + 20 * recentFoundItems.size();
        double x = (canvas.getWidth() - width) / 2;
        double y = canvas.getHeight() - height - 20; // Bottom of screen

        // Background
        gc.setFill(Color.rgb(0, 0, 0, 0.85));
        gc.fillRoundRect(x, y, width, height, 10, 10);

        // Border
        gc.setStroke(Color.GOLDENROD);
        gc.setLineWidth(2);
        gc.strokeRoundRect(x, y, width, height, 10, 10);

        // Text
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        gc.fillText("Items Found:", x + 10, y + 25);

        for (int i = 0; i < recentFoundItems.size(); i++) {
            gc.fillText("- " + recentFoundItems.get(i).getName(),
                    x + 20, y + 45 + i * 20);
        }
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
            roomManager.transitionToRoom(targetRoom,false,player.getX(),player.getY());
            FadeTransition fadeIn = new FadeTransition(Duration.millis(TRANSITION_DURATION), canvas);
            fadeIn.setFromValue(0.4);
            fadeIn.setToValue(1.0);
            fadeIn.play();
        });
        fadeOut.play();
    }
    private void checkCassettePlayer() {
        if (hidingSpotManager.isHiding()) {
            cassettePrompt.hide();
            nearCassettePlayer = false;
            return;
        }

        Room currentRoom = world.getCurrentRoom();
        nearCassettePlayer = false;

        JSONObject cassetteLayer = roomManager.getCurrentRoom().getObjectGroup("CassettePlayer");
        if (cassetteLayer != null) {
            JSONArray objects = cassetteLayer.getJSONArray("objects");
            for (int i = 0; i < objects.length(); i++) {
                JSONObject obj = objects.getJSONObject(i);
                double objX = obj.getDouble("x");
                double objY = obj.getDouble("y");
                // Convert player position to pixels
                double playerX = player.getX() * roomManager.getCurrentRoom().getTileWidth();
                double playerY = player.getY() * roomManager.getCurrentRoom().getTileHeight();
                // Use actual player size for detection
                double distance = Math.hypot(playerX - objX, playerY - objY);
                double detectionRadius = 50; // pixels

                if (distance < detectionRadius) {
                    nearCassettePlayer = true;
                    break;
                }
            }
        }
        if (nearCassettePlayer) {
            cassettePrompt.show("Save progress using a Cassette", "");
        } else {
            cassettePrompt.hide();
        }
    }
    public void openSaveMenu() {
        // Check if player has cassette
        boolean hasCassette = player.getInventory().getItems().stream()
                .anyMatch(item -> item instanceof Cassette);

        if (!hasCassette) {
            addConsoleMessage("You need a Cassette to save your game!");
            // Auto-show console if hidden
            if (!consoleVisible) {
                toggleConsole();
            }
            return;
        }

        // Open save menu
        SaveMenuGUI saveMenu = new SaveMenuGUI(this);
        rootPane.getChildren().add(saveMenu);

        // Pause game
        scene.setCursor(Cursor.DEFAULT);
        player.setMovementEnabled(false);
        gameLoop.stop();
    }
    private void loadGameState(File saveFile) {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(saveFile))) {
            GameStateGUI state = (GameStateGUI) in.readObject();
            applyGameState(state);
        } catch (Exception e) {
            System.err.println("Failed to load game: " + e.getMessage());
            e.printStackTrace();
        }
    }
    private void applyGameState(GameStateGUI state) {
        // Apply loaded state to game
        player.setHealth(state.getPlayerHealth());
        player.setCurrentStamina(state.getPlayerStamina());
        player.setPosition(state.getPlayerX(), state.getPlayerY());
        player.setCurrentRoomName(state.getCurrentRoomName());

        // Clear existing inventory and add saved items
        player.getInventory().clear();
        for (Item item : state.getInventory()) {
            player.getInventory().addItem(item);
        }

        // Equip weapon
        player.equipWeapon(state.getEquippedWeapon());

        // Apply world state
        world.setStalkerDistance(state.getStalkerDistance());
        world.restoreLockStates(state.getLockStates());

        // Apply searched spots
        Map<String, List<String>> searchedSpots = state.getSearchedSpots();
        for (Room room : world.getRooms().values()) {
            if (searchedSpots.containsKey(room.getName())) {
                for (SearchSpot spot : room.getSearchSpots()) {
                    if (searchedSpots.get(room.getName()).contains(spot.getName())) {
                        spot.setSearched(true);
                    }
                }
            }
        }

        // Apply dropped items
        Map<String, List<ItemPosition>> droppedItems = state.getDroppedItems();
        for (Room room : world.getRooms().values()) {
            if (droppedItems.containsKey(room.getName())) {
                for (ItemPosition itemPos : droppedItems.get(room.getName())) {
                    room.addItem(itemPos.getItem(), itemPos.getX(), itemPos.getY());
                }
            }
        }

        roomManager.transitionToRoom(
                state.getCurrentRoomName(),
                true,
                state.getPlayerX(),
                state.getPlayerY()
        );
    }
    private void checkEndingObject() {
        if (hidingSpotManager.isHiding()) {
            stalkerClawPrompt.hide();
            nearStalkerClaw = false;
            return;
        }

        RoomRenderer room = roomManager.getCurrentRoom();
        if (room == null || !"enter_hall".equals(roomManager.getCurrentRoomName())) {
            stalkerClawPrompt.hide();
            nearStalkerClaw = false;
            return;
        }

        JSONObject endingLayer = room.getObjectGroup("USE_STALKER_CLAW");
        if (endingLayer == null) {
            stalkerClawPrompt.hide();
            nearStalkerClaw = false;
            return;
        }

        JSONArray objects = endingLayer.getJSONArray("objects");
        nearStalkerClaw = false;
        for (int i = 0; i < objects.length(); i++) {
            JSONObject obj = objects.getJSONObject(i);
            double objX = obj.getDouble("x");
            double objY = obj.getDouble("y");
            double objWidth = obj.getDouble("width");
            double objHeight = obj.getDouble("height");

            // Use rectangle check instead of point distance
            if (isPlayerInRectangle(objX, objY, objWidth, objHeight)) {
                nearStalkerClaw = true;
                break;
            }
        }

        for (int i = 0; i < objects.length(); i++) {
            JSONObject obj = objects.getJSONObject(i);
            if (isPlayerNear(obj.getDouble("x"), obj.getDouble("y"))) {
                nearStalkerClaw = true;
                break;
            }
        }

        if (nearStalkerClaw && player.getInventory().findItem("Stalker's Claw") != null) {
            stalkerClawPrompt.show("Insert Stalker Claw", "");
        } else {
            stalkerClawPrompt.hide();
        }
    }
    private boolean isPlayerInRectangle(double rectX, double rectY, double rectWidth, double rectHeight) {
        RoomRenderer room = roomManager.getCurrentRoom();
        if (room == null) return false;

        double playerX = player.getX() * room.getTileWidth();
        double playerY = player.getY() * room.getTileHeight();
        double playerSize = 20; // Player circle diameter

        // Calculate player bounds
        double playerLeft = playerX - playerSize / 2;
        double playerRight = playerX + playerSize / 2;
        double playerTop = playerY - playerSize / 2;
        double playerBottom = playerY + playerSize / 2;

        // Calculate rectangle bounds
        double rectRight = rectX + rectWidth;
        double rectBottom = rectY + rectHeight;

        // Check for overlap
        boolean xOverlap = playerRight > rectX && playerLeft < rectRight;
        boolean yOverlap = playerBottom > rectY && playerTop < rectBottom;

        return xOverlap && yOverlap;
    }
    private void triggerEndingSequence(Stage primaryStage) {
        gameLoop.stop();
        StackPane endPane = new StackPane();
        endPane.setStyle("-fx-background-color: black;");
        endPane.setOpacity(0);
        rootPane.getChildren().add(endPane);

        FadeTransition fadeIn = new FadeTransition(Duration.seconds(2), endPane);
        fadeIn.setToValue(1.0);
        fadeIn.setOnFinished(e -> showEndScreen(primaryStage));
        fadeIn.play();
    }

    private void showEndScreen(Stage primaryStage) {
        scene.setCursor(Cursor.DEFAULT);
        VBox endMenu = new VBox(20);
        endMenu.setAlignment(Pos.CENTER);

        Label endLabel = new Label("THE END");
        endLabel.setFont(Font.font(80));
        endLabel.setTextFill(Color.WHITE);

        Button mainMenuBtn = new Button("Main Menu");
        mainMenuBtn.getStyleClass().add("pause-button");
        mainMenuBtn.setOnAction(e -> {
            primaryStage.close();
            new MainMenu().start(new Stage());
        });

        Button exitBtn = new Button("Exit Game");
        exitBtn.getStyleClass().add("pause-button");
        exitBtn.setOnAction(e -> Platform.exit());

        endMenu.getChildren().addAll(endLabel, mainMenuBtn, exitBtn);

        StackPane endScreen = new StackPane(endMenu);
        endScreen.setStyle("-fx-background-color: black;");

        rootPane.getChildren().clear();
        rootPane.getChildren().add(endScreen);
    }
    private void showPauseMenu(Stage primaryStage) {
        if (isPaused) return;

        Pane overlay = new Pane();
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);");
        overlay.setPrefSize(scene.getWidth(), scene.getHeight());

        VBox menu = new VBox(20);
        menu.setAlignment(Pos.CENTER);
        menu.getStyleClass().add("pause-menu");

        Label title = new Label("PAUSE MENU");
        title.getStyleClass().add("pause-title");

        Button continueBtn = new Button("Continue");
        continueBtn.getStyleClass().add("pause-button");
        continueBtn.setOnAction(e -> hidePauseMenu(overlay));

        Button loadBtn = new Button("Load");
        loadBtn.getStyleClass().add("pause-button");
        loadBtn.setOnAction(e -> {
            LoadMenuGUI.show(primaryStage, this::handleSaveSelected);
        });

        Button mainMenuBtn = new Button("Main Menu");
        mainMenuBtn.getStyleClass().add("pause-button");
        mainMenuBtn.setOnAction(e -> {
            primaryStage.close();
            new MainMenu().start(new Stage());
        });

        Button exitBtn = new Button("Exit");
        exitBtn.getStyleClass().add("pause-button");
        exitBtn.setOnAction(e -> Platform.exit());

        menu.getChildren().addAll(title, continueBtn, loadBtn, mainMenuBtn, exitBtn);

        StackPane menuContainer = new StackPane(menu);
        menuContainer.setAlignment(Pos.CENTER);
        overlay.getChildren().add(menuContainer);

        rootPane.getChildren().add(overlay);
        isPaused = true;
        scene.setCursor(Cursor.DEFAULT);
        gameLoop.stop();
    }

    private void hidePauseMenu(Pane overlay) {
        rootPane.getChildren().remove(overlay);
        isPaused = false;
        if (!consoleVisible) {
            scene.setCursor(Cursor.NONE);
        }
        gameLoop.start();
    }
    private void handleSaveSelected(File saveFile) {
        if (saveFile != null) {
            loadGameState(saveFile);
            hidePauseMenu(null);
        }
    }
    public World getWorld() {
        return world;
    }

    public Player getPlayer() {
        return player;
    }

    public Scene getScene() {
        return scene;
    }

    public Game getGame() {
        return game;
    }

    public StackPane getRootPane() {
        return rootPane;
    }

    public InventoryGUI getInventoryGUI() {
        return inventoryGUI;
    }

    public static void main(String[] args) {
        launch(args);
    }
}