
package Model;

public class MonsterAttack {
    // MFR3 - Information about a monster's attack
    private String id;
    private String name;
    private int damage;
    private String description;
    private String effectID;

    // Creates an attack using information from GameWorldBuilder
    public MonsterAttack(String id, String name, int damage,
                         String description, String effect) {
        this.id = id;
        this.name = name;
        this.damage = damage;
        this.description = description;
        this.effectID = effect;
    }

    // MFR3 - Gets the attack's ID for matching
    public String getId() {
        return id;
    }

    // MFR3 - Gets the attack's name
    public String getName() {
        return name;
    }

    // MFR3 - Gets the damage dealt by the attack
    public int getDamage() {
        return damage;
    }

    // MFR3 - Gets the attack's description
    public String getDescription() {
        return description;
    }

    // MFR3 - Gets the attack's special effect
    public String getEffectID() {
        return effectID;

    }
}
