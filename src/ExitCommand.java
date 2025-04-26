/**
 * Class used to implement the exit command, which exits the game
 */
public class ExitCommand implements Command {
    /**
     * What is going to occur upon using this command
     */
    @Override
    public void execute() {
        System.out.println("Exiting the game...");

    }
}
