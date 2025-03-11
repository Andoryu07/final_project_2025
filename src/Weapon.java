public class Weapon extends Item{
    private int damage;
    private int ammo;

    public Weapon(String name, String description, int damage, int ammo) {
        super(name, description);
        this.damage = damage;
        this.ammo = ammo;
    }

    public int getDamage() { return damage; }
    public boolean hasAmmo() { return ammo > 0; }
    public void use(Player player) {
        if (ammo > 0) {
            ammo--;
            System.out.println("Used weapon: " + name + " (Remaining ammo:  " + ammo + ")");
        } else {
            System.out.println("You have no ammo left!");
        }
    }
}
