/**
 * Class used to implement items, which fall under the category of 'key items'
 */
public class KeyItem extends Item {
    /**
     * Constructor, contains super from Item
     * @param name name of the item
     * @param description description of the item
     */
    public KeyItem(String name, String description) {
        super(name, description);
    }

    /**
     * Method used to implement the use/effects of the KeyItem
     * @param player Who had used the item
     */
    @Override
    public void use(Player player) {
        System.out.println("You have used " + name + ".");
    }
}
