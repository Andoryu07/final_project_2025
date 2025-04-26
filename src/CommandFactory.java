import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Class used to create the commands map, which is later used to call commands
 */
public class CommandFactory {
    /**
     * World instance
     */
    private final World world;
    /**
     * Player instance
     */
    private final Player player;
    /**
     * Scanner
     */
    private final Scanner scanner;
    /**
     * Map used to store the commands and their 'initializers'
     */
    private final Map<String, CommandCreator> commandCreators;

    /**
     * Constructor
     * @param world World instance
     * @param player Player instance
     * @param scanner Scanner
     */
    public CommandFactory(World world, Player player, Scanner scanner) {
        this.world = world;
        this.player = player;
        this.scanner = scanner;
        this.commandCreators = new HashMap<>();
        initializeCommands();
    }

    /**
     * Puts the commands into the map, adjusts each command's behavior
     */
    private void initializeCommands() {
        // Commands without arguments
        commandCreators.put("inventory", args -> new InventoryCommand(player));
        commandCreators.put("help", args -> new HelpCommand());
        commandCreators.put("search", args -> new SearchCommand(player));
        commandCreators.put("exit", args -> new ExitCommand());


        commandCreators.put("go", args -> {
            try {
                int roomIndex = Integer.parseInt(args);
                return new MoveCommand(world, roomIndex, scanner);
            } catch (NumberFormatException e) {
                throw new CommandException("Invalid room number.");
            }
        });

        commandCreators.put("take", args -> {
            Item item = findItemInRoom(args);
            if (item == null) {
                throw new CommandException("Item not found in this room.");
            }
            return new TakeCommand(player, item);
        });

        commandCreators.put("drop", args -> {
            Item item = player.findItemInInventory(args);
            if (item == null) {
                throw new CommandException("You don't have this item.");
            }
            return new DropCommand(player, item);
        });

        commandCreators.put("use", args -> {
            Item item = player.findItemInInventory(args);
            if (item == null) {
                throw new CommandException("You don't have this item.");
            }
            return new UseCommand(player, item);
        });

        commandCreators.put("examine", args -> {
            Item item = player.findItemInInventory(args);
            if (item == null) {
                throw new CommandException("You don't have this item.");
            }
            return new ExamineCommand(player, item);
        });

        commandCreators.put("equip", args -> {
            Item item = player.findItemInInventory(args);
            if (item == null) {
                throw new CommandException("You don't have this item.");
            }
            if (!(item instanceof Weapon)) {
                throw new CommandException("That's not a weapon!");
            }
            return new EquipCommand(player, (Weapon) item);
        });

        commandCreators.put("insert", args -> {
            return new InsertCommand(world, args.toUpperCase(), player);
        });
    }

    /**
     * Method used to take player's input and create the requested command, exception handling
     * @param input player's console input
     * @return command requested, if no problems had been created/the command exists
     */
    public Command createCommand(String input) {
        String[] parts = input.split(" ", 2);
        String commandName = parts[0].toLowerCase();
        String argument = (parts.length > 1) ? parts[1] : null;

        CommandCreator creator = commandCreators.get(commandName);
        if (creator == null) {
            throw new CommandException("Unknown command. Type 'help' for a list of commands.");
        }

        try {
            return creator.create(argument);
        } catch (CommandException e) {
            throw e;
        } catch (Exception e) {
            throw new CommandException("Error executing command: " + e.getMessage());
        }
    }

    /**
     * Helping interface used to create a player-requested command, throws special Command exception(helps in debugging)
     */
    private interface CommandCreator {
        Command create(String args) throws CommandException;
    }

    /**
     * Method used to find a certain item in the room
     * @param itemName item we're looking for
     * @return the item we're looking for, null if the item hasn't been found
     */
    private Item findItemInRoom(String itemName) {
        for (Item item : world.getCurrentRoom().getItems()) {
            if (item.getName().equalsIgnoreCase(itemName)) {
                return item;
            }
        }
        return null;
    }
}
