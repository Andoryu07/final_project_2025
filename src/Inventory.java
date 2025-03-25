import java.util.ArrayList;
import java.util.List;

public class Inventory {
    private List<Item> items;
    private int capacity;

    public Inventory(int capacity) {
        this.items = new ArrayList<>();
        this.capacity = capacity;
    }

    // Add this method to find an item by name
    public Item findItem(String itemName) {
        for (Item item : items) {
            if (item.getName().equalsIgnoreCase(itemName)) {
                return item;
            }
        }
        return null;
    }

    public boolean addItem(Item item) {
        if (items.size() < capacity) {
            items.add(item);
            return true;
        }
        return false;
    }

    public void removeItem(Item item) {
        items.remove(item);
    }

    public List<Item> getItems() {
        return items;
    }

    public boolean canAddItem() {
        return items.size() < capacity;
    }

    public void printInventory() {
        if (items.isEmpty()) {
            System.out.println("Your inventory is empty.");
        } else {
            System.out.println("Inventory:");
            for (Item item : items) {
                System.out.println("- " + item.getName());
            }
        }
    }
}