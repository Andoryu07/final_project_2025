import java.io.Serializable;
import javafx.geometry.Point2D;

/**
 * Class used to save the positions of the items dropped in the world, used for serialization
 */
public class ItemPosition implements Serializable {
    /**
     * Used for serialization
     */
    private static final long serialVersionUID = 1L;
    /**
     * Item dropped
     */
    private final Item item;
    /**
     * X coordinate of the item
     */
    private final double x;
    /**
     * Y coordinate of the item
     */
    private final double y;

    /**
     * Constructor
     * @param item item dropped
     * @param x X coordinate of the item
     * @param y Y coordinate of the item
     */
    public ItemPosition(Item item, double x, double y) {
        this.item = item;
        this.x = x;
        this.y = y;
    }

    /**
     * Getter for 'item'
     * @return value of 'item\
     */
    public Item getItem() { return item; }

    /**
     * Getter for 'x'
     * @return value of 'x'
     */
    public double getX() { return x; }

    /**
     * Getter for 'y'
     * @return value of 'y'
     */
    public double getY() { return y; }

    /**
     * Getter for both 'x' and 'y'
     * @return value of 'x' and 'y'
     */
    public Point2D getPosition() { return new Point2D(x, y); }
}