public class InventoryCommand implements Command {
    private Player player;
    public InventoryCommand(Player player) {
        this.player = player;
    }

    @Override
    public void execute() {
        System.out.println("\nYour inventory:");
        player.getInventory().printInventory();
    }
}


