/**
 * Class used to implement the insert Command, which is used for inserting Gear pieces into a mechanism
 */
public class InsertCommand implements Command {
    /**
     * World instance
     */
    private final World world;
    /**
     * Name of the gear piece player is attempting to insert
     */
    private final String gearName;
    /**
     * Player instance
     */
    private final Player player;

    /**
     * Constructor
     * @param world which world are we in
     * @param gearName name of the gear piece player is attempting to insert
     * @param player which player is attempting to use the insert command
     */
    public InsertCommand(World world, String gearName, Player player) {
        this.world = world;
        this.gearName = gearName;
        this.player = player;
    }

    /**
     * What is supposed to happen upon using this command
     */
    @Override
    public void execute() {
        world.insertGearPiece(gearName, player);
    }
}
