import java.util.ArrayList;
import java.util.List;

public class Player extends Character {
    private Inventory inventory;
    private Weapon equippedWeapon;
    private int turnsSinceLastFlashlightUse = 3;


    public Player(String name, int health, Room startRoom) {
        super(name, health, startRoom);
        this.inventory = new Inventory(1);

    }
    public void equipWeapon(Weapon weapon) {
        this.equippedWeapon = weapon;
        System.out.println("Equipped " + weapon.getName());
        if (weapon.isInfiniteUse()) {
            System.out.println("This weapon has unlimited uses");
        } else {
            System.out.printf("Ammo: %d/%d%n",
                    weapon.getCurrentAmmo(), weapon.getMaxAmmo());
        }
    }
    public void attack(Enemy enemy) {
        if (equippedWeapon == null) {
            System.out.println("You punch feebly for 5 damage!");
            enemy.takeDamage(5);
        } else {
            // Remove the ammo check here - let the weapon handle it
            System.out.printf("You attack with %s for %d damage!%n",
                    equippedWeapon.getName(), equippedWeapon.getDamage());
            enemy.takeDamage(equippedWeapon.getDamage());
            equippedWeapon.use(this);
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
    public boolean hasItem(String itemName) {
        return inventory.getItems().stream()
                .anyMatch(item -> item.getName().equalsIgnoreCase(itemName));
    }
    public Weapon getEquippedWeapon() {
        return equippedWeapon;
    }
    public Inventory getInventory() {
        return inventory;
    }
    public Item findItemInInventory(String itemName) {
        return inventory.findItem(itemName);
    }
}

