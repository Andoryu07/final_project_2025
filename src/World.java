import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class World {

    private final Map<Integer, Room> rooms = new HashMap<>();
    private Room currentRoom;

    public void loadFromFile(String filePath) {
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

            //Sets spawn location
            currentRoom = rooms.get(0);

        } catch (IOException e) {
            System.err.println("Error loading the file: " + e.getMessage());
        }
    }

    public void moveToRoom(int index) {
        if (currentRoom.getNeighbors().contains(index)) {
            currentRoom = rooms.get(index);
            System.out.println("Moved to room: " + currentRoom.getName());
        } else {
            System.out.println("You can't move to the room with the index " + index);
        }
    }

    public void printCurrentRoom() {
        System.out.println("You're located in the room: " + currentRoom.getName());
        System.out.println("Adjacent rooms:");

        for (int neighborIndex : currentRoom.getNeighbors()) {
            Room neighbor = rooms.get(neighborIndex);
            if (neighbor != null) {
                System.out.println(" - " + neighbor.getName());
            }
        }
    }

}

