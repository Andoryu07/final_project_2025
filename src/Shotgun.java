/**
 * Class used to implement the weapon Shotgun
 */
public class Shotgun extends Weapon {
    /**
     * Constructor, containing super from Weapon class
     */
    public Shotgun() {
        super("Shotgun", "Strong, but slow", 45, 2);
    }

    /**
     * Method configuring the shotgun's behavior upon being used
     * @param player Who had used the shotgun
     */
    @Override
    public void use(Player player) {
        super.use(player);
        System.out.println("BOOM! Shotgun blast fired.");
    }
}