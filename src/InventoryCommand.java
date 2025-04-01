/**
 * Class used to implement the Inventory command(Upon use, the items in player's inventory will be printed out)
 */
public class InventoryCommand implements Command {
    /**
     * Instance of Player
     */
    private Player player;

    /**
     * Constructor
     * @param player Who had used the command
     */
    public InventoryCommand(Player player) {
        this.player = player;
    }

    /**
     * Determines, what happens upon using the command(Prints out items in the user/player's inventory + storage left)
     */
    @Override
    public void execute() {
        System.out.printf("Inventory: %d/%d slots used%n",
                player.getInventory().getItems().size(),
                player.getInventory().getCapacity());
        for (Item item : player.getInventory().getItems()) {
            System.out.println(item.getName());
        }
    }
}


