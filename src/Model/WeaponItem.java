package Model;

public class WeaponItem extends Item {
    private final int damageBoost;

    public WeaponItem(String id, String category, String name, String description, int damageBoost) {
        super(id, category, name, description);
        this.damageBoost = damageBoost;
    }

    public int getDamageBoost() {
        return damageBoost;
    }
}
