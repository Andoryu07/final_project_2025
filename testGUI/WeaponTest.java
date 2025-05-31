import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
/**
 * Tests weapon equipping functionality.
 * Verifies that weapons can be properly equipped from inventory.
 */
public class WeaponTest {
    /**
     * Tests equipping a weapon from inventory.
     * Verifies that:
     * - Weapon can be successfully equipped
     * - Equipped weapon matches the weapon in inventory
     * - Player's getEquippedWeapon() returns the correct weapon
     */
    @Test
    public void testWeaponEquip() {
        // Setup
        World world = new World(null);
        Player player = new Player("TestPlayer", 100, world, "test_room");
        Weapon pistol = new Pistol();
        player.getInventory().addItem(pistol);

        // Test
        player.equipWeapon(pistol);

        // Verify
        assertEquals(pistol, player.getEquippedWeapon());
    }
}