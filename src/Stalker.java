public class Stalker extends Enemy {
    public Stalker(Room startRoom) {
        super("Stalker", 120, startRoom);
        this.distanceFromPlayer = 3;
    }
    public void setDistanceFromPlayer(int distance) {
        this.distanceFromPlayer = distance;
    }

    @Override
    protected void initializeAttacks() {
        attacks.put("Claw Stab", 30);
        attacks.put("Bite", 20);
    }
}