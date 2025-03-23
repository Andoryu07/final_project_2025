import java.util.ArrayList;
import java.util.List;

//represents the rooms of the game
public class Room {
    private final int index;// numeral id of the room
    private final String name;// room name
    private final List<Integer> neighbors;// List of the indexes of neighbor rooms
    private List<Item> items;
    private List<Character> characters;
    private List<SearchSpot> searchSpots;//Arraylist used to store already searched spots
    public Room(int index, String name, List<Integer> neighbors) {
        this.index = index;
        this.name = name;
        this.neighbors = neighbors;
        this.items = new ArrayList<>();
        this.characters = new ArrayList<>();
        this.searchSpots = new ArrayList<>();
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

    public List<Item> getItems() {
        return items;
    }

    public List<Character> getCharacters() {
        return characters;
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
    public void addItem(Item item) {
        items.add(item);
        System.out.println("Item " + item.getName() + " has been added to the room: " + name);
    }
    public void addSearchSpot(SearchSpot spot) {
        searchSpots.add(spot);
    }

    public List<SearchSpot> getUnsearchedSpots() {
        List<SearchSpot> unsearched = new ArrayList<>();
        for (SearchSpot spot : searchSpots) {
            if (!spot.isSearched()) {
                unsearched.add(spot);
            }
        }
        return unsearched;
    }

    public SearchSpot getSearchSpot(int index) {
        return (index >= 0 && index < searchSpots.size()) ? searchSpots.get(index) : null;
    }
}


