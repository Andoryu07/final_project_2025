public class StalkerFinalBattle extends Enemy {
    public StalkerFinalBattle(Room startRoom) {
        super("StalkerFinalBattle", 250, startRoom);
        this.distanceFromPlayer = 0; // Doesn't move, stays in the same room
    }

    @Override
    protected void initializeAttacks() {
        attacks.put("Claw Stab", 30);
        attacks.put("Tail Slash", 50);
    }
}