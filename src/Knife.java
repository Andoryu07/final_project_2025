/**
 * Class used to implement the weapon 'Knife'
 */
public class Knife extends Weapon {
    /**
     * Constructor, contains super from Weapon
     */
    public Knife() {
        super("Knife", "Sharp blade for melee combat", 25, -1); // -1 = infinite use

    }

    /**
     * Method to implement the use of the weapon 'Knife'
     * @param player Who had used the weapon
     */
    @Override
    public void use(Player player) {
        System.out.println("You slash with the knife!");
    }
}