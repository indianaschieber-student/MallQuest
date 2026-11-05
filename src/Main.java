import Model.*;
import Controller.*

public class Main {
    //This class contains the main method to run the program. First, it creates an object
    //for the game model and the game view, then it creates an object for the game controller
    // and passes the model and view objects to it.
    // Finally, it calls the startGame() method of the controller to start the game.
    public static void main(String[] args) {
        GameModel model = new GameModel();
        //GameView view = new GameView();
        //GameController controller = new GameController(model, view);
        //controller.startGame();
    }
}
