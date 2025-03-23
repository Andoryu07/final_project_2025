import java.util.List;
import java.util.Scanner;

public class SearchCommand implements Command {
    private Player player;
    private Scanner scanner;
    public SearchCommand(Player player) {
        this.player = player;
        this.scanner = new Scanner(System.in);
    }

    @Override
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
                Item foundItem = chosenSpot.search();
                if (foundItem != null) {
                    System.out.println("You found: " + foundItem.getName());
                    player.pickUpItem(foundItem);
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


