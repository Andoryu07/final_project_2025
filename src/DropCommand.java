public class DropCommand implements Command {
    private Player player;
    private Item item;
    public DropCommand(Player player, Item item) {
        this.player = player;
        this.item = item;
    }
    @Override
    public void execute() {
        player.dropItem(item);
    }
}
