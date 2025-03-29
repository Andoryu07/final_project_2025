import java.util.Scanner;

public class Lock {
    private final String requiredItem;
    private final String lockedMessage;
    private final String unlockPrompt;
    private boolean isLocked;
    private final boolean consumesItem;

    public Lock(String requiredItem, String lockedMessage, String unlockPrompt,boolean consumesItem) {
        this.requiredItem = requiredItem;
        this.lockedMessage = lockedMessage;
        this.unlockPrompt = unlockPrompt;
        this.isLocked = true;
        this.consumesItem = consumesItem;
    }
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
//    protected boolean handleStandardUnlock(Player player, Scanner scanner) {
//        String choice = showUnlockPrompt(player, scanner);
//        if (choice == null) return false;
//
//        choice = scanner.nextLine().trim();
//        if (choice.equals("1")) {
//            setLocked(false);
//            System.out.println(getUnlockPrompt());
//            return true;
//        } else if (choice.equals("2")) {
//            System.out.println("You decide not to proceed.");
//        }
//        return false;
//    }
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
    public String getLockedMessage() {
        return lockedMessage;
    }

    public String getUnlockPrompt() {
        return unlockPrompt;
    }

    public String getRequiredItem() {
        return requiredItem;
    }

    public void setLocked(boolean locked) {
        isLocked = locked;
    }
    public boolean isLocked() {
        return isLocked;
    }

    public void unlock() {
        this.isLocked = false;
    }
}