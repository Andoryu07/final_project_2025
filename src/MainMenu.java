import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Class used to create, style and set the initial main menu of the game
 */
public class MainMenu extends Application {
    /**
     * Which stage the main menu appears in
     */
    private Stage primaryStage;
    /**
     * Used to track the most recent save
     */
    private File latestSave;

    /**
     * Method used to 'start' the main menu sequence
     * @param primaryStage which stage to open the main menu in
     */
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

    /**
     * Method used to create and set the main menu behavior and appearance
     * @return the main menu contents in a VBox
     */
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

    /**
     * Method used to create the menu button, based on the text provided
     * @param text the text of the button created
     * @return the button created
     */
    private Button createMenuButton(String text) {
        Button btn = new Button(text);
        btn.getStyleClass().add("menu-button");
        btn.setMinWidth(250);
        btn.setMinHeight(50);
        return btn;
    }

    /**
     * Method used to start a new game
     */
    private void startNewGame() {
        // Clear existing saves and start new game
        GameGUI game = new GameGUI();
        Stage gameStage = new Stage();
        game.start(gameStage);
        primaryStage.close();
    }

    /**
     * Method used to implement the 'Continue latest save' button's feature
     */
    private void continueLatestSave() {
        if (latestSave != null) {
            GameGUI game = new GameGUI();
            Stage gameStage = new Stage();
            game.loadGame(latestSave);
            game.start(gameStage);
            primaryStage.close();
        }
    }

    /**
     * Method used to implement the 'Controls' button's feature
     */
    private void showControls() {
        ControlsMenu.show(primaryStage);
    }

    /**
     * Method used to implement the 'Load' button's feature
     */
    private void showLoadMenu() {
        LoadMenuGUI.show(primaryStage, this::handleSaveSelected);
    }

    /**
     * Method used to load the selected save file from the load menu
     * @param saveFile which save file is supposed to be loaded
     */
    private void handleSaveSelected(File saveFile) {
        if (saveFile != null) {
            GameGUI game = new GameGUI();
            Stage gameStage = new Stage();
            game.loadGame(saveFile);
            game.start(gameStage);
            primaryStage.close();
        }
    }

    /**
     * Method used to run the main menu
     * @param args args
     */
    public static void main(String[] args) {
        launch(args);
    }
}