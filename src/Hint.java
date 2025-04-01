/**
 * Class used for implementing the Hint item type
 */
public class Hint extends Item {
    /**
     * Constructor, contains super from Item class
     * @param name name of the Hint item
     * @param description description of the Hint item
     */
    public Hint(String name, String description) {
        super(name, description);
    }

    /**
     * Implements what's supposed to occur upon using the Hint item(Prints the item's description)
     * @param player Who had used the item
     */
    @Override
    public void use(Player player) {
        System.out.println("📜 Hint: " + description);
    }
}

