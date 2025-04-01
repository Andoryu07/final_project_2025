/**
 * Class used to implement the item PistolAmmo
 */
public class PistolAmmo extends Ammo {
    /**
     * Constructor, contains super from ammo
     * @param amount the amount of pistol ammo one item PistolAmmo contains
     */
    public PistolAmmo(int amount) {
        super("Pistol ammo", "Ammunition used for the weapon Pistol", amount);
    }
}
