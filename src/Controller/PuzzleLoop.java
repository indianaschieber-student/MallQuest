package Controller;

import Model.*;
import View.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PuzzleLoop {

    public void startPuzzleLoop(Puzzle puzzle, Room room, GameView view, int attemptsLeft, Player player, Map<String, Item> items){
        while (attemptsLeft > 0) {
            view.printPuzzleDescription(room.getRoomName(), puzzle.getDescription());
            view.printMessage("You have " + attemptsLeft + " attempts left. Enter one of the answers for the prompt, " +
                    "its corresponding number, or enter 'Quit' to exit the puzzle.");
            List<PuzzleStep> puzzleSteps = puzzle.getPuzzleSteps();
            List<String> playerInputs = new ArrayList<>();
            for (int x=0; x < puzzleSteps.size(); x++){
                //Goes through the list of puzzle steps and asks the player each prompt
                //Player input stored as a String in a list of playerInputs
                String input = view.promptForPuzzleSolution((puzzleSteps.get(x)).getPrompt());
                String newInput = switchPlayerInput(input, (puzzleSteps.get(x)).getChoices());
                if (newInput.equalsIgnoreCase("QUIT")){
                    return;
                }
                playerInputs.add(newInput);
            }
            //Now, compare the player inputs against the correct answers
            //If player enters correct solution, puzzle is solved, player is awarded escape room stamp (stamp is removed from room)
            // and player's puzzle solved count goes up
            if (puzzle.attempt(playerInputs) == true){
                view.printMessage(puzzle.getCorrectText());
                view.printMessage("You successfully solved the puzzle! An escape room stamp has been added to your inventory!");
                Item stamp = items.get("QI001");
                (player.getPlayerInventory()).add(stamp);
                (room.getRoomInventory()).remove(stamp);
                player.increaseRoomsVisited();
                return;
            }
            //Print message telling the player they failed to solve the puzzle,
            // what their answers were, restart puzzle loop
            //If player runs out of attempts, kick them from the room and 50/50 chance to lose a random item
            else{
                attemptsLeft--;
                view.printMessage("You failed to solve the puzzle...");
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
