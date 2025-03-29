public class Shotgun extends Weapon {
    public Shotgun() {
        super("Shotgun", "Strong, but slow", 45, 2);
    }

    @Override
    public void use(Player player) {
        super.use(player);
        System.out.println("BOOM! Shotgun blast fired.");
    }
}