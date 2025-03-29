public class Flashlight extends Item {
    private int battery;

    public Flashlight() {
        super("Flashlight","A flashlight used to enter dark places. Requires Batteries to function.");
        this.battery = 100;
    }

    @Override
    public void use(Player player) {
        if (battery > 0) {
            battery -= 10;
            System.out.println("🔦 Flashlight is turned on. Battery: " + battery + "%");
        } else {
            System.out.println("❌ The flashlight is out of battery!");
        }
    }

    public void recharge() {
        this.battery = 100;
        System.out.println("🔋 Flashlight fully recharged!");
    }


    public int getBatteryLevel() {
        return battery;
    }

}
