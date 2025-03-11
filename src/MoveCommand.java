public class MoveCommand implements Command {
    private World world;
    private int targetRoom;
    public MoveCommand(World world, int targetRoom) {
        this.world = world;
        this.targetRoom = targetRoom;
    }
    @Override
    public void execute() {
        world.moveToRoom(targetRoom);
    }


}
