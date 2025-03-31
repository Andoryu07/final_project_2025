import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class GearLock implements Serializable {
    private static final long serialVersionUID = 1L;
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
        // First try to find by display name ("Gear Piece 1")
        Item item = player.findItemInInventory(gearName);

        // If not found, try to find by internal name ("GEAR_PIECE_1")
        if (item == null) {
            String internalName = gearName.toUpperCase().replace(" ", "_")
                    .replace("GEAR", "GEAR")
                    .replace("PIECE", "PIECE");
            item = player.findItemInInventory(internalName);
        }

        if (item == null || !(item instanceof GearPiece)) {
            System.out.println("❌ You don't have " + gearName + " in your inventory!");
            return false;
        }

        GearPiece gear = (GearPiece) item;
        String internalGearName = gear.getInternalName();

        // Rest of the method uses internalGearName for logic
        if (insertedGears.contains(internalGearName)) {
            System.out.println("🔩 " + gear.getName() + " has already been inserted.");
            return false;
        }

        player.getInventory().removeItem(gear);
        insertedGears.add(internalGearName);
        System.out.println("✅ You inserted " + gear.getName() + " into the mechanism.");

        if (insertedGears.size() == REQUIRED_GEAR_COUNT) {
            unlockDoor();
        } else {
            System.out.println("⚙️ " + (REQUIRED_GEAR_COUNT - insertedGears.size()) + " more gear pieces needed.");
        }
        return true;
    }

        public void unlockDoor() {
            System.out.println("\n🚪 The mechanism clicks... The door to " + lockedRoom.getName() + " is now open!");
            lockedRoom.unlock();
        }

        public boolean isUnlocked () {
            return insertedGears.size() == REQUIRED_GEAR_COUNT;
        }
    public Set<String> getInsertedGears() {
        return Collections.unmodifiableSet(insertedGears);
    }

    public void setInsertedGears(Set<String> gears) {
        this.insertedGears.clear();
        this.insertedGears.addAll(gears);

        if (insertedGears.size() == REQUIRED_GEAR_COUNT) {
            unlockDoor();
        }
    }
    }
