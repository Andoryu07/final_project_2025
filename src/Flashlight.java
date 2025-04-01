import java.io.Serializable;

/**
 * Class used to implement the item Flashlight and its behavior, value,etc.
 */
public class Flashlight extends Item implements Serializable {
    /**
     * Used for serialization
     */
    private static final long serialVersionUID = 1L;
    /**
     * int value of how much battery charge is left in the flashlight
     */
    private int battery;
    /**
     * boolean value, indicating whether the player is currently located in Celery or not
     */
    private boolean isInCelery = false;

    /**
     * Constructor, uses Item class super
     */
    public Flashlight() {
        super("Flashlight","A flashlight used to enter dark places. Requires Batteries to function.");
        this.battery = 100;
    }

    /**
     * Method for using the Flashlight's battery(Flashlight can only be used in the Celery stealth sequence, that's why the condition)
     * @param amount amount of battery charge to subtract/remove
     */
    public void useBattery(int amount) {
        if (!isInCelery) {
            System.out.println("Flashlight battery can only be used in the basement!");
            return;
        }
        battery = Math.max(0, battery - amount);//Math.max picks the argument, which is closer to Integer.MAX_VALUE
        if (battery <= 0) {
            System.out.println("⚠️ Flashlight battery depleted!");
        }
    }

    /**
     * Use method, specifically made for the in-combat use(Blinding the enemy with a flashlight), or Celery SS
     * @param player which player is using the Flashlight
     */
    @Override
    public void use(Player player) {
        if (!isInCelery && !player.isFighting()) {
            System.out.println("❌ The flashlight only works in the basement!");
            return;
        }
        if (battery > 0) {
            battery -= 10;
            System.out.println("🔦 Flashlight beam shines (Battery: " + battery + "%)");
            // Special combat effect if used during fighting
            if (player.isFighting()) {
                System.out.println("The bright light temporarily blinds the enemy!");
                battery -= 10;
            }
        } else {
            System.out.println("❌ The flashlight is out of battery!");
        }
    }

    /**
     * Sets the boolean value of isInCelery to a given value, works like a setter
     * @param inCelery value, which the isInCelery field will be changed to
     */
    public void setInCelery(boolean inCelery) {
        this.isInCelery = inCelery;
        if (inCelery) {
            System.out.println("The flashlight automatically turns on as you enter the dark basement.");
        } else {
            System.out.println("The flashlight automatically turns off as you leave the basement.");
        }
    }

    /**
     * Method used to fully charge the Flashlight's battery(When the player uses Batteries)
     */
    public void recharge() {
        this.battery = 100;
        System.out.println("🔋 Flashlight fully recharged!");
    }

    /**
     * Setter for 'battery'
     * @param level the value 'battery' will be set to
     */
    public void setBatteryLevel(int level) {
        this.battery = Math.min(100, Math.max(0, level));
    }

    /**
     * Getter for 'isInCelery'
     * @return boolean value of 'isInCelery'
     */
    public boolean getIsInCelery() {
        return isInCelery;
    }

    /**
     * Method used to determine, whether the Flashlight's battery had been depleted
     * @return boolean value of whether the Flashlight's battery had been depleted
     */
    public boolean isCharged() {
        return battery > 0;
    }

    /**
     * Getter for 'battery'
     * @return int value of 'battery'
     */
    public int getBatteryLevel() {
        return battery;
    }

}
