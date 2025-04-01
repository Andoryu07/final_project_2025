/**
 * Class used to implement the Help command(Prints out all available commands player can use)
 */
public class HelpCommand implements Command {
    /**
     * Method used to implement, what happens upon using this command
     */
    @Override
        public void execute() {
            System.out.println("\nAvailable commands:");
            System.out.println("  go <room_index>   - Move to a different room");
            System.out.println("  take <item>       - Pick up an item");
            System.out.println("  drop <item>       - Drop an item");
            System.out.println("  equip <weapon>    - Equip a weapon");
            System.out.println("  use <item>        - Use an item");
            System.out.println("  examine <object>  - Examine an object");
            System.out.println("  inventory         - Show inventory");
            System.out.println("  help              - Show this help menu");
            System.out.println("  exit              - Exit the game");
            System.out.println("  search            - Search the environment");
            System.out.println("  insert            - insert a gear piece into a certain mechanism");
        }


}

