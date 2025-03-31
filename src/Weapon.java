import java.io.Serializable;

public abstract class Weapon extends Item implements Serializable {
    private static final long serialVersionUID = 1L;
    protected int damage;
    protected int maxAmmo;
    protected int currentAmmo;
    protected boolean infiniteUse;
    public Weapon(String name, String description, int damage, int maxAmmo) {
        super(name, description);
        this.damage = damage;
        this.maxAmmo = maxAmmo;
        this.currentAmmo = maxAmmo;
        this.infiniteUse = (maxAmmo < 0);  // Negative ammo = infinite use(used for knife and other potentional melee weapons)
    }

    public boolean hasAmmo() {
        return infiniteUse || currentAmmo > 0;  // Changed this line
    }

    public int getDamage() {
        return damage;
    }

    public void reload(int ammoAmount) {
        currentAmmo = Math.min(maxAmmo, currentAmmo + ammoAmount);
    }

    public int getMaxAmmo() {
        return maxAmmo;
    }

    public int getCurrentAmmo() {
        return currentAmmo;
    }

    public void use(Player player) {
        if (!infiniteUse) {  // Only consume ammo if not infinite
            currentAmmo--;
        }
    }

    public boolean isInfiniteUse() {
        return maxAmmo < 0;
    }
}