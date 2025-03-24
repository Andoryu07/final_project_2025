import java.util.ArrayList;
import java.util.List;

public class Inventory {
    private List<Item> items;
    private int capacity;
    public Inventory(int capacity) {
        this.items = new ArrayList<>();
        this.capacity = capacity;
    }
    public boolean addItem(Item item) {
        if (items.size() < capacity) {
            items.add(item);
            return true;
        } else {
            System.out.println("Inventory is full! Cannot add: " + item.getName());
            return false;
        }
    }

    public void removeItem(Item item) {
        items.remove(item);
    }
    public void printInventory() {
        for (Item item : items) {
            System.out.println("- " + item.getName());
        }
    }

    public List<Item> getItems() {
        return items;
    }

    public Item findItem(String itemName) {
        for (Item item : items) {
            if (item.getName().equalsIgnoreCase(itemName)) {
                return item;
            }
        }
        return null;
    }
}
