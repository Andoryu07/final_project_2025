public class Batteries extends Item {
    public Batteries() {
        super("Batteries", "Used to recharge the flashlight.");
    }

    @Override
    public void use(Player player) {
        for (Item item : player.getInventory().getItems()) {
            if (item instanceof Flashlight) {
                Flashlight flashlight = (Flashlight) item;
                flashlight.recharge();
                System.out.println("🔋 Flashlight recharged!");
                return;
            }
        }
        System.out.println("❌ You don't have a flashlight to recharge.");
    }

}
