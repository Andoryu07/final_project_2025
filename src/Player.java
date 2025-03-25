import java.util.ArrayList;
import java.util.List;

public class Player extends Character {
    private Inventory inventory;
    private Weapon equippedWeapon;


    public Player(String name, int health, Room startRoom) {
        super(name, health, startRoom);
        this.inventory = new Inventory(1);

    }

    public void attack(Enemy enemy) {
        if (equippedWeapon != null && equippedWeapon.hasAmmo()) {
            enemy.takeDamage(equippedWeapon.getDamage());
            equippedWeapon.use(this);
            System.out.println("You attacked" + enemy.getName() + " with " + equippedWeapon.getName());
        } else {
            System.out.println("You have no ammo left!");
        }
    }

    public void heal(int amount) {
        this.health += amount;
        System.out.println(name + " healed himself for " + amount + " HP. Current health: " + health);
    }

    public void pickUpItem(Item item) {
        if (inventory.addItem(item)) {
            System.out.println("You've picked up: " + item.getName());
            currentRoom.removeItem(item);
        } else {
            System.out.println("Inventory is full! You can't pick up the item: " + item.getName());
        }
    }
    public void pickUpItems(List<Item> items) {
        for (Item item : items) {
            if (inventory.addItem(item)) {
                System.out.println("You've picked up: " + item.getName());
                currentRoom.removeItem(item); // Remove from search spot
            } else {
                // Inventory full - drop to room
                currentRoom.addItem(item);
                System.out.println("Inventory full! " + item.getName() + " fell to the ground.");
            }
        }
    }
    public void dropItem(Item item) {
        if (inventory.getItems().contains(item)) {
            inventory.removeItem(item);
            currentRoom.addItem(item);
            System.out.println("You dropped " + item.getName() + " on the ground.");
        } else {
            System.out.println("You don't have this item to drop.");
        }
    }
    public Inventory getInventory() {
        return inventory;
    }
    public Item findItemInInventory(String itemName) {
        return inventory.findItem(itemName);
    }
}

