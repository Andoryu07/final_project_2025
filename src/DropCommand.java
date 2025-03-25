public class DropCommand implements Command {
    private Player player;
    private Item item;

    public DropCommand(Player player, Item item) {
        this.player = player;
        this.item = item;
    }

    @Override
    public void execute() {
        if (player.getInventory().getItems().contains(item)) {
            player.getInventory().removeItem(item);
            player.getCurrentRoom().addItem(item);
            System.out.println("You dropped " + item.getName() + " on the ground.");
        } else {
            System.out.println("You don't have this item to drop.");
        }
    }
}