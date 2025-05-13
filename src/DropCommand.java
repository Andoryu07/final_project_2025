/**
 * Class used to implement and specify the behavior of the Drop command(Used to drop items from player's inventory)
 */
public class DropCommand implements Command {
    /**
     * Instance of Player
     */
    private Player player;
    /**
     * Instance of Item
     */
    private Item item;

    /**
     * Constructor
     * @param player Specifies the player
     * @param item Specifies the item
     */
    public DropCommand(Player player, Item item) {
        this.player = player;
        this.item = item;
    }

    /**
     * Used to implement the command's behavior, contains the condition of not being able to drop Knife item(Player needs to have some sort of defense in an attack at all time)
     */
    @Override
    public void execute() {
        if (player.getInventory().getItems().contains(item) && !item.getName().equals("Knife")) {
            player.getInventory().removeItem(item);
            player.getCurrentRoom().addItem(item, player.getX(), player.getY());
            System.out.println("You dropped " + item.getName() + " on the ground.");
        } else {
            System.out.println("You don't have this item to drop or you can't drop this item.");
        }
    }
}