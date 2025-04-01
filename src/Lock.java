import java.io.Serializable;
import java.util.Scanner;

/**
 * Method used to implement the Locks in the game, their fields, behavior and values
 */
public class Lock implements Serializable {
    /**
     * Used for serialization
     */
    private static final long serialVersionUID = 1L;
    /**
     * String(name) of the required item to unlock this lock
     */
    private final String requiredItem;
    /**
     * message for if the lock's status is locked
     */
    private final String lockedMessage;
    /**
     * Message for if the player unlocks the lock
     */
    private final String unlockPrompt;
    /**
     * Boolean value indicating whether the lock is unlocked/locked
     */
    private boolean isLocked;
    /**
     * Boolean value indicating whether unlocking the lock consumes the item required
     */
    private final boolean consumesItem;

    /**
     * Constructor
     * @param requiredItem String(name) of the required item to unlock this lock
     * @param lockedMessage Message for if the player unlocks the lock
     * @param unlockPrompt Boolean value indicating whether the lock is unlocked/locked
     * @param consumesItem Boolean value indicating whether unlocking the lock consumes the item required
     */
    public Lock(String requiredItem, String lockedMessage, String unlockPrompt,boolean consumesItem) {
        this.requiredItem = requiredItem;
        this.lockedMessage = lockedMessage;
        this.unlockPrompt = unlockPrompt;
        this.isLocked = true;
        this.consumesItem = consumesItem;
    }

    /**
     * Method handling interacting with and attempting to unlock the lock, checking if the player owns required item
     * @param player Who is attempting to unlock the lock
     * @param scanner Scanner
     * @return the player's choice, null if player doesn't own the required item
     */
    protected String showUnlockPrompt(Player player, Scanner scanner) {
        System.out.println(getLockedMessage());

        if (!player.hasItem(getRequiredItem())) {
            System.out.println("You need: " + getRequiredItem());
            return null;  // Return null if player doesn't have the item
        }

        System.out.println("1. Use " + getRequiredItem());
        System.out.println("2. Back away");
        System.out.print(">> ");

        return scanner.nextLine().trim();  // Return the player's choice
    }

    /**
     * Method used for attempting to unlock the door, applying changes if successful
      * @param player Who's attempting to unlock the lock
     * @param scanner Scanner
     * @return boolean value true/false, based on whether the lock had been unlocked or not
     */
public boolean attemptUnlock(Player player, Scanner scanner) {
    System.out.println(lockedMessage);

    if (!player.hasItem(requiredItem)) {
        System.out.println("Required item: " + requiredItem);
        return false;
    }

    System.out.println("1. Use " + requiredItem);
    System.out.println("2. Back away");
    System.out.print(">> ");

    while (true) {
        String choice = scanner.nextLine().trim();
        if (choice.equals("1")) {
            isLocked = false;
            System.out.println(unlockPrompt);
            // Consume the item if needed
            if (consumesItem) {
                player.removeItem(requiredItem);
                System.out.println("(The " + requiredItem + " has been used up)");
            }
            return true;
        } else if (choice.equals("2")) {
            System.out.println("You decide not to proceed.");
            return false;
        }
        System.out.print("Invalid choice. Enter 1 or 2: ");
    }
}

    /**
     * Getter for 'lockedMessage'
     * @return value of 'lockedMessage'
     */
    public String getLockedMessage() {
        return lockedMessage;
    }

    /**
     * Getter for 'unlockPrompt'
     * @return value of 'unlockPrompt'
     */
    public String getUnlockPrompt() {
        return unlockPrompt;
    }

    /**
     * Getter for 'requiredItem'
     * @return value of 'requiredItem'
     */
    public String getRequiredItem() {
        return requiredItem;
    }

    /**
     * Setter for 'isLocked'
     * @param locked sets the value of 'isLocked'
     */
    public void setLocked(boolean locked) {
        isLocked = locked;
    }
    /**
     * Getter for 'isLocked'
     * @return value of 'isLocked'
     */
    public boolean isLocked() {
        return isLocked;
    }

}