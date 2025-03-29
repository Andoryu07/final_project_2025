public class HelpCommand implements Command {
    @Override
        public void execute() {
            System.out.println("\nAvailable commands:");
            System.out.println("  go <room_index>   - Move to a different room");
            System.out.println("  take <item>       - Pick up an item");
            System.out.println("  drop <item>       - Drop an item");
            System.out.println("  equip <weapon>    - Equip a weapon");
            System.out.println("  use <item>        - Use an item");
            System.out.println("  attack <enemy>    - Attack an enemy");
            System.out.println("  talk <NPC>        - Talk to a character");
            System.out.println("  examine <object>  - Examine an object");
            System.out.println("  inventory         - Show inventory");
            System.out.println("  help              - Show this help menu");
            System.out.println("  exit              - Exit the game");
            System.out.println("  search            - Search the environment");
        }


}

