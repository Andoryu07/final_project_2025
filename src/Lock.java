import java.util.Scanner;

public class Lock {
    private final String requiredItem;
    private final String lockedMessage;
    private final String unlockPrompt;
    private boolean isLocked;

    public Lock(String requiredItem, String lockedMessage, String unlockPrompt) {
        this.requiredItem = requiredItem;
        this.lockedMessage = lockedMessage;
        this.unlockPrompt = unlockPrompt;
        this.isLocked = true;
    }

    public boolean attemptUnlock(Player player, Scanner scanner) {
        if (!isLocked) return true;

        System.out.println("\n" + lockedMessage);

        if (player.hasItem(requiredItem)) {
            System.out.println(unlockPrompt);
            System.out.println("1. Use " + requiredItem);
            System.out.println("2. Cancel");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine();
            if (choice.equals("1")) {
                System.out.println("You used " + requiredItem + " to unlock it!");
                isLocked = false;
                return true;
            }
        } else {
            System.out.println("You need: " + requiredItem);
        }
        return false;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public void unlock() {
        this.isLocked = false;
    }
}