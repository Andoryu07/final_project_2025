import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
//Configures the world, loads rooms and allows the player's movement
public class World {

    private final Map<Integer, Room> rooms = new HashMap<>();//Saves rooms based on their index
    private Room currentRoom;//current room, in which the player is right now
    //Method for loading the rooms from the file
    public void loadFromFile(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                //Splits the rows based on spaces
                String[] parts = line.split(" ");
                int index = Integer.parseInt(parts[0]);//Rooms' indexes
                String name = parts[1];
                List<Integer> neighbors = new ArrayList<>();
                //Loading the neighbor rooms
                for (int i = 2; i < parts.length; i++) {
                    neighbors.add(Integer.parseInt(parts[i]));
                }
                //Puts the rooms into the map
                rooms.put(index, new Room(index, name, neighbors));
            }

            // Sets spawn loaction
            currentRoom = rooms.get(0);

        } catch (IOException e) {
            System.err.println("Error loading the file: " + e.getMessage());
        }
    }
    //Method used to move to a different location based on index given
    public void moveToRoom(int index) {
        if (currentRoom.getNeighbors().contains(index)) {
            currentRoom = rooms.get(index);
            System.out.println("\nMoved to the room: " + currentRoom.getName());
        } else {
            System.out.println("\nCan't move to the room with the index " + index);
        }
    }
    //Method used to print the current room and available rooms to travel to
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

