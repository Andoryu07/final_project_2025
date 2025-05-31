
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class SaveMenuGUI extends StackPane {
    private static final String SAVE_FOLDER = "saves/";
    private final GameGUI gameGUI;
    private final VBox saveListContainer;
    private final List<Button> saveButtons = new ArrayList<>();

    public SaveMenuGUI(GameGUI gameGUI) {
        this.gameGUI = gameGUI;
        getStyleClass().add("save-menu");

        // Main container
        VBox mainContainer = new VBox(20);
        mainContainer.setAlignment(Pos.CENTER);
        mainContainer.setPadding(new Insets(20));
        mainContainer.setStyle("-fx-background-color: rgba(0, 0, 0, 0.9); -fx-border-color: #555; -fx-border-width: 2;");

        // Title
        Label title = new Label("SAVED GAMES");
        title.getStyleClass().add("save-title");

        // Save list container
        saveListContainer = new VBox(10);
        saveListContainer.setAlignment(Pos.CENTER);
        saveListContainer.setPrefWidth(400);

        // New save button
        Button newSaveButton = new Button("NEW SAVE");
        newSaveButton.getStyleClass().add("save-button");
        newSaveButton.setOnAction(e -> createNewSave());

        // Close button
        Button closeButton = new Button("CLOSE");
        closeButton.getStyleClass().add("save-button");
        closeButton.setOnAction(e -> closeMenu());

        mainContainer.getChildren().addAll(title, saveListContainer, newSaveButton, closeButton);
        getChildren().add(mainContainer);

        // Load existing saves
        refreshSaveList();
    }

    private void refreshSaveList() {
        saveListContainer.getChildren().clear();

        File saveDir = new File(SAVE_FOLDER);
        File[] saveFiles = saveDir.listFiles((dir, name) -> name.endsWith(".dat"));

        if (saveFiles != null && saveFiles.length > 0) {
            for (File saveFile : saveFiles) {
                HBox entry = new HBox(10);
                entry.getStyleClass().add("save-entry");
                entry.setAlignment(Pos.CENTER_LEFT);
                entry.setPadding(new Insets(10));

                VBox infoBox = new VBox(5);
                Label nameLabel = new Label(saveFile.getName());
                nameLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");

                // Add save metadata if available
                Label metaLabel = new Label(SaveHelper.getSaveMetadata(saveFile));
                metaLabel.setStyle("-fx-text-fill: #aaa; -fx-font-size: 12;");

                infoBox.getChildren().addAll(nameLabel, metaLabel);

                Button saveButton = new Button("OVERWRITE");
                saveButton.getStyleClass().add("save-button");
                saveButton.setOnAction(e -> confirmOverwrite(saveFile));

                entry.getChildren().addAll(infoBox, saveButton);
                saveListContainer.getChildren().add(entry);
            }
        } else {
            Label noSaves = new Label("No saved games found");
            noSaves.getStyleClass().add("no-saves");
            saveListContainer.getChildren().add(noSaves);
        }
    }

    private String getSaveMetadata(File saveFile) {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(saveFile))) {
            GameStateGUI state = (GameStateGUI) in.readObject();
            return String.format("Room: %s | Health: %d",
                    state.getCurrentRoomName(), state.getPlayerHealth());
        } catch (Exception e) {
            return "Unknown save data";
        }
    }

    private void confirmOverwrite(File saveFile) {
        Pane overlay = new Pane();
        overlay.setStyle("-fx-background-color: rgba(0,0,0,0.5);");
        overlay.setPrefSize(800, 600);
        VBox dialog = new VBox(20);
        dialog.setAlignment(Pos.CENTER);
        dialog.setPadding(new Insets(20));
        dialog.setStyle("-fx-background-color: #2a2a3a; -fx-border-color: #4a4a7a; -fx-border-width: 2;");
        Label message = new Label("Overwrite " + saveFile.getName() + "?");
        message.setStyle("-fx-text-fill: white; -fx-font-size: 16;");
        HBox buttons = new HBox(20);
        buttons.setAlignment(Pos.CENTER);
        Button confirmButton = new Button("Confirm");
        confirmButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
        confirmButton.setOnAction(e -> {
            saveGame(saveFile);
            getChildren().remove(overlay);
        });
        Button cancelButton = new Button("Cancel");
        cancelButton.setStyle("-fx-background-color: #34495e; -fx-text-fill: white;");
        cancelButton.setOnAction(e -> getChildren().remove(overlay));
        buttons.getChildren().addAll(confirmButton, cancelButton);
        dialog.getChildren().addAll(message, buttons);
        dialog.setLayoutX((overlay.getWidth() - dialog.getWidth()) / 2);
        dialog.setLayoutY((overlay.getHeight() - dialog.getHeight()) / 2);
        overlay.getChildren().add(dialog);
        getChildren().add(overlay);
    }

    private void createNewSave() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        File saveFile = new File(SAVE_FOLDER + "save_" + timestamp + ".dat");
        saveGame(saveFile);
    }

    private void saveGame(File saveFile) {
        try {
            Player player = gameGUI.getPlayer();
            Inventory inventory = player.getInventory();

            // Find first cassette in inventory
            Optional<Item> cassette = inventory.getItems().stream()
                    .filter(item -> item instanceof Cassette)
                    .findFirst();

            if (!cassette.isPresent()) {
                gameGUI.addConsoleMessage("You need a Cassette to save your game!");
                return;
            }

            GameStateGUI state = createGameState();
            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(saveFile))) {
                // Remove cassette
                inventory.removeItem(cassette.get());
                out.writeObject(state);
                if (SaveHelper.saveGame(gameGUI.getPlayer(), gameGUI.getWorld())) {
                    gameGUI.addConsoleMessage("Game saved successfully!");
                    closeMenu();
                } else {
                    gameGUI.addConsoleMessage("Failed to save game!");
                }


                // Close save menu after successful save
                closeMenu();
            }
        } catch (IOException e) {
            gameGUI.addConsoleMessage("Failed to save game: " + e.getMessage());
        }
        refreshSaveList();
    }

    private GameStateGUI createGameState() {
        GameStateGUI state = new GameStateGUI();
        Player player = gameGUI.getPlayer();
        World world = gameGUI.getWorld();

        // Player state
        state.setPlayerHealth(player.getHealth());
        state.setPlayerStamina(player.getCurrentStamina());
        state.setPlayerPosition(player.getX(), player.getY());
        state.setCurrentRoomName(player.getCurrentRoomName()); // Fixed to use player's room
        state.setInventory(new ArrayList<>(player.getInventory().getItems()));
        state.setEquippedWeapon(player.getEquippedWeapon());

        // World state
        state.setSearchedSpots(getSearchedSpots(world));
        state.setDroppedItems(getDroppedItems(world));
        state.setLockStates(world.getAllLockStates());
        state.setStalkerDistance(world.getStalkerDistance());

        return state;
    }

    private Map<String, List<String>> getSearchedSpots(World world) {
        Map<String, List<String>> searchedSpots = new HashMap<>();
        for (Room room : world.getRooms().values()) {
            List<String> spotNames = new ArrayList<>();
            for (SearchSpot spot : room.getSearchSpots()) {
                if (spot.isSearched()) {
                    spotNames.add(spot.getName());
                }
            }
            searchedSpots.put(room.getName(), spotNames);
        }
        return searchedSpots;
    }

    private Map<String, List<ItemPosition>> getDroppedItems(World world) {
        Map<String, List<ItemPosition>> droppedItems = new HashMap<>();
        for (Room room : world.getRooms().values()) {
            List<ItemPosition> items = new ArrayList<>();
            for (Item item : room.getItems()) {
                Point2D position = room.getItemPosition(item);
                items.add(new ItemPosition(item, position.getX(), position.getY()));
            }
            droppedItems.put(room.getName(), items);
        }
        return droppedItems;
    }

    private void closeMenu() {
        gameGUI.getRootPane().getChildren().remove(this);
        if (!gameGUI.consoleVisible && !gameGUI.getInventoryGUI().isInventoryVisible()) {
            gameGUI.getScene().setCursor(Cursor.NONE);
        }
        gameGUI.getPlayer().setMovementEnabled(true);
        gameGUI.gameLoop.start();
    }
}