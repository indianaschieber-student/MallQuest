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
    
    public Map<String, Item> createGameItems(String itemFile) throws FileNotFoundException {
    Map<String, Item> itemsByID = new HashMap<>();
    File file = new File(itemFile);
    if (!file.exists()) return itemsByID;
    
    Scanner scanner = new Scanner(file);
    while (scanner.hasNextLine()) {
        String line = scanner.nextLine().trim();
        if (line.isEmpty() || line.startsWith("#")) continue;
        
        String[] data = line.split("\\|", -1);
        if (data.length >= 4) {
            String itemID = data[0].trim();
            String category = data[1].trim();
            String name = data[2].trim();
            String description = data[3].trim();
            
            Item item = new Item(itemID, category, name, description);
            itemsByID.put(itemID, item);
        }
    }
    scanner.close();
    return itemsByID;
}

public Map<String, Puzzle> createGamePuzzles(String puzzleFile) {
    Map<String, Puzzle> puzzlesByID = new HashMap<>();
    try {
        // Leverages the loadFromFile method already built into your Puzzle class!
        puzzlesByID.putAll(Puzzle.loadFromFile(puzzleFile));
    } catch (IOException e) {
        System.out.println("Could not load puzzle file: " + puzzleFile);
    }
    return puzzlesByID;
}

    // Reads MonsterAttacks.txt and stores each attack by its ID.
    public Map<String, MonsterAttack> createMonsterAttacks()
            throws FileNotFoundException {

        Map<String, MonsterAttack> attacksByID = new HashMap<>();

        File file = new File("MonsterAttacks.txt");
        Scanner fileReader = new Scanner(file);

        while (fileReader.hasNextLine()) {
            String line = fileReader.nextLine().trim();

            // Skip empty lines and comments.
            if (line.isEmpty() || line.startsWith("#")) {
                continue;
            }

            String[] data = line.split("\\|", -1);

            if (data.length != 5) {
                System.out.println("Invalid attack data: " + line);
                continue;
            }

            String id = data[0].trim();
            String name = data[1].trim();
            int damage = Integer.parseInt(data[2].trim());
            String description = data[3].trim();
            String effect = data[4].trim();

            MonsterAttack newAttack = new MonsterAttack(id, name, damage, description, effect);

            attacksByID.put(id, newAttack);
        }

        fileReader.close();
        return attacksByID;
    }


    public Map<String, Monster> createGameMonsters()
            throws FileNotFoundException {

        Map<String, Monster> monstersByID = new HashMap<>();

        // Load all monster attacks first.
        Map<String, MonsterAttack> attacksByID =
                createMonsterAttacks();

        File file = new File("Monsters.txt");
        Scanner fileReader = new Scanner(file);

        while (fileReader.hasNextLine()) {
            String line = fileReader.nextLine().trim();

            // Skip blank lines and comments.
            if (line.isEmpty() || line.startsWith("#")) {
                continue;
            }

            String[] data = line.split("\\|", -1);

            // ID, Name, Description, Location, Level, HP, AttackIDs
            if (data.length != 7) {

                System.out.println("Invalid monster data: " + line);
                continue;
            }

            String id = data[0].trim();
            String name = data[1].trim();
            String description = data[2].trim();
            String locationFound = data[3].trim();

            int lvl = Integer.parseInt(data[4].trim());
            int maxHp = Integer.parseInt(data[5].trim());

            ArrayList<MonsterAttack> monsterAttacks = new ArrayList<>();

            String[] attackIDs = data[6].split(",");

            for (String attackID : attackIDs) {
                attackID = attackID.trim();

                MonsterAttack monsterAttack = attacksByID.get(attackID);

                if (monsterAttack != null) {
                    monsterAttacks.add(monsterAttack);

                }
                else {
                    System.out.println("Missing attack " + attackID + " for monster " + id);
                }
            }

            Monster newMonster = new Monster(id, name, description, locationFound, lvl, maxHp, monsterAttacks);

            monstersByID.put(id, newMonster);
        }

        fileReader.close();

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

            //create the Room objects, then ad them to the GameMap
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
            for (String id : data[4].trim().split(",")) {
                roomItemIDs.add(id.trim());
            }
            ArrayList<Item> roomItems = new ArrayList<>();
            for (String id : roomItemIDs) {
                Item item = itemsByID.get(id);
            }

            //The room object is instantiated with an ID, name, description, inventory, and adjacent Room exits
            //Then, the method checks for monsters and puzzles in the text file, then uses the ID to lookup the corresponding
            //object and add it to the appropriate room
            Room room = new Room(roomID, roomName, roomDescription, roomItems, adjacentRooms);

            List<String> roomMonsterIDs = new ArrayList<>();
            if(data[5] != null){
                for (String id : data[5].trim().split(",")) {
                    roomMonsterIDs.add(id.trim());
                }
                Random rand = new Random();
                int randomIndex = rand.nextInt(roomMonsterIDs.size());

                Monster roomMonster = monstersByID.get(roomMonsterIDs);

                room.addMonster(roomMonster);
            }

            if(data[6] != null){
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
