import java.util.HashSet;
import java.util.Set;

public class GearLock {
    private static final int REQUIRED_GEAR_COUNT = 4;
    private final Set<String> insertedGears = new HashSet<>();
    private final Room lockedRoom;
    private final Room entryRoom;

    public GearLock(Room entryRoom, Room lockedRoom) {
        this.entryRoom = entryRoom;
        this.lockedRoom = lockedRoom;
        lockedRoom.isLocked = true; // Ensure the room starts locked
    }

    public boolean insertGear(String gearName, Player player) {
        // First check if the player has the item in their inventory
        boolean hasItem = player.getInventory().getItems().stream()
                .anyMatch(item -> item.getName().equalsIgnoreCase(gearName));

        if (!hasItem) {
            System.out.println("❌ You don't have " + gearName + " in your inventory!");
            return false;
        }

        if (insertedGears.contains(gearName.toUpperCase())) {
            System.out.println("🔩 " + gearName + " has already been inserted.");
            return false;
        }

        // Remove the actual item from player's inventory
        player.getInventory().getItems().removeIf(item ->
                item.getName().equalsIgnoreCase(gearName));

        insertedGears.add(gearName.toUpperCase());
        System.out.println("✅ You inserted " + gearName + " into the mechanism.");

        if (insertedGears.size() == REQUIRED_GEAR_COUNT) {
            unlockDoor();
        } else {
            System.out.println("⚙️ " + (REQUIRED_GEAR_COUNT - insertedGears.size()) + " more gear pieces needed.");
        }
        return true;
    }

    private void unlockDoor() {
        System.out.println("\n🚪 The mechanism clicks... The door to " + lockedRoom.getName() + " is now open!");
        lockedRoom.unlock();
    }

    public boolean isUnlocked() {
        return insertedGears.size() == REQUIRED_GEAR_COUNT;
    }
}