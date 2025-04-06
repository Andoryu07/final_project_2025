import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Class used to store and save the game's data, which we later use when loading the game(Serialization)
 */
public class GameState implements Serializable {
    /**
     * Used for serialization
     */
    private static final long serialVersionUID = 1L;
    // Player data
    /**
     * int used to store player's health amount
     */
    private int playerHealth;
    /**
     * Instance of Weapon used to store the player's equipped weapon
     */
    private Weapon equippedWeapon;
    /**
     * List used to store the items in player's inventory
     */
    private List<Item> inventory;
    /**
     * String used to store the player's current room name
     */
    private String currentRoomName;
    // World state
    /**
     * Map used to store already searched spots for each room
     */
    private Map<String, List<String>> searchedSpotsPerRoom;
    /**
     * Set used to store, which gears player had inserted
     */
    private Set<String> insertedGears;
    /**
     * Map used to store states of locks in the game(Name,hasItBeenUnlockedYet?)
     */
    private Map<String, Boolean> lockStates;
    /**
     * int value used to store the Stalker's distance(in rooms) away from Player
     */
    private int stalkerDistance;
    // Flashlight state
    /**
     * int value used to store the amount of current flashlight battery charge left
     */
    private int flashlightBattery;
    /**
     * boolean used to store the information, whether the Flashlight's state isInCelery is true or not
     */
    private boolean isFlashlightInCelery;
    // Other game state
    /**
     * Boolean used to store the information, whether the player is currently in a fight or not(isFighting)
     */
    private boolean isPlayerFighting;
    /**
     * Boolean used to store the information, whether the player is currently blocking or not(isBlocking)
     */
    private boolean isPlayerBlocking;

    // Constructor

    /**
     * Constructor
     */
    public GameState() {
    }

    // Getters and Setters

    /**
     * Getter for 'playerHealth'
     *
     * @return value of 'playerHealth'
     */
    public int getPlayerHealth() {
        return playerHealth;
    }

    /**
     * Setter for 'playerHealth'
     *
     * @param playerHealth what to set 'playerHealth' to
     */
    public void setPlayerHealth(int playerHealth) {
        this.playerHealth = playerHealth;
    }

    /**
     * Getter for 'equippedWeapon'
     *
     * @return value of 'equippedWeapon'
     */
    public Weapon getEquippedWeapon() {
        return equippedWeapon;
    }

    /**
     * Setter for 'equippedWeapon'
     *
     * @param equippedWeapon what to set 'equippedWeapon' to
     */
    public void setEquippedWeapon(Weapon equippedWeapon) {
        this.equippedWeapon = equippedWeapon;
    }

    /**
     * Getter for 'inventory'
     *
     * @return inventory
     */
    public List<Item> getInventory() {
        return inventory;
    }

    /**
     * Setter for 'inventory'
     *
     * @param inventory what to set the list 'inventory' to
     */
    public void setInventory(List<Item> inventory) {
        this.inventory = inventory;
    }

    /**
     * Getter for 'currentRoomName'
     *
     * @return String value of 'currentRoomName'
     */
    public String getCurrentRoomName() {
        return currentRoomName;
    }

    /**
     * Setter for 'currentRoomName'
     *
     * @param currentRoomName what to set the value of 'currentRoomName' to
     */
    public void setCurrentRoomName(String currentRoomName) {
        this.currentRoomName = currentRoomName;
    }

    /**
     * Getter for 'insertedGears'
     *
     * @return the set 'insertedGears'
     */
    public Set<String> getInsertedGears() {
        return insertedGears;
    }

    /**
     * Setter for 'insertedGears'
     *
     * @param insertedGears what to set the 'insertedGears' set to
     */
    public void setInsertedGears(Set<String> insertedGears) {
        this.insertedGears = insertedGears;
    }

    /**
     * Getter for 'lockStates' map
     *
     * @return the map 'lockStates'
     */
    public Map<String, Boolean> getLockStates() {
        return lockStates;
    }

    /**
     * Setter for the map 'lockStates'
     *
     * @param lockStates what to set the map 'lockStates' to
     */
    public void setLockStates(Map<String, Boolean> lockStates) {
        this.lockStates = lockStates;
    }

    /**
     * Getter for 'stalkerDistance'
     *
     * @return value of 'stalkerDistance'
     */
    public int getStalkerDistance() {
        return stalkerDistance;
    }

    /**
     * Setter for 'stalkerDistance'
     *
     * @param stalkerDistance what to set the value of 'stalkerDistance' to
     */
    public void setStalkerDistance(int stalkerDistance) {
        this.stalkerDistance = stalkerDistance;
    }

    /**
     * Getter for 'flashlightBattery'
     *
     * @return value of 'flashlightBattery'
     */
    public int getFlashlightBattery() {
        return flashlightBattery;
    }

    /**
     * Setter for 'flashlightBattery'
     *
     * @param flashlightBattery what to set the value of 'flashlightBattery' to
     */
    public void setFlashlightBattery(int flashlightBattery) {
        this.flashlightBattery = flashlightBattery;
    }

    /**
     * Getter for 'isFlashlightInCelery'
     *
     * @return value of 'isFlashlightInCelery'
     */
    public boolean isFlashlightInCelery() {
        return isFlashlightInCelery;
    }

    /**
     * Setter for 'isFlashlightInCelery'
     *
     * @param flashlightInCelery what to set the value of 'isFlashlightInCelery' to
     */
    public void setFlashlightInCelery(boolean flashlightInCelery) {
        isFlashlightInCelery = flashlightInCelery;
    }

    /**
     * Getter for 'isPlayerFighting'
     *
     * @return value of 'isPlayerFighting'
     */
    public boolean isPlayerFighting() {
        return isPlayerFighting;
    }

    /**
     * Setter for 'isPlayerFighting'
     *
     * @param playerFighting what to set the value of 'isPlayerFighting' to
     */
    public void setPlayerFighting(boolean playerFighting) {
        isPlayerFighting = playerFighting;
    }

    /**
     * Getter for 'isPLayerBlocking'
     *
     * @return the value of 'isPLayerBlocking'
     */
    public boolean isPlayerBlocking() {
        return isPlayerBlocking;
    }

    /**
     * Setter for 'isPLayerBlocking'
     *
     * @param playerBlocking what to set the value of 'isPLayerBlocking' to
     */
    public void setPlayerBlocking(boolean playerBlocking) {
        isPlayerBlocking = playerBlocking;
    }

    /**
     * Getter for 'searchedSpotsPerRoom'
     *
     * @return Map of 'searchedSpotsPerRoom'
     */
    public Map<String, List<String>> getSearchedSpotsPerRoom() {
        return searchedSpotsPerRoom;
    }

    /**
     * Setter for 'searchedSpotsPerRoom'
     *
     * @param searchedSpotsPerRoom sets the map of 'searchedSpotsPerRoom'
     */
    public void setSearchedSpotsPerRoom(Map<String, List<String>> searchedSpotsPerRoom) {
        this.searchedSpotsPerRoom = searchedSpotsPerRoom;
    }
}

