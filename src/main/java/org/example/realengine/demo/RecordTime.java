package org.example.realengine.demo;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * The {@code RecordTime} class manages and persists the best times achieved on different game maps.
 * <p>It stores map paths as keys and the best time in seconds as values, allowing for saving to and loading from a file.
 */
public class RecordTime {
    /**
     * A {@code Map} storing the best times for each game map. The key is the map path (String),
     * and the value is the best time achieved in seconds (Integer).
     */
    private final Map<String, Integer> bestTimes = new HashMap<>();
    /**
     * The file path where the best times data is stored.
     */
    private final String filePath;

    /**
     * Constructs a new {@code RecordTime} instance and attempts to load existing best times from the specified file path.
     * <p>If the file does not exist or an {@code IOException} occurs during loading, a {@code RuntimeException} is thrown.
     * @param filePath The absolute or relative path to the file where the best times are stored or will be saved.
     * @throws RuntimeException if an {@code IOException} occurs during the initial loading of data from the file.
     */
    public RecordTime(String filePath) {
        this.filePath = filePath;
        try {
            loadFromFile(filePath);
        } catch (IOException _) {
            throw new RuntimeException();
        }
    }

    /**
     * Saves a new time for a given map path if it is better (lower) than the currently recorded best time for that map.
     * <p> If no time is recorded for the map, the new time is set as the best time.
     * @param mapPath The unique identifier or path of the game map.
     * @param seconds The time in seconds to be recorded for the map.
     */
    public void saveTime(String mapPath, int seconds) {
        if (!bestTimes.containsKey(mapPath) || seconds < bestTimes.get(mapPath)) {
            bestTimes.put(mapPath, seconds);
        }
    }

    /**
     * Retrieves the best recorded time for a specific game map.
     * <p>If no time is recorded for the given map path, {@code Integer.MAX_VALUE} is returned, indicating no best time has been set.
     * @param mapPath The unique identifier or path of the game map.
     * @return The best time in seconds for the specified map, or {@code Integer.MAX_VALUE} if no time is recorded.
     */
    public Integer getBestTime(String mapPath) {
        return bestTimes.get(mapPath) == null ? Integer.MAX_VALUE : bestTimes.get(mapPath);
    }

    /**
     * Saves the current best times data from the {@code bestTimes} map to the file specified by {@code filePath}.
     * <p>This method overwrites the existing content of the file. Any {@code IOException} that occurs during saving is caught and ignored.
     */
    public void saveToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, false))) {
            for (Map.Entry<String, Integer> entry : bestTimes.entrySet()) {
                writer.write(entry.getKey() + "," + entry.getValue());
                writer.newLine();
            }
        } catch (IOException _) {
        }
    }

    /**
     * Loads best times data from the file specified by {@code filePath} into the {@code bestTimes} map.
     * <p>The map is cleared before loading new data. Each line in the file is expected to be in the format "mapPath,seconds".
     * <p>Lines that do not conform to this format or contain invalid number formats are skipped.
     * @param filePath The path to the file from which to load the best times.
     * @throws IOException if an I/O error occurs during reading from the file.
     */
    public void loadFromFile(String filePath) throws IOException {
        bestTimes.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    String mapPath = parts[0];
                    try {
                        bestTimes.put(mapPath, Integer.parseInt(parts[1]));
                    } catch (NumberFormatException _) {
                    }
                }
            }
        }
    }
}
