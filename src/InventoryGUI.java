import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Class used to create, implement, visualize and set the inventory's behavior
 */
public class InventoryGUI extends StackPane implements Inventory.InventoryObserver {
    /**
     * Size of the individual item slot
     */
    private static final int SLOT_SIZE = 100;
    /**
     * Padding for individual slots
     */
    private static final int PADDING = 40;
    /**
     * Size of the gap between individual slots
     */
    private static final int GAP_SIZE = 20;
    /**
     * Background color of the inventory
     */
    private static final Color BACKGROUND_COLOR = Color.rgb(0, 0, 0, 0.85);
    /**
     * Color of the slot
     */
    private static final Color SLOT_COLOR = Color.rgb(100, 100, 100, 0.5);
    /**
     * Color of the slot's border
     */
    private static final Color SLOT_BORDER = Color.rgb(200, 200, 200, 0.7);
    /**
     * Player instance
     */
    private final Player player;
    /**
     * Grid used for the inventory
     */
    private final GridPane grid;
    /**
     * Rectangle of the inventory
     */
    private final Rectangle bg;
    /**
     * boolean property for the inventory visibility
     */
    private final BooleanProperty inventoryVisible = new SimpleBooleanProperty(false);
    /**
     * Tooltip popup
     */
    private final Popup tooltipPopup = new Popup();
    /**
     * Action popup
     */
    private final Popup actionPopup = new Popup();
    /**
     * Which item's slot is the cursor currently located on
     */
    private Item currentHoverItem;
    /**
     * GameGUI instance
     */
    private final GameGUI gameGUI;

    /**
     * Constructor
     * @param player player instance
     * @param gameGUI GameGUI instance
     */
    public InventoryGUI(Player player, GameGUI gameGUI) {
        this.gameGUI = gameGUI;
        this.player = player;
        setInventoryVisible(false);
        player.getInventory().addObserver(this);
        // Main container
        HBox mainContent = new HBox(40);
        mainContent.setAlignment(Pos.CENTER);
        // Grid setup
        grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(GAP_SIZE);
        grid.setVgap(GAP_SIZE);
        grid.setPadding(new Insets(PADDING));
        // Background container
        StackPane gridContainer = new StackPane();
        bg = new Rectangle(700, 500);
        bg.setFill(BACKGROUND_COLOR);
        bg.setStroke(Color.WHITE);
        bg.setStrokeWidth(2);
        bg.setArcWidth(20);
        bg.setArcHeight(20);
        bg.setEffect(new DropShadow(10, Color.BLACK));
        gridContainer.getChildren().addAll(bg, grid);
        mainContent.getChildren().add(gridContainer);
        VBox tooltipContent = new VBox(5);
        tooltipContent.getStyleClass().add("tooltip-box");
        Label tooltipName = new Label();
        tooltipName.getStyleClass().add("item-name");
        Label tooltipDesc = new Label();
        tooltipDesc.getStyleClass().add("item-description");
        tooltipContent.getChildren().addAll(tooltipName, tooltipDesc);
        tooltipPopup.getContent().add(tooltipContent);
        // Action menu setup
        VBox actionContent = new VBox(8);
        actionContent.getStyleClass().add("action-box");
        actionPopup.getContent().add(actionContent);
        // Main layout
        getChildren().add(mainContent);
        setAlignment(Pos.CENTER);
        setVisible(false);
        tooltipPopup.setAutoHide(true);
        actionPopup.setAutoHide(true);
        tooltipPopup.setAutoFix(true);
        actionPopup.setAutoFix(true);
        // Event handling
        setOnMouseMoved(e -> {
            if (tooltipPopup.isShowing() && currentHoverItem != null) {
                updateTooltipPosition(e.getScreenX() + 15, e.getScreenY() + 15);
            }
        });
        // Close menu when clicking outside
        setOnMouseClicked(e -> {
            if (!actionPopup.getContent().get(0).getBoundsInParent().contains(e.getX(), e.getY())) {
                actionPopup.hide();
            }
        });
        this.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.windowProperty().addListener((obsWin, oldWin, newWin) -> {
                    if (newWin != null) {
                        newWin.focusedProperty().addListener((obsFocused, wasFocused, isNowFocused) -> {
                            if (!isNowFocused) {
                                Platform.runLater(this::hideAllPopups);
                            }
                        });
                    }
                });
            }
        });
    }

    /**
     * Method used to update the inventory's display
     */
    private void updateDisplay() {
        grid.getChildren().clear();
        int capacity = player.getInventory().getCapacity();
        int cols = (int) Math.ceil(Math.sqrt(capacity));
        for (int i = 0; i < capacity; i++) {
            StackPane slot = createSlot();
            if (i < player.getInventory().getItems().size()) {
                Item item = player.getInventory().getItems().get(i);
                addItemToSlot(slot, item);
            }
            grid.add(slot, i % cols, i / cols);
        }
    }

    /**
     * Method used to update the inventory
     */
    @Override
    public void inventoryUpdated() {
        Platform.runLater(() -> {
            updateDisplay();
            // Flash the inventory if it's visible
            if (isInventoryVisible()) {
                animateInventoryUpdate();
            }
        });
    }

    /**
     * Method used to create an animation for the inventory changes
     */
    private void animateInventoryUpdate() {
        ScaleTransition st = new ScaleTransition(Duration.millis(100), grid);
        st.setFromX(1.0);
        st.setFromY(1.0);
        st.setToX(1.05);
        st.setToY(1.05);
        st.setAutoReverse(true);
        st.setCycleCount(2);
        st.play();
    }

    /**
     * Method used to add an item into the inventory slot, including its image
     * @param slot which slot to put the item into
     * @param item which item is supposed to be put into the slot
     */
    private void addItemToSlot(StackPane slot, Item item) {
        ImageView iv = loadItemImage(item);
        if (iv != null) {
            slot.setOnMouseEntered(e -> showTooltip(item, e.getScreenX(), e.getScreenY()));
            slot.setOnMouseExited(e -> hideTooltip());
            slot.setOnMouseClicked(e -> {
                if (e.getButton() == MouseButton.PRIMARY) {
                    showActionMenu(item, e.getScreenX(), e.getScreenY());
                    e.consume();
                }
            });
            slot.getChildren().add(iv);
        }
    }

    /**
     * Method used to get the tooltip(hover effect) of the individual slots
     * @param item which item is in the hovered over slot
     * @param x x coordinate of the cursor
     * @param y y coordinate of the cursor
     */
    private void showTooltip(Item item, double x, double y) {
        currentHoverItem = item;
        VBox tooltip = (VBox) tooltipPopup.getContent().get(0);
        ((Label) tooltip.getChildren().get(0)).setText(item.getName());
        ((Label) tooltip.getChildren().get(1)).setText(item.getDescription());
        tooltipPopup.show(getScene().getWindow());
        updateTooltipPosition(x + 15, y + 15);
    }

    /**
     * Method used to update the tooltip's position
     * @param x what to set the tooltip's x coordinate to
     * @param y what to set the tooltip's y coordinate to
     */
    private void updateTooltipPosition(double x, double y) {
        tooltipPopup.setX(x);
        tooltipPopup.setY(y);
    }

    /**
     * Method used to hide the tooltip
     */
    private void hideTooltip() {
        currentHoverItem = null;
        tooltipPopup.hide();
    }

    /**
     * Method used to show the action menu(clicking on an item slot)
     * @param item which item slot did the player click on
     * @param screenX x coordinate on the screen
     * @param screenY y coordinate on the screen
     */
    private void showActionMenu(Item item, double screenX, double screenY) {
        Stage ownerStage = (Stage) getScene().getWindow();
        actionPopup.show(ownerStage);
        VBox actionBox = (VBox) actionPopup.getContent().get(0);
        actionBox.getChildren().clear();
        Point2D windowCoord = new Point2D(
                ownerStage.getX() + screenX,
                ownerStage.getY() + screenY
        );
        // Create buttons
        Button actionBtn = createActionButton(item);
        Button infoBtn = createInfoButton(item);
        Button dropBtn = createDropButton(item);
        actionBox.getChildren().addAll(actionBtn, infoBtn, dropBtn);
        actionPopup.show(getScene().getWindow());
        actionPopup.setX(ownerStage.getX() + screenX + 15);
        actionPopup.setY(ownerStage.getY() + screenY + 15);
        ownerStage.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (!isNowFocused) {
                actionPopup.hide();
            }
        });
    }

    /**
     * Method used to dropping an item, removing it from the inventory, etc.
     * @param item which item is the player attempting to drop
     */
    private void handleDropItem(Item item) {
        World world = gameGUI.getWorld();
        if (world != null) {
            Room currentRoom = world.getCurrentRoom();
            if (currentRoom != null) {
                double x = player.getX();
                double y = player.getY();
                System.out.println("[DROP] Dropping " + item.getName() +
                        " at " + x + "," + y + " in " + currentRoom.getName());
                currentRoom.addItem(item, x, y);
                player.getInventory().removeItem(item);
                System.out.println("[DROP] Room items after drop: " + currentRoom.getItems());
            }
        }
    }

    /**
     * Method used to create and style the Drop button
     * @param item which item to create the button for
     * @return the Drop button created
     */
    private Button createDropButton(Item item) {
        Button btn = new Button("Drop");
        if (item instanceof Knife) {
            btn.setDisable(true);
        }
        btn.getStyleClass().add("inventory-button");
        btn.setOnAction(e -> {
            handleDropItem(item);
            actionPopup.hide();
        });
        return btn;
    }

    /**
     * Method used to create and style the Equip button
     * @param item which item to create the button for
     * @return the Equip button created
     */
    private Button createActionButton(Item item) {
        Button btn = new Button();
        if (item instanceof Weapon) {
            btn.setText(player.getEquippedWeapon() == item ? "Unequip" : "Equip");
        } else {
            btn.setText("Use");
            boolean isUsable = item instanceof Consumable || item instanceof KeyItem;
            btn.setDisable(!isUsable);
        }
        btn.getStyleClass().add("inventory-button");
        btn.setOnAction(e -> {
            handleItemAction(item);
            actionPopup.hide();
        });
        return btn;
    }
    /**
     * Method used to create and style the Info button
     * @param item which item to create the button for
     * @return the Info button created
     */
    private Button createInfoButton(Item item) {
        Button btn = new Button("Info");
        btn.getStyleClass().add("inventory-button");
        btn.setOnAction(e -> showItemDescription(item));
        return btn;
    }

    /**
     * Method used to show the item's description
     * @param item which item's description to show
     */
    private void showItemDescription(Item item) {
        String info = String.format("[%s]\n%s\n\n%s",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")),
                item.getName(),
                item.getDescription()
        );
        gameGUI.addConsoleMessage(info);
        hideAllPopups();
        // Auto-show console if hidden
        if (!gameGUI.consoleVisible) {
            gameGUI.toggleConsole();
        }
        if (isInventoryVisible()) {
            toggle();
        }
    }

    /**
     * Method used to 'use' the item (Equip/Use button)
     * @param item which item is the player attempting to use
     */
    private void handleItemAction(Item item) {
        if (item instanceof Weapon) {
            Weapon weapon = (Weapon) item;
            if (player.getEquippedWeapon() == weapon) {
                player.equipWeapon(null);
            } else {
                player.equipWeapon(weapon);
            }
        } else if (item instanceof Consumable || item instanceof KeyItem) {
            item.use(player);
            if (item instanceof Consumable) {
                player.getInventory().removeItem(item);
            }
        }
        updateDisplay();
    }

    /**
     * Method used to toggle the inventory's visibility
     */
    public void toggle() {
        boolean newVisibility = !isInventoryVisible();
        setInventoryVisible(newVisibility);
        if (newVisibility) {
            // When opening inventory
            setMouseTransparent(false);
            updateInventorySize();
            setVisible(true);
            gameGUI.getScene().setCursor(Cursor.DEFAULT);
            gameGUI.getPlayer().setMovementEnabled(false);
            gameGUI.gameLoop.stop();
        } else {
            hideAllPopups();
            setMouseTransparent(true);
            setVisible(false);
            if (!gameGUI.consoleVisible) {
                gameGUI.getScene().setCursor(Cursor.NONE);
                gameGUI.getPlayer().setMovementEnabled(true);
                gameGUI.gameLoop.start();
            }
        }
        updateDisplay();
    }

    /**
     * Method used to update the inventory's grid size based on the inventory's capacity
     */
    private void updateInventorySize() {
        int capacity = player.getInventory().getCapacity();
        int cols = (int) Math.ceil(Math.sqrt(capacity));
        int rows = (int) Math.ceil((double) capacity / cols);

        double contentWidth = cols * (SLOT_SIZE + GAP_SIZE) + 2 * PADDING;
        double contentHeight = rows * (SLOT_SIZE + GAP_SIZE) + 2 * PADDING;

        double maxWidth = getScene().getWidth() * 0.8;
        double maxHeight = getScene().getHeight() * 0.8;

        bg.setWidth(Math.min(contentWidth, maxWidth));
        bg.setHeight(Math.min(contentHeight, maxHeight));
    }

    /**
     * Method used to create the slots
     * @return the slot created
     */
    private StackPane createSlot() {
        StackPane slot = new StackPane();
        slot.setPrefSize(SLOT_SIZE, SLOT_SIZE);

        Rectangle slotBg = new Rectangle(SLOT_SIZE - 2, SLOT_SIZE - 2);
        slotBg.setFill(SLOT_COLOR);
        slotBg.setStroke(SLOT_BORDER);
        slotBg.setStrokeWidth(1.5);
        slotBg.setArcWidth(10);
        slotBg.setArcHeight(10);

        slot.getChildren().add(slotBg);
        return slot;
    }

    /**
     * Method used to load the item's image from a file
     * @param item which item's image are we attempting to load
     * @return the image of the item
     */
    private ImageView loadItemImage(Item item) {
        try {
            String path = "/sprites/items/" +
                    item.getName().toUpperCase().replace(" ", "_") + ".png";

            InputStream stream = getClass().getResourceAsStream(path);
            if (stream == null) {
                throw new RuntimeException("Missing item image: " + path);
            }

            Image img = new Image(stream);
            ImageView iv = new ImageView(img);
            iv.setOnMouseEntered(e -> {

            });

            iv.setOnMouseExited(e -> {

            });
            iv.setFitWidth(64);
            iv.setFitHeight(64);
            return iv;
        } catch (Exception e) {
            System.err.println("Error loading item image: " + e.getMessage());
            return null;
        }
    }

    /**
     * Method used to determine whether the inventory is visible
     * @return is the inventory visible?
     */
    public boolean isInventoryVisible() {
        return inventoryVisible.get();
    }

    /**
     * Method used to hide all the popups concerning the inventory
     */
    public void hideAllPopups() {
        Platform.runLater(() -> {
            if (tooltipPopup != null) tooltipPopup.hide();
            if (actionPopup != null) actionPopup.hide();
        });
    }

    /**
     * Setter for inventoryVisible
     * @param value what to set the value of 'inventoryVisible' to
     */
    private void setInventoryVisible(boolean value) {
        inventoryVisible.set(value);
    }
}