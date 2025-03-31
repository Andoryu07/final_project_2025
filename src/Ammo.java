/**
 * Class used to create Ammo items' variables, fields, and specify their behavior under certain circumstances
 */
public class Ammo extends Item{
    /**
     * Used to specify the amount of ammo
     */
    protected int amount;

    /**
     * Constructor for ammo
     * @param name name of the ammo 'type'
     * @param description a short description used to explain the ammo type's usage and behavior
     * @param amount amount of the certain ammo type
     */
    public Ammo(String name, String description, int amount) {
        super(name, description);
        this.amount = amount;
    }

    /**
     * Getter for the field 'amount'
     * @return the int value of amount
     */
    public int getAmount() { return amount; }

    /**
     * Method used to describe the usage of ammo
     * @param player Specifies which player is trying to use the ammo
     */
    public void use(Player player) {
        System.out.println("You can't use this item like that...");
    }
}
