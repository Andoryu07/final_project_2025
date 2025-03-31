import java.io.Serializable;

public abstract class Character implements Serializable {
    private static final long serialVersionUID = 1L;
    protected String name;
    protected int health;
    protected Room currentRoom;

    public Character(String name, int health, Room currentRoom) {
        this.name = name;
        this.health = health;
        this.currentRoom = currentRoom;
    }

    public String getName() {
        return name;
    }

    public int getHealth() {
        return health;
    }

    public void takeDamage(int damage) {
        health -= damage;
        if (health < 0) health = 0;
    }

    public boolean isDefeated() {
        return health <= 0;
    }

    public Room getCurrentRoom() {
        return currentRoom;
    }

    public void setCurrentRoom(Room room) {
        this.currentRoom = room;
    }


}
