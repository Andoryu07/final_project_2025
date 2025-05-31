import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
/**
 * Tests the functionality of adding items to the player's inventory.
 * Verifies that items can be successfully added and retrieved from the inventory.
 */
public class InventoryTest {
    /**
     * Tests adding a single item into an empty inventory.
     * Verifies that:
     * - The add operation returns true (success)
     * - The inventory size increases by 1
     * - The item can be retrieved by name
     */
    @Test
    public void testAddItemToInventory() {
        // Setup
        Inventory inventory = new Inventory(10);
        Item testItem = new Bandage(); // Assuming Bandage is a concrete Item subclass

        // Test
        boolean result = inventory.addItem(testItem);

        // Verify
        assertTrue(result);
        assertEquals(1, inventory.getItems().size());
        assertEquals(testItem, inventory.findItem("Bandage"));
    }
    /**
     * Tests adding items beyond the inventory's capacity.
     * Verifies that:
     * - Items can be added up to capacity
     * - Attempting to add beyond capacity returns false
     * - Inventory size doesn't exceed capacity
     */
    @Test
    public void testInventoryCapacity() {
        // Setup
        Inventory inventory = new Inventory(2); // Small capacity for testing
        Item item1 = new Bandage();
        Item item2 = new PistolAmmo(10);
        Item item3 = new HealingSerum();

        // Test
        inventory.addItem(item1);
        inventory.addItem(item2);
        boolean result = inventory.addItem(item3); // Should fail

        // Verify
        assertFalse(result);
        assertEquals(2, inventory.getItems().size());
    }
}