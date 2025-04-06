import java.io.Serializable;

/**
 * Class used to implement Weapon items, their values, fields, usage
 */
public abstract class Weapon extends Item implements Serializable {
    /**
     * Used for serialization
     */
    private static final long serialVersionUID = 1L;
    /**
     * How much damage the weapon makes
     */
    protected int damage;
    /**
     * Max ammo/Mag size
     */
    protected int maxAmmo;
    /**
     * Current ammo amount in the mag
     */
    protected int currentAmmo;
    /**
     * Method deciding, whether a said weapon has unlimited ammo
     */
    protected boolean infiniteUse;

    /**
     * Constructor, uses super from Item
     * @param name name of the weapon
     * @param description description of the weapon
     * @param damage How much damage the weapon makes
     * @param maxAmmo  Max ammo/Mag size
     */
    public Weapon(String name, String description, int damage, int maxAmmo) {
        super(name, description);
        this.damage = damage;
        this.maxAmmo = maxAmmo;
        this.currentAmmo = maxAmmo;
        this.infiniteUse = (maxAmmo < 0);  // Negative ammo = infinite use(used for knife and other potentional melee weapons)
    }

    /**
     * Decides whether the current weapon has ammo or not
     * @return true/false based on whether the current weapon has ammo or not
     */
    public boolean hasAmmo() {
        return infiniteUse || currentAmmo > 0;
    }

    /**
     * Getter for 'damage'
     * @return value of 'damage'
     */
    public int getDamage() {
        return damage;
    }

    /**
     * Method used to reload the weapon
     * @param ammoAmount how much ammo to reload
     */
    public void reload(int ammoAmount) {
        currentAmmo = Math.min(maxAmmo, currentAmmo + ammoAmount);
    }

    /**
     * Getter for 'maxAmmo'
     * @return value of 'maxAmmo'
     */
    public int getMaxAmmo() {
        return maxAmmo;
    }

    /**
     * Getter for 'currentAmmo'
     * @return value of 'currentAmmo'
     */
    public int getCurrentAmmo() {
        return currentAmmo;
    }

    /**
     * Method to implement the use of a said weapon
     * @param player Who is using the weapon
     */
    public void use(Player player) {
        if (!infiniteUse) {  // Only consume ammo if not infinite
            currentAmmo--;
        }
    }

    /**
     * Getter for 'infiniteUse'
     * @return value of infiniteUse
     */
    public boolean isInfiniteUse() {
        return maxAmmo < 0;
    }
}