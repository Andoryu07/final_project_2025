
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.*;

/**
 * Class used to implement the world, its layout, methods, fields, behavior, etc.
 */
public class World implements Serializable {
    /**
     * Used for serialization
     */
    private static final long serialVersionUID = 1L;
    /**
     * Map containing all the rooms(index,room)
     */
    private final Map<Integer, Room> rooms = new HashMap<>();
    /**
     * Current room
     */
    private Room currentRoom;
    /**
     * Player instance
     */
    private Player player;
    /**
     * Gear lock instance
     */
    private GearLock gearLock;
    /**
     * laboratory room instance
     */
    private final Room laboratory;
    /**
     * Game instance
     */
    private final Game game;
    private Map<Integer, Map<String, List<Item>>> searchSpotItems;
    /**
     * Method used to load the rooms from the file
     *
     * @param roomFilePath       file path of the file containing the room layouts
     * @param searchSpotFilePath file path of the file containing the search spot layouts
     */
    public void loadFromFile(String roomFilePath, String searchSpotFilePath) {
        loadRooms(roomFilePath);
        loadSearchSpots(searchSpotFilePath);
    }

    /**
     * Constructor, initializes Laboratory room
     * @param player Player
     * @param game Game
     */
    public World(Player player, Game game) {
        this.player = player;
        this.game = game;
        laboratory = new Room("Laboratory", true);
        this.searchSpotItems = new HashMap<>();
    }

    /**
     * Method used to initialize the locks/limitations, and setting their unlocking conditions
     */
    public void initializeLocks() {
        // Cellar requires charged flashlight
        Room Cellar = findRoomByName("Cellar");
        Cellar.setLock(new Lock("Flashlight",
                "\nThe doorway to the cellar is pitch black. You'd have no way to defend yourself against potential enemies!",
                "\nYou switch on your flashlight, its beam cutting through the darkness...", false) {
            @Override
            public boolean attemptUnlock(Player player, Scanner scanner) {
                String choice = showUnlockPrompt(player, scanner);
                if (choice == null) return false;  // Player doesn't have item
                if (choice.equals("1")) {
                    Item flashlight = player.findItemInInventory("Flashlight");
                    if (!((Flashlight) flashlight).isCharged()) {
                        System.out.println("Your flashlight batteries are dead!");
                        return false;
                    }
                    setLocked(false);
                    System.out.println(getUnlockPrompt());
                    return true;
                }
                return false;
            }
        });
        // Wardrobe_2 requires key
        Room diningRoom = findRoomByName("Dining_Room");
        diningRoom.addSearchSpotLock("Wardrobe_2", new Lock(
                "Key to Dining Room Wardrobe 2",
                "The wardrobe is securely locked.",
                "The key fits perfectly in the lock...", true) {
        });

        // Garden requires knife
        Room garden = findRoomByName("Garden");
        garden.setLock(new Lock(
                "Knife",
                "The garden door is taped shut from the outside.",
                "You cut through the tape with your knife, unlocking the door, entering the garden...", false) {
        });

        // Garden House requires key
        Room gardenHouse = findRoomByName("Garden_House");
        gardenHouse.setLock(new Lock(
                "Key to Garden House",
                "The garden house is securely locked.",
                "The key turns smoothly in the lock...", true) {
        });

        // Wall safe requires hammer
        Room bathroom = findRoomByName("Bathroom");
        bathroom.addSearchSpotLock("Wall_safe", new Lock(
                "Hammer",
                "The safe is sealed behind a wooden panel.",
                "You smash through the panel with your hammer, revealing a hidden wall safe...", true) {
        });
    }

    /**
     * Setter for 'player'
     *
     * @param player Sets the value of 'player'
     */
    public void setPlayer(Player player) {
        this.player = player;
    }

    /**
     * Method used to initialize the Gear lock/mechanism in Cellar
     */
    public void initializeGearLock() {
        Room cellar = findRoomByName("Cellar");
        Room laboratory = findRoomByName("Laboratory");

        if (cellar != null && laboratory != null) {
            gearLock = new GearLock(cellar, laboratory);
            cellar.setGearLock(gearLock);

        } else {
            System.err.println("Error: Could not initialize GearLock, rooms not found.");
        }
    }

    /**
     * Method used to insert a gear piece into the gear lock
     *
     * @param gearName Name of the gear piece which the player is attempting to insert
     * @param player   who is trying to insert the gear piece
     */
    public void insertGearPiece(String gearName, Player player) {
        System.out.println("Current room: " + player.getCurrentRoom().getName());
        if (player.getCurrentRoom().getName().equalsIgnoreCase("Cellar")) {
            if (gearLock != null) {
                gearLock.insertGear(gearName, player);
            } else {
                System.out.println("The gear mechanism seems broken...");
            }
        } else {
            System.out.println("There is no gear mechanism here.");
        }
    }

    /**
     * Method used to find a room based on the input given
     *
     * @param name name of the room we're looking for
     * @return room based on the name given, null if no such room had been found
     */
    public Room findRoomByName(String name) {
        return rooms.values().stream().filter(room -> room.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    /**
     * Method used to load rooms into the game from the file
     *
     * @param filePath filepath of the file, from which we're going to load the rooms
     */
    private void loadRooms(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" ");
                int index = Integer.parseInt(parts[0]);
                String name = parts[1];
                List<Integer> neighbors = new ArrayList<>();
                for (int i = 2; i < parts.length; i++) {
                    neighbors.add(Integer.parseInt(parts[i]));
                }
                rooms.put(index, new Room(index, name, neighbors));
            }
            currentRoom = rooms.get(0);
        } catch (IOException e) {
            System.err.println("Error loading rooms: " + e.getMessage());
        }
    }

    /**
     * Method used to load the search spots from a file
     *
     * @param filePath of the file we're going to load the spots from
     */
    private void loadSearchSpots(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" ", 3);
                int roomIndex = Integer.parseInt(parts[0]);
                String spotName = parts[1];
                String[] itemNames = parts[2].split(",");

                List<Item> items = new ArrayList<>();
                for (String itemName : itemNames) {
                    if (!itemName.equalsIgnoreCase("Empty")) {
                        Item item = createItem(itemName.trim());
                        if (item != null) items.add(item);
                    }
                }
                searchSpotItems.computeIfAbsent(roomIndex, k -> new HashMap<>())
                        .put(spotName, items);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public List<Item> getSearchSpotItems(int roomIndex, String spotName) {
        return searchSpotItems.getOrDefault(roomIndex, new HashMap<>())
                .getOrDefault(spotName, new ArrayList<>());
    }
    public void loadSearchSpotsFromTMJ(Room room, JSONObject tmjData) {
        try {
            JSONArray layers = tmjData.getJSONArray("layers");
            for (int i = 0; i < layers.length(); i++) {
                JSONObject layer = layers.getJSONObject(i);
                if ("SearchSpots".equals(layer.getString("name"))) {
                    JSONArray objects = layer.getJSONArray("objects");
                    for (int j = 0; j < objects.length(); j++) {
                        JSONObject obj = objects.getJSONObject(j);
                        String name = obj.getString("name");
                        // Store coordinates in pixels as they come from Tiled
                        double x = obj.getDouble("x");
                        double y = obj.getDouble("y");
                        double width = obj.getDouble("width");
                        double height = obj.getDouble("height");

                        List<Item> items = getSearchSpotItems(room.getIndex(), name);
                        SearchSpot spot = new SearchSpot(name, items, x, y, width, height);
                        room.addSearchSpot(spot);
                        // Debug output
                        System.out.printf("Loaded search spot: %s at (%.1f, %.1f) size (%.1f, %.1f)%n",
                                name, x, y, width, height);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading search spots for room " + room.getName());
            e.printStackTrace();
        }
    }

    /**
     * Creates the items included in a file
     *
     * @param itemName name of the item
     * @return the item based on the name given, null if no such item had been found
     */
    private Item createItem(String itemName) {
        switch (itemName.toUpperCase()) {
            case "KNIFE":
                return new Knife();
            case "PISTOL":
                return new Pistol();
            case "SHOTGUN":
                return new Shotgun();
            case "PISTOL_AMMO":
                return new PistolAmmo(10);
            case "SHOTGUN_AMMO":
                return new ShotgunShells(5);
            case "BANDAGE":
                return new Bandage();
            case "HEALING_SERUM":
                return new HealingSerum();
            case "CASSETTE":
                return new Cassette(this);
            case "GEAR_PIECE_1":
                return new GearPiece("GEAR_PIECE_1");
            case "GEAR_PIECE_2":
                return new GearPiece("GEAR_PIECE_2");
            case "GEAR_PIECE_3":
                return new GearPiece("GEAR_PIECE_3");
            case "GEAR_PIECE_4":
                return new GearPiece("GEAR_PIECE_4");
            case "FLASHLIGHT":
                return new Flashlight();
            case "BATTERIES":
                return new Batteries();
            case "REPAIR_TOOL":
                return new KeyItem("Repair Tool", "A tool for fixing machines.");
            case "HAMMER":
                return new KeyItem("Hammer", "Useful for breaking or fixing things.");
            case "STALKERS_CLAW":
                return new KeyItem("Stalker's Claw", "A trophy from the final enemy.");
            case "CURING_SERUM":
                return new KeyItem("Curing Serum", "A special serum with mysterious properties.");
            case "HINT_1":
                return new Hint("Hint 1", "You need to wander into the basement. To do so, you need to find 4 gear pieces to unlock the door. The first one may be located somewhere outside...");
            case "HINT_2":
                return new Hint("Hint 2", "The second gear piece might be somewhere in the garden house.");
            case "HINT_3":
                return new Hint("Hint 3", "The third gear piece is probably located in the bathroom somewhere...you're gonna need a hammer.");
            case "HINT_4":
                return new Hint("Hint 4", "The last gear piece is located somewhere in the lower floor.");
            case "KEY_TO_DINING_ROOM_WARDROBE_2":
                return new KeyItem("Key to Dining Room Wardrobe 2", "A key. It seems to fit into a wardrobe in the dining room.");
            case "KEY_TO_GARDEN_HOUSE":
                return new KeyItem("Key to Garden House", "A key used to access the garden house.");
            default:
                return null;
        }
    }

    /**
     * Method used to move the player into by him desired room, if possible
     *
     * @param index   index of the room player wants to move into
     * @param scanner scanner used to register player's input
     */
    public void moveToRoom(int index, Scanner scanner) {
        Room nextRoom = rooms.get(index);

        if (nextRoom == null) {
            System.out.println("\nInvalid index. This room does not exist.");
            return;
        }
        if (nextRoom.getLock() != null && nextRoom.getLock().isLocked()) {
            if (!nextRoom.canAccess(player, scanner)) {
                return;
            }
        }
        if (!currentRoom.getNeighbors().contains(index)) {
            System.out.println("\nInvalid move. You can't go there directly.");
            return;
        }
        // Special case for Cellar
        if (nextRoom.getName().equalsIgnoreCase("Cellar")) {
            Flashlight flashlight = (Flashlight) player.findItemInInventory("Flashlight");
            if (flashlight == null || !flashlight.isCharged()) {
                System.out.println("You need a working flashlight to enter the cellar!");
                return;
            }

            CellarStealthSystem stealth = new CellarStealthSystem(player, game::loadCheckpoint);
            stealth.startSequence(scanner);
            return;
        }

        if (nextRoom.isLocked()) {
            System.out.println("\nThis room is locked. Find a way to unlock it.");
            return;
        }

        currentRoom = nextRoom;
        System.out.println("\nMoved to the room: " + currentRoom.getName());
        // Check for Stalker movement after regular movement
        if (currentRoom.getName().equals("Secret_Chamber") &&
                findEnemyByName("StalkerFinalBattle") != null) {
            startCombat(findEnemyByName("StalkerFinalBattle"));
            return;
        }
        // Handle regular Stalker movement
        Enemy stalker = findEnemyByName("Stalker");
        if (stalker != null && player.hasItem("Knife")) {
            ((Stalker) stalker).moveCloser();
            System.out.println("[DEBUG] Stalker distance: " + ((Stalker) stalker).distanceFromPlayer);

            if (((Stalker) stalker).isInCombatRange()) {
                System.out.println("The Stalker has caught up to you!");
                startCombat(stalker);
            }
        }


        // Check for zombies in current room
        List<Enemy> enemiesInRoom = getEnemiesInRoom(currentRoom);
        if (!enemiesInRoom.isEmpty()) {
            // Check if any of the enemies are zombies
            Optional<Enemy> zombie = enemiesInRoom.stream()
                    .filter(e -> e instanceof Zombie)
                    .findFirst();

            if (zombie.isPresent()) {
                startCombat(zombie.get());
            }
        }
    }

    /**
     * Method used to start combat between player and an enemy
     *
     * @param enemy which enemy is the player going to fight against
     */
    private void startCombat(Enemy enemy) {
        new CombatSystem(player, enemy, new Scanner(System.in), game::loadCheckpoint).startCombat();
    }

    /**
     * Method used to find an enemy by his name
     *
     * @param name name of the enemy we're looking for
     * @return the enemy, or null if no such enemy had been found
     */
    private Enemy findEnemyByName(String name) {
        for (Room room : rooms.values()) {
            for (Character character : room.getCharacters()) {
                if (character instanceof Enemy && character.getName().equalsIgnoreCase(name)) {
                    return (Enemy) character;
                }
            }
        }
        return null;
    }

    /**
     * Searches the room for any characters of the enemy type located in a certain room
     *
     * @param room the room we're going to search
     * @return list of enemies located in the room given
     */
    private List<Enemy> getEnemiesInRoom(Room room) {
        return room.getCharacters().stream()
                .filter(c -> c instanceof Enemy)
                .map(c -> (Enemy) c)
                .toList();
    }

    /**
     * Method used to get the stalker's distance from player
     *
     * @return distance of stalker from player, -1 if Stalker instance doesn't exist
     */
    public int getStalkerDistance() {
        Enemy stalker = findEnemyByName("Stalker");
        if (stalker instanceof Stalker) {
            return ((Stalker) stalker).distanceFromPlayer;
        }
        return -1; //
    }

    /**
     * Method used to set the stalker's distance
     *
     * @param distance what are we going to set the distance to
     */
    public void setStalkerDistance(int distance) {
        Enemy stalker = findEnemyByName("Stalker");
        if (stalker instanceof Stalker) {
            ((Stalker) stalker).distanceFromPlayer = distance;
        }
    }

    /**
     * Method used to initialize enemies
     */
    public void initializeEnemies() {
        // Final boss
        if (rooms.containsKey(11)) {
            Room room = rooms.get(11);
            room.addCharacter(new StalkerFinalBattle( this, room.getName()));
        }

        // Zombies
        if (rooms.containsKey(7)) {
            Room room = rooms.get(7);
            for (int i = 0; i < 5; i++) {
                room.addCharacter(new Zombie(this, room.getName()));
            }
        }

        // Stalker
        if (rooms.containsKey(0)) {
            Room room = rooms.get(0);
            room.addCharacter(new Stalker(this, room.getName()));
        }
    }

    /**
     * Method used to print the current room and it's neighbors
     */
    public void printCurrentRoom() {
        System.out.println("\n🔹 You're currently at the room: " + currentRoom.getName());
        System.out.println("🔽 Choose where to travel next:");
        for (int neighborIndex : currentRoom.getNeighbors()) {
            Room neighbor = rooms.get(neighborIndex);
            if (neighbor != null) {
                System.out.println("   [" + neighborIndex + "] " + neighbor.getName());
            }
        }
    }

    /**
     * Getter for 'currentRoom'
     *
     * @return value of 'currentRoom'
     */
    public Room getCurrentRoom() {
        return currentRoom;
    }
    public void setCurrentRoom(Room room) {
        this.currentRoom = room;
        if (player != null) {
            player.setCurrentRoom(room);
        }
    }

    /**
     * Method used to check and give states of all locks, containing room, search spot and gear locks
     *
     * @return map of all the locks and their unlock states (Name,unlockedState)
     */
    public Map<String, Boolean> getAllLockStates() {
        Map<String, Boolean> lockStates = new HashMap<>();

        // Room locks
        for (Room room : rooms.values()) {
            if (room.getLock() != null) {
                lockStates.put(room.getName() + "_room", room.getLock().isLocked());
            }
        }

        // Search spot locks
        for (Room room : rooms.values()) {
            for (Map.Entry<String, Lock> entry : room.getSearchSpotLocks().entrySet()) {
                lockStates.put(room.getName() + "_" + entry.getKey(), entry.getValue().isLocked());
            }
        }

        // Gear lock
        if (gearLock != null) {
            lockStates.put("gear_lock", gearLock.isUnlocked());
        }

        return lockStates;
    }

    /**
     * Method used to efficiently lock up all the locks
     *
     * @param lockStates which locks are we going to lock up
     */
    public void restoreLockStates(Map<String, Boolean> lockStates) {
        // Room locks
        for (Room room : rooms.values()) {
            if (room.getLock() != null) {
                Boolean isLocked = lockStates.get(room.getName() + "_room");
                if (isLocked != null) {
                    room.getLock().setLocked(isLocked);
                }
            }
        }

        // Search spot locks
        for (Room room : rooms.values()) {
            for (Map.Entry<String, Lock> entry : room.getSearchSpotLocks().entrySet()) {
                Boolean isLocked = lockStates.get(room.getName() + "_" + entry.getKey());
                if (isLocked != null) {
                    entry.getValue().setLocked(isLocked);
                }
            }
        }

        // Gear lock
        if (gearLock != null) {
            Boolean isUnlocked = lockStates.get("gear_lock");
            if (isUnlocked != null && isUnlocked) {
                gearLock.unlockDoor();
            }
        }
    }

    /**
     * Getter for 'gearLock'
     *
     * @return value of 'gearLock'
     */
    public GearLock getGearLock() {
        return gearLock;
    }

    /**
     * Getter for player
     * @return value of player
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Getter for map 'rooms'
     *
     * @return map 'rooms
     */
    public Map<Integer, Room> getRooms() {
        return rooms;
    }
    public Room getRoomByIndex(int index) {
        return rooms.get(index);
    }

    public Game getGame() {
        return game;
    }
    public Room getRoomByName(String name) {
        return rooms.values().stream()
                .filter(r -> r.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }
    public void addRoom(Room room) {
        // Find the next available index
        int nextIndex = rooms.keySet().stream()
                .max(Integer::compare)
                .orElse(-1) + 1;
        rooms.put(nextIndex, room);
    }
}

