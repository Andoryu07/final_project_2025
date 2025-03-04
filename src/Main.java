import java.util.Scanner;
//Logic of the game
public class Main {

    public static void main(String[] args) {
        World world = new World();
        world.loadFromFile("src/game_layout.txt");//Loading the rooms

        Scanner scanner = new Scanner(System.in);
        boolean running = true;
        //Main game loop, runs until the player requests it to stop
        while (running) {
            world.printCurrentRoom();//Prints current room and available rooms
            System.out.print("\nEnter the number of the room you would like to move to(or type 'exit' to end program): ");
            String input = scanner.nextLine();
            //Checks, whether the player wants to exit the game or not
            if (input.equalsIgnoreCase("exit")) {
                running = false;
                System.out.println("\nExiting the game...");
            } else {
                try {
                    int roomIndex = Integer.parseInt(input);//transfers the input into a number
                    world.moveToRoom(roomIndex);//attempts the travel
                } catch (NumberFormatException e) {
                    System.out.println("\nInvalid input, please try again.");
                }
            }
        }

        scanner.close();//Closing the scanner upon exiting
    }
}
