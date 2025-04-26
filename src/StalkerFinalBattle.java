/**
 * Class used to implement the StalkerFinalBattle enemy(final boss)
 */
public class StalkerFinalBattle extends Enemy {
    /**
     * Constructor, contains super from Enemy
     * @param world world, which the stalker spawns in
     */
    public StalkerFinalBattle(World world,String startingRoomName) {
        super("StalkerFinalBattle", 250, world,startingRoomName);
        this.distanceFromPlayer = 0; // Doesn't move, stays in the same room
    }

    /**
     * Method to initialize this enemy's attacks
     */
    @Override
    protected void initializeAttacks() {
        attacks.put("Claw Stab", 30);
        attacks.put("Tail Slash", 50);
    }
}