import javafx.geometry.Point2D;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Helper class used for saving the game
 */
public class SaveHelper {
    /**
     * Method used to save the game, creating a new save file in the correct directory
     * @param player who is attempting to save the game
     * @param world which world is the data taken from
     * @return has the saving been successful?
     */
    public static boolean saveGame(Player player, World world) {
        try {
            String timestamp = LocalDateTime.now().format(
                    DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            File saveDir = new File("saves/");
            if (!saveDir.exists()) saveDir.mkdirs();
            File saveFile = new File(saveDir, "save_" + timestamp + ".dat");
            try (ObjectOutputStream out = new ObjectOutputStream(
                    new FileOutputStream(saveFile))) {
                GameStateGUI state = new GameStateGUI();
                state.setPlayerHealth(player.getHealth());
                state.setPlayerStamina(player.getCurrentStamina());
                state.setPlayerPosition(player.getX(), player.getY());
                state.setCurrentRoomName(player.getCurrentRoomName());
                state.setInventory(new ArrayList<>(player.getInventory().getItems()));
                state.setEquippedWeapon(player.getEquippedWeapon());
                state.setSearchedSpots(getSearchedSpots(world));
                state.setDroppedItems(getDroppedItems(world));
                state.setLockStates(world.getAllLockStates());
                state.setStalkerDistance(world.getStalkerDistance());
                out.writeObject(state);
                return true;
            }
        } catch (IOException e) {
            System.err.println("Failed to save game: " + e.getMessage());
            return false;
        }
    }

    /**
     * Method used to get all the already searched spots
     * @param world which world is the data taken from
     * @return the map of all the already searched spots
     */
    private static Map<String, List<String>> getSearchedSpots(World world) {
        Map<String, List<String>> searchedSpots = new HashMap<>();
        for (Room room : world.getRooms().values()) {
            List<String> spotNames = new ArrayList<>();
            for (SearchSpot spot : room.getSearchSpots()) {
                if (spot.isSearched()) {
                    spotNames.add(spot.getName());
                }
            }
            searchedSpots.put(room.getName(), spotNames);
        }
        return searchedSpots;
    }

    /**
     * Method used to obtain the save's metadata
     * @param saveFile which file are we accessing
     * @return the metadata
     */
    public static String getSaveMetadata(File saveFile) {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(saveFile))) {
            GameStateGUI state = (GameStateGUI) in.readObject();
            return String.format("Health: %d | Room: %s",
                    state.getPlayerHealth(),
                    state.getCurrentRoomName());
        } catch (Exception e) {
            return "Unknown save data";
        }
    }

    /**
     * Method used to get all the dropped items in the world
     * @param world which world is the data taken from
     * @return the map of all dropped items
     */
    private static Map<String, List<ItemPosition>> getDroppedItems(World world) {
        Map<String, List<ItemPosition>> droppedItems = new HashMap<>();
        for (Room room : world.getRooms().values()) {
            List<ItemPosition> items = new ArrayList<>();
            for (Map.Entry<Item, Point2D> entry : room.getItemPositions().entrySet()) {
                Point2D pos = entry.getValue();
                items.add(new ItemPosition(entry.getKey(), pos.getX(), pos.getY()));
            }
            droppedItems.put(room.getName(), items);
        }
        return droppedItems;
    }
}