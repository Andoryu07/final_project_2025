import java.io.*;
import java.util.*;

/**
 * Class used to create and implement item Cassette's behavior, it's use and values
 */
public class Cassette extends Item {
    /**
     * List of rooms, where the player can use the Cassette, in order to save the game
     */
    private static final List<String> SAVE_LOCATIONS = Arrays.asList("Caravan", "Laboratory", "Library");
    /**
     * String, specifying where the save files would be saved to
     */
    private static final String SAVE_FOLDER = "saves/";
    /**
     * An int value, specifying which save slot will the game get saved to, upon using the Cassette
     */
    private static int saveSlotCounter = 1;
    /**
     * Instance of the World class, used to access its methods and fields
     */
    private final World world;

    /**
     * Constructor, contains a super from the Item class, and a boolean, to check whether the directory was created
     * @param world Specifies, which World to create the Cassette in
     */
    public Cassette(World world) {
        super("Cassette", "A cassette. Can be used to save the game in specific locations.");
        this.world = world;
        new File(SAVE_FOLDER).mkdirs();//boolean to check whether the directory exists
    }

    /**
     * Override method, used to specify, what happens upon using the Cassette(If the player is located in one of the save locations, he will get a menu, if not, he will be warned about his misuse of the item)
     * @param player Specifies, which player attempted to use the item
     */
    @Override
    public void use(Player player) {
        Room currentRoom = player.getCurrentRoom();
        if (SAVE_LOCATIONS.contains(currentRoom.getName())) {
            showSaveMenu(player);
        } else {
            System.out.println("❌ You can only save the game in Caravan, Laboratory, or Library.");
        }
    }

    /**
     * A menu, which occurs if the player is located in a save location, allowing him the choice to save, or cancel his action
     * @param player Specifies, which player is using the Cassette
     */
    private void showSaveMenu(Player player) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\n💾 Cassette Player Menu:");
        System.out.println("1. Save game");
        System.out.println("2. Walk away");
        System.out.print("Choose an option: ");

        String choice = scanner.nextLine();
        if (choice.equals("1")) {
            player.getInventory().removeItem(this);
            saveGame(player);
        } else {
            System.out.println("You walk away from the cassette player.");
        }
    }

    /**
     * Method used to save the game into a save folder, writing the objects specified in GameState into a file
     * @param player Used to specify which player is saving the game
     */
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

    /**
     * Creates a GameState instance and sets all of its fields' values to the ones of the game
     * @param player Used to specify which player's data to set into the GameState
     * @return the GameState instance
     */
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

        // Get world state
        state.setInsertedGears(world.getGearLock().getInsertedGears());
        state.setLockStates(world.getAllLockStates());
        Map<String, List<String>> searchedSpotsMap = getSearchedSpotsMap();
        state.setSearchedSpotsPerRoom(searchedSpotsMap);

        // Get stalker distance
        state.setStalkerDistance(world.getStalkerDistance());
        return state;
    }

    /**
     * Extracted method used to create map of rooms and their searched spots
     * @return map of searched spots in individual rooms
     */
    private Map<String, List<String>> getSearchedSpotsMap() {
        Map<String, List<String>> searchedSpotsMap = new HashMap<>();
        for (Room room : world.getRooms().values()) {
            List<String> searchedSpotNames = new ArrayList<>();
            for (SearchSpot spot : room.getSearchSpots()) {
                if (spot.isSearched()) {
                    searchedSpotNames.add(spot.getName());
                }
            }
            if (!searchedSpotNames.isEmpty()) {
                searchedSpotsMap.put(room.getName(), searchedSpotNames);
            }
        }
        return searchedSpotsMap;
    }
}