import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

/**
 * Class used to create, style and set the behavior and attributes of the prompts used in the game
 */
public class Prompt {
    /**
     * Message displayed in the prompt
     */
    private String message;
    /**
     * Target room for transition prompts
     */
    private String targetRoom;
    /**
     * Is the prompt active
     */
    private boolean active;
    /**
     * Width of the prompt
     */
    private final double width = 300;
    /**
     * Height of the prompt
     */
    private final double height = 60;

    /**
     * Constructor
     * @param message Message displayed in the prompt
     * @param targetRoom Target room for transition prompts
     */
    public void show(String message, String targetRoom) {
        this.message = message;
        this.targetRoom = targetRoom;
        this.active = true;
    }

    /**
     * Method used to hide the prompt
     */
    public void hide() {
        this.active = false;
    }

    /**
     * Getter for 'active'
     * @return value of 'active'
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Getter for 'targetRoom'
     * @return value of 'targetRoom'
     */
    public String getTargetRoom() {
        return targetRoom;
    }

    /**
     * Method used to render the prompt
     * @param gc GraphicsContext instance
     * @param screenWidth width of the screen
     * @param screenHeight height of the screen
     */
    public void render(GraphicsContext gc, double screenWidth, double screenHeight) {
        if (!active) return;
        // Calculate position (centered at bottom)
        double x = (screenWidth - width) / 2;
        double y = screenHeight - height - 20;
        // Save the current graphics state
        gc.save();
        // Reset any scaling that might be applied
        gc.setTransform(1, 0, 0, 1, 0, 0);
        // Draw semi-transparent background
        gc.setFill(Color.rgb(0, 0, 0, 0.7));
        gc.fillRoundRect(x, y, width, height, 10, 10);
        // Draw border
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(2);
        gc.strokeRoundRect(x, y, width, height, 10, 10);
        // Draw text
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", 16));
        // Draw key prompt (left-aligned)
        gc.setTextAlign(TextAlignment.LEFT);
        gc.fillText("[F]", x + 20, y + height / 2 + 5);
        // Draw message (centered)
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText(message, x + width / 2 + 20, y + height / 2 + 5);
        // Restore graphics state
        gc.restore();
    }
}