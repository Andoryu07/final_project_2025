import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class World {
    private final Map<Integer, Room> rooms = new HashMap<>();
    private Room currentRoom;
    private Player player;

    public void loadFromFile(String roomFilePath, String searchSpotFilePath) {
        loadRooms(roomFilePath);
        loadSearchSpots(searchSpotFilePath);
    }
    public World(Player player) {
        this.player = player;
    }
    public void setPlayer(Player player) {
        this.player = player;
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

                    System.out.println("Added search spot '" + spotName + "' to room: " + room.getName());
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
            case "KNIFE": return new Knife();
            case "PISTOL": return new Pistol();
            case "SHOTGUN": return new Shotgun();
            case "PISTOL_AMMO": return new PistolAmmo(10);
            case "SHOTGUN_AMMO": return new ShotgunShells(5);
            case "BANDAGE": return new Bandage();
            case "HEALING_SERUM": return new HealingSerum();
            case "CASSETTE": return new KeyItem("Cassette", "A tape for saving the game.");
            case "GEAR_PIECE_1": return new GearPiece();
            case "GEAR_PIECE_2": return new GearPiece();
            case "GEAR_PIECE_3": return new GearPiece();
            case "GEAR_PIECE_4": return new GearPiece();
            case "FLASHLIGHT": return new Flashlight();
            case "BATTERIES": return new KeyItem("Batteries", "Used to recharge the flashlight.");
            case "REPAIR_TOOL": return new KeyItem("Repair Tool", "A tool for fixing machines.");
            case "HAMMER": return new KeyItem("Hammer", "Useful for breaking or fixing things.");
            case "STALKERS_CLAW": return new KeyItem("Stalker's Claw", "A trophy from the final enemy.");
            case "CURING_SERUM": return new KeyItem("Curing Serum", "A special serum with mysterious properties.");
            case "HINT_1": return new Hint("Hint 1", "You need to wander into the basement. To do so, you need to find 4 gear pieces to unlock the door. The first one may be located somewhere outside...");
            case "HINT_2": return new Hint("Hint 2", "The second gear piece might be somewhere in the garden house.");
            case "HINT_3": return new Hint("Hint 3", "The third gear piece is probably located in the bathroom somewhere...you're gonna need a hammer.");
            case "HINT_4": return new Hint("Hint 4", "The last gear piece is located somewhere in the lower floor.");
            case "KEY_TO_DINING_ROOM_WARDROBE_2": return new KeyItem("Key to Dining Room Wardrobe 2", "A key. It seems to fit into a wardrobe in the dining room.");
            case "KEY_TO_GARDEN_HOUSE": return new KeyItem("Key to Garden House", "A key used to access the garden house.");
            default: return null;
        }
    }



    public void moveToRoom(int index) {
        if (currentRoom.getNeighbors().contains(index)) {
            currentRoom = rooms.get(index);
            player.setCurrentRoom(currentRoom);  // Update Player's current room
            System.out.println("\nMoved to the room: " + currentRoom.getName());
        } else {
            System.out.println("\nCan't move to the room with the index " + index);
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

