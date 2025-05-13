import javafx.geometry.Point2D;

import java.io.Serializable;
import java.util.*;

/**
 * Class used to implement rooms' behavior, values, fields and their methods
 */
public class Room implements Serializable {
    /**
     * Used for serialization
     */
    private static final long serialVersionUID = 1L;
    /**
     * Numeral index of the room, loaded from file
     */
    private  int index;// numeral id of the room
    /**
     * Name of the room, loaded from file
     */
    private final String name;// room name
    /**
     * Boolean indicating, whether the room is locked or not
     */
    public boolean isLocked;
    /**
     * List of neighbors of a room(Which rooms are adjacent to a certain room/player can move to), loaded from file
     */
    private List<Integer> neighbors;// List of the indexes of neighbor rooms
    /**
     * List of items in the certain room
     */
    private List<Item> items;
    /**
     * Characters located in the certain room
     */
    private List<Character> characters;
    /**
     * Search spots located in the certain room
     */
    private List<SearchSpot> searchSpots;//Arraylist used to store already searched spots
    /**
     * GearLock instance
     */
    private GearLock gearLock;
    /**
     * Lock instance
     */
    private Lock roomLock;
    /**
     * Map containing locks on search spots
     */
    private Map<String,Lock> searchSpotLocks = new HashMap<>();

    /**
     * Setter for gearLock
     * @param gearLock sets the value of gearLock
     */
    public void setGearLock(GearLock gearLock) {
        this.gearLock = gearLock;
    }

    /**
     * Setter for roomLock
     * @param lock sets the value of roomLock
     */
    public void setLock(Lock lock) {
        this.roomLock = lock;
    }

    /**
     * Adds a search spot into the room
     * @param spotName name of the search spot
     * @param lock decides whether the added search spot has a lock
     */
    public void addSearchSpotLock(String spotName, Lock lock) {
        searchSpotLocks.put(spotName, lock);
    }

    /**
     * Method used to determine, whether the player can access the room
     * @param player Who is trying to access the room
     * @param scanner Scanner
     * @return boolean true/false based on whether the room is/n't null, locked, or the player tried to unlock the room lock
     */
    public boolean canAccess(Player player, Scanner scanner) {
        return roomLock == null || !roomLock.isLocked() ||
                roomLock.attemptUnlock(player, scanner);
    }
    private Map<Item, Point2D> itemPositions = new HashMap<>();
    /**
     * Method used to determine, whether you can search a spot in the room
     * @param spotName name of the search spot player wants to search
     * @param player Who is trying to search the spot
     * @param scanner Scanner
     * @return true/false based on whether player can search the spot or not
     */
    public boolean canSearchSpot(String spotName, Player player, Scanner scanner) {
        Lock lock = searchSpotLocks.get(spotName);
        if (lock == null) return true; // No lock - always accessible

        if (lock.isLocked()) {
            System.out.println("\nThis spot is locked!");
            boolean unlocked = lock.attemptUnlock(player, scanner);
            if (!unlocked) {
                System.out.println("The spot remains locked.");
            }
            return unlocked;
        }
        return true; // Already unlocked
    }

    /**
     * Getter for 'roomLock
     * @return value of 'roomLock'
     */
    public Lock getLock() {
        return roomLock;
    }

    /**
     * Constructor
     * @param name name of the room
     * @param isLocked is the room locked or not
     */
    public Room(String name, boolean isLocked) {
        this.name = name;
        this.isLocked = isLocked;
        this.searchSpots = new ArrayList<>();
        this.items = new ArrayList<>();
        this.characters = new ArrayList<>();
    }

    /**
     * Constructor
     * @param index index of the room
     * @param name name of the room
     * @param neighbors list of adjacent rooms
     */
    public Room(int index, String name, List<Integer> neighbors) {
        this.index = index;
        this.name = name;
        this.neighbors = neighbors;
        this.items = new ArrayList<>();
        this.characters = new ArrayList<>();
        this.searchSpots = new ArrayList<>();

    }

    /**
     * Getter for 'isLocked'
     * @return value of 'isLocked'
     */
    public boolean isLocked() {
        return isLocked;
    }

    /**
     * Method to unlock a room
     */
    public void unlock() {
        this.isLocked = false;
    }

    /**
     * Getter for 'index'
     * @return value of 'index'
     */
    public int getIndex() {
        return index;
    }

    /**
     * Getter for 'name'
     * @return value of 'name'
     */
    public String getName() {
        return name;
    }

    /**
     * Getter for list 'neighbors'
     * @return value of 'neighbors' list
     */
    public List<Integer> getNeighbors() {
        return neighbors;
    }



    /**
     * To string
     * @return formulated info about a room
     */
    @Override
    public String toString() {
        return "Room{" +
                "index=" + index +
                ", name='" + name + '\'' +
                ", neighbors=" + neighbors +
                '}';
    }

    /**
     * Removes item from the room
     * @param item item to remove from the room
     */
    public boolean removeItem(Item item) {
        boolean removed = items.remove(item);
        if (removed) {
            itemPositions.remove(item);
        }
        return removed;
    }

    /**
     * Method to add an item into the room
     * @param item item to add into the room
     */
    public void addItem(Item item, double x, double y) {
        items.add(item);
        itemPositions.put(item, new Point2D(x, y));

    }
    /**
     * Method to add a search spot into the room
     * @param spot which spot to add into the room
     */
    public void addSearchSpot(SearchSpot spot) {
        if (searchSpots == null) {
            searchSpots = new ArrayList<>();
        }
        searchSpots.add(spot);
    }

    /**
     * Method used to create a list of un searched spots in a room
     * @return list of un searched spots in the room
     */
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

    /**
     * Getter for map searchSpotLocks
     * @return map searchSpotLocks
     */
    public Map<String, Lock> getSearchSpotLocks() {
        return Collections.unmodifiableMap(searchSpotLocks);
    }

    /**
     * Method to add an character into the room
     * @param character which character to add
     */
    public void addCharacter(Character character) {
        characters.add(character);
    }

    /**
     * Getter for list 'characters'
     * @return list characters
     */
    public List<Character> getCharacters() {
        return characters;
    }

    /**
     * Setter for neighbors
     * @param neighbors what to set the list to
     */
    public void setNeighbors(List<Integer> neighbors) {
        this.neighbors = neighbors;
    }

    /**
     * Getter for searchSpots
     * @return list of searchSpots
     */
    public List<SearchSpot> getSearchSpots() {
        if (searchSpots == null) {
            searchSpots = new ArrayList<>();
        }
        return searchSpots;
    }
    public void clearSearchSpots() {
        searchSpots.clear();
    }
    public Point2D getItemPosition(Item item) {
        return itemPositions.get(item);
    }

    /**
     * Getter for list 'items'
     * @return value of 'items' list
     */
    public List<Item> getItems() {
        return new ArrayList<>(items); // Return copy of list
    }

    public Map<Item, Point2D> getItemPositions() {
        return itemPositions;
    }
}
