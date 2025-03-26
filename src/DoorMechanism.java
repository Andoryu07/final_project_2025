import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class DoorMechanism {
    private static final int TOTAL_GEAR_PIECES = 4;
    private int insertedGears = 0;
    private boolean isUnlocked = false;

    public boolean isUnlocked() {
        return isUnlocked;
    }

    public void interact(Player player) {
        if (isUnlocked) {
            System.out.println("🔓 The door is already unlocked.");
            return;
        }

        List<Item> inventoryItems = player.getInventory().getItems();
        List<GearPiece> gearPieces = new ArrayList<>();

        for (Item item : inventoryItems) {
            if (item instanceof GearPiece) {
                gearPieces.add((GearPiece) item);
            }
        }

        if (gearPieces.isEmpty()) {
            System.out.println("❌ You have no gear pieces to insert.");
            return;
        }

        System.out.println("⚙️ Choose a gear piece to insert:");
        for (int i = 0; i < gearPieces.size(); i++) {
            System.out.println((i + 1) + ". " + gearPieces.get(i).getName());
        }

        Scanner scanner = new Scanner(System.in);
        System.out.print("> ");
        int choice;
        try {
            choice = Integer.parseInt(scanner.nextLine()) - 1;
        } catch (NumberFormatException e) {
            System.out.println("Invalid choice.");
            return;
        }

        if (choice < 0 || choice >= gearPieces.size()) {
            System.out.println("Invalid selection.");
            return;
        }

        GearPiece selectedGear = gearPieces.get(choice);
        player.getInventory().removeItem(selectedGear);

        insertedGears++;
        System.out.println("✅ " + selectedGear.getName() + " inserted into the mechanism.");

        if (insertedGears == TOTAL_GEAR_PIECES) {
            isUnlocked = true;
            System.out.println("🎉 All gear pieces inserted! The Laboratory door is now unlocked.");
        }
    }
}


