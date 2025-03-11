public class Stalker extends Enemy{
    public Stalker(Room startRoom) {
        super("Stalker", 200, 30, startRoom);
    }

    @Override
    public void attack(Player player) {
        System.out.println(name + " has approached you and dealt MASSIVE damage!");
        player.takeDamage(40);
    }
}
