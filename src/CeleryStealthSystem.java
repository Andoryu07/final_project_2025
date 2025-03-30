import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CeleryStealthSystem {
    private static final int GRID_SIZE = 5;
    private int playerX = 0; // A=0, B=1, etc.
    private int playerY = 4; // 1=0, 2=1, etc. (so 5A(starting location) is [0][4])
    private boolean[][] zombiePositions;
    private Player player;
    private Flashlight flashlight;
    private boolean lightsOn = false;

    public CeleryStealthSystem(Player player) {
        this.player = player;
        this.flashlight = (Flashlight) player.findItemInInventory("Flashlight");
        initializeZombiePositions();
    }

    private void initializeZombiePositions() {
        zombiePositions = new boolean[GRID_SIZE][GRID_SIZE];
        // Mark zombie positions (x,y)
        zombiePositions[3][0] = true; // 1D
        zombiePositions[2][1] = true; // 2C
        zombiePositions[4][2] = true; // 3E
        zombiePositions[1][3] = true; // 4B
        zombiePositions[0][4] = true; // 5A (starting position is safe)
    }

    public void startSequence(Scanner scanner) {
        flashlight.setInCelery(true);
        try {
            System.out.println("\n=== CELERY STEALTH SEQUENCE ===");
            System.out.println("You enter the pitch black cellar. You'll need to navigate to the power box somewhere in the basement.");
            System.out.println("Commands: forward, back, left, right, quit");
            while (!lightsOn && player.getHealth() > 0) {
                    // Check battery at start of each turn
                    if (!handleBatteryDepletion()) {
                        break;
                    }
                printCurrentPosition();
                System.out.print("Choose direction: ");
                String input = scanner.nextLine().trim().toLowerCase();

                switch (input) {
                    case "forward":
                        move(0, -1); // Decrease Y
                        break;
                    case "back":
                        move(0, 1); // Increase Y
                        break;
                    case "left":
                        move(-1, 0); // Decrease X
                        break;
                    case "right":
                        move(1, 0); // Increase X
                        break;
                    case "quit":
                        System.out.println("You quickly retreat from the cellar!");
                        return;
                    default:
                        System.out.println("Invalid command! Use: forward/back/left/right/quit");
                }

                // Check if reached power box
                if (playerX == 3 && playerY == 2) { // 3D
                    attemptPowerBoxRepair(scanner);
                }
            }

            if (lightsOn) {
                startZombieCombat();
            }
        } finally {
            flashlight.setInCelery(false); //Ensures the state always changes back to normal
        }
    }

    private void move(int dx, int dy) {
        int newX = playerX + dx;
        int newY = playerY + dy;

        // Check boundaries
        if (newX < 0 || newX >= GRID_SIZE || newY < 0 || newY >= GRID_SIZE) {
            System.out.println("Can't move that way - you'd hit a wall!");
            return;
        }

        // Check for zombies
        if (zombiePositions[newX][newY]) {
            System.out.println("You stumbled right into a zombie!");
            player.takeDamage(player.getHealth()); // Instant death
            System.exit(0);
            //TODO: Handle game over differently
        }

        // Move is valid
        playerX = newX;
        playerY = newY;
        flashlight.useBattery(10); // Each move uses 5% battery
        System.out.println("Moved to position " + getPositionString());

    }
    private boolean handleBatteryDepletion() {
        if (flashlight.getBatteryLevel() > 0) {
            return true; // Still has battery
        }
        System.out.println("\n⚠️ Flashlight battery depleted!");
        // Check for batteries in inventory
        if (player.hasItem("Batteries")) {
            System.out.println("Automatically replacing batteries...");
            player.removeItem("Batteries");
            flashlight.recharge();
            return true;
        } else {
            System.out.println("No batteries left! You're plunged into darkness!");
            System.out.println("The zombies find you in the dark...");
            player.takeDamage(player.getHealth()); // Instant death
            return false;
        }
    }
    private void attemptPowerBoxRepair(Scanner scanner) {
        if (player.hasItem("Repair Tool")) {
            System.out.println("You found the power box! Repair it? (yes/no)");
            String choice = scanner.nextLine().trim().toLowerCase();
            if (choice.equals("yes")) {
                player.removeItem("Repair Tool");
                lightsOn = true;
                System.out.println("You repaired the power box! The lights flicker on...");
                System.out.println("Suddenly, 5 zombies emerge from the darkness!");
                startZombieCombat();
            } else {
                System.out.println("You leave the power box untouched.");
            }
        } else {
            System.out.println("You found the power box, but you don't have a Repair Tool!");
        }
    }

    private void startZombieCombat() {
        Room currentRoom = player.getCurrentRoom();
        List<Enemy> zombies = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            zombies.add(new Zombie(currentRoom));
        }
        // Fight each zombie one by one
        for (Enemy zombie : zombies) {
            System.out.println("\nA zombie shambles towards you!");
            CombatSystem combat = new CombatSystem(player, zombie);
            combat.startCombat();
        }
        System.out.println("\nYou've defeated all the zombies in the cellar!");
        // The game will automatically return to normal play after this

    }

    private String getPositionString() {
        char col = (char) ('A' + playerX);
        int row = playerY + 1;
        return row + "" + col;
    }

    private void printCurrentPosition() {
        printMap();
        System.out.println("\nCurrent position: " + getPositionString());
        System.out.println("Flashlight battery: " + flashlight.getBatteryLevel() + "%");
        System.out.println("Available directions: " + getAvailableDirections());
    }

    private String getAvailableDirections() {
        List<String> directions = new ArrayList<>();

        if (playerY > 0) directions.add("forward");
        if (playerY < GRID_SIZE - 1) directions.add("back");
        if (playerX > 0) directions.add("left");
        if (playerX < GRID_SIZE - 1) directions.add("right");

        return directions.isEmpty() ? "none" : String.join(", ", directions);
    }

    private void printMap() {
        System.out.println("\n=== CELERY MAP ===");

        // Print column headers (A-E)
        System.out.print("   ");
        for (char c = 'A'; c <= 'E'; c++) {
            System.out.print(c + "   ");
        }
        System.out.println();

        // Print each row (1-5)
        for (int y = 0; y < GRID_SIZE; y++) {
            // Row number
            System.out.print((y + 1) + " ");

            for (int x = 0; x < GRID_SIZE; x++) {
                if (x == playerX && y == playerY) {
                    System.out.print("[•] "); // Player position
                } else {
                    System.out.print("[ ] "); // All other squares (empty)
                }
            }
            System.out.println();
        }

        // Simplified legend
        System.out.println("\nLegend:");
        System.out.println("• - Your position");
    }
}

