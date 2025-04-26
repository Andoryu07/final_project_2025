import java.util.*;

/**
 * Class used to implement the stealth system in the celery, which is required to complete in order to finish the game
 */
public class CeleryStealthSystem {
    /**
     * Specifies the size of the grid, on which the player is going to be moving
     */
    private static final int GRID_SIZE = 5;
    /**
     * int, containing the player's X coordinate location
     */
    private int playerX = 0; // A=0, B=1, etc.
    /**
     * int, containing the player's Y coordinate location
     */
    private int playerY = 4; // 1=0, 2=1, etc. (so 5A(starting location) is [0][4])
    /**
     * Boolean list, used to specify, on which coordinates [X][Y] the zombies will be located(If player occurs in any of these tiles, he will get eliminated instantly)
     */
    private boolean[][] zombiePositions;
    /**
     * Instance of Player class
     */
    private Player player;
    /**
     * Instance of Flashlight class
     */
    private Flashlight flashlight;
    /**
     * Boolean used to save the state of whether the player had completed the Stealth sequence(Repairing the power box and turning the lights back on)
     */
    private boolean lightsOn = false;
    /**
     * Runnable instance, used for loading the latest checkpoint
     */
    private final Runnable checkpointLoader;

    /**
     * Constructor, initializes zombies' positions
     * @param player Who is attempting the stealth sequence
     * @param checkpointLoader Which Runnable to use
     */
    public CeleryStealthSystem(Player player, Runnable checkpointLoader) {
        this.player = player;
        this.flashlight = (Flashlight) player.findItemInInventory("Flashlight");
        this.checkpointLoader = checkpointLoader;
        initializeZombiePositions();
    }

    /**
     * Method used to put zombies into the map using the X and Y positions of the grid
     */
    private void initializeZombiePositions() {
        zombiePositions = new boolean[GRID_SIZE][GRID_SIZE];
        // Mark zombie positions (x,y)
        zombiePositions[3][0] = true; // 1D
        zombiePositions[2][1] = true; // 2C
        zombiePositions[4][2] = true; // 3E
        zombiePositions[1][3] = true; // 4B
        zombiePositions[0][4] = true; // 5A (starting position is safe)
    }

    /**
     * Method controlling the run of the stealth sequence system, allowing player's movement, checking for their battery charge, handling game over
     * @param scanner Scanner instance, to connect different classes' scanner to register player's inputs
     */
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

    /**
     * Moves the player's position based on his input, while also checking, if the new tile contains a zombie, or not
     * @param dx how many tiles will the player move on the X coordinate
     * @param dy how many tiles will the player move on the Y coordinate
     */
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
            System.out.println("You stumbled right into a zombie and died!");
            player.takeDamage(player.getHealth()); // Instant death
            this.loadLatestCheckpoint();

        }

        // Move is valid
        playerX = newX;
        playerY = newY;
        flashlight.useBattery(10); // Each move uses 5% battery
        System.out.println("Moved to position " + getPositionString());

    }

    /**
     * Handles the case, where player's battery depletes during the stealth sequence, recharging it automatically(if player owns Batteries) if possible, ending the game if not
     * @return returns true/false value based on whether the player has battery charge available or not
     */
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

    /**
     * Handles the case, where player finds the power box, giving him the opportunity to fix it, if they own the right tools, if the repair is a success, zombies will attack the player
     * @param scanner which Scanner to use for tracking player's inputs
     */
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

    /**
     * Method activated upon the repair of the power box, operating the fight against zombies, 5 in a row
     */
    private void startZombieCombat() {
        Room currentRoom = player.getCurrentRoom();
        List<Enemy> zombies = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            zombies.add(new Zombie(player.getWorld(),player.getCurrentRoom().getName()));
        }
        // Fight each zombie one by one
        for (Enemy zombie : zombies) {
            System.out.println("\nA zombie shambles towards you!");
            new CombatSystem(player, zombie, new Scanner(System.in), this::loadLatestCheckpoint).startCombat();
        }
        System.out.println("\nYou've defeated all the zombies in the cellar!");
        // The game will automatically return to normal play after this

    }

    /**
     * Method used to load the latest checkpoint during the stealth sequence(upon dying), resets player's position
     */
    private void loadLatestCheckpoint() {
            checkpointLoader.run();
            this.playerX = 0;
            this.playerY = 4;
            if (flashlight != null) {
                flashlight.setInCelery(true);
            }
            this.lightsOn = false;
            System.out.println("\nYou wake up at the cellar entrance...");
            startSequence(new Scanner(System.in)); // Restart the sequence

    }

    /**
     * Method used to get a better formulated X and Y position of the player
     * @return the position of the player in a row(numbers) + column(characters) format
     */
    private String getPositionString() {
        char col = (char) ('A' + playerX);
        int row = playerY + 1;
        return row + "" + col;
    }

    /**
     * Method used to print information about the player's state, including his position on the grid, his flashlight battery charge, and available directions he can move in
     */
    private void printCurrentPosition() {
        printMap();
        System.out.println("\nCurrent position: " + getPositionString());
        System.out.println("Flashlight battery: " + flashlight.getBatteryLevel() + "%");
        System.out.println("Available directions: " + getAvailableDirections());
    }

    /**
     * Method used to figure out, which directions the player can move in
     * @return Available directions("none" if directions is empty, list of directions if not)
     */
    private String getAvailableDirections() {
        List<String> directions = new ArrayList<>();

        if (playerY > 0) directions.add("forward");
        if (playerY < GRID_SIZE - 1) directions.add("back");
        if (playerX > 0) directions.add("left");
        if (playerX < GRID_SIZE - 1) directions.add("right");

        return directions.isEmpty() ? "none" : String.join(", ", directions);
    }

    /**
     * Method used to print the grid map, including a circle in the tile the player is currently located in
     */
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

