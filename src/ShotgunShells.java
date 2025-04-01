/**
 * Class used to implement the ammo type and item ShotgunShells
 */
public class ShotgunShells extends Ammo {
    /**
     * Constructor, contains super from Ammo class
     * @param amount amount of shells in one ShotgunShells item
     */
    public ShotgunShells(int amount) {
        super("Shotgun shells", "Ammunition used for the weapon Shotgun", amount);
    }
}
