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
        view.printMessage("Welcome Shopper.");
        //First, the view prompts the player for their name.
        // The name is then stored as a player attribute
        model.setPlayerName(view.promptForPlayerName());
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
