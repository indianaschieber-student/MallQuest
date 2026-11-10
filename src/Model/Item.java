package Model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class Item {
    private final String id;
    private final String category;
    private final String name;
    private final String description;

    public Item(String id, String category, String name, String description) {
        this.id = id;
        this.category = category;
        this.name = name;
        this.description = description;
    }

    // Turns one line of Items.txt into an Item or its appropriate subclass
    public static Item fromLine(String line) {
        String[] parts = line.split("\\|", -1); // -1 keeps empty fields (blank descriptions)
        if (parts.length != 4) {
            throw new IllegalArgumentException("Bad item line: " + line);
        }
        
        String id = parts[0].trim();
        String category = parts[1].trim();
        String name = parts[2].trim();
        String description = parts[3].trim();

        // Instantiate subclasses based on category
        switch (category.toLowerCase()) {
            case "cosmetic":
                return new CosmeticItem(id, category, name, description);
            case "lore":
                return new LoreItem(id, category, name, description);
            case "quest":
                return new QuestItem(id, category, name, description);
            case "consumable":
            case "weapon":
                return new WeaponItem(id, category, name, description, 0);
            default:
                return new Item(id, category, name, description);
        }
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

    public String getId() { return id; }
    public String getCategory() { return category; }
    public String getName() { return name; }
    public String getDescription() { return description; }

    @Override
    public String toString() {
        return name + " (" + id + ")";
    }
}
