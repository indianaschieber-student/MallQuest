
package Model;

public class AttackEffect {
    private String effectID;
    private int skipTurns;
    private double accuracyModifier;
    private int damageModifier;
    private int defenseModifier;
    private double escapeModifier;
    private int accuracyTurns;
    private int damageTurns;
    private int defenseTurns;
    private int escapeTurns;
    private int escapeBlockTurns;
    private boolean escapeBlockedForBattle;
    private boolean nextAttackMisses;
    private String specialAction;

    public AttackEffect(String effectID) {
        this.effectID = effectID;
        applyEffect();
    }

    // Getters
    public String getEffectID() { return effectID; }
    public int getSkipTurns() { return skipTurns; }
    public double getAccuracyModifier() { return accuracyModifier; }
    public int getDamageModifier() { return damageModifier; }
    public int getDefenseModifier() { return defenseModifier; }
    public double getEscapeModifier() { return escapeModifier; }
    public int getAccuracyTurns() { return accuracyTurns; }
    public int getDamageTurns() { return damageTurns; }
    public int getDefenseTurns() { return defenseTurns; }
    public int getEscapeTurns() { return escapeTurns; }
    public boolean isEscapeBlocked() {
        return escapeBlockedForBattle || escapeBlockTurns > 0;
    }
    public boolean isNextAttackMisses() { return nextAttackMisses; }
    public String getSpecialAction() { return specialAction; }

    // Setter
    public void setEffectID(String effectID) {
        this.effectID = effectID;
        clear();
        applyEffect();
    }

    // Reset effects
    private void clear() {
        skipTurns = 0;
        accuracyModifier = 0;
        damageModifier = 0;
        defenseModifier = 0;
        escapeModifier = 0;
        accuracyTurns = 0;
        damageTurns = 0;
        defenseTurns = 0;
        escapeTurns = 0;
        escapeBlockTurns = 0;
        escapeBlockedForBattle = false;
        nextAttackMisses = false;
        specialAction = null;
    }

    // Finds and applies the matching effect ID
    public void applyEffect() {
        clear();

        if (effectID == null) {
            return;
        }

        switch (effectID.trim()) {
            case "ATKFX000":
                break;

            case "ATKFX001":
            case "ATKFX024":
                skipTurns = 1;
                break;

            case "ATKFX002":
                accuracyModifier = 0.70;
                accuracyTurns = 1;
                damageModifier = -3;
                damageTurns = 1;
                break;

            case "ATKFX003":
                accuracyModifier = -0.40;
                accuracyTurns = 1;
                break;

            case "ATKFX005":
                accuracyModifier = -0.20;
                accuracyTurns = 2;
                break;

            case "ATKFX007":
            case "ATKFX033":
            case "ATKFX051":
                damageModifier = -3;
                damageTurns = 1;
                break;

            case "ATKFX009":
                specialAction = "ALLOW_ESCAPE";
                break;

            case "ATKFX011":
                defenseModifier = -3;
                break;

            case "ATKFX012":
                accuracyModifier = -0.30;
                break;

            case "ATKFX013":
                accuracyModifier = -0.10;
                accuracyTurns = 1;
                break;

            case "ATKFX014":
                accuracyModifier = -0.20;
                damageModifier = -5;
                break;

            case "ATKFX015":
                accuracyModifier = -0.20;
                break;

            case "ATKFX016":
                escapeBlockTurns = 1;
                break;

            case "ATKFX017":
                specialAction = "EXTRA_ENCOUNTER";
                break;

            case "ATKFX019":
                damageModifier = -4;
                break;

            case "ATKFX020":
                specialAction = "POSSIBLE_SKIP_TURN";
                break;

            case "ATKFX021":
                specialAction = "MONSTER_FLEES";
                break;

            case "ATKFX022":
                specialAction = "SHOULDER_CHECK_IF_REFUSED";
                break;

            case "ATKFX023":
                specialAction = "HELP_OR_ESCAPE";
                break;

            case "ATKFX025":
                specialAction = "MONSTER_ACCURACY_IF_REFUSED";
                break;

            case "ATKFX026":
                specialAction = "POSSIBLE_PANIC_FLEE";
                break;

            case "ATKFX027":
            case "ATKFX040":
                damageModifier = -2;
                break;

            case "ATKFX028":
                defenseModifier = -2;
                defenseTurns = 2;
                break;

            case "ATKFX029":
                specialAction = "MONSTER_NEXT_DAMAGE_PLUS_2";
                break;

            case "ATKFX030":
                damageModifier = -4;
                damageTurns = 1;
                break;

            case "ATKFX031":
                specialAction = "REPOSITION_PLAYER";
                break;

            case "ATKFX032":
                escapeModifier = -0.40;
                break;

            case "ATKFX034":
                specialAction = "POSSIBLE_STUN";
                break;

            case "ATKFX035":
                escapeModifier = 0.20;
                break;

            case "ATKFX036":
                specialAction = "CHECK_TISSUE";
                break;

            case "ATKFX037":
                specialAction = "RESTRAIN_PLAYER";
                break;

            case "ATKFX038":
                specialAction = "HEAL_MONSTER_4";
                break;

            case "ATKFX039":
                escapeModifier = -0.15;
                break;

            case "ATKFX042":
                nextAttackMisses = true;
                break;

            case "ATKFX043":
                specialAction = "STEAL_ITEM_30_PERCENT";
                break;

            case "ATKFX044":
                escapeModifier = 0.25;
                break;

            case "ATKFX045":
                defenseModifier = -3;
                defenseTurns = 1;
                break;

            case "ATKFX046":
                specialAction = "GRAPPLE_PLAYER";
                break;

            case "ATKFX047":
                accuracyModifier = -0.15;
                break;

            case "ATKFX048":
                specialAction = "FREE_ATTACK_OR_ESCAPE";
                break;

            case "ATKFX049":
                specialAction = "KNOCK_PLAYER_BACK";
                break;

            case "ATKFX050":
                escapeBlockedForBattle = true;
                break;

            case "ATKFX053":
                damageModifier = -2;
                damageTurns = 1;
                specialAction = "MONSTER_DEFENSE_ONE_TURN";
                break;

            default:
                specialAction = "UNKNOWN_EFFECT";
                break;
        }
    }

    // Decrease temporary effect durations after a used player turn
    public void endPlayerTurn() {
        // TODO MFR3 - Review effects with unspecified durations.
        // Some monster effects do not define how many turns they last.
        // Do not assign arbitrary durations until the team confirms them.
        // Effects with defined durations should expire after the
        // appropriate number of completed player turns.

        if (accuracyTurns > 0 && --accuracyTurns == 0) {
            accuracyModifier = 0;
        }

        if (damageTurns > 0 && --damageTurns == 0) {
            damageModifier = 0;
        }

        if (defenseTurns > 0 && --defenseTurns == 0) {
            defenseModifier = 0;
        }

        if (escapeTurns > 0 && --escapeTurns == 0) {
            escapeModifier = 0;
        }

        if (escapeBlockTurns > 0) {
            escapeBlockTurns--;
        }
    }

    // Checks and consumes a skipped turn
    public boolean useSkipTurn() {
        if (skipTurns <= 0) {
            return false;
        }

        skipTurns--;
        return true;
    }

    // Checks and consumes an automatic attack miss
    public boolean useNextAttackMiss() {
        if (!nextAttackMisses) {
            return false;
        }

        nextAttackMisses = false;
        return true;
    }
}
