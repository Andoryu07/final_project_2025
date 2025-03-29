import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class World {
    private final Map<Integer, Room> rooms = new HashMap<>();
    private Room currentRoom;
    private Player player;
    private GearLock gearLock;
    private final Room laboratory;

    public void loadFromFile(String roomFilePath, String searchSpotFilePath) {
        loadRooms(roomFilePath);
        loadSearchSpots(searchSpotFilePath);
    }

    public World(Player player) {
        this.player = player;
        laboratory = new Room("Laboratory",true);




    }
    public void initializeLocks() {
        // Celery requires charged flashlight
        Room celery = findRoomByName("Celery");
        celery.setLock(new Lock("Flashlight",
                "\nThe doorway to the cellar is pitch black. You'd have no way to defend yourself against potential enemies!",
                "\nYou switch on your flashlight, its beam cutting through the darkness...",false) {
            @Override
            public boolean attemptUnlock(Player player, Scanner scanner) {
                String choice = showUnlockPrompt(player, scanner);
                if (choice == null) return false;  // Player doesn't have item
                if (choice.equals("1")) {
                    Item flashlight = player.findItemInInventory("Flashlight");
                    if (!((Flashlight)flashlight).isCharged()) {
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
        Room diningRoom = findRoomByName("Living_Room");
        diningRoom.addSearchSpotLock("Wardrobe_2", new Lock(
                "Key to Dining Room Wardrobe 2",
                "The wardrobe is securely locked.",
                "The key fits perfectly in the lock...",true) {
//            @Override
//            public boolean attemptUnlock(Player player, Scanner scanner) {
//                return handleStandardUnlock(player,scanner);
//            }
        });

        // Garden requires knife
        Room garden = findRoomByName("Garden");
        garden.setLock(new Lock(
                "Knife",
                "The garden door is taped shut from the outside.",
                "You cut through the tape with your knife, unlocking the door, entering the garden...",false) {
//            @Override
//            public boolean attemptUnlock(Player player, Scanner scanner) {
//               return handleStandardUnlock(player,scanner);
//            }
        });

        // Garden House requires key
        Room gardenHouse = findRoomByName("Garden_House");
        gardenHouse.setLock(new Lock(
                "Key to Garden House",
                "The garden house is securely locked.",
                "The key turns smoothly in the lock...",true) {
//            @Override
//            public boolean attemptUnlock(Player player, Scanner scanner) {
//                return handleStandardUnlock(player,scanner);
//            }
        });

        // Wall safe requires hammer
        Room bathroom = findRoomByName("Bathroom");
        bathroom.addSearchSpotLock("Wall_safe", new Lock(
                "Hammer",
                "The safe is sealed behind a wooden panel.",
                "You smash through the panel with your hammer, revealing a hidden wall safe...",true) {
//            @Override
//            public boolean attemptUnlock(Player player, Scanner scanner) {
//                return handleStandardUnlock(player,scanner);
//            }
        });
    }
    public void setPlayer(Player player) {
        this.player = player;
    }

    public void initializeGearLock() {
        Room cellar = findRoomByName("Celery");
        Room laboratory = findRoomByName("Laboratory");

        if (cellar != null && laboratory != null) {
            gearLock = new GearLock(cellar, laboratory);
            cellar.setGearLock(gearLock);

        } else {
            System.err.println("Error: Could not initialize GearLock, rooms not found.");
        }
    }

    public void insertGearPiece(String gearName, Player player) {
        System.out.println("Current room: " + player.getCurrentRoom().getName());
        if (player.getCurrentRoom().getName().equalsIgnoreCase("Celery")) {
            if (gearLock != null) {
                gearLock.insertGear(gearName, player);
            } else {
                System.out.println("The gear mechanism seems broken...");
            }
        } else {
            System.out.println("There is no gear mechanism here.");
        }
    }


    private Room findRoomByName(String name) {
        return rooms.values().stream().filter(room -> room.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

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

    private void loadSearchSpots(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isEmpty()) continue;  // Skip empty lines

                String[] parts = line.split(" ", 3);
                if (parts.length < 3) continue;  // If line format is invalid, skip it

                try {
                    int roomIndex = Integer.parseInt(parts[0]);
                    Room room = rooms.get(roomIndex);  // Get the room based on the index
                    if (room == null) {
                        System.err.println("Warning: Room with index " + roomIndex + " not found!");
                        continue;  // Skip if the room doesn't exist
                    }

                    String spotName = parts[1];  // Spot name
                    String[] items = parts[2].split(",");  // Split items by comma

                    List<Item> hiddenItems = new ArrayList<>();
                    for (String itemName : items) {
                        if (!itemName.equalsIgnoreCase("Empty")) {
                            Item item = createItem(itemName.trim());  // Create item
                            if (item != null) {
                                hiddenItems.add(item);  // Add valid item to the list
                            }
                        }
                    }

                    // Add the search spot to the room
                    SearchSpot spot = new SearchSpot(spotName, hiddenItems);
                    room.addSearchSpot(spot);

                } catch (NumberFormatException e) {
                    System.err.println("Error parsing room index: " + parts[0]);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading search spots: " + e.getMessage());
        }
    }


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
                return new KeyItem("Cassette", "A tape for saving the game.");
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
                return new KeyItem("Batteries", "Used to recharge the flashlight.");
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


    public void moveToRoom(int index,Scanner scanner) {
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

        // Special check for Laboratory
        if (nextRoom.getName().equalsIgnoreCase("Laboratory") &&
                (gearLock == null || !gearLock.isUnlocked())) {
            System.out.println("\nThe door to the Laboratory is locked. You need to insert all 4 gear pieces in the Celery first.");
            return;
        }

        if (nextRoom.isLocked()) {
            System.out.println("\nThis room is locked. Find a way to unlock it.");
            return;
        }

        currentRoom = nextRoom;
        player.setCurrentRoom(currentRoom);
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
            ((Stalker)stalker).moveCloser();
            System.out.println("[DEBUG] Stalker distance: " + ((Stalker)stalker).distanceFromPlayer);

            if (((Stalker)stalker).isInCombatRange()) {
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

//        // Check for Secret Chamber
//        if (currentRoom.getName().equalsIgnoreCase("Secret_Chamber")) {
//            System.out.println("\n******************************************************************");
//            System.out.println("* You've found your sister! The nightmare is finally over...      *");
//            System.out.println("*                                                                *");
//            System.out.println("*                      T H E    E N D                            *");
//            System.out.println("******************************************************************");
//            System.exit(0); // Exit the game
//        }
    }


    private void startCombat(Enemy enemy) {
        new CombatSystem(player, enemy).startCombat();
    }
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
    private List<Enemy> getEnemiesInRoom(Room room) {
        return room.getCharacters().stream()
                .filter(c -> c instanceof Enemy)
                .map(c -> (Enemy)c)
                .toList();
    }

    public void initializeEnemies() {
        // Final boss
        if (rooms.containsKey(11)) {
            rooms.get(11).addCharacter(new StalkerFinalBattle(rooms.get(11)));
        }

        // Zombies
        if (rooms.containsKey(7)) { // Garden House
            for (int i = 0; i < 5; i++) {
                rooms.get(7).addCharacter(new Zombie(rooms.get(7)));
            }
        }

        if (rooms.containsKey(10)) { // Celery
            for (int i = 0; i < 5; i++) {
                rooms.get(10).addCharacter(new Zombie(rooms.get(10)));
            }
        }

        // Stalker
        if (rooms.containsKey(0)) { // Enter Hall
            rooms.get(0).addCharacter(new Stalker(rooms.get(0)));
        }
    }
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

    public Room getCurrentRoom() {
        return currentRoom;
    }
}

