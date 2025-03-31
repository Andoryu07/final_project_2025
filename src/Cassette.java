import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Cassette extends Item {
    private static final List<String> SAVE_LOCATIONS = Arrays.asList("Caravan", "Laboratory", "Library");
    private static final String SAVE_FOLDER = "saves/";
    private static int saveSlotCounter = 1;
    private final World world;

    public Cassette(World world) {
        super("Cassette", "A cassette. Can be used to save the game in specific locations.");
        this.world = world;
        new File(SAVE_FOLDER).mkdirs();//boolean to check whether the directory exists
    }

    @Override
    public void use(Player player) {
        Room currentRoom = player.getCurrentRoom();
        if (SAVE_LOCATIONS.contains(currentRoom.getName())) {
            showSaveMenu(player);
        } else {
            System.out.println("❌ You can only save the game in Caravan, Laboratory, or Library.");
        }
    }

    private void showSaveMenu(Player player) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\n💾 Cassette Player Menu:");
        System.out.println("1. Save game");
        System.out.println("2. Walk away");
        System.out.print("Choose an option: ");

        String choice = scanner.nextLine();
        if (choice.equals("1")) {
            saveGame(player);
            player.getInventory().removeItem(this);
        } else {
            System.out.println("You walk away from the cassette player.");
        }
    }

    private void saveGame(Player player) {
        try {
            String filename = SAVE_FOLDER + "save_" + saveSlotCounter + ".dat";
            GameState state = createGameState(player);

            try (ObjectOutputStream out = new ObjectOutputStream(
                    new FileOutputStream(filename))) {
                out.writeObject(state);
                System.out.println("✅ Game saved successfully in slot " + saveSlotCounter);
                saveSlotCounter++;
            }
        } catch (IOException e) {
            System.out.println("❌ Failed to save game: " + e.getMessage());
        }
    }

    private GameState createGameState(Player player) {
        GameState state = new GameState();
        // Set all the game state properties
        state.setPlayerHealth(player.getHealth());
        state.setEquippedWeapon(player.getEquippedWeapon());
        state.setInventory(player.getInventory().getItems());
        state.setCurrentRoomName(player.getCurrentRoom().getName());

        // Get flashlight state
        Flashlight flashlight = (Flashlight) player.findItemInInventory("Flashlight");
        if (flashlight != null) {
            state.setFlashlightBattery(flashlight.getBatteryLevel());
            state.setFlashlightInCelery(flashlight.getIsInCelery());
        }

        // Get combat state
        state.setPlayerFighting(player.isFighting());
        state.setPlayerBlocking(player.isBlocking());

        // Get world state (you'll need to add methods to World class)
        state.setInsertedGears(world.getGearLock().getInsertedGears());
        state.setLockStates(world.getAllLockStates());

        // Get stalker distance
        state.setStalkerDistance(world.getStalkerDistance());
        return state;
    }
}