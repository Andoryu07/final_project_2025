import java.util.Scanner;

public class MoveCommand implements Command {
    private World world;
    private int targetRoom;
    private Scanner scanner;

    public MoveCommand(World world, int targetRoom) {
        this.world = world;
        this.targetRoom = targetRoom;
        this.scanner = scanner;
    }

    @Override
    public void execute() {
        world.moveToRoom(targetRoom, scanner);
    }
}