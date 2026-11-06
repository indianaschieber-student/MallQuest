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
                        "Enter the name of an adjacent room to walk to it, or enter 'Help' to see a list of player commands.\n +
                        "We here at Kirva Shopping Mall wish you the best of luck on your Mall Quest!");
        isPlaying = true;
        while (isPlaying == true){
            isPlaying = false;
        }
    }


    public String parseCommand() {

        return "" ;

    }

    public void startNewGame(){

    }

    public void loadGame(){

    }

}
