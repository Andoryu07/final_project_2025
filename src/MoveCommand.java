import java.util.Scanner;

/**
 * Class used to implement the Move command, allowing player to move to adjacent/neighbor rooms
 */
public class MoveCommand implements Command {
    /**
     * World instance
     */
    private World world;
    /**
     * Index of the room player wants to move into
     */
    private int targetRoom;
    /**
     * Scanner
     */
    private Scanner scanner;

    /**
     * Scanner
     * @param world World instance
     * @param targetRoom index of the room player wants to move into
     * @param scanner Scanner
     */
    public MoveCommand(World world, int targetRoom,Scanner scanner) {
        this.world = world;
        this.targetRoom = targetRoom;
        this.scanner = scanner;
    }

    /**
     * Executes the command, attempts to execute the method to move player into desired room
     */
    @Override
    public void execute() {
        world.moveToRoom(targetRoom, scanner);
    }
}