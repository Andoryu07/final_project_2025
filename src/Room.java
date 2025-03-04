import java.util.List;
//represents the rooms of the game
public class Room {
    private final int index;// numeral id of the room
    private final String name;// room name
    private final List<Integer> neighbors;// List of the indexes of neighbor rooms

    public Room(int index, String name, List<Integer> neighbors) {
        this.index = index;
        this.name = name;
        this.neighbors = neighbors;
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
}

