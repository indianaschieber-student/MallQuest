package Controller;

import Model.*;
import View.GameView;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

public class combatLoop {
    private boolean isPlaying = false;
    private GameModel model = new GameModel();
    private GameView view = new GameView();

    // MFR4 - Randomly selects a monster attack during combat
    private Random combatRandom = new Random();

    // MFR3 - Handles combat between the player and a monster
    private void combatLoop () {


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

            // Display the monster's current HP
            view.printMessage(monster.getName() + " HP: " + monster.getHp() + "/" + monster.getMaxHp()
            );

            // MFR3 - Display the player's current and maximum HP
            view.printMessage("Your HP: " + player.getCurrentHp() + "/" + player.getMaxHp());

            // MFR3 - Display available combat actions
            view.printMessage("Choose an action: ATTACK, USE ITEM, ACT, RUN or CHECK STATUS");

            String combatCommand = view.promptForCommand().trim().toUpperCase(Locale.ROOT);


            // MFR3 - Player attacks the monster
            if (combatCommand.equals("ATTACK")) {

                // MFR3 - Use the player's current base attack damage
                int damage = player.getBaseAttack();

                // Reduce the monster's HP
                monster.takeDamage(damage);

                view.printMessage("You attacked " + monster.getName() + " and dealt " + damage + " damage!");

                // Check if the monster has been defeated

                if (monster.isDefeated()) {

                    handleMonsterVictory(monster, player);

                    inCombat = false;
                }


                else {
                    // MFR4 - Monster retaliates if it survived
                    monsterAttackTurn(monster, player);

                    // TODO MFR3 - Player defeat recovery

                    if (player.getCurrentHp() <= 0) {
                        view.printMessage("[TODO] Player defeat recovery: lose one random item and respawn at Pharmacy (F1PH) with 1 HP.");

                        inCombat = false;
                    }
                }
            }


            // MFR3 - Player attempts to use an item during combat
            else if (combatCommand.equals("USE ITEM")) {

                // Call the combat item selection method
                boolean itemUsed = useCombatItem(player, monster);

                // If a valid item was used, the monster gets a turn
                if (itemUsed) {

                    // Check whether the item defeated the monster
                    if (monster.isDefeated()) {

                        handleMonsterVictory(monster, player);

                        inCombat = false;
                    }

                    else {

                        // Monster retaliates after item use
                        monsterAttackTurn(monster, player);

                        // Check whether the player was defeated
                        if (player.getCurrentHp() <= 0) {

                            // TODO MFR3 - Player defeat recovery
                            // SRS requires respawn at Pharmacy with 1 HP
                            // get pharmacy ID
                            // and removal of one random inventory item.
                            view.printMessage("[TODO] Player defeat recovery: " +
                                    "lose one random item and respawn at Pharmacy (F1PH) with 1 HP.");

                            // Temporarily end combat, not the game
                            inCombat = false;
                        }
                    }
                }
            }


            // MFR4 - Player attempts an alternative action on a monster
            else if (combatCommand.equals("ACT")) {

                // Attempt to perform an action on the monster
                boolean actionPerformed = actOnMonster(monster);

                // A successful action may consume the player's turn
                if (actionPerformed) {

                    if (monster.isDefeated()) {

                        handleMonsterVictory(monster, player);

                        inCombat = false;
                    }

                    else {

                        // Monster takes its turn unless an ACT effect prevents it
                        monsterAttackTurn(monster, player);

                        // Check whether the player was defeated
                        if (player.getCurrentHp() <= 0) {
                            view.printMessage("[TODO] Player defeat recovery: lose one random item and respawn at Pharmacy (F1PH) with 1 HP.");

                            inCombat = false;
                        }
                    }
                }
            }


            // MFR3 - Player attempts to escape combat
            else if (combatCommand.equals("RUN")) {

                // Check whether the monster allows escape
                if (monster.canEscape()) {

                    view.printMessage("You successfully escaped from " + monster.getName() + "!");

                    // End combat
                    inCombat = false;
                } else {
                    view.printMessage("You cannot escape from " + monster.getName() + "!");

                    // Monster retaliates after failed escape
                    monsterAttackTurn(monster, player);

                    // MFR4 - Check whether the player has been defeated
                    if (player.getCurrentHp() <= 0) {
                        view.printMessage("You have been defeated! Game over.");
                        isPlaying = false;
                        inCombat = false;
                    }
                }
            }


            // MFR2 - Player checks the monster's current status
            else if (combatCommand.equals("CHECK STATUS") || combatCommand.equals("STATUS")) {

                // Display the monster's current combat information
                checkMonsterStatus(monster);

                // Checking status does not consume a combat turn
            }

            // Handle invalid combat commands
            else {
                view.printMessage(
                        "Invalid command. Please enter ATTACK, USE ITEM, ACT, RUN, or CHECK STATUS."
                );
            }


        }

    }


    // MFR4 - Monster attacks the player during combat
    private void monsterAttackTurn (Monster monster, Player player){

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

        // Get the selected attack's damage
        int damage = selectedAttack.getDamage();

        // Display the monster's attack
        view.printMessage(monster.getName() + " used " + selectedAttack.getName() + "!");

        // MFR4 - Monster deals damage to the player
        player.takeDamage(damage);

        // Display how much damage the monster dealt
        view.printMessage(monster.getName() + " dealt " + damage + " damage to you!");

        // Display the player's remaining HP
        view.printMessage("Your remaining HP: " + player.getCurrentHp() + "/" + player.getMaxHp());

        // TODO MFR4:
        // Apply selectedAttack.getEffect() once the team
        // finishes deciding how status effects will work.
    }


    // MFR3 / IFR6 - Handles using an item during combat
    private boolean useCombatItem(Player player, Monster monster) {

        // TODO INVENTORY #1:
        // Player.java currently stores playerInventory as a private field,
        // but does not provide a getter to access the items.
        view.printMessage("[TODO INVENTORY #1] Cannot display inventory yet. " + "Player.java needs an inventory getter.");

        // TODO INVENTORY #2 - Item selection
        view.printMessage("[TODO] Display numbered inventory and select an item.");

        // TODO CONSUMABLE #1 - Healing
        view.printMessage("[TODO] Healing items will restore player HP " + "without exceeding maximum HP.");

        // TODO CONSUMABLE #2 - Combat damage
        view.printMessage("[TODO] Combat damage items will reduce monster HP.");

        // TODO CONSUMABLE #3 - Debuff removal
        view.printMessage("[TODO] Debuff-removing items will clear applicable effects.");

        // TODO INVENTORY #3 - Item removal
        view.printMessage("[TODO] Remove the consumed item from inventory.");

        // No item was used, so the player's turn is not consumed.
        return false;
    }


    // MFR2 - Displays the monster's current combat status
    private void checkMonsterStatus(Monster monster) {

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

        view.printMessage(
                "HP: " + monster.getHp()
                        + "/" + monster.getMaxHp()
        );

        // Display the monster's level
        view.printMessage(
                "Level: " + monster.getLvl()
        );

        // Display active status conditions
        String status = monster.getStatus();

        if (status == null || status.trim().isEmpty()) {
            status = "None";
        }

        view.printMessage(
                "Status: " + status
        );

        view.printMessage("----------------------");
    }


    // MFR4 - Handles alternative interactions with monsters
    private boolean actOnMonster(Monster monster) {

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
    private void handleMonsterVictory(Monster monster, Player player) {

        // Announce the monster's defeat
        view.printMessage(
                "You defeated " + monster.getName() + "!"
        );

        // Calculate XP based on the monster's level
        int xpReward = monster.getLvl() * 10;

        // Give the player XP
        boolean leveledUp = player.gainXp(xpReward);

        view.printMessage(
                "You earned " + xpReward + " XP!"
        );

        // Announce a level-up if one occurred
        if (leveledUp) {
            view.printMessage(
                    "LEVEL UP! You are now Level "
                            + player.getPlayerLevel() + "!"
            );

            view.printMessage(
                    "Your maximum HP is now "
                            + player.getMaxHp()
                            + " and your attack is now "
                            + player.getBaseAttack() + "!"
            );
        }

        // TODO MFR3 - Monster reward items
        view.printMessage(
                "[TODO] Award the monster's designated reward items."
        );

        // TODO MFR3 - Escape Room Stamp
        view.printMessage(
                "[TODO] Award an Escape Room Stamp if applicable."
        );
    }


}
