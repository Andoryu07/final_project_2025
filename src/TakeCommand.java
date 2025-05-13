/**
 * Class used to implement the Take command(used to pick up items in rooms, in which the player had dropped them)
 */
public class TakeCommand implements Command {
    /**
     * Instance of Player
     */
    private Player player;
    /**
     * Item instance
     */
    private Item item;

    /**
     * Constructor
     *
     * @param player Who is using the command
     * @param item  Which item is the user/player trying to pick up/take
     */
    public TakeCommand(Player player, Item item) {
        this.player = player;
        this.item = item;

    }

    /**
     * Method declaring what is supposed to occur upon executing this command
     */
    @Override
    public void execute() {
        World world = player.getWorld();
        if (world != null) {
            Room currentRoom = world.getCurrentRoom();
            if (currentRoom != null) {
                // Directly check if the room contains this item instance
                if (currentRoom.getItems().contains(item)) {
                    if (player.getInventory().addItem(item)) {
                        currentRoom.removeItem(item);
                        System.out.println("Picked up: " + item.getName());
                    }
                }
            }
        }
    }
}