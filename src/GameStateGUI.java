import java.io.Serializable;
import java.util.*;

public class GameStateGUI implements Serializable {
    private static final long serialVersionUID = 1L;

    // Player state
    private int playerHealth;
    private double playerStamina;
    private double playerX;
    private double playerY;
    private String currentRoomName;
    private List<Item> inventory;
    private Weapon equippedWeapon;

    // World state
    private Map<String, List<String>> searchedSpots; // Room -> List of searched spot names
    private Map<String, List<ItemPosition>> droppedItems; // Room -> List of items with positions
    private Map<String, Boolean> lockStates; // Lock name -> locked state
    private int stalkerDistance;

    // Getters and setters
    public int getPlayerHealth() { return playerHealth; }
    public void setPlayerHealth(int health) { this.playerHealth = health; }

    public double getPlayerStamina() { return playerStamina; }
    public void setPlayerStamina(double stamina) { this.playerStamina = stamina; }

    public double getPlayerX() { return playerX; }
    public double getPlayerY() { return playerY; }
    public void setPlayerPosition(double x, double y) {
        this.playerX = x;
        this.playerY = y;
    }

    public String getCurrentRoomName() {
        return currentRoomName;
    }
    public void setCurrentRoomName(String name) { this.currentRoomName = name; }

    public List<Item> getInventory() { return inventory; }
    public void setInventory(List<Item> inventory) { this.inventory = inventory; }

    public Weapon getEquippedWeapon() { return equippedWeapon; }
    public void setEquippedWeapon(Weapon weapon) { this.equippedWeapon = weapon; }

    public Map<String, List<String>> getSearchedSpots() { return searchedSpots; }
    public void setSearchedSpots(Map<String, List<String>> searchedSpots) {
        this.searchedSpots = searchedSpots;
    }

    public Map<String, List<ItemPosition>> getDroppedItems() { return droppedItems; }
    public void setDroppedItems(Map<String, List<ItemPosition>> droppedItems) {
        this.droppedItems = droppedItems;
    }

    public Map<String, Boolean> getLockStates() { return lockStates; }
    public void setLockStates(Map<String, Boolean> lockStates) {
        this.lockStates = lockStates;
    }

    public int getStalkerDistance() { return stalkerDistance; }
    public void setStalkerDistance(int distance) { this.stalkerDistance = distance; }
}