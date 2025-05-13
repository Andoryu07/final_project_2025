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
    @Override
    public boolean equals(Object o) {
        return this == o; // Check if it's the exact same object
    }

    @Override
    public int hashCode() {
        return System.identityHashCode(this); // Unique hash code per instance
    }

}
