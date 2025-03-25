import java.util.ArrayList;
import java.util.List;

//represents the rooms of the game
public class Room {
    private  int index;// numeral id of the room
    private final String name;// room name
    public boolean isLocked;
    private List<Integer> neighbors;// List of the indexes of neighbor rooms
    private List<Item> items;
    private List<Character> characters;
    private List<SearchSpot> searchSpots;//Arraylist used to store already searched spots
    private GearLock gearLock;

    public void setGearLock(GearLock gearLock) {
        this.gearLock = gearLock;
    }

    public Room(String name, boolean isLocked) {
        this.name = name;
        this.isLocked = isLocked;
    }
    public Room(int index, String name, List<Integer> neighbors) {
        this.index = index;
        this.name = name;
        this.neighbors = neighbors;
        this.items = new ArrayList<>();
        this.characters = new ArrayList<>();
        this.searchSpots = new ArrayList<>();

    }

    public boolean isLocked() {
        return isLocked;
    }

    public void unlock() {
        this.isLocked = false;
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
        }
    }
    public void addItem(Item item) {
        if (!items.contains(item)) {
            items.add(item);
            System.out.println("Item " + item.getName() + " has been added to the room: " + name);
        } else {
            System.out.println("The item " + item.getName() + " is already in the room.");
        }
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

        System.out.println("Unsearched spots in room " + this.getName() + ": " + unsearched.size());
        return unsearched;
    }


    public SearchSpot getSearchSpot(int index) {
        return (index >= 0 && index < searchSpots.size()) ? searchSpots.get(index) : null;
    }
}


