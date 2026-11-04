package Model;

import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.HashMap;

public class GameWorldBuilder {

    /*public Map <String, Item> createGameItems() {

    }*/
    /*public Map <String, Puzzle> createGamePuzzles() {

    }*/
    /*public Map <String, Monster> createGameMonsters(){
    According to the SRS, each room has a list of potential Monster encounters. Upon creating the world,
    Each room should generate one monster from the list of potential encounters for a room,
    barring 2 or 3 exceptions mentioned in the SRS
    }*/
    //The method to create the Rooms should be last; the Items, Puzzles, and Monsters should
    // be created first, so that the Rooms can then be populated with Items, Puzzles, and Monsters
    //The format for the text file for the Rooms is:
    // RoomID|RoomName|RoomDescription|Adjacent Room RoomIDs|ItemIDs|MonsterIDs|PuzzleID
    //Adjacent RoomIDS, ItemIDs, and MonsterIDs are all comma separated
    public Map <String, Room> createGameMap(String roomFile)
        throws FileNotFoundException {
        Map<String, Room> gameMap = new HashMap<>();

        File roomsFile = new File(roomFile);
        Scanner fileScanner = new Scanner(roomsFile);

        //pass 1 to create initial Room objects with only the roomID, roomName, and roomDescription
        while (fileScanner.hasNextLine()) {
            String line = fileScanner.nextLine().trim();
            String[] data = line.split("\\|");

            String roomID = data[0].trim();
            String roomName = data[1].trim();
            String roomDescription = data[2].trim();

            Room room = new Room(roomID, roomName, roomDescription);
            gameMap.put(roomID, room);
        }
        fileScanner.close();

        Scanner fileScanner2 = new Scanner(roomsFile);
        //pass 2 to add the connections, items, monsters, and puzzles to the Room objects
        while (fileScanner2.hasNextLine()) {
            String line = fileScanner2.nextLine().trim();
            String[] data = line.split("\\|");

            String roomID = data[0].trim();
            String roomName = data[1].trim();
            String roomDescription = data[2].trim();

            //String
        }


        return gameMap;
    }
}
