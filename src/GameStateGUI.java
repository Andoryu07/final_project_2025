import java.io.Serializable;
import java.util.*;

/**
 * Class used to store the game's values, used for serialization
 */
public class GameStateGUI implements Serializable {
    /**
     * Used for serialization
     */
    private static final long serialVersionUID = 1L;

    // Player state
    /**
     * Player's health
     */
    private int playerHealth;
    /**
     * Player's stamina
     */
    private double playerStamina;
    /**
     * Player's x coordinate
     */
    private double playerX;
    /**
     * Player's y coordinate
     */
    private double playerY;
    /**
     * Name of the player's current room
     */
    private String currentRoomName;
    /**
     * Player's current inventory
     */
    private List<Item> inventory;
    /**
     * Player's current equipped weapon
     */
    private Weapon equippedWeapon;

    // World state
    /**
     * Map used to store already searched spots
     */
    private Map<String, List<String>> searchedSpots;
    /**
     * Map used to store dropped items(items with positions)
     */
    private Map<String, List<ItemPosition>> droppedItems;
    /**
     * Map used to store the lock states of all locks
     */
    private Map<String, Boolean> lockStates;
    /**
     * Stalker's distance from the player
     */
    private int stalkerDistance;

    // Getters and setters

    /**
     * Getter for 'playerHealth'
     * @return value of 'playerHealth'
     */
    public int getPlayerHealth() { return playerHealth; }

    /**
     * Setter for 'playerHealth'
     * @param health what to set the 'playerHealth' to
     */
    public void setPlayerHealth(int health) { this.playerHealth = health; }

    /**
     * Getter for 'playerStamina'
     * @return value of 'playerStamina'
     */
    public double getPlayerStamina() { return playerStamina; }

    /**
     * Setter for 'playerStamina'
     * @param stamina what to set 'playerStamina' to
     */
    public void setPlayerStamina(double stamina) { this.playerStamina = stamina; }

    /**
     * Getter for 'playerX'
     * @return value of 'playerX'
     */
    public double getPlayerX() { return playerX; }

    /**
     * Getter for 'playerY'
     * @return value of 'playerY'
     */
    public double getPlayerY() { return playerY; }

    /**
     * Setter for both 'playerX' and 'playerY'
     * @param x what to set the value of 'playerX' to
     * @param y what to set the value of 'playerY' to
     */
    public void setPlayerPosition(double x, double y) {
        this.playerX = x;
        this.playerY = y;
    }

    /**
     * Getter for 'currentRoomName'
     * @return value of 'currentRoomName'
     */
    public String getCurrentRoomName() {
        return currentRoomName;
    }

    /**
     * Setter for 'currentRoomName'
     * @param name what to set the value of 'currentRoomName' to
     */
    public void setCurrentRoomName(String name) { this.currentRoomName = name; }

    /**
     * Getter for 'inventory'
     * @return value of 'inventory'
     */
    public List<Item> getInventory() { return inventory; }

    /**
     * Setter for 'inventory'
     * @param inventory what to set the value of 'inventory' to
     */
    public void setInventory(List<Item> inventory) { this.inventory = inventory; }

    /**
     * Getter for 'equippedWeapon'
     * @return value of 'equippedWeapon'
     */
    public Weapon getEquippedWeapon() { return equippedWeapon; }

    /**
     * Setter for 'equippedWeapon'
     * @param weapon what to set the value of 'equippedWeapon' to
     */
    public void setEquippedWeapon(Weapon weapon) { this.equippedWeapon = weapon; }

    /**
     * Getter for 'searchedSpots'
     * @return value of 'searchedSpots'
     */
    public Map<String, List<String>> getSearchedSpots() { return searchedSpots; }

    /**
     * Setter for 'searchedSpots'
     * @param searchedSpots what to set the value of 'searchedSpots' to
     */
    public void setSearchedSpots(Map<String, List<String>> searchedSpots) {
        this.searchedSpots = searchedSpots;
    }

    /**
     * Getter for 'droppedItems'
     * @return value of 'droppedItems'
     */
    public Map<String, List<ItemPosition>> getDroppedItems() { return droppedItems; }

    /**
     * Setter for 'droppedItems'
     * @param droppedItems what to set the value of 'droppedItems' to
     */
    public void setDroppedItems(Map<String, List<ItemPosition>> droppedItems) {
        this.droppedItems = droppedItems;
    }

    /**
     * Getter for 'lockStates'
     * @return value of 'lockStates'
     */
    public Map<String, Boolean> getLockStates() { return lockStates; }

    /**
     * Setter for 'lockStates'
     * @param lockStates what to set the value of 'lockStates' to
     */
    public void setLockStates(Map<String, Boolean> lockStates) {
        this.lockStates = lockStates;
    }

    /**
     * Getter for 'stalkerDistance'
     * @return value of 'stalkerDistance'
     */
    public int getStalkerDistance() { return stalkerDistance; }

    /**
     * Setter for 'stalkerDistance'
     * @param distance what to set the value of 'stalkerDistance' to
     */
    public void setStalkerDistance(int distance) { this.stalkerDistance = distance; }
}