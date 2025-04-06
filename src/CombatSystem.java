import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

/**
 * Class used to implement the combat system of the game
 */
public class CombatSystem {
    /**
     * Instance of Player
     */
    private Player player;
    /**
     * Instance of Enemy
     */
    private Enemy enemy;
    /**
     * Scanner
     */
    private Scanner scanner;
    /**
     * int value of how many player's turns had elapsed since the player had last used the 'Blind enemy' in combat
     */
    private int turnsSinceLastFlashlightUse = 3;
    /**
     * Runnable(works like an interface, has a use() method, we can change the behavior of the said method(what to run/do), will be used for loading the latest checkpoint)
     */
    private final Runnable checkpointLoader;

    /**
     * Constructor
     * @param player Specifies the player
     * @param enemy Specifies the enemy
     */
    public CombatSystem(Player player, Enemy enemy,Scanner scanner,Runnable checkpointLoader) {
        this.player = player;
        this.enemy = enemy;
        this.scanner = scanner;
        this.checkpointLoader = checkpointLoader;

    }

    /**
     * Method used to start combat, informing the player, and starting the fighting loop
     */
    public void startCombat() {
        player.setFighting(true);
        System.out.println("\n⚔️ Combat started against " + enemy.getName() + "! ⚔️");

        while (player.getHealth() > 0 && enemy.getHealth() > 0) {
            playerTurn();
            if (enemy.getHealth() <= 0) break;

            enemyTurn();
        }

        endCombat();
    }

    /**
     * Method to simulate player's turn, giving him the health information and options to proceed with during combat
     */
    private void playerTurn() {
        if (player.isDefeated()) return; // Skip if player already dead

        System.out.println("\nYour turn! Health: " + player.getHealth());
        System.out.println("Enemy health: " + enemy.getHealth());

        // Show equipped weapon status
        if (player.getEquippedWeapon() != null) {
            Weapon w = player.getEquippedWeapon();
            String ammoStatus = w.infiniteUse ? "∞" : w.getCurrentAmmo() + "/" + w.getMaxAmmo();
            System.out.printf("Weapon: %s (%s ammo)%n", w.getName(), ammoStatus);
        }

        printCombatOptions();
        processPlayerChoice(scanner.nextLine());
    }

    /**
     * Method used to print player's choices during his turn
     */
    private void printCombatOptions() {
        System.out.println("1. Attack with equipped weapon");
        System.out.println("2. Heal");
        System.out.println("3. Block (reduce next damage by 50%)"); // New option
        if (player.getEquippedWeapon() != null && !player.getEquippedWeapon().isInfiniteUse()) {
            System.out.println("4. Reload");
        }
        System.out.println("5. Equip weapon");
        if (player.hasItem("Flashlight") && turnsSinceLastFlashlightUse >= 3) {
            System.out.println("6. Blind enemy with flashlight");
        }
        System.out.println("0. Give up");
        System.out.print("Choose an action: ");
    }

    /**
     * Method used to register player's choice, and acting upon it
     * @param choice Player's choice
     */
    private void processPlayerChoice(String choice) {
        switch (choice) {
            case "1":
                attackWithWeapon();
                break;
            case "2":
                useHealingItem();
                break;
            case "3": // New block option
                player.setBlocking(true);
                System.out.println("You put up your defense, ready to block the next attack(Reduces damage by 50%)!");
                break;
            case "4": // Reload
                if (player.getEquippedWeapon() != null &&
                        !player.getEquippedWeapon().isInfiniteUse()) {
                    reloadWeapon();
                } else {
                    System.out.println("You can't reload this weapon!");
                    playerTurn();
                }
                break;
            case "5": // Equip
                showEquipMenu();
                break;
            case "6":
                if (player.hasItem("Flashlight") && turnsSinceLastFlashlightUse >= 3) {
                    useFlashlight();
                } else {
                    System.out.println("Invalid choice!");
                    playerTurn();
                }
                break;
            case "0": // Give up
                System.out.println("You surrender to the enemy...");
                player.takeDamage(player.getHealth()); // Instant death
                handleDefeat();
                return; // Immediately exit combat
            default:
                System.out.println("Invalid choice!");
                playerTurn();
        }
    }

    /**
     * Used to handle the case of player being defeated in combat, giving him the choice to reload his last save(if one exists), or exit the game
     */
    private void handleDefeat() {
        System.out.println("\n☠️ You were defeated! ☠️");
        System.out.println("1. Load last checkpoint");
        System.out.println("2. Exit game");
        System.out.print("Choose an option: ");

        String input = scanner.nextLine();
        if (input.equals("1")) {
            checkpointLoader.run();
        } else {
            System.out.println("Game over. Exiting the game.");
            System.exit(0);
        }
    }

    /**
     * Method, which is called upon the player chooses 'Equip weapon', giving the player the choice to equip any weapon from his inventory, registering his choice
     */
    private void showEquipMenu() {
        List<Weapon> weapons = player.getInventory().getItems().stream()
                .filter(item -> item instanceof Weapon)
                .map(item -> (Weapon)item)
                .toList();

        if (weapons.isEmpty()) {
            System.out.println("You have no weapons!");
            playerTurn();
            return;
        }

        System.out.println("\nAvailable weapons:");
        for (int i = 0; i < weapons.size(); i++) {
            Weapon w = weapons.get(i);
            String ammoStatus = w.isInfiniteUse() ? "∞" : w.getCurrentAmmo() + "/" + w.getMaxAmmo();
            System.out.printf("%d. %s (%s ammo)%n", i+1, w.getName(), ammoStatus);
        }

        System.out.print("Choose weapon to equip: ");
        try {
            int choice = Integer.parseInt(scanner.nextLine()) - 1;
            if (choice >= 0 && choice < weapons.size()) {
                player.equipWeapon(weapons.get(choice));
            } else {
                System.out.println("Invalid choice!");
            }
        } catch (NumberFormatException e) {
            System.out.println("Please enter a number!");
        }
        playerTurn();
    }

    /**
     * Simulates the 'Attack with weapon' choice, uses the player's equipped weapon and deals the damage to the enemy
     */
    private void attackWithWeapon() {
        Weapon weapon = player.getEquippedWeapon();
        if (weapon == null) {
            System.out.println("You have no weapon equipped!");
            playerTurn();
            return;
        }

        if (weapon.hasAmmo()) {
            player.attack(enemy);
        } else {
            System.out.println("Weapon has no ammo! Choose another action.");
            playerTurn();
        }
    }

    /**
     * Simulates the 'Use Healing item' choice, allowing the player to use any healing item in his inventory, healing himself for a bit of health
     */
    private void useHealingItem() {
        List<Item> healingItems = player.getInventory().getItems().stream()
                .filter(item -> item instanceof HealingSerum || item instanceof Bandage)
                .toList();

        if (healingItems.isEmpty()) {
            System.out.println("You have no healing items!");
            playerTurn();
            return;
        }

        System.out.println("Available healing items:");
        for (int i = 0; i < healingItems.size(); i++) {
            System.out.println((i + 1) + ". " + healingItems.get(i).getName());
        }

        System.out.print("Choose item to use: ");
        try {
            int choice = Integer.parseInt(scanner.nextLine()) - 1;
            if (choice >= 0 && choice < healingItems.size()) {
                Item item = healingItems.get(choice);
                item.use(player);
            } else {
                System.out.println("Invalid choice!");
                playerTurn();
            }
        } catch (NumberFormatException e) {
            System.out.println("Please enter a number!");
            playerTurn();
        }
    }

    /**
     * Simulates the 'Reload weapon' option, allowing the player to reload his weapon, if possible
     */
    private void reloadWeapon() {
        Weapon weapon = player.getEquippedWeapon();
        if (weapon == null) {
            System.out.println("You have no weapon equipped!");
            playerTurn();
            return;
        }

        String ammoType = weapon instanceof Pistol ? "Pistol Ammo" : "Shotgun Shells";
        List<Item> ammoItems = player.getInventory().getItems().stream()
                .filter(item -> item.getName().equalsIgnoreCase(ammoType))
                .toList();

        if (ammoItems.isEmpty()) {
            System.out.println("You have no " + ammoType + "!");
            playerTurn();
            return;
        }

        Ammo ammo = (Ammo) ammoItems.get(0);
        weapon.reload(ammo.getAmount());
        player.getInventory().removeItem(ammo);
        System.out.println("Reloaded " + weapon.getName() + "!");
    }

    /**
     * Simulates the 'Blind enemy with flashlight' option, allowing the player to blind the enemy for 1 turn, if the requirements had been met
     */
    private void useFlashlight() {
        Flashlight flashlight = (Flashlight)player.findItemInInventory("Flashlight");
        if (flashlight == null) {
            System.out.println("You don't have a flashlight!");
            return;
        }
        // Lets the flashlight handle all logic including battery check
        flashlight.use(player);
        if (flashlight.isCharged()) {
            turnsSinceLastFlashlightUse = 0;
            System.out.println("Enemy is blinded for their next turn!");
        }
    }

    /**
     * Simulates the enemy's turn - performs a random attack, unless blinded, in which case skips turn
     */
    private void enemyTurn() {
        if (turnsSinceLastFlashlightUse == 0) {
            System.out.println(enemy.getName() + " is blinded and misses their turn!");
            turnsSinceLastFlashlightUse++;
        } else {
            AttackResult attack = enemy.performRandomAttack(); // Changed from Enemy.AttackResult
            System.out.printf("%s uses %s for %d damage%n", enemy.getName(), attack.attackName, attack.damage);
            //damage reduction will happen inside takeDamage() if blocking
            player.takeDamage(attack.damage);
            turnsSinceLastFlashlightUse++;
        }
    }

    /**
     * Method used to end combat, handling the outcome depending on who had reached 0 health first
     */
    private void endCombat() {
        if (player.getHealth() <= 0) {
            System.out.println("You were defeated by " + enemy.getName() + "!");
            player.setFighting(false);
            handleDefeat();
        } else {
            player.setFighting(false);
            System.out.println("You defeated " + enemy.getName() + "!");
            if (enemy instanceof Stalker) {
                ((Stalker) enemy).retreat();
            }
            // Handle loot/drops
        }
    }

    /**
     * Setter for 'turnsSinceLastFlashlightUse'
     * @param turns what to set the 'turnsSinceLastFlashlightUse' to
     */
    public void setTurnsSinceLastFlashlightUse(int turns) {
        this.turnsSinceLastFlashlightUse = turns;
    }

    /**
     * Setter for scanner, used for testing
     * @param scanner which scanner to set
     */
    public void setScanner(Scanner scanner) {
        this.scanner = scanner;
    }

    /**
     * Getter for turnsSinceLastFlashlightUse
     * @return value of turnsSinceLastFlashlightUse
     */
    public int getTurnsSinceLastFlashlightUse() {
        return turnsSinceLastFlashlightUse;
    }
}