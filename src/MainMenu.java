import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

public class MainMenu extends Application {
    private Stage primaryStage;
    private File latestSave;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("The Game");
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);

        // Find latest save
        File saveDir = new File("saves/");
        File[] saveFiles = saveDir.listFiles((dir, name) -> name.endsWith(".dat"));
        if (saveFiles != null && saveFiles.length > 0) {
            Arrays.sort(saveFiles, Comparator.comparingLong(File::lastModified).reversed());
            latestSave = saveFiles[0];
        }

        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #1a1a1a, #0d0d0d);");
        VBox menuLayout = createMainMenu();
        StackPane.setAlignment(menuLayout, Pos.CENTER);

        root.getChildren().add(menuLayout);

        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().add(getClass().getResource("/menu-styles.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private VBox createMainMenu() {
        VBox layout = new VBox(30);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(50));
        layout.setBackground(Background.EMPTY);

        // Title
        Label title = new Label("THE GAME");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 60));
        title.setTextFill(Color.WHITE);
        title.setEffect(new javafx.scene.effect.DropShadow(10, Color.GOLD));

        // Buttons
        Button newGameBtn = createMenuButton("New Game");
        Button continueBtn = createMenuButton("Continue Latest Save");
        Button controlsBtn = createMenuButton("Controls");
        Button loadBtn = createMenuButton("Load");
        Button exitBtn = createMenuButton("Exit");

        // Disable continue if no save exists
        continueBtn.setDisable(latestSave == null);

        // Button Actions
        newGameBtn.setOnAction(e -> startNewGame());
        continueBtn.setOnAction(e -> continueLatestSave());
        controlsBtn.setOnAction(e -> showControls());
        loadBtn.setOnAction(e -> showLoadMenu());
        exitBtn.setOnAction(e -> primaryStage.close());

        layout.getChildren().addAll(title, newGameBtn, continueBtn, controlsBtn, loadBtn, exitBtn);
        return layout;
    }

    private Button createMenuButton(String text) {
        Button btn = new Button(text);
        btn.getStyleClass().add("menu-button");
        btn.setMinWidth(250);
        btn.setMinHeight(50);
        return btn;
    }

    private void startNewGame() {
        // Clear existing saves and start new game
        GameGUI game = new GameGUI();
        Stage gameStage = new Stage();
        game.start(gameStage);
        primaryStage.close();
    }

    private void continueLatestSave() {
        if (latestSave != null) {
            GameGUI game = new GameGUI();
            Stage gameStage = new Stage();
            game.loadGame(latestSave);
            game.start(gameStage);
            primaryStage.close();
        }
    }

    private void showControls() {
        ControlsMenu.show(primaryStage);
    }

    private void showLoadMenu() {
        LoadMenuGUI.show(primaryStage, this::handleSaveSelected);
    }

    private void handleSaveSelected(File saveFile) {
        if (saveFile != null) {
            GameGUI game = new GameGUI();
            Stage gameStage = new Stage();
            game.loadGame(saveFile);
            game.start(gameStage);
            primaryStage.close();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}