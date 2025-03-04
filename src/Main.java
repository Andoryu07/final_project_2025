public class Main {

        public static void main(String[] args) {
            World world = new World();
            world.loadFromFile("src/game_layout.txt");

            //Test to print out the current room
            world.printCurrentRoom();

            //Moving to different rooms
            world.moveToRoom(1);
            world.moveToRoom(5);
            world.moveToRoom(0);
        }

}
