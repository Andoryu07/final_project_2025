public class Stalker extends Enemy {
    public Stalker(Room startRoom) {
        super("Stalker", 120, startRoom);
        this.distanceFromPlayer = 3;
    }

    @Override
    protected void initializeAttacks() {
        attacks.put("Claw Stab", 30);
        attacks.put("Bite", 20);
    }
}