public class Flashlight extends Item{
    private int battery;
    public Flashlight() {
        super("Flashlight", "Helps to see in the dark, needs battery to function");
        this.battery = 100;
    }
    @Override
    public void use(Player player) {
        if (battery > 0) {
            battery -= 10;
            System.out.println("Flashlight is turned on, remaining battery: " + battery + "%");
        } else {
            System.out.println("Battery is wasted");
        }
    }
}
