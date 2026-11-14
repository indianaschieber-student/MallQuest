package Controller;

import Model.*;
import View.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PuzzleLoop {

    public void startPuzzleLoop(Puzzle puzzle, Room room, GameView view, GameModel model,
                                int attemptsLeft, Player player, Map<String, Item> items){
        while (attemptsLeft > 0) {
            view.printPuzzleDescription(room.getRoomName(), puzzle.getDescription());
            view.printMessage("You have " + attemptsLeft + " attempts left. Enter one of the answers for the prompt, " +
                    "its corresponding number, \nenter 'Attempts' to see how many attempts you have left, 'Hint' to see " +
                    "where to find the puzzle solution, \nor enter 'Quit' to exit the puzzle.");
            List<PuzzleStep> puzzleSteps = puzzle.getPuzzleSteps();
            List<String> playerInputs = new ArrayList<>();
            for (int x=0; x < puzzleSteps.size(); x++){
                //Goes through the list of puzzle steps and asks the player each prompt
                //Player input stored as a String in a list of playerInputs
                String input = view.promptForPuzzleSolution((puzzleSteps.get(x)).getPrompt());
                String newInput = switchPlayerInput(input, (puzzleSteps.get(x)).getChoices());
                while (newInput.equalsIgnoreCase("HINT")||
                        (newInput.equalsIgnoreCase("REQUEST HINT"))||
                (newInput.equalsIgnoreCase("ATTEMPTS"))||
                        (newInput.equalsIgnoreCase("CHECK ATTEMPTS"))){
                    if (newInput.equalsIgnoreCase("HINT")||
                            (newInput.equalsIgnoreCase("REQUEST HINT"))){
                        view.printPuzzleHint(puzzle.getHint());
                        input = view.promptForPuzzleSolution((puzzleSteps.get(x)).getPrompt());
                        newInput = switchPlayerInput(input, (puzzleSteps.get(x)).getChoices());
                    }
                    else if (newInput.equalsIgnoreCase("ATTEMPTS")||
                            newInput.equalsIgnoreCase("CHECK ATTEMPTS")){
                        view.printMessage("You have " + attemptsLeft + " attempts left.");
                        input = view.promptForPuzzleSolution((puzzleSteps.get(x)).getPrompt());
                        newInput = switchPlayerInput(input, (puzzleSteps.get(x)).getChoices());
                    }

                }
                if (newInput.equalsIgnoreCase("QUIT")){
                    return;
                }
                playerInputs.add(newInput);
            }
            //Now, compare the player inputs against the correct answers
            //If player enters correct solution, puzzle is solved, player is awarded escape room stamp (stamp is removed from room)
            // and player's puzzle solved count goes up
            if (puzzle.attempt(playerInputs) == true){
                view.printPuzzleSuccess(puzzle.getCorrectText());
                Item stamp = items.get("QI001");
                (player.getPlayerInventory()).add(stamp);
                (room.getRoomInventory()).remove(stamp);
                player.increasePuzzlesSolved();
                return;
            }
            attemptsLeft--;
            //Print message telling the player they failed to solve the puzzle,
            // what their answers were, restart puzzle loop
            //If player runs out of attempts, kick them from the room and 50/50 chance to lose a random item
            if (attemptsLeft == 0){
                view.printPuzzleFailure(puzzle.getIncorrectText());
                int random = 1; //(int)(Math.random() * 2);//generate a random int 1 or 0
                if (random == 1){
                    String itemName = player.removeRandomConsumable();
                    if(!(itemName.isBlank())){
                        view.printLostItem(itemName);  // 50/50 chance the player loses a consumable, if they have any
                    }
                }
                String oldRoomName = (player.getPlayerRoom()).getRoomName();
                String newRoomName = model.ejectPlayer();
                if (!(newRoomName == null||newRoomName.isBlank())){
                    view.printPlayerEjected(oldRoomName,newRoomName);
                }
            }
            else{
                view.printPuzzleTryAgain(playerInputs);
                continue;
            }
        }

    }

    public void startAlternatePuzzleLoop (Puzzle puzzle, Room room, GameView view, GameModel model,
                                          int attemptsLeft, Player player, Map<String, Item> items, List<Item> inventory){
        if (inventory.isEmpty()){
            view.printMessage("Your inventory is empty; you cannot solve this puzzle with nothing to offer the plant!");
            return;
        }

        while (attemptsLeft > 0) {
            view.printPuzzleDescription(room.getRoomName(), puzzle.getDescription());
            view.printMessage("You have " + attemptsLeft + " attempts left. Enter the name of an item from your" +
                    " inventory, \nenter 'Attempts' to see how many attempts you have left, " +
                    "or enter 'Quit' to exit the puzzle.");

            Map<String, Integer> itemCounts = new LinkedHashMap<>();
            for (Item item : inventory) {
                itemCounts.put(item.getName(), itemCounts.getOrDefault(item.getName(), 0) + 1);
            }
            List<String> itemNames = new ArrayList<>();
            for (Item item : inventory) {
                itemNames.add(item.getName());
            }
            List<PuzzleStep> puzzleSteps = puzzle.getPuzzleSteps();
            List<String> playerInputs = new ArrayList<>();
            for (int x=0; x < puzzleSteps.size(); x++){
                view.printMessage("--- Your Inventory ---");
                //Copied code to display player inventory
                // Count occurrences by item name to group duplicates cleanly

                for (Map.Entry<String, Integer> entry : itemCounts.entrySet()) {
                    view.printMessage("- " + entry.getKey() + " x" + entry.getValue());
                }
                //Goes through the list of puzzle steps and asks the player each prompt
                //Player input stored as a String in a list of playerInputs
                String input = view.promptForPuzzleSolution((puzzleSteps.get(x)).getPrompt());
                while (input.equalsIgnoreCase("HINT")||
                        (input.equalsIgnoreCase("REQUEST HINT"))||
                        (input.equalsIgnoreCase("ATTEMPTS"))||
                        (input.equalsIgnoreCase("CHECK ATTEMPTS"))){
                    if (input.equalsIgnoreCase("HINT")||
                            (input.equalsIgnoreCase("REQUEST HINT"))){
                        view.printPuzzleHint(puzzle.getHint());
                        input = view.promptForPuzzleSolution((puzzleSteps.get(x)).getPrompt());
                    }
                    else if (input.equalsIgnoreCase("ATTEMPTS")||
                            input.equalsIgnoreCase("CHECK ATTEMPTS")){
                        view.printMessage("You have " + attemptsLeft + " attempts left.");
                        input = view.promptForPuzzleSolution((puzzleSteps.get(x)).getPrompt());
                    }

                }
                if (input.equalsIgnoreCase("QUIT")){
                    return;
                }
                playerInputs.add(input);
            }

            boolean inputInInventory = false;
            for (String itemName : itemNames){
                if (itemName.equalsIgnoreCase(playerInputs.get(0))) {
                    inputInInventory = true;
                    break;
                }
            }
            if(!inputInInventory){
                view.printMessage("That item is not in your inventory!");
                continue;
            }

            //Now, compare the player inputs against the correct answers
            //If player enters correct solution, puzzle is solved, player is awarded escape room stamp (stamp is removed from room)
            // and player's puzzle solved count goes up
            if ((puzzle.attempt(playerInputs))){
                view.printPuzzleSuccess(puzzle.getCorrectText());
                Item stamp = items.get("QI001");
                (player.getPlayerInventory()).add(stamp);
                (room.getRoomInventory()).remove(stamp);
                player.increasePuzzlesSolved();
                return;
            }
            attemptsLeft--;
            //Print message telling the player they failed to solve the puzzle,
            // what their answers were, restart puzzle loop
            //If player runs out of attempts, kick them from the room and 50/50 chance to lose a random item
            if (attemptsLeft == 0){
                view.printPuzzleFailure(puzzle.getIncorrectText());
                int random = 1; //(int)(Math.random() * 2);//generate a random int 1 or 0
                if (random == 1){
                    String itemName = player.removeRandomConsumable();
                    if(!(itemName.isBlank())){
                        view.printLostItem(itemName);  // 50/50 chance the player loses a consumable, if they have any
                    }
                }
                String oldRoomName = (player.getPlayerRoom()).getRoomName();
                String newRoomName = model.ejectPlayer();
                if (!(newRoomName == null||newRoomName.isBlank())){
                    view.printPlayerEjected(oldRoomName,newRoomName);
                }
            }
            else{
                view.printPuzzleTryAgain(playerInputs);
                continue;
            }
        }



    }

    //The maximum number of steps in a puzzle is 6, so check for 1-6 and
    // switch with the corresponding answer choice, if applicable
    public String switchPlayerInput(String input, List<String> answerChoices){
        String output = switch(input){
            case "1" -> answerChoices.get(0);
            case "2" -> {
                if (answerChoices.size() > 1) { yield answerChoices.get(1); }
                else yield input;
            }
            case "3" -> {
                if (answerChoices.size() > 2) { yield answerChoices.get(2); }
                else yield input;
            }
            case "4" -> {
                if (answerChoices.size() > 4) { yield answerChoices.get(3); }
                else yield input;
            }
            case "5" -> {
                if (answerChoices.size() > 4) { yield answerChoices.get(4); }
                else yield input;
            }
            case "6" -> {
                if (answerChoices.size() > 5) { yield answerChoices.get(5); }
                else yield input;
            }
            default -> input;
        };
        return output;
    }

}
