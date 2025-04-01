import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
/**
 * Test class for verifying the functionality of the {@link Player} class.
 * This class tests player inventory management, combat mechanics, and item interactions.
 */
class PlayerTest {
    /** The Player instance under test */
    private Player player;
    /** Test Room instance for player location */
    private Room testRoom;
    /** Test Weapon instance for equipment tests */
    private Weapon testWeapon;
    /**
     * Initializes test environment before each test method execution.
     * Creates a fresh player instance with:
     * - 100 health points
     * - Empty inventory
     * - Located in a test room
     */
    @BeforeEach
    void setUp() {
        testRoom = new Room(0, "Test", List.of());
        player = new Player("Test", 100, testRoom);
        testWeapon = new Pistol(); // Requires Pistol class in test scope
    }
    /**
     * Tests weapon equipping functionality.
     * Verifies that:
     * - Weapons can be properly equipped from inventory
     * - Equipped weapon reference is correctly maintained
     */
    @Test
    void equipWeapon_ValidWeapon_SetsEquippedWeapon() {
        player.getInventory().addItem(testWeapon);
        player.equipWeapon(testWeapon);
        assertEquals(testWeapon, player.getEquippedWeapon());
    }
    /**
     * Tests damage calculation when blocking.
     * Verifies that:
     * - Blocking reduces incoming damage by 50%
     * - Health is properly decremented
     */
    @Test
    void takeDamage_WithBlock_ReducesDamageByHalf() {
        player.setBlocking(true);
        player.takeDamage(20);
        assertEquals(90, player.getHealth()); // 20 * 0.5 = 10 damage
    }
    /**
     * Tests item collection functionality.
     * Verifies that:
     * - Items can be added to inventory when not full
     * - Inventory correctly reflects added items
     */
    @Test
    void pickUpItem_InventoryNotFull_AddsItem() {
        Item bandage = new Bandage();
        player.pickUpItem(bandage);
        assertTrue(player.getInventory().getItems().contains(bandage));
    }
    /**
     * Tests item lookup functionality.
     * Verifies that:
     * - Items can be found by name in inventory
     * - Correct item references are returned
     */
    @Test
    void findItemInInventory_ExistingItem_ReturnsItem() {
        Item testItem = new Bandage();
        player.getInventory().addItem(testItem);
        assertEquals(testItem, player.findItemInInventory("Bandage"));
    }
    /**
     * Tests damage blocking mechanics (alternate implementation).
     * Verifies consistent damage reduction when blocking.
     */
    @Test
    void takeDamage_WhileBlocking_ReducesDamage() {
        player.setBlocking(true);
        player.takeDamage(20);
        assertEquals(90, player.getHealth()); // 50% reduction
    }


}