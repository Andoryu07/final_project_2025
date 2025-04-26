import java.io.Serializable;

/**
 * Class used to implement the user/player, his fields, values and behavior, choices
 */
public class Player extends Character implements Serializable {
    /**
     * Used for serialization
     */
    private static final long serialVersionUID = 1L;
    /**
     * Inventory instance
     */
    private Inventory inventory;
    /**
     * Weapon instance storing the equipped weapon
     */
    private Weapon equippedWeapon;
    /**
     * Boolean to decide whether the player is using the block option(in fight) or not
     */
    private boolean isBlocking = false;
    /**
     * Boolean to decide whether the player is currently in a fight or not
     */
    private boolean isFighting = false;
    /**
     * World instance
     */
    private World world;
    /**
     * Constructor, contains super from Character
     * @param name name of the Player
     * @param health health of the Player
     * @param world World, in which the Player is in
     */
    public Player(String name, int health, World world, String startingRoomName) {
        super(name, health, world,startingRoomName);
        this.inventory = new Inventory(10);

    }

    /**
     * Method for player to equip the weapon, if he owns at least one
     * @param weapon Weapon the player wants to equip
     */
    public void equipWeapon(Weapon weapon) {
        this.equippedWeapon = weapon;
        if (weapon != null) {
            System.out.println("Equipped " + weapon.getName());
            if (weapon.isInfiniteUse()) {
                System.out.println("This weapon has unlimited uses");
            } else {
                System.out.printf("Ammo: %d/%d%n",
                        weapon.getCurrentAmmo(), weapon.getMaxAmmo());
            }
        } else {
            System.out.println("No weapon equipped");
        }
    }

    /**
     * Method for player to attempt and attack the enemy in a fight, only works if player has a weapon equipped
     * @param enemy enemy player is attempting to attack
     */
    public void attack(Enemy enemy) {
        if (equippedWeapon == null) {
            System.out.println("You have no weapon equipped");
        } else {
            // Remove the ammo check here - let the weapon handle it
            System.out.printf("You attack with %s for %d damage!%n",
                    equippedWeapon.getName(), equippedWeapon.getDamage());
            enemy.takeDamage(equippedWeapon.getDamage());
            equippedWeapon.use(this);
        }
    }

    /**
     * Method used to heal the player when he chooses to in a fight
     * @param amount amount of health player will heal from this action
     */
    public void heal(int amount) {
        this.health += amount;
        System.out.println(name + " healed himself for " + amount + " HP. Current health: " + health);
    }

    /**
     * Method used for a player to pick up and add an item into his inventory
     * @param item which item is the player attempting to pick up
     */
    public void pickUpItem(Item item) {
        if (inventory.addItem(item)) {
            System.out.println("You've picked up: " + item.getName());
            getCurrentRoom().removeItem(item);
        } else {
            System.out.println("Inventory is full! You can't pick up the item: " + item.getName());
        }
    }

    /**
     * Checks whether a certain item is in the player's inventory
     * @param itemName name of the item attempting to find
     * @return true/false based on whether player's inventory contains the said item
     */
    public boolean hasItem(String itemName) {
        return inventory.getItems().stream()
                .anyMatch(item -> item.getName().equalsIgnoreCase(itemName));
    }

    /**
     * Method used to remove an item from the inventory, if it's there
     * @param itemName name of the item we're attempting to remove
     * @return true/false based on whether the removal of the item was successful or not
     */
    public boolean removeItem(String itemName) {
        Item item = findItemInInventory(itemName);
        if (item != null) {
            inventory.removeItem(item);
            return true;
        }
        return false;
    }

    /**
     * Method used to decrease player's health by a certain amount
     * @param damage amount of health to decrease by
     */
    public void takeDamage(int damage) {
        if (isBlocking) {
            damage = (int) (damage * 0.5); // Reduce damage by 50%
            isBlocking = false; // Block only lasts one turn
        }
        this.health -= damage;
        if (this.health < 0) this.health = 0;
    }

    /**
     * Setter for 'health'
     * @param health what to set 'health' to
     */
    public void setHealth(int health) {
        this.health = Math.max(0, health); // Ensure health doesn't go negative
    }

    /**
     * Getter for 'equippedWeapon'
     * @return value of 'equippedWeapon'
     */
    public Weapon getEquippedWeapon() {
        return equippedWeapon;
    }

    /**
     * Getter for 'inventory'
     * @return list 'inventory'
     */
    public Inventory getInventory() {
        return inventory;
    }

    /**
     * method used to find a certain item in player's inventory
     * @param itemName name of the item we're attempting to find
     * @return the item we were looking for if it was found, null if not
     */
    public Item findItemInInventory(String itemName) {
        return inventory.findItem(itemName);
    }

    /**
     * Setter for 'isBlocking'
     * @param blocking sets the value of 'isBlocking'
     */
    public void setBlocking(boolean blocking) {
        this.isBlocking = blocking;
    }

    /**
     * Setter for 'isFighting'
     * @param fighting sets the value of 'isFighting'
     */
    public void setFighting(boolean fighting) {
        isFighting = fighting;
    }

    /**
     * Getter for 'isFighting'
     * @return value of 'isFighting'
     */
    public boolean isFighting() {
        return isFighting;
    }

    /**
     * Getter for 'isBlocking'
     * @return value of 'isBlocking'
     */
    public boolean isBlocking() {
        return isBlocking;
    }

    /**
     * Getter for World
     * @return world
     */
    public World getWorld() {
        return world;
    }

    /**
     * Setter for World
     * @param world what to set the world to
     */
    public void setWorld(World world) {
        this.world = world;
    }

    /**
     * Method used to set player's current room
     * @param room what room to set player's current room to
     */
    public void setCurrentRoom(Room room) {
        this.currentRoomName = room.getName();
        if (world != null) {
            world.setCurrentRoom(room);
        }
    }

}

