import java.util.List;
import java.util.Scanner;

public class CombatSystem {
    private Player player;
    private Enemy enemy;
    private Scanner scanner;
    private int turnsSinceLastFlashlightUse = 3;

    public CombatSystem(Player player, Enemy enemy) {
        this.player = player;
        this.enemy = enemy;
        this.scanner = new Scanner(System.in);
    }

    public void startCombat() {
        System.out.println("\n⚔️ Combat started against " + enemy.getName() + "! ⚔️");

        while (player.getHealth() > 0 && enemy.getHealth() > 0) {
            playerTurn();
            if (enemy.getHealth() <= 0) break;

            enemyTurn();
        }

        endCombat();
    }

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


    private void printCombatOptions() {
        System.out.println("1. Attack with equipped weapon");
        System.out.println("2. Heal");
        if (player.getEquippedWeapon() != null && !player.getEquippedWeapon().isInfiniteUse()) {
            System.out.println("3. Reload");
        }
        System.out.println("4. Equip weapon");
        if (player.hasItem("Flashlight") && turnsSinceLastFlashlightUse >= 3) {
            System.out.println("5. Blind enemy with flashlight");
        }

        System.out.println("0. Give up");
        System.out.print("Choose an action: ");
    }

    private void processPlayerChoice(String choice) {
        switch (choice) {
            case "1":
                attackWithWeapon();
                break;
            case "2":
                useHealingItem();
                break;
            case "3": // Reload
                if (player.getEquippedWeapon() != null &&
                        !player.getEquippedWeapon().isInfiniteUse()) {
                    reloadWeapon();
                } else {
                    System.out.println("You can't reload this weapon!");
                    playerTurn();
                }
                break;
            case "4": // Equip
                showEquipMenu();
                break;
             case "5":
                if (player.hasItem("Flashlight") && turnsSinceLastFlashlightUse >= 3) {
                    useFlashlight();
                } else {
                    System.out.println("Invalid choice!");
                    playerTurn();
                }
                break;
            case "09": // Give up
                System.out.println("You surrender to the enemy...");
                player.takeDamage(player.getHealth()); // Instant death
                handleDefeat();
                return; // Immediately exit combat
            default:
                System.out.println("Invalid choice!");
                playerTurn();
        }
    }

    private void handleDefeat() {
        System.out.println("\n☠️ You were defeated! ☠️");
        System.out.println("1. Load last checkpoint");
        System.out.println("2. Exit game");
        System.out.print("Choose an option: ");

        String input = scanner.nextLine();
        if (input.equals("1")) {
            // TODO: Implement checkpoint loading
            System.out.println("Checkpoint loading not yet implemented");
        }
        System.exit(0); // Temporary until checkpoint system exists
    }
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
                System.out.println("Equipped " + weapons.get(choice).getName());
            } else {
                System.out.println("Invalid choice!");
            }
        } catch (NumberFormatException e) {
            System.out.println("Please enter a number!");
        }
        playerTurn();
    }
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

    private void useFlashlight() {
        Flashlight flashlight = (Flashlight) player.getInventory().findItem("Flashlight");
        if (flashlight.getBatteryLevel() >= 20) {
            flashlight.use(player);
            turnsSinceLastFlashlightUse = 0;
            System.out.println("You blinded the enemy! They'll miss their next attack.");
            playerTurn(); // Get another turn
        } else {
            System.out.println("Not enough battery!");
            playerTurn();
        }
    }

    private void enemyTurn() {
        if (turnsSinceLastFlashlightUse == 0) {
            System.out.println(enemy.getName() + " is blinded and misses their turn!");
            turnsSinceLastFlashlightUse++;
        } else {
            enemy.performRandomAttack(player);
            turnsSinceLastFlashlightUse++;
        }
    }

    private void endCombat() {
        if (player.getHealth() <= 0) {
            System.out.println("You were defeated by " + enemy.getName() + "!");
            // Handle game over
        } else {
            System.out.println("You defeated " + enemy.getName() + "!");
            if (enemy instanceof Stalker) {
                ((Stalker) enemy).retreat();
            }
            // Handle loot/drops
        }
    }
}