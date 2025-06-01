import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Class used to set up the controls menu
 */
public class ControlsMenu {
    /**
     * Map used to store and easily access the controls(inputs) <Action, What needs to be pressed>
     */
    private static final Map<String, String> CONTROLS = new LinkedHashMap<>();
    static {
        CONTROLS.put("Move", "WASD / Arrow Keys");
        CONTROLS.put("Sprint", "Shift");
        CONTROLS.put("Interact", "E");
        CONTROLS.put("Search Spots", "Mouse Wheel");
        CONTROLS.put("Inventory", "I");
        CONTROLS.put("Pickup Item", "F");
        CONTROLS.put("Hide", "F");
        CONTROLS.put("Toggle Console", "~");
        CONTROLS.put("Fullscreen", "F11");
        CONTROLS.put("Pause", "ESC");
    }

    /**
     * Method used to show and configure the Controls menu visual/functions
     * @param owner which window to display the menu in
     */
    public static void show(Stage owner) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(owner);
        dialog.setTitle("Controls");
        dialog.initStyle(StageStyle.UTILITY);
        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(25));
        layout.setStyle("-fx-background-color: rgba(30, 30, 40, 0.95); -fx-background-radius: 10;");
        layout.setBorder(new Border(new BorderStroke(
                Color.rgb(100, 100, 255),
                BorderStrokeStyle.SOLID,
                new CornerRadii(10),
                new BorderWidths(2)
        )));
        Label title = new Label("CONTROLS");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        title.setTextFill(Color.WHITE);
        title.setPadding(new Insets(0, 0, 15, 0));
        GridPane grid = new GridPane();
        grid.setHgap(30);
        grid.setVgap(15);
        grid.setAlignment(Pos.CENTER);
        int row = 0;
        for (Map.Entry<String, String> entry : CONTROLS.entrySet()) {
            Label action = new Label(entry.getKey() + ":");
            action.setFont(Font.font("Arial", 18));
            action.setTextFill(Color.LIGHTGRAY);
            Label key = new Label(entry.getValue());
            key.setFont(Font.font("Arial", FontWeight.BOLD, 18));
            key.setTextFill(Color.WHITE);
            grid.add(action, 0, row);
            grid.add(key, 1, row);
            row++;
        }
        Button closeBtn = new Button("Close");
        closeBtn.getStyleClass().add("menu-button");
        closeBtn.setMinWidth(120);
        closeBtn.setOnAction(e -> dialog.close());
        VBox.setMargin(closeBtn, new Insets(20, 0, 0, 0));
        layout.getChildren().addAll(title, grid, closeBtn);
        Scene scene = new Scene(layout, 450, 500);
        scene.setFill(Color.TRANSPARENT);
        dialog.setScene(scene);
        dialog.showAndWait();
    }
}