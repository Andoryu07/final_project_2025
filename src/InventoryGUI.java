import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Popup;

import java.io.InputStream;

public class InventoryGUI extends StackPane implements Inventory.InventoryObserver {
    private static final int SLOT_SIZE = 100;
    private static final int PADDING = 40;
    private static final int GAP_SIZE = 20;
    private static final Color BACKGROUND_COLOR = Color.rgb(0, 0, 0, 0.85);
    private static final Color SLOT_COLOR = Color.rgb(100, 100, 100, 0.5);
    private static final Color SLOT_BORDER = Color.rgb(200, 200, 200, 0.7);
    private final Player player;
    private final GridPane grid;
    private final Rectangle bg;
    private final BooleanProperty inventoryVisible = new SimpleBooleanProperty(false);
    private final Popup tooltipPopup = new Popup();
    private final Popup actionPopup = new Popup();
    private Item currentHoverItem;
    public InventoryGUI(Player player) {
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
        // Toltip setup
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
    }

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

    private void addItemToSlot(StackPane slot, Item item) {
        ImageView iv = loadItemImage(item);
        if (iv != null) {
            // Hover handling
            slot.setOnMouseEntered(e -> showTooltip(item, e.getScreenX(), e.getScreenY()));
            slot.setOnMouseExited(e -> hideTooltip());

            // Click handling
            slot.setOnMouseClicked(e -> {
                if (e.getButton() == MouseButton.PRIMARY) {
                    showActionMenu(item, e.getScreenX(), e.getScreenY());
                    e.consume();
                }
            });
            slot.getChildren().add(iv);
        }
    }
    private void showTooltip(Item item, double x, double y) {
        currentHoverItem = item;
        VBox tooltip = (VBox) tooltipPopup.getContent().get(0);
        ((Label) tooltip.getChildren().get(0)).setText(item.getName());
        ((Label) tooltip.getChildren().get(1)).setText(item.getDescription());
        tooltipPopup.show(getScene().getWindow());
        updateTooltipPosition(x + 15, y + 15);
    }


    private void updateTooltipPosition(double x, double y) {
        tooltipPopup.setX(x);
        tooltipPopup.setY(y);
    }

    private void hideTooltip() {
        currentHoverItem = null;
        tooltipPopup.hide();
    }


    private void showActionMenu(Item item, double screenX, double screenY) {
        VBox actionBox = (VBox) actionPopup.getContent().get(0);
        actionBox.getChildren().clear();

        // Create buttons
        Button actionBtn = createActionButton(item);
        Button infoBtn = createInfoButton(item);

        actionBox.getChildren().addAll(actionBtn, infoBtn);
        actionPopup.show(getScene().getWindow());
        actionPopup.setX(screenX + 15);
        actionPopup.setY(screenY + 15);
    }

    private Button createActionButton(Item item) {
        Button btn = new Button(item instanceof Weapon ? "Equip" : "Use");
        btn.getStyleClass().add("inventory-button");
        btn.setOnAction(e -> {
            handleItemAction(item);
            actionPopup.hide();
        });
        return btn;
    }

    private Button createInfoButton(Item item) {
        Button btn = new Button("Info");
        btn.getStyleClass().add("inventory-button");
        btn.setOnAction(e -> showItemDescription(item));
        return btn;
    }

    private void showItemDescription(Item item) {
        // Optional: Implement description display logic
        System.out.println("Item Info: " + item.getDescription());
    }

    private void handleItemAction(Item item) {
        if (item instanceof Weapon) {
            player.equipWeapon((Weapon) item);
        } else {
            item.use(player);
            if (item instanceof Consumable) {
                player.getInventory().removeItem(item);
            }
        }
        updateDisplay();
    }


    @Override
    public void inventoryUpdated() {
        Platform.runLater(this::updateDisplay);
//        if (isInventoryVisible()) updateDisplay();
    }

    public void toggle() {
        setInventoryVisible(!isInventoryVisible());
        if (isInventoryVisible()) {
            // Update size when showing inventory
            updateInventorySize();
            setMouseTransparent(false);

        } else {
            setMouseTransparent(true);
        }
        setVisible(isInventoryVisible());
        updateDisplay();
    }


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

    public boolean isInventoryVisible() {
        return inventoryVisible.get();
    }

    private void setInventoryVisible(boolean value) {
        inventoryVisible.set(value);
    }
}