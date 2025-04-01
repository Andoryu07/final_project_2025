import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Class used for the implementation of inventory
 */
public class Inventory implements Serializable {
    /**
     * Used for serialization
     */
    private static final long serialVersionUID = 1L;
    /**
     * List used to store the items player owns
     */
    private List<Item> items;
    /**
     * Capacity of the inventory(How many items player can store in it)
     */
    private int capacity;

    /**
     * Constructor
     * @param capacity Capacity of the inventory(How many items player can store in it)
     */
    public Inventory(int capacity) {
        this.items = new ArrayList<>();
        this.capacity = capacity;
    }

    /**
     * Getter for 'capacity'
     * @return value of 'capacity'
     */
    public int getCapacity() {
        return capacity;
    }

    /**
     * Method used to locate a certain item by name
     * @param itemName name of the item we are looking for
     * @return the item, if it's found in the inventory, null if not
     */
    public Item findItem(String itemName) {
        for (Item item : items) {
            if (item.getName().equalsIgnoreCase(itemName)) {
                return item;
            }
        }
        return null;
    }

    /**
     * Method used to quickly clear/empty the inventory
     */
    public void clear() {
        items.clear();
    }

    /**
     * Method to add items into the inventory
     * @param item to add into the inventory
     * @return true/false based on whether the item can and had been added
     */
    public boolean addItem(Item item) {
        if (items.size() < capacity) {
            items.add(item);
            return true;
        }
        return false;
    }

    /**
     * Method to remove an item from inventory
     * @param item to remove from inventory
     */
    public void removeItem(Item item) {
        items.remove(item);
    }

    /**
     * Getter for the 'items' list
     * @return 'items' list
     */
    public List<Item> getItems() {
        return items;
    }


}