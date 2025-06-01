public class PistolAmmo extends Ammo {
    /**
     * Constructor, contains super from ammo
     * @param amount the amount of pistol ammo one item PistolAmmo contains
     */
    public PistolAmmo(int amount) {
        super("Pistol ammo", "Ammunition used for the weapon Pistol", amount);
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