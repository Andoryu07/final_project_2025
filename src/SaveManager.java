import java.io.*;
import java.util.*;

public class SaveManager {
    private static final String SAVE_FOLDER = "saves/";

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

    public static boolean deleteSave(String filename) {
        File file = new File(SAVE_FOLDER + filename);
        return file.delete();
    }

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