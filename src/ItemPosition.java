import java.io.Serializable;
import javafx.geometry.Point2D;

public class ItemPosition implements Serializable {
    private static final long serialVersionUID = 1L;

    private final Item item;
    private final double x;
    private final double y;

    public ItemPosition(Item item, double x, double y) {
        this.item = item;
        this.x = x;
        this.y = y;
    }

    public Item getItem() { return item; }
    public double getX() { return x; }
    public double getY() { return y; }
    public Point2D getPosition() { return new Point2D(x, y); }
}