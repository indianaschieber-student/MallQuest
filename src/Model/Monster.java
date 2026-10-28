package Model;

public class Monster {
    private String id;
    private String name;
    private String description;
    private String locationFound;
    private int lvl;

    //for combat features
    private int hp;
    private int maxHp;
    private int attack;
    private String status;
    private boolean escapeBlocked;

    public Monster(String id, String name, String description, String locationFound) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.lvl = 1; // Default level
        this.locationFound = locationFound;

        //for combat features
        this.hp = 100; // Default HP
        this.maxHp = 100; // Default max HP
        this.attack = 10; // Default attack
        this.status = "Healthy"; // Default status
        this.escapeBlocked = false; // Default escape blocked status
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    // Set monster level
    public void setLvl(int lvl) {
        if (lvl > 0) {
            this.lvl = lvl;
        }
    }

    // MFR1 - Gets monster information
    public String inspect() {
        return name + " - Level " + lvl + ". " + description;
    }

    // MFR2 - Get monster's current status
    public String checkStatus() {
        return name + " - HP: " + hp + " / " + maxHp
                + " | Level: " + lvl
                + " | Status: " + status;
    }

    // MFR3 - Monster takes damage
    public void takeDamage(int damage) {
        if (damage > 0) {
            hp -= damage;
        }

        if (hp < 0) {
            hp = 0;
        }
    }

    // MFR3 - Check if monster is defeated
    public boolean isDefeated() {
        return hp <= 0;
    }

    // MFR3 - Restore monster HP
    public void restoreHp() {
        hp = maxHp;
    }

    // MFR4 - Change monster status
    public void setStatus(String newStatus) {
        status = newStatus;
    }

    // MFR4 - Reduce monster's attack
    public void reduceAttack(int amount) {
        if (amount > 0) {
            attack -= amount;
        }

        if (attack < 0) {
            attack = 0;
        }
    }

    // MFR5 - Check if player can escape
    public boolean canEscape() {
        return !escapeBlocked;
    }

    // MFR5 - Set whether escape is blocked
    public void setEscapeBlocked(boolean blocked) {
        escapeBlocked = blocked;
    }

    // MFR8 - Monster encounter warning
    public String getEncounterWarning() {
        return "Warning: " + name + " is here and appears hostile.";
    }
}
