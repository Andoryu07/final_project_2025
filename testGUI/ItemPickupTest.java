import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
/**
 * Tests item pickup functionality between rooms and player inventory.
 * Verifies the transfer of items from game world to player inventory.
 */
public class ItemPickupTest {
    /**
     * Tests picking up an item from a room.
     * Verifies that:
     * - Item is successfully transferred to player inventory
     * - Item is removed from the room
     * - Player's hasItem() method correctly identifies the item
     */
    @Test
    public void testItemPickup() {
        // Setup
        World world = new World(null);
        Player player = new Player("TestPlayer", 100, world, "test_room");
        Room testRoom = new Room("test_room", false);
        world.addRoom(testRoom);
        world.setCurrentRoom(testRoom);

        Item testItem = new Bandage();
        testRoom.addItem(testItem, 5.0, 5.0);

        // Test
        player.pickUpItem(testItem);

        // Verify
        assertTrue(player.hasItem("Bandage"));
        assertTrue(testRoom.getItems().isEmpty());
    }
}