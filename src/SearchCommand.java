import java.util.List;
import java.util.Scanner;

/**
 * Class used to implement the search command
 */
public class SearchCommand implements Command {
    /**
     * Instance of Player
     */
    private Player player;
    /**
     * Scanner
     */
    private Scanner scanner;

    /**
     * Constructor
     * @param player Specifies the player using the command
     */
    public SearchCommand(Player player) {
        this.player = player;
        this.scanner = new Scanner(System.in);
    }

    /**
     * Method used to implement the search command, its behavior under certain circumstances, etc.(Prints out available search spots in the current room and handles player choice)
     */
    public void execute() {
        Room currentRoom = player.getCurrentRoom();
        List<SearchSpot> unsearchedSpots = currentRoom.getUnsearchedSpots();

        if (unsearchedSpots.isEmpty()) {
            System.out.println("There is nothing left to search here.");
            return;
        }

        System.out.println("\n🔍 Searchable places:");
        for (int i = 0; i < unsearchedSpots.size(); i++) {
            System.out.println("[" + (i + 1) + "] " + unsearchedSpots.get(i).getName());
        }

        System.out.print("\nEnter the number of the spot you want to search: ");
        try {
            int choice = Integer.parseInt(scanner.nextLine()) - 1;

            if (choice >= 0 && choice < unsearchedSpots.size()) {
                SearchSpot chosenSpot = unsearchedSpots.get(choice);

                // First check if we can access this spot
                if (!currentRoom.canSearchSpot(chosenSpot.getName(), player, scanner)) {
                    return; // Lock check handles all messaging
                }

                // If we get here, the spot is accessible
                List<Item> foundItems = chosenSpot.search();
                if (foundItems != null && !foundItems.isEmpty()) {
                    System.out.println("You found:");
                    for (Item item : foundItems) {
                        System.out.println("- " + item.getName());
                        player.getInventory().addItem(item);
                        System.out.println("You've picked up: " + item.getName());
                    }
                } else {
                    System.out.println("You found nothing.");
                }
            } else {
                System.out.println("Invalid choice.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
        }
    }
}