import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * Class used to implement the search spot, its values and fields
 */
public class SearchSpot implements Serializable {
    /**
     * Used for serialization
     */
    private static final long serialVersionUID = 1L;
    /**
     * Name of the search spot
     */
    private String name;
    /**
     * Has the search spot been searched yet, or not
     */
    private boolean searched;
    /**
     * List of items hidden inside the search spot
     */
    private List<Item> hiddenItems;

    /**
     * Constructor
     * @param name Name of the search spot
     * @param hiddenItems List of items hidden inside the search spot
     */
    public SearchSpot(String name, List<Item> hiddenItems) {
        this.name = name;
        this.hiddenItems = hiddenItems;
        this.searched = false;
    }

    /**
     * Getter for name
     * @return value of name
     */
    public String getName() {
        return name;
    }

    /**
     * Getter for 'searched'
     * @return value of 'searched'
     */
    public boolean isSearched() {
        return searched;
    }

    /**
     * Method used to search the search spot
     * @return the items hidden inside the search spot, null if no items are in the search spot
     */
    public List<Item> search() {
        if (!searched) {
            searched = true;
            return hiddenItems;
        }
        return null;
    }
}



