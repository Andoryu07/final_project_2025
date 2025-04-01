/**
 * Class used to create and implement examine Command's behavior
 */
public class ExamineCommand implements Command {
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
     * @param player which player is using the examine command
     * @param item which item is supposed to be examined
     */
    public ExamineCommand(Player player, Item item) {
        this.player = player;
        this.item = item;
    }

    /**
     * Use method, upon use, prints out the picked item's name and description
     */
    @Override
    public void execute() {
        System.out.println("🔍 " + item.getName() + ": " + item.getDescription());
    }
}
