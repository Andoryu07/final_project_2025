import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test class for {@link World} functionality.
 * This class verifies the behavior of world navigation, room management, locking mechanisms,
 * and file loading operations.
 */
class WorldTest {
    /** The World instance under test */
    private World world;
    /** Mock Player instance for testing */
    private Player mockPlayer;
    /** Mock Inventory instance for testing */
    private Inventory mockInventory;
    /** Mock Game instance for testing */
    private Game mockGame;
    /**
     * Initializes test environment before each test method execution.
     * Creates mock objects and sets up a basic world with two connected rooms.
     */
    @BeforeEach
    void setUp() {
        mockPlayer = mock(Player.class);
        mockInventory = mock(Inventory.class);
        mockGame = mock(Game.class);
        world = new World(mockPlayer,mockGame);

        // Setup basic rooms
        Room room0 = new Room(0, "Enter_Hall", List.of(1, 2));
        Room room1 = new Room(1, "Library", List.of(0));
        world.getRooms().put(0, room0);
        world.getRooms().put(1, room1);
        world.setCurrentRoom(room0);

        // Mock player behavior
        when(mockPlayer.getCurrentRoom()).thenReturn(room0);
        when(mockPlayer.getInventory()).thenReturn(mockInventory);
    }
    /**
     * Tests successful room navigation when moving to a valid adjacent room.
     * Verifies that current room updates correctly.
     */
    @Test
    void moveToRoom_ValidMove_UpdatesCurrentRoom() {
        // Setup neighbors
        Room room0 = world.getRooms().get(0);
        Room room1 = world.getRooms().get(1);
        room0.setNeighbors(List.of(1));

        world.moveToRoom(1, new Scanner("1"));
        assertEquals("Library", world.getCurrentRoom().getName());
    }
    /**
     * Tests room lookup by name functionality.
     * Verifies that existing rooms can be found by their name.
     */
    @Test
    void findRoomByName_ExistingRoom_ReturnsRoom() {
        assertNotNull(world.findRoomByName("Library"));
    }
    /**
     * Tests initialization of room locking mechanisms.
     * Verifies that special rooms get their lock conditions properly configured.
     */
    @Test
    void initializeLocks_SetsCorrectLockConditions() {
        Room celery = new Room(10, "Celery", List.of());
        Room livingRoom = new Room(2, "Living_Room", List.of());
        Room garden = new Room(6, "Garden", List.of());
        Room gardenHouse = new Room(7, "Garden_House", List.of());
        Room bathroom = new Room(4, "Bathroom", List.of());

        world.getRooms().put(10, celery);
        world.getRooms().put(2, livingRoom);
        world.getRooms().put(6, garden);
        world.getRooms().put(7, gardenHouse);
        world.getRooms().put(4, bathroom);

        world.initializeLocks();
        assertTrue(world.getAllLockStates().containsKey("Celery_room"));
    }
    /**
     * Tests gear piece insertion mechanism in the Celery room.
     * Verifies that valid gear pieces are properly registered in the gear lock system.
     */
    @Test
    void insertGearPiece_InCeleryWithValidGear_UpdatesGearLock() {
        // Setup
        World testWorld = new World(mockPlayer,mockGame);

        // Create and add required rooms
        Room celeryRoom = new Room(10, "Celery", List.of());
        Room laboratoryRoom = new Room(11, "Laboratory", List.of());
        testWorld.getRooms().put(10, celeryRoom);
        testWorld.getRooms().put(11, laboratoryRoom);

        // Initialize gear lock AFTER rooms are added
        testWorld.initializeGearLock();

        // Create a real gear piece for testing
        GearPiece gearPiece = new GearPiece("GEAR_PIECE_1");

        // Mock player behavior
        when(mockPlayer.getCurrentRoom()).thenReturn(celeryRoom);
        when(mockPlayer.findItemInInventory(anyString())).thenReturn(gearPiece);

        // For void methods, use doNothing() or doAnswer()
        doNothing().when(mockInventory).removeItem(any(Item.class));

        // Test
        testWorld.insertGearPiece("GEAR_PIECE_1", mockPlayer);

        // Verify
        assertNotNull(testWorld.getGearLock());
        assertTrue(testWorld.getGearLock().getInsertedGears().contains("GEAR_PIECE_1"));

        // Verify interactions
        verify(mockInventory).removeItem(gearPiece);
    }
    /**
     * Tests world initialization from file system.
     * Verifies that rooms and search spots are properly loaded from configuration files.
     *
     * @param tempDir Temporary directory provided by JUnit for test file storage
     * @throws Exception if file operations fail
     */
    @Test
    void loadFromFile_ValidFiles_PopulatesRoomsAndSearchSpots(@TempDir Path tempDir) throws Exception {
        // Create test files
        Path roomsFile = tempDir.resolve("rooms.txt");
        Path spotsFile = tempDir.resolve("spots.txt");

        Files.write(roomsFile, List.of(
                "0 Enter_Hall 1",
                "1 Library 0"
        ));

        Files.write(spotsFile, List.of(
                "0 Bookshelf KEY_TO_DINING_ROOM_WARDROBE_2",
                "1 Desk Empty"
        ));

        // Test
        world.loadFromFile(roomsFile.toString(), spotsFile.toString());

        // Verify
        assertNotNull(world.findRoomByName("Enter_Hall"));
        assertNotNull(world.findRoomByName("Library"));

        Room library = world.findRoomByName("Library");
        assertFalse(library.getSearchSpots().isEmpty());
    }
}