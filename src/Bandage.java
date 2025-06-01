/**
 * Class used to implement the item Bandage, which is used for healing
 */
public class Bandage extends HealingItem {

    /**
     * Constructor, includes a super from HealingItem class, to specify the values of Bandage
     */
    public Bandage() {
        super("Bandage", "Regenerates a small amount of HP", 20);
    }

    /**
     * Check if it's the exact same object
     * @param o what are we comparing the bandage item to
     * @return true/false - does it equal?
     */
    @Override
    public boolean equals(Object o) {
        return this == o;
    }

    /**
     * HashCode
     * @return Unique hash code per instance
     */
    @Override
    public int hashCode() {
        return System.identityHashCode(this);
    }

}
