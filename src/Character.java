import java.io.Serializable;

/**
 * Class used to implement and specify Characters' fields, values, behavior
 */
public abstract class Character implements Serializable {
    /**
     * Used for serialization
     */
    private static final long serialVersionUID = 1L;
    /**
     * Name of the character
     */
    protected String name;
    /**
     * Int health value of the character
     */
    protected int health;
    /**
     * Room instance, specifying the character's current room
     */
    protected Room currentRoom;

    /**
     * Constructor
     * @param name Name of the character
     * @param health Int health value of the character
     * @param currentRoom Room instance, specifying the character's current room
     */
    public Character(String name, int health, Room currentRoom) {
        this.name = name;
        this.health = health;
        this.currentRoom = currentRoom;
    }

    /**
     * Getter for 'name'
     * @return String value of 'name'
     */
    public String getName() {
        return name;
    }

    /**
     * Getter for 'health'
     * @return int value of 'health'
     */
    public int getHealth() {
        return health;
    }

    /**
     * Method used to decrease the player's health when taking damage
     * @param damage amount of health to decrease by
     */
    public void takeDamage(int damage) {
        health -= damage;
        if (health < 0) health = 0;
    }

    /**
     * Method used to figure out, whether the character has been defeated or not
     * @return boolean value of whether the character's health had reached 0 or not
     */
    public boolean isDefeated() {
        return health <= 0;
    }

    /**
     * Getter for 'currentRoom'
     * @return The current room of specific Character
     */
    public Room getCurrentRoom() {
        return currentRoom;
    }

    /**
     * Setter for 'currentRoom'
     * @param room which room to change the 'currentRoom' value to
     */
    public void setCurrentRoom(Room room) {
        this.currentRoom = room;
    }


}
