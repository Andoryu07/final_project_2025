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
    /**
     * Check if it's the exact same object
     * @param o what are we comparing the bandage item to
     * @return true/false - does it equal?
     */
    @Override
    public boolean equals(Object o) {
        return this == o;
    }
    /**
     * HashCode
     * @return Unique hash code per instance
     */
    @Override
    public int hashCode() {
        return System.identityHashCode(this);
    }


}