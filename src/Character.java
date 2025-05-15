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
     * World instance
     */
    protected transient World world;
    /**
     * Name of the current room
     */
    protected String currentRoomName;//For serialization

    /**
     * Constructor
     * @param name Name of the character
     * @param health Int health value of the character
     * @param world which world the character is in
     */
    public Character(String name, int health, World world, String currentRoomName) {
        this.name = name;
        this.health = health;
        this.world = world;
        this.currentRoomName = currentRoomName;
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
     * Getter for world
     * @return value of world
     */
    public World getWorld() {
        return world;
    }

    /**
     * Setter for 'world'
     * @param world what to set the 'world' to
     */
    public void setWorld(World world) {
        this.world = world;
    }

    /**
     * Method used to get the player's current room, used for serialization
     * @return the current room
     */
    public Room getCurrentRoom() {
        if (world != null) {
            return world.findRoomByName(currentRoomName);
        }
         return null;
    }
}
