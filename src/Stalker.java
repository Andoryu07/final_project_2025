/**
 * Class used to implement the enemy Stalker
 */
public class Stalker extends Enemy {
    /**
     * Constructor, containing super from Enemy
     * @param startRoom the room Stalker spawns/starts off in
     */
    public Stalker(Room startRoom) {
        super("Stalker", 120, startRoom);
        this.distanceFromPlayer = 3;
    }

    /**
     * Method used to initialize Stalker's attacks(name, damage)
     */
    @Override
    protected void initializeAttacks() {
        attacks.put("Claw Stab", 30);
        attacks.put("Bite", 20);
    }
}