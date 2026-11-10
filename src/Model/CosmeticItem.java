package Model;

public class CosmeticItem extends Item {
    private boolean isEquipped;

    public CosmeticItem(String id, String category, String name, String description) {
        super(id, category, name, description);
        this.isEquipped = false;
    }

    public boolean isEquipped() {
        return isEquipped;
    }

    public void setEquipped(boolean equipped) {
        this.isEquipped = equipped;
    }
}
