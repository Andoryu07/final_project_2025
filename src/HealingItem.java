/**
 * Class used to implement the healing items in the game
 */
public class HealingItem extends Consumable {
    /**
     * int value of how much health certain item adds to the person using it
     */
    private int healAmount;

    /**
     * Constructor, contains super from Item class
     * @param name name of the healing item
     * @param description description of the healing item
     * @param healAmount amount of health the healing item heals for
     */
    public HealingItem(String name, String description, int healAmount) {
        super(name, description);
        this.healAmount = healAmount;
    }

    /**
     * Method used to determine what happens upon using the healing item
     * @param player Who used the healing item
     */
    @Override
    public void use(Player player) {
        System.out.println("Item " + name + " has been used, healed " + healAmount + " HP.");
        player.heal(healAmount);
    }
}
