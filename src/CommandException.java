/**
 * Class used to create exception CommandException, used for commands, helps in debugging
 */
public class CommandException extends RuntimeException {
    /**
     * Constructor, uses super from RuntimeException
     * @param message what's supposed to print out upon the command exception is thrown
     */
    public CommandException(String message) {
        super(message);
    }
}
