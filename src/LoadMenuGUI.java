import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.function.Consumer;

public class LoadMenuGUI {
    private static final String SAVE_FOLDER = "saves/";

    public static void show(Stage owner, Consumer<File> saveSelectedHandler) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(owner);
        dialog.setTitle("Load Game");
        dialog.initStyle(StageStyle.UTILITY);

        VBox mainLayout = new VBox(20);
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setPadding(new Insets(25));
        mainLayout.setStyle("-fx-background-color: rgba(30, 30, 40, 0.95); -fx-background-radius: 10;");
        mainLayout.setBorder(new Border(new BorderStroke(
                Color.rgb(100, 100, 255),
                BorderStrokeStyle.SOLID,
                new CornerRadii(10),
                new BorderWidths(2)
        )));

        Label title = new Label("SAVED GAMES");
        title.setFont(javafx.scene.text.Font.font("Arial", FontWeight.BOLD, 28));
        title.setTextFill(Color.WHITE);

        VBox savesContainer = new VBox(15);
        savesContainer.setAlignment(Pos.CENTER);
        savesContainer.setPadding(new Insets(10));

        // List save files
        File saveDir = new File(SAVE_FOLDER);
        File[] saveFiles = saveDir.listFiles((dir, name) -> name.endsWith(".dat"));

        if (saveFiles != null && saveFiles.length > 0) {
            Arrays.sort(saveFiles, Comparator.comparingLong(File::lastModified).reversed());

            for (File saveFile : saveFiles) {
                SaveEntry entry = new SaveEntry(saveFile);
                entry.setOnAction(e -> confirmLoad(saveFile, dialog, saveSelectedHandler));
                savesContainer.getChildren().add(entry);
            }
        } else {
            Label noSaves = new Label("No saved games found");
            noSaves.setFont(Font.font(18));
            noSaves.setTextFill(Color.LIGHTGRAY);
            savesContainer.getChildren().add(noSaves);
        }

        ScrollPane scrollPane = new ScrollPane(savesContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scrollPane.setMinHeight(300);
        scrollPane.setMaxHeight(300);

        Button backBtn = new Button("Back");
        backBtn.getStyleClass().add("menu-button");
        backBtn.setMinWidth(120);
        backBtn.setOnAction(e -> dialog.close());

        mainLayout.getChildren().addAll(title, scrollPane, backBtn);

        Scene scene = new Scene(mainLayout, 500, 500);
        scene.setFill(Color.TRANSPARENT);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    private static void confirmLoad(File saveFile, Stage dialog, Consumer<File> saveSelectedHandler) {
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirm Load");
        confirmDialog.setHeaderText("Load this save?");
        confirmDialog.setContentText(saveFile.getName());
        confirmDialog.initStyle(StageStyle.UTILITY);

        confirmDialog.showAndWait().ifPresent(response -> {
            if (response == javafx.scene.control.ButtonType.OK) {
                saveSelectedHandler.accept(saveFile);
                dialog.close();
            }
        });
    }

    private static class SaveEntry extends Button {
        public SaveEntry(File saveFile) {
            super(); // Call the Button constructor
            getStyleClass().add("menu-button");
            setStyle("-fx-background-color: rgba(50, 50, 70, 0.8);");
            setMinWidth(350);
            setAlignment(Pos.CENTER_LEFT);

            // Create a container for the text
            VBox textContainer = new VBox(5);
            textContainer.setAlignment(Pos.CENTER_LEFT);

            // Filename label
            Label nameLabel = new Label(saveFile.getName());
            nameLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");

            // Metadata label
            Label metaLabel = new Label(SaveHelper.getSaveMetadata(saveFile));
            metaLabel.setStyle("-fx-text-fill: #aaa; -fx-font-size: 12;");

            textContainer.getChildren().addAll(nameLabel, metaLabel);

            // Set the graphic instead of text
            setGraphic(textContainer);
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        }
    }
}