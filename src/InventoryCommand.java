public class InventoryCommand implements Command {
    private Player player;
    public InventoryCommand(Player player) {
        this.player = player;
    }

    @Override
    public void execute() {
        System.out.printf("Inventory: %d/%d slots used%n",
                player.getInventory().getItems().size(),
                player.getInventory().getCapacity());
    }
}


