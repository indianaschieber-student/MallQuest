package Model;

public class QuestItem extends Item {
    private boolean isKeyItem;
    private boolean isStamp;

    public QuestItem(String id, String category, String name, String description,
                     boolean isKeyItem, boolean isStamp) {
        super(id, category, name, description);
        this.isKeyItem = isKeyItem;
        this.isStamp = isStamp;
    }

    public QuestItem(String id, String category, String name, String description) {
        super(id, category, name, description);
        this.isKeyItem = true;
        this.isStamp = id.equalsIgnoreCase("QI001");
    }

    public boolean triggerEvent() { return true; }
    public boolean isKeyItem() { return isKeyItem; }
    public boolean isStamp() { return isStamp; }
}
