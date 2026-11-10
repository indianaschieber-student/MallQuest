package Model;

public class LoreItem extends Item {
    private String textContent;
    private String targetPuzzleID;

    public LoreItem(String id, String category, String name, String description,
                    String textContent, String targetPuzzleID) {
        super(id, category, name, description);
        this.textContent = textContent;
        this.targetPuzzleID = targetPuzzleID;
    }

    public LoreItem(String id, String category, String name, String description) {
        super(id, category, name, description);
        this.textContent = description;
        this.targetPuzzleID = "";
    }

    public String readText() {
        return textContent != null && !textContent.isEmpty() ? textContent : getDescription();
    }

    public String provideInfo() {
        return readText();
    }

    public String getTargetPuzzleID() { return targetPuzzleID; }
}
