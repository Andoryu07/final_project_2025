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
     * Constructor, contains the initialization of locks, gear locks, enemies, player,etc.
     */
    public Game() {
        this.world = new World(null);  // Create the world first (with no player initially)
        this.scanner = new Scanner(System.in);
        world.loadFromFile("src/FileImports/game_layout.txt", "src/FileImports/search_spots.txt");

        this.player = new Player("Ethan", 100, world.getCurrentRoom());  // Now create the player with the world’s starting room
        world.setPlayer(player);  // Set the player in the world
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
        this.player = new Player("Ethan", 100, world.getCurrentRoom());
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
     * Applies the game's stats(saved in game state) to the loaded game, used when loading the game in 'loadGame' method
     * @param state
     */
    private void applyGameState(GameState state) {
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
            world.setCurrentRoom(targetRoom);
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
     * Method used to create and process player's commands
     * @param input Player's console input
     * @return boolean value on whether the game shall continue or not
     */
    private boolean processInput(String input) {
        String[] parts = input.split(" ", 2);
        String commandName = parts[0].toLowerCase();
        String argument = (parts.length > 1) ? parts[1] : null;

        switch (commandName) {
            case "insert":
                if (argument != null) {
                    world.insertGearPiece(argument.toUpperCase(), player);
                } else {
                    System.out.println("Specify which gear piece to insert.");
                }
                break;
            case "go":
                if (argument != null) {
                    try {
                        int roomIndex = Integer.parseInt(argument);
                        new MoveCommand(world, roomIndex,scanner).execute();
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid room number.");
                    }
                } else {
                    System.out.println("Specify a room index.");
                }
                break;
            case "equip":
                if (argument != null) {
                    Item item = player.findItemInInventory(argument);
                    if (item instanceof Weapon) {
                        player.equipWeapon((Weapon) item);
                    } else {
                        System.out.println("That's not a weapon!");
                    }
                } else {
                    System.out.println("Specify a weapon to equip");
                }
                break;
            case "take":
                if (argument != null) {
                    Item item = findItemInRoom(argument);
                    if (item != null) {
                        new TakeCommand(player, item).execute();
                    } else {
                        System.out.println("Item not found in this room.");
                    }
                } else {
                    System.out.println("Specify an item to take.");
                }
                break;
            case "drop":
                if (argument != null) {
                    Item item = player.findItemInInventory(argument); // Updated this line
                    if (item != null) {
                        new DropCommand(player, item).execute();
                    } else {
                        System.out.println("You don't have this item.");
                    }
                } else {
                    System.out.println("Specify an item to drop.");
                }
                break;
            case "use":
                if (argument != null) {
                    Item item = player.findItemInInventory(argument); // Updated this line
                    if (item != null) {
                        new UseCommand(player, item).execute();
                    } else {
                        System.out.println("You don't have this item.");
                    }
                } else {
                    System.out.println("Specify an item to use.");
                }
                break;
            case "examine":
                if (argument != null) {
                    Item item = player.findItemInInventory(argument); // Updated this line
                    if (item != null) {
                        new ExamineCommand(player, item).execute();
                    } else {
                        System.out.println("You don't have this item.");
                    }
                } else {
                    System.out.println("Specify an item to examine.");
                }
                break;
            case "inventory":
                new InventoryCommand(player).execute();
                break;
            case "help":
                new HelpCommand().execute();
                break;
            case "exit":
                System.out.println("Exiting game...");
                return false;
            case "search":
                new SearchCommand(player).execute();
                break;
            default:
                System.out.println("Unknown command. Type 'help' for a list of commands.");
        }
        return true;
    }

    /**
     * Method used to try and locate an item the player had attempted to pick up, using the 'take' command
     * @param itemName Name of the item we're trying to locate
     * @return the item if found, null if not found
     */
    private Item findItemInRoom(String itemName) {
        for (Item item : world.getCurrentRoom().getItems()) {
            if (item.getName().equalsIgnoreCase(itemName)) {
                return item;
            }
        }
        return null;
    }

}
