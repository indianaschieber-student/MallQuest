package Model;

import View.*;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.HashMap;

public class GameModel {

    private GameWorldBuilder gameworldbuilder = new GameWorldBuilder();
    private Map<String,Item> gameItems;
    private Map<String,Puzzle> gamePuzzles;
    private Map<String,Monster> gameMonsters;
    private Map<String,Room> gameMap;
    private Player player1;

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

        //Creates a player object player1, spawns in Floor 1 Hallway, the designated starting area
        Room startingRoom = gameMap.get("F01H");
        this.player1 = new Player(startingRoom);
    }

    public Map<String,Item> getGameItems(){
        return this.gameItems;
    }
    public Map<String,Puzzle> getGamePuzzles(){
        return this.gamePuzzles;
    }
    public Map<String,Monster> getGameMonsters(){
        return this.gameMonsters;
    }
    public Map<String,Room> getGameMap(){
        return this.gameMap;
    }

    public void setPlayerName(String name) {
        player1.setPlayerName(name);
    }
    public String getPlayerName(){
        return player1.getPlayerName();
    }
    public Room getCurrentRoom() {
        Room currentRoom = player1.getPlayerRoom();
        return currentRoom;
    }
    public String getCurrentRoomDescription(){
        Room currentRoom = player1.getPlayerRoom();
        return currentRoom.getRoomDescription();
    }
    public String getCurrentRoomName(){
        Room currentRoom = player1.getPlayerRoom();
        return currentRoom.getRoomName();
    }
    //method for moving the player. Accepts a string that represents a room name. If the room name matches
    //the name of a room in the list of adjacent rooms, the player will move there
    public boolean movePlayerToRoom(String roomName){
        //For loop checks the list of adjacent rooms for the player's current room
        //If the name the player entered is the same as the name of a room in the list,
        // the player will move to that room
        for (Room exit : player1.getPlayerRoom().getRoomExits()){
            if (exit.getRoomName().equalsIgnoreCase(roomName.trim())){
                return player1.movePlayer(exit, gameMap);
            }
        }
        return false;
    }
}
