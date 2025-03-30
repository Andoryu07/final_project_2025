public class Flashlight extends Item {
    private int battery;
    private boolean isInCelery = false;

    public Flashlight() {
        super("Flashlight","A flashlight used to enter dark places. Requires Batteries to function.");
        this.battery = 100;
    }
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
    public void setInCelery(boolean inCelery) {
        this.isInCelery = inCelery;
        if (inCelery) {
            System.out.println("The flashlight automatically turns on as you enter the dark basement.");
        } else {
            System.out.println("The flashlight automatically turns off as you leave the basement.");
        }
    }

    public void recharge() {
        this.battery = 100;
        System.out.println("🔋 Flashlight fully recharged!");
    }

    public boolean isCharged() {
        return battery > 0;
    }
    public int getBatteryLevel() {
        return battery;
    }

}
