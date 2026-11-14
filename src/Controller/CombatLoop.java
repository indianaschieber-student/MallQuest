package Controller;

import Model.*;
import View.GameView;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

public class CombatLoop {
    private static boolean isPlaying = false;

    // References to the existing game model and view
    private static GameModel model;
    private static GameView view;

    // MFR4 - Randomly selects a monster attack during combat
    private static Random combatRandom = new Random();
    // MFR3 - Stores active monster attack effects
    private static ArrayList<AttackEffect> activeEffects = new ArrayList<>();
    // MFR3 - Tracks whether a monster has fled from combat
    private static boolean monsterFled = false;
    // MFR3 - Bonus damage for the monster's next attack
    private static int monsterNextAttackBonus = 0;

    // MFR3 - Handles combat between the player and a monster
    public static void combatLoop (GameModel gameModel, GameView gameView) {

        model = gameModel;
        view = gameView;

        // Clear effects when starting a new battle
        activeEffects.clear();
        // Reset monster fleeing status for each new battle
        monsterFled = false;
        // Reset temporary monster damage bonus
        monsterNextAttackBonus = 0;


        // Gets the player's current room
        Room room = model.getCurrentRoom();

        // Gets the monster inside that room
        Monster monster = room.getRoomMonster();

        // Gets the existing player from GameModel
        Player player = model.getPlayer();

        // Checks whether a monster exists in the room
        if (monster == null) {
            view.printMessage("There is no monster to fight here.");
            return;
        }

        // Checks whether the monster has already been defeated
        if (monster.isDefeated()) {
            view.printMessage(monster.getName() + " has already been defeated.");
            return;
        }

        // Announces the beginning of combat
        view.printMessage("You are now fighting " + monster.getName() + "!");

        // MFR3 - Keeps combat running until the monster is defeated
        // or the player successfully escapes
        boolean inCombat = true;

        while (inCombat) {

            // End combat if the monster has fled
            if (monsterFled) {
                inCombat = false;
                break;
            }

            // MFR3 - Handle skipped player turns
            if (checkSkippedTurn()) {

                // A skipped turn still counts as a completed turn
                updateEffectDurations();

                // Monster gets another attack
                monsterAttackTurn(monster, player);

                if (player.getCurrentHp() <= 0) {
                    handlePlayerDefeat(player);
                    inCombat = false;
                }

                continue;
            }

            // Display the monster's current HP
            view.printMessage(monster.getName() + " HP: " + monster.getHp() + "/" + monster.getMaxHp()
            );

            // MFR3 - Display the player's current and maximum HP
            view.printMessage("Your HP: " + player.getCurrentHp() + "/" + player.getMaxHp());

            // MFR3 - Display available combat actions
            // MFR3 - Display numbered combat menu
            view.printMessage("--- COMBAT ---");
            view.printMessage("[1] Attack");
            view.printMessage("[2] Use Item");
            view.printMessage("[3] Run");
            // MFR3 - Special option for the Dodgeball Bully
            if ("MST023".equals(monster.getId())
                    && player.findItemInInventory("LI009") != null) {

                view.printMessage("[4] Invoke Name");
            }
            view.printMessage("[5] Check Status");

            String combatCommand = view.promptForCommand().trim().toUpperCase(Locale.ROOT);


            // MFR3 - Player attacks the monster
            if (combatCommand.equals("1") || combatCommand.equals("ATTACK")) {


                // MFR3 - Check whether the player's attack hits
                boolean attackHits = playerAttackHits(player, monster);

                if (attackHits) {

                    // Calculate damage using the player's attack and active effects
                    int damage = player.getBaseAttack() + getDamageModifier();

                    // Prevent negative damage
                    if (damage < 0) {
                        damage = 0;
                    }

                    // TODO MFR3 - Add equipped weapon bonus
                    // once weapon equipment is connected.

                    // Apply damage to the monster
                    monster.takeDamage(damage);

                    view.printMessage(
                            "You attacked " + monster.getName()
                                    + " and dealt " + damage + " damage!"
                    );

                } else {

                    // Missed attacks do not damage the monster
                    view.printMessage(
                            "Your attack missed " + monster.getName() + "!"
                    );
                }


                // Check if the monster has been defeated

                // Check whether the monster has been defeated
                if (monster.isDefeated()) {

                    handleMonsterVictory(monster, player);
                    inCombat = false;

                } else {

                    // Update effects after the player's completed attack
                    updateEffectDurations();

                    // Monster retaliates
                    monsterAttackTurn(monster, player);

                    if (player.getCurrentHp() <= 0) {
                        handlePlayerDefeat(player);
                        inCombat = false;
                    }
                }
            }


            // MFR3 - Player attempts to use an item during combat
            else if (combatCommand.equals("2") || combatCommand.equals("USE ITEM")) {

                boolean itemUsed = useCombatItem(player, monster);

                if (itemUsed) {

                    // Update effects after a successful item use
                    updateEffectDurations();

                    if (monster.isDefeated()) {
                        handleMonsterVictory(monster, player);
                        inCombat = false;
                    } else {
                        monsterAttackTurn(monster, player);

                        if (player.getCurrentHp() <= 0) {
                            handlePlayerDefeat(player);
                            inCombat = false;
                        }
                    }
                }
            }


            // MFR3 - Player attempts to escape combat
            else if (combatCommand.equals("3") || combatCommand.equals("RUN")) {

                // Check whether the monster or an active effect blocks escape
                if (monster.canEscape() && !isEscapeBlockedByEffect()) {

                    // Move player to the adjacent hallway
                    String hallway = model.ejectPlayer();

                    view.printMessage(
                            "You successfully escaped from " + monster.getName() + "!"
                    );

                    if (hallway != null) {
                        view.printMessage(
                                "You returned to " + hallway + "."
                        );
                    }

                    // End combat
                    inCombat = false;

                } else {

                    view.printMessage(
                            "You cannot escape from " + monster.getName() + "!"
                    );

                    // A failed escape consumes the player's turn
                    updateEffectDurations();

                    // Monster attacks
                    monsterAttackTurn(monster, player);

                    if (player.getCurrentHp() <= 0) {
                        handlePlayerDefeat(player);
                        inCombat = false;
                    }
                }
            }


            // MFR3 - Special Dodgeball Bully encounter
            else if (combatCommand.equals("4")
                    || combatCommand.equals("INVOKE NAME")) {

                // Check whether the player can use this special action
                if ("MST023".equals(monster.getId())
                        && player.findItemInInventory("LI009") != null) {

                    view.printMessage(
                            "You call out 'Beezulbulb!' — The Dodgeball Bully freezes and vanishes."
                    );

                    // Instantly defeat the monster
                    monster.takeDamage(monster.getHp());

                    // Process the normal victory rewards
                    handleMonsterVictory(monster, player);

                    // End combat
                    inCombat = false;

                } else {
                    view.printMessage(
                            "You cannot use Invoke Name in this battle."
                    );
                }
            }


            // MFR2 - Player checks the monster's current status
            else if (combatCommand.equals("5")
                    || combatCommand.equals("CHECK STATUS")) {

                // Display the monster's current combat information
                checkMonsterStatus(monster);

                // Checking status does not consume a combat turn
            }

            // Handle invalid combat commands
            else {
                view.printMessage(
                        "Invalid command. Please enter ATTACK, USE ITEM, RUN, or CHECK STATUS."
                );
            }


        }

    }


    // MFR4 - Monster attacks the player during combat
    public static void monsterAttackTurn (Monster monster, Player player){

        // Get all attacks assigned to this monster
        ArrayList<MonsterAttack> attacks = monster.getAttacks();

        // Check if the monster has any attacks
        if (attacks.isEmpty()) {
            view.printMessage(monster.getName() + " has no attacks!");
            return;
        }

        // Randomly select an attack from the monster's attack list
        int randomIndex = combatRandom.nextInt(attacks.size());
        MonsterAttack selectedAttack = attacks.get(randomIndex);

        // MFR3 - Apply bonus damage from the previous monster attack
        int damage = Math.max(
                0, selectedAttack.getDamage() + monsterNextAttackBonus
        );

        // Reset bonus after applying it once
        monsterNextAttackBonus = 0;

        // Display the monster's attack
        view.printMessage(monster.getName() + " used " + selectedAttack.getName() + "!");

        // Monster deals damage to the player
        player.takeDamage(damage);
        // Apply any additional effect from the monster's attack
        applyMonsterAttackEffect(selectedAttack, monster, player);

        // Display how much damage the monster dealt
        view.printMessage(monster.getName() + " dealt " + damage + " damage to you!");

        // Display the player's remaining HP
        view.printMessage("Your remaining HP: " + player.getCurrentHp() + "/" + player.getMaxHp());

    }


    // MFR3 / IFR6 - Handles using an item during combat
    public static boolean useCombatItem(Player player, Monster monster) {

        String itemTarget = view.promptForItemName();
        if (itemTarget != null && !itemTarget.trim().isEmpty()) {
            Item item = player.findItemInInventory(itemTarget.trim());
            if (item != null) {
                if (item instanceof Consumable) {
                    Consumable consumable = (Consumable) item;
                    consumable.consume(player);
                    view.printMessage("You used " + consumable.getName() + "!");
                    if (consumable.isSingleUse()) {
                        player.removeItem(item);
                    }
                    return true;
                } else {
                    view.printMessage("You can't use that item in combat.");
                }
            } else {
                view.printMessage("You don't have that item.");
            }
        }

        // No valid item was used, so the player's turn is not consumed.
        return false;
    }


    // MFR2 - Displays the monster's current combat status
    private static void checkMonsterStatus(Monster monster) {

        // Check whether a monster exists
        if (monster == null) {
            view.printMessage("You don't see that creature here.");
            return;
        }

        // Display the monster's name
        view.printMessage("--- MONSTER STATUS ---");

        // Display the monster's current and maximum HP
        view.printMessage(
                "Monster: " + monster.getName()
        );

        view.printMessage("HP: " + monster.getHp() + "/" + monster.getMaxHp());

        // Display the monster's level
        view.printMessage(
                "Level: " + monster.getLvl()
        );

        // Display active status conditions
        String status = monster.getStatus();

        if (status == null || status.trim().isEmpty()) {
            status = "None";
        }

        view.printMessage("Status: " + status);

        view.printMessage("----------------------");
    }


    // MFR4 - Handles alternative interactions with monsters
    private static boolean actOnMonster(Monster monster) {

        if (monster == null) {
            view.printMessage("There is no monster to interact with.");
            return false;
        }

        // Display available ACT options
        view.printMessage("--- ACT ON MONSTER ---");
        view.printMessage("Choose an action: DISTRACT, TAUNT, or PERSUADE");

        String action = view.promptForCommand()
                .trim()
                .toUpperCase(Locale.ROOT);

        switch (action) {

            case "DISTRACT":
                view.printMessage(
                        "[TODO MFR4] Check whether distracting "
                                + monster.getName() + " is effective."
                );
                return false;

            case "TAUNT":
                view.printMessage(
                        "[TODO MFR4] Check whether taunting "
                                + monster.getName() + " is effective."
                );
                return false;

            case "PERSUADE":
                view.printMessage(
                        "[TODO MFR4] Check whether persuading "
                                + monster.getName() + " is effective."
                );
                return false;

            default:
                view.printMessage(
                        "Invalid ACT command. Please enter "
                                + "DISTRACT, TAUNT, or PERSUADE."
                );
                return false;
        }
    }


    // MFR3 - Handles rewards when a monster is defeated
    private static void handleMonsterVictory(Monster monster, Player player) {

        // MFR3 - Announce monster victory
        view.printMessage(
                "You defeated " + monster.getName() + "!"
        );

        // Calculate XP based on monster level
        int xpReward = monster.getLvl() * 10;

        // PFR7 - Display monster level and XP reward
        view.printMessage(
                "You defeated a Level " + monster.getLvl()
                        + " monster and earned " + xpReward + " XP."
        );

        // Award XP and display level-up message
        if (player.gainXp(xpReward)) {
            view.printLevelUp(
                    player.getPlayerLevel(),
                    player.getCurrentHp(),
                    player.getMaxHp(),
                    player.getBaseAttack()
            );
        }

        // Update monsters defeated count
        player.increaseMonstersDefeated();

    }


    // MFR3 - Handles player defeat during combat
    private static void handlePlayerDefeat(Player player) {

        if (player.getCurrentHp() > 0) {
            return;
        }

        // Remove a random consumable item
        String lostItem = player.removeRandomConsumable();

        // Move player to Pharmacy and restore full HP
        model.movePlayerToPharmacy();

        // Display player defeat message
        view.printPlayerCombatLoss();

        // Display lost item if one was removed
        if (lostItem != null && !lostItem.isEmpty()) {
            view.printItemLoss(lostItem);
        }
    }


    // MFR3 - Determines whether the player's attack hits
    private static boolean playerAttackHits(Player player, Monster monster) {

        // Check if an effect forces the attack to miss
        if (checkAutomaticMiss()) {
            return false;
        }

        // Get player and monster levels
        int playerLevel = player.getPlayerLevel();
        int monsterLevel = monster.getLvl();

        // MFR3 - Player has an 80% base chance to hit
        // Active monster effects may increase or decrease accuracy
        double hitChance = 0.80;

        // Apply accuracy modifiers from active effects
        hitChance += getAccuracyModifier();

        // Keep accuracy between 0% and 100%
        if (hitChance < 0) {
            hitChance = 0;
        }

        if (hitChance > 1) {
            hitChance = 1;
        }

        // Random number between 0.0 and 1.0
        double randomNumber = combatRandom.nextDouble();

        return randomNumber < hitChance;
    }



    // MFR3 - Handles special effects from monster attacks
    private static void applyMonsterAttackEffect(MonsterAttack attack, Monster monster, Player player) {

        // Get the effect ID assigned to the monster attack
        String effectID = attack.getEffectID();

        // Check if the attack has an effect
        if (effectID == null || effectID.trim().isEmpty()
                || effectID.equals("ATKFX000")) {
            return;
        }

        // Create the effect using AttackEffect.java
        AttackEffect effect = new AttackEffect(effectID);

        // MFR3 - Handle special monster actions
        String specialAction = effect.getSpecialAction();
        // MFR3 - Handle monster fleeing
        handleMonsterFlee(effect, monster);
        // MFR3 - Handle bonus damage effect
        handleMonsterBonusDamage(effect);

        if ("ALLOW_ESCAPE".equals(specialAction)) {

            view.printMessage(
                    monster.getName() + " gives you an opportunity to escape!"
            );
        }

        // Apply immediate monster healing
        applyMonsterHealing(effect, monster);

        // MFR3 - Handle item-stealing effects
        handleMonsterStealing(effect, monster, player);

        // MFR3 - Store effects with implemented combat behavior
        // Possible stun/skip effects are excluded until their
        // probabilities are confirmed by the team.
        if (!"POSSIBLE_SKIP_TURN".equals(specialAction)
                && !"POSSIBLE_STUN".equals(specialAction)) {

            activeEffects.add(effect);
        }

        // TODO MFR3 - ATKFX034: POSSIBLE_STUN
        // Stun probability has not been specified by the team.
        if ("POSSIBLE_STUN".equals(specialAction)) {
            view.printMessage(
                    monster.getName() + " used an attack that may stun you!"
            );
        }

        view.printMessage(
                "Monster attack effect activated: " + effectID
        );
    }

    // MFR3 - Checks whether the player must skip a turn
    private static boolean checkSkippedTurn() {

        for (AttackEffect effect : activeEffects) {

            if (effect.useSkipTurn()) {

                view.printMessage(
                        "You are stunned and lose your turn!"
                );

                return true;
            }
        }

        return false;
    }

    // MFR3 - Checks whether an effect forces the next attack to miss
    private static boolean checkAutomaticMiss() {

        for (AttackEffect effect : activeEffects) {

            if (effect.useNextAttackMiss()) {
                return true;
            }
        }

        return false;
    }

    // MFR3 - Calculates accuracy changes from monster effects
    private static double getAccuracyModifier() {

        double modifier = 0;

        for (AttackEffect effect : activeEffects) {
            modifier += effect.getAccuracyModifier();
        }

        return modifier;
    }

    // MFR3 - Calculates damage changes from active monster effects
    private static int getDamageModifier() {

        // TODO MFR3 - Defense modifiers
        // AttackEffect.java includes effects that reduce player defense.
        // CombatLoop.java does not currently apply defense modifiers
        // when calculating incoming monster damage.
        // Confirm the defense calculation with the team before implementing.

        int modifier = 0;

        for (AttackEffect effect : activeEffects) {
            modifier += effect.getDamageModifier();
        }

        return modifier;
    }

    // MFR3 - Updates temporary effects after a used player turn
    private static void updateEffectDurations() {

        for (AttackEffect effect : activeEffects) {
            effect.endPlayerTurn();
        }
    }

    // MFR3 - Checks whether an active effect blocks escape
    private static boolean isEscapeBlockedByEffect() {

        for (AttackEffect effect : activeEffects) {

            if (effect.isEscapeBlocked()) {
                return true;
            }
        }

        return false;
    }

    // MFR3 - Handles monster healing effects
    private static void applyMonsterHealing(
            AttackEffect effect, Monster monster) {

        if ("HEAL_MONSTER_4".equals(effect.getSpecialAction())) {

            int hpBefore = monster.getHp();

            monster.heal(4);

            int healedAmount = monster.getHp() - hpBefore;

            view.printMessage(
                    monster.getName() + " restored "
                            + healedAmount + " HP!"
            );
        }
    }

    // MFR3 - Calculates escape chance changes from active effects
    private static double getEscapeModifier() {

        double modifier = 0.0;

        for (AttackEffect effect : activeEffects) {
            modifier += effect.getEscapeModifier();
        }

        return modifier;
    }

    // MFR3 - Determines whether the player successfully escapes
    private static boolean playerEscapeSucceeds(double baseEscapeChance) {

        double escapeChance = baseEscapeChance + getEscapeModifier();

        // Keep probability between 0% and 100%
        escapeChance = Math.max(0.0, Math.min(1.0, escapeChance));

        return combatRandom.nextDouble() < escapeChance;
    }


    // MFR3 - Handles monsters fleeing from combat
    private static void handleMonsterFlee(AttackEffect effect, Monster monster) {

        if ("MONSTER_FLEES".equals(effect.getSpecialAction())) {

            view.printMessage(
                    monster.getName() + " ran away from the battle!"
            );

            // Mark the monster as having fled
            monsterFled = true;

            // Remove the fleeing monster's remaining HP
            // so the player cannot fight it again
            monster.takeDamage(monster.getHp());
        }
    }


    // MFR3 - Monster attempts to steal a random consumable
    private static void handleMonsterStealing(
            AttackEffect effect, Monster monster, Player player) {

        if (!"STEAL_ITEM_30_PERCENT".equals(effect.getSpecialAction())) {
            return;
        }

        // Monster has a 30% chance to steal
        if (combatRandom.nextDouble() >= 0.30) {
            view.printMessage(
                    monster.getName() + " tried to steal an item, but failed!"
            );
            return;
        }

        // Find consumables that can safely be stolen
        ArrayList<Item> stealableItems = new ArrayList<>();

        for (Item item : player.getPlayerInventory()) {

            if (item instanceof Consumable) {
                stealableItems.add(item);
            }
        }

        // No eligible items
        if (stealableItems.isEmpty()) {
            view.printMessage(
                    monster.getName()
                            + " tried to steal an item, but you have no stealable items!"
            );
            return;
        }

        // Select a random consumable
        Item stolenItem = stealableItems.get(
                combatRandom.nextInt(stealableItems.size())
        );

        // Remove the selected item
        player.removeItem(stolenItem);

        view.printMessage(
                monster.getName() + " stole your "
                        + stolenItem.getName() + "!"
        );
    }

    // MFR3 - Applies bonus damage to the monster's next attack
    private static void handleMonsterBonusDamage(AttackEffect effect) {

        if ("MONSTER_NEXT_DAMAGE_PLUS_2".equals(effect.getSpecialAction())) {
            monsterNextAttackBonus += 2;

            view.printMessage(
                    "The monster's next attack will deal 2 extra damage!"
            );
        }
    }
}
