import java.io.Serializable;
import java.util.Objects;

/**
 * Class used to implement items, their values, fields and use
 */
public abstract class Item implements Serializable {
    /**
     * Used for serialization
     */
    private static final long serialVersionUID = 1L;
    /**
     * Name of the item
     */
    protected String name;
    /**
     * Description of the item
     */
    protected String description;

    /**
     * Constructor
     * @param name name of the item
     * @param description description of the item
     */
    public Item(String name, String description) {
        this.name = name;
        this.description = description;
    }

    /**
     * Getter for 'name'
     * @return value of 'name'
     */
    public String getName() {
        return name;
    }

    /**
     * Getter for 'description'
     * @return value of 'description'
     */
    public String getDescription() {
        return description;
    }

    /**
     * Method used to implement the item's behavior
     * @param player Who is using the item
     */
    public abstract void use(Player player);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return Objects.equals(name, item.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}