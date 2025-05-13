import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Class used to implement the Lock located in the Cellar(Player has to insert all 4 GearPieces to proceed)
 */
public class GearLock implements Serializable {
    /**
     * Used for serialization
     */
    private static final long serialVersionUID = 1L;
    /**
     * static int value stating the amount of required gear pieces
     */
    private static final int REQUIRED_GEAR_COUNT = 4;
    /**
     * Set containing strings(names) of already inserted Gears into the mechanism
     */
    private final Set<String> insertedGears = new HashSet<>();
    /**
     * Used to reference the Laboratory room, which is locked until the mechanism in Cellar is unlocked
     */
    private final Room lockedRoom;
    /**
     * Used to reference the room, in which you can operate the mechanism
     */
    private final Room entryRoom;
    /**
     * Constructor
     *
     * @param entryRoom  Representing the room, in which you can operate the mechanism
     * @param lockedRoom Representing the room, which is locked due to the mechanism
     */
    public GearLock(Room entryRoom, Room lockedRoom) {
        this.entryRoom = entryRoom;
        this.lockedRoom = lockedRoom;
        lockedRoom.isLocked = true; // Ensure the room starts locked
    }

    /**
     * Method used to insert player requested gear into the mechanism, if the input is valid and player owns the said gear piece
     *
     * @param gearName Name of the gear piece player wants to insert
     * @param player   Which player is trying to insert the gear piece
     * @return true/false based on whether the insertion was successful/not
     */
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
    /**
     * Method used to unlock the door mechanism, used after all gear pieces are inserted, unlocking the lockedRoom(Laboratory in this case)
     */
    public void unlockDoor() {
        System.out.println("\n🚪 The mechanism clicks... The door to " + lockedRoom.getName() + " is now open!");
        lockedRoom.unlock();
    }

    /**
     * Used to check, if the insertedGears set's size equals the required gear count static int value(if all gear pieces had been inserted) or not
     * @return boolean value whether all gear pieces had been inserted yet or not
     */
    public boolean isUnlocked() {
        return insertedGears.size() == REQUIRED_GEAR_COUNT;
    }

    /**
     * Getter for the set 'insertedGears'
     * @return set 'insertedGears'
     */
    public Set<String> getInsertedGears() {
        return Collections.unmodifiableSet(insertedGears);
    }

    /**
     * Setter for the set 'insertedGears', additionally checks, whether all gear pieces had been inserted yet
     * @param gears sets the contents of the 'insertedGears' set
     */
    public void setInsertedGears(Set<String> gears) {
        this.insertedGears.clear();
        this.insertedGears.addAll(gears);

        if (insertedGears.size() == REQUIRED_GEAR_COUNT) {
            unlockDoor();
        }
    }
}
