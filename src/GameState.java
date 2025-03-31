import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GameState implements Serializable {
    private static final long serialVersionUID = 1L;
    // Player data
    private int playerHealth;
    private Weapon equippedWeapon;
    private List<Item> inventory;
    private String currentRoomName;
    // World state
    private Set<String> insertedGears;
    private Map<String, Boolean> lockStates;
    private int stalkerDistance;
    // Flashlight state
    private int flashlightBattery;
    private boolean isFlashlightInCelery;
    // Other game state
    private boolean isPlayerFighting;
    private boolean isPlayerBlocking;

    // Constructor
    public GameState() {}

    // Getters and Setters
    public int getPlayerHealth() { return playerHealth; }
    public void setPlayerHealth(int playerHealth) { this.playerHealth = playerHealth; }

    public Weapon getEquippedWeapon() { return equippedWeapon; }
    public void setEquippedWeapon(Weapon equippedWeapon) { this.equippedWeapon = equippedWeapon; }

    public List<Item> getInventory() { return inventory; }
    public void setInventory(List<Item> inventory) { this.inventory = inventory; }

    public String getCurrentRoomName() { return currentRoomName; }
    public void setCurrentRoomName(String currentRoomName) { this.currentRoomName = currentRoomName; }

    public Set<String> getInsertedGears() { return insertedGears; }
    public void setInsertedGears(Set<String> insertedGears) { this.insertedGears = insertedGears; }

    public Map<String, Boolean> getLockStates() { return lockStates; }
    public void setLockStates(Map<String, Boolean> lockStates) { this.lockStates = lockStates; }

    public int getStalkerDistance() { return stalkerDistance; }
    public void setStalkerDistance(int stalkerDistance) { this.stalkerDistance = stalkerDistance; }

    public int getFlashlightBattery() { return flashlightBattery; }
    public void setFlashlightBattery(int flashlightBattery) { this.flashlightBattery = flashlightBattery; }

    public boolean isFlashlightInCelery() { return isFlashlightInCelery; }
    public void setFlashlightInCelery(boolean flashlightInCelery) { isFlashlightInCelery = flashlightInCelery; }

    public boolean isPlayerFighting() { return isPlayerFighting; }
    public void setPlayerFighting(boolean playerFighting) { isPlayerFighting = playerFighting; }

    public boolean isPlayerBlocking() { return isPlayerBlocking; }
    public void setPlayerBlocking(boolean playerBlocking) { isPlayerBlocking = playerBlocking; }
}

