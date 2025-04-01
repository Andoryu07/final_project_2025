import org.junit.jupiter.api.Test;

import java.io.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
/**
 * Test class for verifying the serialization/deserialization functionality of {@link GameState}.
 * This class ensures that game state data remains consistent when saved and loaded.
 */
public class GameStateTest {
    /**
     * Tests complete serialization and deserialization cycle (round-trip) for GameState.
     * Verifies that:
     * - All game state data is properly serialized
     * - Serialized data can be correctly deserialized
     * - Critical game properties remain identical after round-trip
     *
     * @throws IOException if I/O operations fail during serialization
     * @throws ClassNotFoundException if class definitions are missing during deserialization
     */
    @Test
    void serialization_RoundTrip_PreservesData() throws IOException, ClassNotFoundException {
        GameState original = new GameState();
        original.setPlayerHealth(75);
        original.setEquippedWeapon(new Pistol());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        new ObjectOutputStream(baos).writeObject(original);

        GameState loaded = (GameState) new ObjectInputStream(
                new ByteArrayInputStream(baos.toByteArray())).readObject();

        assertEquals(75, loaded.getPlayerHealth());
        assertEquals("Pistol", loaded.getEquippedWeapon().getName());
    }
}
