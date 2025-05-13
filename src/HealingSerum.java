/**
 * Class for the implementation of a HealingItem HealingSerum
 */
public class HealingSerum extends HealingItem {
    /**
     * Constructor, contains super from HealingItem class
     */
    public HealingSerum() {
        super("Healing Serum", "Regenerates a huge chunk of HP", 50);
    }
    @Override
    public boolean equals(Object o) {
        return this == o;
    }

    @Override
    public int hashCode() {
        return System.identityHashCode(this);
    }
}
