import java.util.Arrays;
import java.util.List;

public class Cassette extends Item {
    private static final List<String> SAVE_LOCATIONS = Arrays.asList("Caravan", "Laboratory", "Library");

    public Cassette() {
        super("Cassette", "A cassette. Can be used to save the game in specific locations.");
    }
    @Override
    public void use(Player player) {
        Room currentRoom = player.getCurrentRoom();
        if (SAVE_LOCATIONS.contains(currentRoom.getName())) {
            System.out.println("💾 Game saved successfully!");
        } else {
            System.out.println("❌ You can only save the game in Caravan, Laboratory, or Library.");
        }
    }

}



