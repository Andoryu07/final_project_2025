import java.util.ArrayList;
import java.util.List;
//represents the rooms of the game
public class Room {
    private final int index;// numeral id of the room
    private final String name;// room name
    private final List<Integer> neighbors;// List of the indexes of neighbor rooms
    private List<Item> items;
    public Room(int index, String name, List<Integer> neighbors) {
        this.index = index;
        this.name = name;
        this.neighbors = neighbors;
        this.items = new ArrayList<>();
    }

    public int getIndex() {
        return index;
    }

    public String getName() {
        return name;
    }

    public List<Integer> getNeighbors() {
        return neighbors;
    }

    @Override
    public String toString() {
        return "Room{" +
                "index=" + index +
                ", name='" + name + '\'' +
                ", neighbors=" + neighbors +
                '}';
    }
    public void removeItem(Item item) {
        if (items.remove(item)) {
            System.out.println("Item " + item.getName() + " has been removed from the room: " + name);
        } else {
            System.out.println("Item  " + item.getName() + " is not located in this room.");
        }
    }
}

