/**
 * Class used to implement the Pistol weapon
 */
public class Pistol extends Weapon {
    /**
     * Constructor, contains super from Weapon class
     */
    public Pistol() {
        super("Pistol", "Medium power, uses ammo", 35, 6);
    }

    /**
     * Use method, implements Pistol's behavior upon use, contains super from Weapon class
     * @param player
     */
    @Override
    public void use(Player player) {
        super.use(player);
        System.out.println("Bang! Pistol shot fired.");
    }
}

