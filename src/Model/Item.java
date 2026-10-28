package Model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Item {
    private final String id;
    private final String category;
    private final String name;
    private final String description;
    private final String locationFound;

    public Item(String id, String category, String name,
                String description, String locationFound) {
        this.id = id;
        this.category = category;
        this.name = name;
        this.description = description;
        this.locationFound = locationFound;
    }

    // Turns one line of Items.txt into an Item
    public static Item fromLine(String line) {
        String[] parts = line.split("\\|", -1); // -1 keeps empty fields (blank descriptions)
        if (parts.length != 5) {
            throw new IllegalArgumentException("Bad item line: " + line);
        }
        return new Item(parts[0].trim(), parts[1].trim(), parts[2].trim(),
                        parts[3].trim(), parts[4].trim());
    }

    // Reads the whole file and returns a map of itemId -> Item
    public static Map<String, Item> loadFromFile(String path) throws IOException {
        Map<String, Item> items = new LinkedHashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue; // skip blank lines and comments
                }
                Item item = fromLine(line);
                items.put(item.getId(), item);
            }
        }
        return items;
    }

    // Pulls room IDs (F1FF, F2TD, EX01...) out of the location text
    public List<String> getLocationIds() {
        List<String> ids = new ArrayList<>();
        Matcher m = Pattern.compile("\\b(?:F\\d[A-Z]+|EX\\d+)\\b").matcher(locationFound);
        while (m.find()) {
            ids.add(m.group());
        }
        return ids;
    }

    public String getId() { return id; }
    public String getCategory() { return category; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getLocationFound() { return locationFound; }

    @Override
    public String toString() {
        return name + " (" + id + ")";
    }
}
