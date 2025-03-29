import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Game {
    private World world;
    private Player player;
    private Scanner scanner;
    private Map<String, Command> commands;

    public Game() {
        this.world = new World(null);  // Create the world first (with no player initially)
        this.scanner = new Scanner(System.in);
        world.loadFromFile("src/FileImports/game_layout.txt", "src/FileImports/search_spots.txt");

        this.player = new Player("Ethan", 100, world.getCurrentRoom());  // Now create the player with the world’s starting room
        world.setPlayer(player);  // Set the player in the world
        world.initializeGearLock();
        world.initializeEnemies();
        this.commands = new HashMap<>();
    }

    public void start() {
        boolean running = true;
        while (running) {
            world.printCurrentRoom();
            System.out.print("\nEnter command: ");
            String input = scanner.nextLine();
            running = processInput(input);
        }
        scanner.close();
    }

    private boolean processInput(String input) {
        String[] parts = input.split(" ", 2);
        String commandName = parts[0].toLowerCase();
        String argument = (parts.length > 1) ? parts[1] : null;

        switch (commandName) {
            case "insert":
                if (argument != null) {
                    world.insertGearPiece(argument.toUpperCase(), player);
                } else {
                    System.out.println("Specify which gear piece to insert.");
                }
                break;
            case "go":
                if (argument != null) {
                    try {
                        int roomIndex = Integer.parseInt(argument);
                        new MoveCommand(world, roomIndex).execute();
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid room number.");
                    }
                } else {
                    System.out.println("Specify a room index.");
                }
                break;
            case "equip":
                if (argument != null) {
                    Item item = player.findItemInInventory(argument);
                    if (item instanceof Weapon) {
                        player.equipWeapon((Weapon) item);
                    } else {
                        System.out.println("That's not a weapon!");
                    }
                } else {
                    System.out.println("Specify a weapon to equip");
                }
                break;
            case "take":
                if (argument != null) {
                    Item item = findItemInRoom(argument);
                    if (item != null) {
                        new TakeCommand(player, item).execute();
                    } else {
                        System.out.println("Item not found in this room.");
                    }
                } else {
                    System.out.println("Specify an item to take.");
                }
                break;
            case "drop":
                if (argument != null) {
                    Item item = player.findItemInInventory(argument); // Updated this line
                    if (item != null) {
                        new DropCommand(player, item).execute();
                    } else {
                        System.out.println("You don't have this item.");
                    }
                } else {
                    System.out.println("Specify an item to drop.");
                }
                break;
            case "use":
                if (argument != null) {
                    Item item = player.findItemInInventory(argument); // Updated this line
                    if (item != null) {
                        new UseCommand(player, item).execute();
                    } else {
                        System.out.println("You don't have this item.");
                    }
                } else {
                    System.out.println("Specify an item to use.");
                }
                break;
            case "attack":
                if (argument != null) {
                    Enemy enemy = findEnemyInRoom(argument);
                    if (enemy != null) {
                        new AttackCommand(player, enemy).execute();
                    } else {
                        System.out.println("No such enemy here.");
                    }
                } else {
                    System.out.println("Specify an enemy to attack.");
                }
                break;
            case "talk":
                if (argument != null) {
                    Character npc = findCharacterInRoom(argument);
                    if (npc != null) {
                        new TalkCommand(player, npc).execute();
                    } else {
                        System.out.println("No such character here.");
                    }
                } else {
                    System.out.println("Specify a character to talk to.");
                }
                break;
            case "examine":
                if (argument != null) {
                    Item item = player.findItemInInventory(argument); // Updated this line
                    if (item != null) {
                        new ExamineCommand(player, item).execute();
                    } else {
                        System.out.println("You don't have this item.");
                    }
                } else {
                    System.out.println("Specify an item to examine.");
                }
                break;
            case "inventory":
                new InventoryCommand(player).execute();
                break;
            case "help":
                new HelpCommand().execute();
                break;
            case "exit":
                System.out.println("Exiting game...");
                return false;
            case "search":
                new SearchCommand(player).execute();
                break;
            default:
                System.out.println("Unknown command. Type 'help' for a list of commands.");
        }
        return true;
    }


    private Item findItemInRoom(String itemName) {
        for (Item item : world.getCurrentRoom().getItems()) {
            if (item.getName().equalsIgnoreCase(itemName)) {
                return item;
            }
        }
        return null;
    }

    private Enemy findEnemyInRoom(String enemyName) {
        for (Character character : world.getCurrentRoom().getCharacters()) {
            if (character instanceof Enemy && character.getName().equalsIgnoreCase(enemyName)) {
                return (Enemy) character;
            }
        }
        return null;
    }

    private Character findCharacterInRoom(String name) {
        for (Character character : world.getCurrentRoom().getCharacters()) {
            if (character.getName().equalsIgnoreCase(name)) {
                return character;
            }
        }
        return null;
    }

}
