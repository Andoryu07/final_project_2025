/**
 * Class used to tag certain items as 'consumable' - will be depleted after being used
 */
public abstract class Consumable extends Item {
    /**
     * Constructor
     * @param name name of the item
     * @param description description of the item
     */
    public Consumable(String name, String description) {
        super(name, description);
    }
}
