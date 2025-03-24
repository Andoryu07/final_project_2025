public class RepairTool extends Item {
    public RepairTool() {
        super("Repair Tool", "Repair tool, you can repair stuff. Might come in handy later.");
    }

    @Override
    public void use(Player player) {
        Room currentRoom = player.getCurrentRoom();
        if (currentRoom.getName().equalsIgnoreCase("Cellar")) {
            System.out.println("🔧 You repaired the mechanism in the Cellar!");
        } else {
            System.out.println("❌ There's nothing to repair here.");
        }
    }
}
