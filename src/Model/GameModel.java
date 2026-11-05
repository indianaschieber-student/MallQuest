package Model;

import java.io.FileNotFoundException;
import java.util.Map;
import java.util.HashMap;

public class GameModel {

    private GameWorldBuilder gameworldbuilder = new GameWorldBuilder();
    private Map<String,Item> gameItems;
    private Map<String,Puzzle> gamePuzzles;
    private Map<String,Monster> gameMonsters;
    private Map<String,Room> gameMap;
    private Player player;

    //GameModel constructor
    //When the GameModel object is created, it calls the GameWorldBuilder to create all the game objects
    public GameModel(){
        GameWorldBuilder worldBuilder = new GameWorldBuilder();

        try {
            // Creates the Game Items from Items.txt
            gameItems = worldBuilder.createGameItems("Items.txt");
        }
        //Error message displays if Items.txt can't be found
        catch (FileNotFoundException exception) {
            System.out.println("Error: Items.txt could not be found.");
            System.out.println("Place Items.txt in the program's file directory.");
            return;
        }

        try {
            gamePuzzles = worldBuilder.createGamePuzzles("Puzzles.txt");
        }
        catch (FileNotFoundException exception) {
            System.out.println("Error: Puzzles.txt could not be found.");
            System.out.println("Place Puzzles.txt in the program's file directory.");
            return;
        }

        try {
            gameMonsters = worldBuilder.createGameMonsters("Monsters.txt");
        }
        catch (FileNotFoundException exception) {
            System.out.println("Error: Monsters.txt could not be found.");
            System.out.println("Place Monsters.txt in the program's file directory.");
            return;
        }

        try {
            gameMap = worldBuilder.createGameMap("Rooms.txt", gameItems, gamePuzzles, gameMonsters);
        }
        catch (FileNotFoundException exception) {
            System.out.println("Error: Rooms.txt could not be found.");
            System.out.println("Place Rooms.txt in the program's file directory.");
            return;
        }

        //Creates a player object, spawns in Floor 1 Hallway, the designated starting area
        Room startingRoom = gameMap.get("F01H");
        Player player1 = new Player(startingRoom);

    }

}
