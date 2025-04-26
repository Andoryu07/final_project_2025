import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.*;

/**
 * Class used to store the game main logic and important methods
 */
public class Game {
    /**
     * Instance of World class
     */
    private World world;
    /**
     * Instance of Player class
     */
    private Player player;
    /**
     * Scanner
     */
    private Scanner scanner;
    /**
     * CommandFactory instance
     */
    private CommandFactory commandFactory;

    /**
     * Constructor, contains the initialization of locks, gear locks, enemies, player,etc.
     */
    public Game() {
        this.world = new World(null,this);
        this.scanner = new Scanner(System.in);
        world.loadFromFile("src/FileImports/game_layout.txt", "src/FileImports/search_spots.txt");
        this.player = new Player("Ethan", 100, world,world.getRooms().get(0).getName());
        world.setPlayer(player);  // Set the player in the world
        player.setWorld(world);
        commandFactory = new CommandFactory(world, player, scanner);
        world.initializeLocks();
        world.initializeGearLock();
        world.initializeEnemies();
        // Check for saved games
        File saveDir = new File("saves/");
        if (saveDir.exists() && saveDir.listFiles((dir, name) -> name.startsWith("save_")).length > 0) {
            showLoadMenu();
        } else {
            initializeNewGame();
        }
    }

    /**
     * Method used to start a new game
     */
    private void initializeNewGame() {
        world.loadFromFile("src/FileImports/game_layout.txt", "src/FileImports/search_spots.txt");
        this.player = new Player("Ethan", 100, world,world.getRooms().get(0).getName());
        player.setWorld(world);
        world.setPlayer(player);
        world.initializeLocks();
        world.initializeGearLock();
        world.initializeEnemies();

    }

    /**
     * Method used to show the load menu, after the player had previously made saves, and attempts to run the program again
     */
    private void showLoadMenu() {
        System.out.println("╔════════════════════════════╗");
        System.out.println("║       SAVED GAMES         ║");
        System.out.println("╠════════════════════════════╣");

        File[] saveFiles = new File("saves/").listFiles((dir, name) -> name.startsWith("save_"));
        Arrays.sort(saveFiles, Comparator.comparingLong(File::lastModified).reversed());

        for (int i = 0; i < saveFiles.length; i++) {
            System.out.printf("║ %d. %-20s ║%n", i+1, saveFiles[i].getName());
        }
        System.out.println("║ " + (saveFiles.length+1) + ". New Game            ║");
        System.out.println("╚════════════════════════════╝");
        System.out.print("Choose an option: ");

        try {
            int choice = Integer.parseInt(scanner.nextLine());
            if (choice > 0 && choice <= saveFiles.length) {
                loadGame(saveFiles[choice-1]);
            } else if (choice == saveFiles.length+1) {
                initializeNewGame();
            } else {
                System.out.println("Invalid choice. Starting new game.");
                initializeNewGame();
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Starting new game.");
            initializeNewGame();
        }
    }

    /**
     * Method used to load the data saved in GameState, when choosing to load a previous save
     * @param saveFile Specifies, which saveFile is trying to be load
     */
    private void loadGame(File saveFile) {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(saveFile))) {
            GameState state = (GameState) in.readObject();
            applyGameState(state);
            System.out.println("✅ Game loaded successfully!");
        } catch (Exception e) {
            System.out.println("❌ Failed to load game: " + e.getMessage());
            initializeNewGame();
        }
    }

    /**
     * Method used to load checkpoint upon dying
     */
    public void loadCheckpoint() {
        File[] saveFiles = new File("saves/").listFiles((dir, name) -> name.startsWith("save_"));
        if (saveFiles == null || saveFiles.length == 0) {
            System.out.println("No save files found! Starting new game.");
            initializeNewGame();
            return;
        }
        Arrays.sort(saveFiles, Comparator.comparingLong(File::lastModified).reversed());
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(saveFiles[0]))) {
            GameState state = (GameState) in.readObject();
            applyGameState(state);
            System.out.println("✅ Checkpoint loaded successfully!");
        } catch (Exception e) {
            System.out.println("❌ Failed to load checkpoint: " + e.getMessage());
            initializeNewGame();
        }
    }
    /**
     * Applies the game's stats(saved in game state) to the loaded game, used when loading the game in 'loadGame' method
     * @param state used to access GameState class
     */
    public void applyGameState(GameState state) {
        // Restore player state
        player.setHealth(state.getPlayerHealth());
        player.getInventory().clear();
        for (Item item : state.getInventory()) {
            player.getInventory().addItem(item);
        }
        Weapon weapon = state.getEquippedWeapon();
        if (weapon != null) {
            player.equipWeapon(weapon);
        } else {
            player.equipWeapon(null);
        }

        // Restore room
        Room targetRoom = world.findRoomByName(state.getCurrentRoomName());
        if (targetRoom != null) {
            player.setCurrentRoom(targetRoom);

        }

        // Restore flashlight
        Flashlight flashlight = (Flashlight) player.findItemInInventory("Flashlight");
        if (flashlight != null) {
            flashlight.setBatteryLevel(state.getFlashlightBattery());
            flashlight.setInCelery(state.isFlashlightInCelery());
        }

        // Restore combat state
        player.setFighting(state.isPlayerFighting());
        player.setBlocking(state.isPlayerBlocking());

        // Restore world state
        world.getGearLock().setInsertedGears(state.getInsertedGears());
        world.restoreLockStates(state.getLockStates());
        Map<String, List<String>> searchedSpotsMap = state.getSearchedSpotsPerRoom();
        if (searchedSpotsMap != null) {
            for (Map.Entry<String, List<String>> entry : searchedSpotsMap.entrySet()) {
                Room room = world.findRoomByName(entry.getKey());
                if (room != null) {
                    for (SearchSpot spot : room.getSearchSpots()) {
                        if (entry.getValue().contains(spot.getName())) {
                            spot.markAsSearched();//Marks the spot as searched
                        }
                    }
                }
            }
        }

        // Restore stalker
       world.setStalkerDistance(state.getStalkerDistance());
    }

    /**
     * Starts the game loop, registering player's inputs
     */
    public void start() {
        boolean running = true;
        while (running) {
            world.printCurrentRoom();
            System.out.print("\nEnter command: ");
            String input = scanner.nextLine();
            running = processInput(input);
        }
        scanner.close();
    }

    /**
     * Method used to process player's commands
     * @param input Player's console input
     * @return boolean value on whether the game shall continue or not
     */
    private boolean processInput(String input) {
        try {
            Command command = commandFactory.createCommand(input);
            if (command instanceof ExitCommand) {
                command.execute();
                return false;
            }
            command.execute();
            return true;
        } catch (CommandException e) {
            System.out.println(e.getMessage());
            return true;
        }
    }


}
