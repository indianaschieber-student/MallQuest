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

    public Monster(String id, String name, String description, String locationFound, int lvl, int maxHp, int attack) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.locationFound = locationFound;

        //Each Monster receives its own stats
        this.lvl = lvl;
        this.hp = maxHp;
        this.maxHp = maxHp;
        this.attack = attack;

        //Starting combat conditions
        this.status = "None";
        this.escapeBlocked = false; // Default escape blocked status
    }

    public String getId() {
        return id;
    }

    // Set monster level
    public void setLvl(int lvl) {
        if (lvl > 0) {
            this.lvl = lvl;
        }
    }

    // MFR1 - Gets monster information

    /*
     (this method does not follow the MVC model)

    public String inspect() {

        return name + " - Level " + lvl + ". " + description;
    }
    */

    public int getLvl(){
        return lvl;
    }

    public String getLocationFound(){
        return locationFound;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }


    // MFR2 - Get monster's current status

    /*

    (this method does not follow the MVC model)

    public String checkStatus() {
        return name + " - HP: " + hp + " / " + maxHp
                + " | Level: " + lvl
                + " | Status: " + status;
    }

     */

    public int getHp() {
        return hp;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public String getStatus() {
        return status;
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

    public int getAttack() {
        return attack;
    }

    /*

    (this method does not follow the MVC model and
    this Monster function does not go here it goes to GameView)

    // MFR8 - Monster encounter warning
    public String getEncounterWarning() {
        return "Warning: " + name + " is here and appears hostile.";
    }

     */
}
