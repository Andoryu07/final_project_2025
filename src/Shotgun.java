public class Shotgun extends Weapon{
    public Shotgun() {
        super("Shotgun", "Strong, but slow", 50, 2);
    }

    @Override
    public void use(Player player) {
        if (hasAmmo()) {
            System.out.println("You used the shotgun");
        } else {
            System.out.println("You have no ammo left");
        }
    }
}
