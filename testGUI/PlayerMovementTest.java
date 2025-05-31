import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
/**
 * Tests player movement mechanics.
 * Verifies that player position updates correctly based on movement inputs.
 */
public class PlayerMovementTest {
    /**
     * Tests basic player position updates.
     * Verifies that:
     * - Position changes correctly when speed is set
     * - Both X and Y coordinates update properly
     * - Position values are calculated with appropriate precision
     */
    @Test
    public void testPlayerPositionUpdate() {
        // Setup
        World world = new World(null);
        Player player = new Player("TestPlayer", 100, world, "test_room");

        // Test
        player.setPosition(5.0, 3.0);
        player.setSpeed(0.5, -0.2);
        player.updatePosition();

        // Verify
        assertEquals(5.5, player.getX(), 0.001);
        assertEquals(2.8, player.getY(), 0.001);
    }
}