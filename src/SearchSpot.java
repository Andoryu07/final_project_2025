import javafx.geometry.Rectangle2D;

import java.io.Serializable;
import java.util.ArrayList;
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
    private double x;
    private double y;
    private double width;
    private double height;
    /**
     * Constructor
     * @param name Name of the search spot
     * @param hiddenItems List of items hidden inside the search spot
     */
    public SearchSpot(String name, List<Item> hiddenItems, double x, double y, double width, double height) {
        this.name = name;
        this.hiddenItems = hiddenItems;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
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
    public List<Item> getItems() { // Rename from search()
        if (!searched) {
            return new ArrayList<>(hiddenItems);
        }
        return null;
    }

    /**
     * Setter for 'searched'
     * @param searched what to set the value of 'searched' to
     */
    public void setSearched(boolean searched) {
        this.searched = searched;
    }

    /**
     * Method used to set the search spot as 'searched'
     */
    public void markAsSearched() {
        this.searched = true;
    }
    public Rectangle2D getArea() {
        return new Rectangle2D(x, y, width, height);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

}



