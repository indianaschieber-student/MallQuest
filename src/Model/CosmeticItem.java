package Model;

public class CosmeticItem extends Item {
    private String slotCategory;
    private boolean isEquipped;

    public CosmeticItem(String id, String category, String name, String description, String slotCategory) {
        super(id, category, name, description);
        this.slotCategory = slotCategory;
        this.isEquipped = false;
    }

    public CosmeticItem(String id, String category, String name, String description) {
        super(id, category, name, description);
        this.slotCategory = "Cosmetic";
        this.isEquipped = false;
    }

    public void equip() { this.isEquipped = true; }
    public void unequip() { this.isEquipped = false; }
    public boolean isEquipped() { return isEquipped; }
    public String getSlotCategory() { return slotCategory; }
}
