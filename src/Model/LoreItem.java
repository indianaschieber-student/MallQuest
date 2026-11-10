package Model;

public class LoreItem extends Item {

    public LoreItem(String id, String category, String name, String description) {
        super(id, category, name, description);
    }

    // Handles the READ/EXAMINE command functionality
    public String readLore() {
        return "Reading " + getName() + ": " + getDescription();
    }
}
