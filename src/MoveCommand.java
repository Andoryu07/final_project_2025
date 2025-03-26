public class MoveCommand implements Command {
    private World world;
    private int targetRoom;
    public MoveCommand(World world, int targetRoom) {
        this.world = world;
        this.targetRoom = targetRoom;
    }
    @Override
    public void execute() {
        if (targetRoom == 11){
            Room currentRoom = world.getCurrentRoom();
            if (currentRoom.getDoorMechanism() != null && !currentRoom.getDoorMechanism().isUnlocked()){
                System.out.println("❌ The door to the Laboratory is locked. You must insert all gear pieces first.");

            } else {
                world.moveToRoom(targetRoom);
            }
        } else {
            world.moveToRoom(targetRoom);
        }
    }


}
