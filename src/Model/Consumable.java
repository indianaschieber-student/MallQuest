package Model;

public class Consumable extends Item {
    private final int hpRestore;
    private final int combatDamage;
    private final boolean removeDebuff;
    private final boolean isSingleUse;

    public Consumable(String id, String category, String name, String description,
                      int hpRestore, int combatDamage, boolean removeDebuff, boolean isSingleUse) {
        super(id, category, name, description);
        this.hpRestore = hpRestore;
        this.combatDamage = combatDamage;
        this.removeDebuff = removeDebuff;
        this.isSingleUse = isSingleUse;
    }

    public void consume(Player player) {
        if (player != null && hpRestore > 0) {
            player.setHp(player.getHp() + hpRestore);
        }
    }

    public String useInCombat(Monster monster) {
        if (monster != null && combatDamage > 0) {
            monster.takeDamage(combatDamage);
            return "Used " + getName() + " dealing " + combatDamage + " damage!";
        }
        return "Used " + getName() + ".";
    }

    public int getHpRestore() { return hpRestore; }
    public int getCombatDamage() { return combatDamage; }
    public boolean isRemoveDebuff() { return removeDebuff; }
    public boolean isSingleUse() { return isSingleUse; }
}
