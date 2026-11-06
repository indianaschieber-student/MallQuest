package Controller;
import Model.*;
import View.*;

import java.util.Map;
import java.util.HashMap;
import java.util.Scanner;

public class GameController {
    private boolean isPlaying = false;
    private GameModel model = new GameModel();
    private GameView view = new GameView();

    //constructor for the GameController class that takes
    //a GameModel and GameView object as arguments
    public GameController(GameModel model, GameView view) {
        this.model = model;
        this.view = view;
    }

    public void startGame() {
        //First, the view prompts the player for their name.
        // The name is then stored as a player attribute
        model.setPlayerName(view.promptForPlayerName());
        //A welcome message that adds some context about the game and tells the player how to see the list of commands
        view.printMessage("Hello, " + model.getPlayerName() + "!\n" +
                        "Welcome to Mall Quest, the Text-Based Adventure Game where you play as a child lost in a shopping mall.\n" +
                        "You must solve puzzles in order to collect 10 Escape Room Stamps and leave through the Exit on the top floor of the mall.\n" +
                        "Enter the name of an adjacent room to walk to it, or enter 'Help' to see a list of player commands.\n" +
                        "We here at Kirva Shopping Mall wish you the best of luck on your Mall Quest!");
        isPlaying = true;
        while (isPlaying == true){
            //create a variable to store the player's current room, then pull the name and description
            //Room currentRoom = model.getCurrentRoom();
            String roomDesc = model.getCurrentRoomDescription();
            String roomName = model.getCurrentRoomName();

            //Print the name and description of the player's current room
            view.printRoomName(roomName);
            view.printRoomDescription(roomDesc);
            //Prompt the player for input
            String command = view.promptForCommand();
            this.parseCommand(command);


            //isPlaying = false;
        }
    }

    //Parse commmand method for general use, while not in combat or solving a puzzle
    public void parseCommand(String command) {

        //First, check to see if the command the player entered is the name of an adjacent room
        //Note to self: add in switch statements to allow the player to enter room abbreviations if time allows
        if (model.movePlayerToRoom(command)) {
            return;
        }
        //Prints a list of commands
        if (command.equalsIgnoreCase("HELP")){
            //view.printCommandList();
        }
        //SRS says "Inspect" but I think "Explore" makes more sense.
        //Shows Items, Monsters, Puzzles and Adjacent Rooms for the player's current room
        //Also, the Inspect command can likely be used on Monsters, Puzzles, and Items and not rooms
        if (command.equalsIgnoreCase("EXPLORE")){

        }
        //Displays a list of rooms the player has visited
        if (command.equalsIgnoreCase("MAP")){

        }
        //Re-prints a Room's Description
        if (command.equalsIgnoreCase("LOOK")){

        }
        //Read Lore Items
        if (command.startsWith("READ")){

        }
        //Interacts with an object in the room (e.g., puzzle elements, items).
        //This is what the SRS says. I'm assuming it's only for items in our inventory though
        //To be honest as I'm looking through the SRS a lot of the finer details of the game
        // seem very unclear, so just use your discretion on this one I guess
        if (command.startsWith("USE")){

        }

        //"SEARCH": Checks the room for hidden items or puzzle hints.
        //This is what is written in the SRS for this command, but I honestly don't know what they mean by this.
        //I don't think this command serves any purpose so we can probably just ignore it

        //Check the player's inventory
        if (command.equalsIgnoreCase("INVENTORY")){

        }
        //Take/Pickup items
        if (command.startsWith("TAKE")||command.startsWith("PICKUP")){

        }
        //Drop items
        if (command.startsWith("DROP")){

        }
        //Solve puzzle command. We may have to back through the SRS and look, but I'm not sure if the player
        // should ask for a hint while solving a puzzle, or before they solve the puzzle
        // If so, there can be a separate method parsePuzzleCommand to handle that interaction
        if (command.equalsIgnoreCase("SOLVE")){

        }
        //This block of code may be a little tricky. The SRS is somewhat unclear, but I think we need a command to inspect
        // monsters and puzzles because there are two FR's for this. However, the Functional Requirements don't seem
        // to have parity on how they want to enter commands. The monster FRs say you can use a number instead of typing a command
        if (command.startsWith("INSPECT")){

        }

        /*if(Check Monster Status){

        }*/

        //Fight monster starts the combat loop. From there, they have a combat interface that may require its own methods
        if (command.equalsIgnoreCase("FIGHT")){

        }

        //This block should go at the very bottom, if the command is not recognized anywhere else
        else{
            view.printMessage("Command not recognized. Type 'Explore' to see a list of adjacent rooms, or 'Help' to " +
                    "see a full list of commands.");
        }

    }

    public void startNewGame(){

    }

    public void loadGame(){

    }

}
