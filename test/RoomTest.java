import org.junit.jupiter.api.*;

import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
/**
 * Test class for verifying the functionality of the {@link Room} class.
 * This class tests room item management, locking mechanisms, and search spot interactions.
 */
class RoomTest {
    /** The Room instance under test */
    private Room room;
    /** Test Item instance for room item tests */
    private Item testItem;
    /**
     * Initializes test environment before each test method execution.
     * Creates a fresh room instance with:
     * - ID: 1
     * - Name: "Test"
     * - Connected to room 2
     * - Empty item list
     */
    @BeforeEach
    void setUp() {
        room = new Room(1, "Test", List.of(2));
        testItem = new KeyItem("Key", "Test key");
    }

    /**
     * Tests room unlocking mechanism.
     * Verifies that:
     * - Locked rooms can be successfully unlocked
     * - Room's locked state updates correctly
     */
    @Test
    void unlock_LockedRoom_BecomesAccessible() {
        room.setLock(new Lock("Key", "", "", true));
        room.unlock();
        assertFalse(room.isLocked());
    }
    /**
     * Tests search spot accessibility.
     * Verifies that:
     * - Unlocked search spots are always accessible
     * - Returns true even for non-existent spots (potential edge case)
     */
    @Test
    void canSearchSpot_UnlockedSpot_ReturnsTrue() {
        assertTrue(room.canSearchSpot("Nonexistent", mock(Player.class), new Scanner("1")));
    }
    /**
     * Tests room accessibility with key items.
     * Verifies that:
     * - Locked rooms can be accessed with the proper key
     * - Player inventory is properly checked for required items
     */
    @Test
    void canAccess_LockedRoomWithKey_ReturnsTrue() {
        Player mockPlayer = mock(Player.class);
        when(mockPlayer.hasItem("Key")).thenReturn(true);
        room.setLock(new Lock("Key", "", "", true));

        assertTrue(room.canAccess(mockPlayer, new Scanner("1")));
    }

    /**
     * Tests locked search spot behavior.
     * Verifies that:
     * - Search spots with locks cannot be accessed without proper items
     * - Locked state properly restricts interaction
     */
    @Test
    void addSearchSpot_WithLock_RequiresUnlock() {
        room.addSearchSpotLock("Chest", new Lock("Key", "", "", true));
        assertFalse(room.canSearchSpot("Chest", mock(Player.class), new Scanner("2")));
    }
}