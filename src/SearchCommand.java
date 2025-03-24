import java.util.List;
import java.util.Scanner;

public class SearchCommand implements Command {
    private Player player;
    private Scanner scanner;

    public SearchCommand(Player player) {
        this.player = player;
        this.scanner = new Scanner(System.in);
    }

    public void execute() {
        Room currentRoom = player.getCurrentRoom();
        System.out.println("Current room: " + currentRoom.getName());  //  debug statement
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
                List<Item> foundItems = chosenSpot.search();

                if (foundItems != null && !foundItems.isEmpty()) {
                    System.out.println("You found:");
                    for (Item item : foundItems) {
                        System.out.println("- " + item.getName());
                        player.pickUpItem(item);
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



