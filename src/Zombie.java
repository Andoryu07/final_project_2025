/**
 * Class used to implement the Zombie enemy
 */
public class Zombie extends Enemy {
    /**
     * Constructor, containing super from Enemy
     * @param world which world does the zombie start in
     */
    public Zombie(World world,String startingRoomName) {
        super("Zombie", 90, world,startingRoomName);
        this.distanceFromPlayer = 0; // Zombies don't move
    }

    /**
     * Method used to initialize this enemy's attacks(name,damage)
     */
    @Override
    protected void initializeAttacks() {
        attacks.put("Scratch", 30);
    }
}