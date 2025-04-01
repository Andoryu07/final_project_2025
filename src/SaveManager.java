import java.io.*;
import java.util.*;

/**
 * Class used to manage save files
 */
public class SaveManager {
    /**
     * Name of the folder for saves
     */
    private static final String SAVE_FOLDER = "saves/";

    /**
     * Method used to get the save files from the folder
     * @return new Arraylist if save directory doesn't exist, or array of names of the files in the save directory
     */
    public static List<String> getSaveFiles() {
        File saveDir = new File(SAVE_FOLDER);
        if (!saveDir.exists()) {
            saveDir.mkdirs();
            return new ArrayList<>();
        }

        File[] files = saveDir.listFiles((dir, name) -> name.startsWith("save_"));
        if (files == null) return new ArrayList<>();

        Arrays.sort(files, Comparator.comparingLong(File::lastModified).reversed());
        return Arrays.stream(files).map(File::getName).toList();
    }

    /**
     * Method used to delete saves
     * @param filename name of the file to delete
     * @return true/false based on whether the file had been successfully deleted
     */
    public static boolean deleteSave(String filename) {
        File file = new File(SAVE_FOLDER + filename);
        return file.delete();
    }

    /**
     * Method used to get the new save name when creating a new save
     * @return name of the new save
     */
    public static String getNewSaveName() {
        List<String> saves = getSaveFiles();
        int maxNumber = saves.stream()
                .map(s -> s.replace("save_", "").replace(".dat", ""))
                .mapToInt(Integer::parseInt)
                .max()
                .orElse(0);
        return "save_" + (maxNumber + 1) + ".dat";
    }
}