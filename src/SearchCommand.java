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
                if (!currentRoom.canSearchSpot(chosenSpot.getName(), player, scanner)) {
                    System.out.println("You cannot search this spot yet!");
                    return;
                }
                List<Item> foundItems = chosenSpot.search();

                if (foundItems != null && !foundItems.isEmpty()) {
                    System.out.println("You found:");
                    handleFoundItems(foundItems, currentRoom);
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

    private void handleFoundItems(List<Item> foundItems, Room currentRoom) {
        for (Item item : foundItems) {
            if (player.getInventory().canAddItem()) {  // Check if inventory has space
                player.getInventory().addItem(item);
                System.out.println("- " + item.getName());
                System.out.println("You've picked up: " + item.getName());
            } else {
                currentRoom.addItem(item);
                System.out.println("- " + item.getName() + " (inventory full, item dropped to the ground)");
            }
        }
    }
}