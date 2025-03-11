public class TakeCommand implements Command {
    private Player player;
    private Item item;
    public TakeCommand(Player player, Item item) {
        this.player = player;
        this.item = item;
    }
    @Override
    public void execute() {
        player.pickUpItem(item);
    }


}
