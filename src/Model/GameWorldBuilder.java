package Model;

import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

public class GameWorldBuilder {

    //These methods will each return a HashMap of their respective objects, with the key being the respective ID,
    // and the value being the object. This will allow the createGameMap method to easily populate the Room objects
    // by pulling the IDs from the Rooms.txt file and retrieving the corresponding objects from the Hashmaps.
    // I think it may also make sense to still store these objects in an ArrayList as well, just in case
    
    public Map <String, Item> createGameItems(String itemFile)
            throws FileNotFoundException {
        Map<String, Item> itemsByID = new HashMap<>();
        //read the Items.txt file and create Item objects, storing the objects in the HashMap with the itemID as the key
        List<Item> gameItems = new ArrayList<>();
        // Optionally, the Item objects can also be stored in an ArrayList for easy iteration,
        // but the HashMap will allow for quick retrieval by ID
        return itemsByID;
    }

    public Map <String, Puzzle> createGamePuzzles(String puzzleFile)
            throws FileNotFoundException {
        Map<String, Puzzle> puzzlesByID = new HashMap<>();
        //read the Puzzles.txt file and create Puzzle objects, storing the objects in the HashMap with the puzzleID as the key
        List<Puzzle> gamePuzzles = new ArrayList<>();
        // Optionally, the Puzzle objects can also be stored in an ArrayList for easy iteration,
        // but the HashMap will allow for quick retrieval by ID
        return puzzlesByID;
    }

    public Map <String, Monster> createGameMonsters(String monsterFile)
            throws FileNotFoundException {
        Map<String, Monster> monstersByID = new HashMap<>();
        //read the Monsters.txt file and create Monster objects, storing the objects in the HashMap with the monsterID as the key
        List<Monster> gameMonsters = new ArrayList<>();
        // Optionally, the Monster objects can also be stored in an ArrayList for easy iteration,
        // but the HashMap will allow for quick retrieval by ID
        return monstersByID;
    }

    //The method to create the Rooms should be last; the Items, Puzzles, and Monsters should
    // be created first, so that the Rooms can then be populated with Items, Puzzles, and Monsters
    //The format for the text file for the Rooms is:
    // RoomID|RoomName|RoomDescription|Adjacent Room RoomIDs|ItemIDs|MonsterIDs|PuzzleID
    //Adjacent RoomIDS, ItemIDs, and MonsterIDs are all comma separated
    public Map <String, Room> createGameMap(String roomFile, Map<String, Item> itemsByID,
                                            Map<String, Puzzle> puzzlesByID, Map<String, Monster> monstersByID)
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

            //create the Room objects, then add them to the GameMap
            Room room = new Room(roomID, roomName, roomDescription);
            gameMap.put(roomID, room);
        }
        fileScanner.close();

        Scanner fileScanner2 = new Scanner(roomsFile);
        //pass 2 to add the connections, items, monsters, and puzzles to the Room objects
        while (fileScanner2.hasNextLine()) {
            String line = fileScanner2.nextLine().trim();
            String[] data = line.split("\\|",-1);

            String roomID = data[0].trim();
            String roomName = data[1].trim();
            String roomDescription = data[2].trim();

            //pulls the Room IDs from the text file into an ArrayList, then creates a list that references
            // their corresponding room objects, using the HashMap gameMap as a reference
            ArrayList<String> adjacentRoomIDs = new ArrayList<>();
            for (String id : data[3].trim().split(",")) {
                adjacentRoomIDs.add(id.trim());
            }
            ArrayList<Room> adjacentRooms = new ArrayList<>();
            for (String id: adjacentRoomIDs) {
                Room adjacentRoom = gameMap.get(id);
                adjacentRooms.add(adjacentRoom);
            }

            //Pulls the Item IDs from the text file into an ArrayList, then creates a list of corresponding Item
            // objects, using the HashMap itemsByID as a reference
            ArrayList<String> roomItemIDs = new ArrayList<>();
            ArrayList<Item> roomItems = new ArrayList<>();
            if(!(data[4].isBlank())){
                for (String id : data[4].trim().split(",")) {
                    roomItemIDs.add(id.trim());
                }
                for (String id : roomItemIDs) {
                    Item item = itemsByID.get(id);
                    roomItems.add(item);
                }
            }


            //The room object is instantiated with an ID, name, description, inventory, and adjacent Room exits
            //Then, the method checks for monsters and puzzles in the text file, then uses the ID to lookup the
            // corresponding object and add it to the appropriate room
            Room room = new Room(roomID, roomName, roomDescription, roomItems, adjacentRooms);

            List<String> roomMonsterIDs = new ArrayList<>();
            if(!(data[5] .isBlank())){
                for (String id : data[5].trim().split(",")) {
                    roomMonsterIDs.add(id.trim());
                }
                Random rand = new Random();
                int randomIndex = rand.nextInt(roomMonsterIDs.size());

                Monster roomMonster = monstersByID.get(roomMonsterIDs.get(randomIndex));

                room.addMonster(roomMonster);
            }

            if(!(data[6].isBlank())){
                String puzzleID = data[6].trim();

                Puzzle roomPuzzle = puzzlesByID.get(puzzleID);

                room.setRoomPuzzle(roomPuzzle);
            }
            //add the updated Room objects to the gameMap
            gameMap.put(roomID, room);
        }
        fileScanner2.close();

        return gameMap;
    }
}
