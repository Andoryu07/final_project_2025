/**
 * Class used to implement the item Batteries and it's behavior and use
 */
public class Batteries extends Consumable {
    /**
     * Constructor, includes super from Item class, to specify the values of Batteries
     */
    public Batteries() {
        super("Batteries", "Used to recharge the flashlight.");
    }
    @Override
    public boolean equals(Object o) {
        return this == o;
    }

    @Override
    public int hashCode() {
        return System.identityHashCode(this);
    }
    /**
     * Override method use, specifies what's supposed to happen upon using the item Batteries(Player's Flashlight, if owned, will get fully recharged and the Batteries item will be used)
     * @param player Specifies which player wants to use the Batteries
     */
    @Override
    public void use(Player player) {
        for (Item item : player.getInventory().getItems()) {
            if (item instanceof Flashlight) {
                Flashlight flashlight = (Flashlight) item;
                flashlight.recharge();
                player.getInventory().removeItem(this);
                System.out.println("🔋 Flashlight recharged!");
                return;
            }
        }
        System.out.println("❌ You don't have a flashlight to recharge.");
    }

}
