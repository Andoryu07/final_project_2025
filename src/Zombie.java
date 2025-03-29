public class Zombie extends Enemy {
    public Zombie(Room startRoom) {
        super("Zombie", 90, startRoom);
        this.distanceFromPlayer = 0; // Zombies don't move
    }

    @Override
    protected void initializeAttacks() {
        attacks.put("Scratch", 30);
    }
}