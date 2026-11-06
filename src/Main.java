import Model.*;
import Controller.*;
import View.*;

public class Main {
    //This class contains the main method to run the program. First, it creates an object
    //for the game model and the game view, then it creates an object for the game controller
    // and passes the model and view objects to it.
    // Finally, it calls the startGame() method of the controller to start the game.
    public static void main(String[] args) {
        /*First, Instantiate the MVC objects. Upon creating a Gamemodel, the GameModel will call the GameWorldBuilder
        to create the Items, Puzzles, Monsters, and Rooms, and also a player in the starting room.
        The Controller then calls the startGame() method to start the game loop
         */
        GameModel model = new GameModel();
        GameView view = new GameView();
        GameController controller = new GameController(model, view);
        controller.startGame();
    }
}
