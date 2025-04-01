/**
 * Class used to implement the Use command(for items)
 */
public class UseCommand implements Command {
    /**
     * Player instance
     */
    private Player player;
    /**
     * Item instance
     */
    private Item item;

    /**
     * Constructor
     * @param player Who is using the command
     * @param item Which item is the user/player trying to use
     */
    public UseCommand(Player player, Item item) {
        this.player = player;
        this.item = item;
    }

    /**
     * Declares, what is about to occur upon using this command
     */
    @Override
    public void execute() {
        item.use(player);
    }


}
