package Controller;
import Model.*;
import View.*;

import java.util.*;

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

    //A helper method for the game loop. This method prints the current room name and description
    //This method is called once when the player initially starts a new game, and
    // then only when the player moves rooms or uses the LOOK command
    private void printCurrentRoom() {
        view.printRoomName(model.getCurrentRoomName());
        view.printRoomDescription(model.getCurrentRoomDescription());
    }

    public void startGame() {
        //First, the view prompts the player for their name.
        // The name is then stored as a player attribute
        model.setPlayerName(view.promptForPlayerName());
        //A welcome message that adds some context about the game and tells the player how to see the list of commands
        view.printWelcome(model.getPlayerName());
        //Display the description for the starting room
        printCurrentRoom();

        isPlaying = true;
        while (isPlaying == true){

            //Prompt the player for input
            String command = view.promptForCommand();
            this.parseCommand(command);

            //isPlaying = false;
        }
    }

    //I used GitHub Copilot to generate this switch block here; it allows the player to enter a room's name
    //or its ID to travel there, as requested by our client
    public String roomAbbreviations(String command) {
        switch (command.trim().toUpperCase(Locale.ROOT)) {
            case "F1H":   return "Floor 1 Hallway";
            case "F2H":   return "Floor 2 Hallway";
            case "FGH":   return "Ground Floor Hallway";
            case "F1AM":  return "Abercrombie And Mitch";
            case "F1FS":  return "Foot Stalker";
            case "F1JS":  return "Tiffany & Jo";
            case "F1BB":  return "Buy Better";
            case "FGMB":  return "A Million Books";
            case "F1DP":  return "Daisy Penny";
            case "FGCX":  return "Chublix";
            case "FGMFC": return "Kavris Food Court";
            case "FGIG":  return "Infinite Grind";
            case "F2SH":  return "Sky-High Doom";
            case "FGFF":  return "Fatal Flora";
            case "F2KMT": return "Kavris Movie Theater";
            case "F2TD":  return "Token of Despair";
            case "F1SOB": return "Salon of Beauty";
            case "F1BS":  return "Big Cutz";
            case "F1OG":  return "Old But Gold";
            case "FGCVS": return "C-Vous-Say Pharmacy";
            case "FGTU":  return "Toys B Us";
            case "F2AS":  return "All Sportz";
            case "FGCSI": return "Informational Service Room";
            case "B01H":  return "Buy Better Back Hallway";
            case "B02H":  return "Old But Gold Back Hallway";
            case "B03H":  return "Kavris Food Court Back Hallway";
            case "B04H":  return "Foot Stalker Back Hallway";
            case "B05H":  return "Chublix Back Hallway";
            case "BO6H":  return "Infinite Grind Back Hallway";
            case "B07H":  return "Sky-High Doom Back Hallway";
            case "B08H":  return "All Sportz Back Hallway";
            case "B09H":  return "Toys B Us Back Hallway";
            case "B10H":  return "Big Cutz Back Hallway";
            case "EX01":  return "The Exit";
            default:      return command;
        }
    }

    //Same deal here, I used GitHub Copilot to create this switch statement that allows the user to enter
    // a number instead of typing out the full command
    public String numberCommand(String command) {
        switch (command.trim()) {
            case "1":  return "EXPLORE";
            case "2":  return "MAP";
            case "3":  return "LOOK";
            case "4":  return "INVENTORY";
            case "5":  return "PICKUP";
            case "6":  return "DROP";
            case "7":  return "EXAMINE";
            case "8":  return "USE";
            case "9":  return "INSPECT";
            case "10": return "SOLVE";
            case "11": return "HINT";
            case "12": return "FIGHT";
            case "13": return "SAVE";
            case "14": return "QUIT";
            default:   return command;
        }
    }

    //Parse commmand method for general use, while not in combat or solving a puzzle
    public void parseCommand(String command) {
        //Convert numbers into their corresponding commands
        command = numberCommand(command);

        //Check to see if the command the player entered is the name of an adjacent room, or its room id
        if (model.movePlayerToRoom(roomAbbreviations(command))) {
            printCurrentRoom();
            return;
        }
        //Prints a list of commands
        if (command.equalsIgnoreCase("HELP")){
            view.printCommandList();
            return;
        }
        //SRS says "Inspect" but I think "Explore" makes more sense
        //Shows Monsters, Puzzles, Items, and Adjacent Rooms for the player's current room
        //Also, the Inspect command can likely be used on Monsters, Puzzles, and Items and not rooms
        if (command.equalsIgnoreCase("EXPLORE")){
            Room room = model.getCurrentRoom();
            String monsterName = "";
            if (room.getRoomMonster() != null){
                Monster monster = room.getRoomMonster();
                monsterName = monster.getName();
            }
            boolean hasPuzzle = room.hasPuzzle();
            List<Item> items = room.getRoomInventory();
            List<String> itemNames = new ArrayList<>();
            for (Item item : items){
                itemNames.add(item.getName());
            }
            List<Room> exits = room.getRoomExits();
            Map<String, String> exitNames = new HashMap<>();
            for (Room tempRoom: exits){
                exitNames.put(tempRoom.getRoomID(),tempRoom.getRoomName());
            }
            view.printExploreCommand(monsterName, hasPuzzle, itemNames, exitNames);
            return;
        }
        //Displays a list of all rooms and which floor they are on
        if (command.equalsIgnoreCase("MAP")){
            Map<String,Room> rooms = model.getGameMap();
            Map<String, String> floor1Rooms = new HashMap<>();
            Map<String, String> floor2Rooms = new HashMap<>();
            Map<String, String> groundFloorRooms = new HashMap<>();
            for (Map.Entry<String, Room> entry : rooms.entrySet()) {
                if(entry.getKey().startsWith("F1")) {
                    Room tempRoom = entry.getValue();
                    floor1Rooms.put(entry.getKey(), tempRoom.getRoomName());
                }
                else if(entry.getKey().startsWith("F2")) {
                    Room tempRoom = entry.getValue();
                    floor2Rooms.put(entry.getKey(), tempRoom.getRoomName());
                }
                else if(entry.getKey().startsWith("FG")) {
                    Room tempRoom = entry.getValue();
                    groundFloorRooms.put(entry.getKey(), tempRoom.getRoomName());
                }
            }
            view.printMap(floor1Rooms,floor2Rooms,groundFloorRooms);
            return;
        }
        //Re-prints a Room's Description
        if (command.equalsIgnoreCase("LOOK")||command.equalsIgnoreCase("LOOK AROUND")){
            printCurrentRoom();
            return;
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
        //Read Lore Items; The SRS explicitly asked for a "READ" command,
        // but I think a generic "EXAMINE" command would make more sense.
        //That way, you can see the item descriptions for all items, not just lore items.
        if (command.startsWith("READ")||command.startsWith("EXAMINE")){

        }
        //Interacts with an object in the room (e.g., puzzle elements, items).
        //This is what the SRS says. I'm assuming it's only for items in our inventory though
        //To be honest as I'm looking through the SRS a lot of the finer details of the game
        // seem very unclear, so just use your discretion on this one I guess
        if (command.startsWith("USE")){

        }

        //This block of code may be a little tricky. The SRS is somewhat unclear, but I think we need a command to inspect
        // monsters and puzzles because there are two FR's for this. However, the Functional Requirements don't seem
        // to have parity on how they want to enter commands. The monster FRs say you can use a number instead of typing a command
        if (command.startsWith("INSPECT")){
            //I think we could also have a generic INSPECT command that will just display the puzzle and monster information,
            // it would seem redundant to ask the player if they want to inspect a puzzle or monster, but
            //Block of code to inspect monster: view the monster's name, text description, and difficulty level and any
            // other relevant information

            //Block of code to inspect puzzle: Presumably returns a puzzle's description

        }

        //The SRS didn't specify that you have to be in the puzzle to request a hint, and this makes sense
        //The Hint option should tell you where to find the lore item with the puzzle solution, or tell you
        // to examine the item in your inventory if you have already picked it up.
        if (command.equalsIgnoreCase("HINT")||command.equalsIgnoreCase("REQUEST HINT")){

        }

        //Solve puzzle command. We may have to back through the SRS and look, but I'm not sure if the player
        // should ask for a hint while solving a puzzle, or before they solve the puzzle
        // If so, there can be a separate method parsePuzzleCommand to handle that interaction
        if (command.equalsIgnoreCase("SOLVE")||command.equalsIgnoreCase("SOLVE PUZZLE")){

        }

        //Fight monster starts the combat loop. From there, they have a combat interface that may require its own methods
        if (command.equalsIgnoreCase("FIGHT")){

        }

        if (command.equalsIgnoreCase("SAVE")||command.equalsIgnoreCase("SAVE GAME")){

        }

        if (command.equalsIgnoreCase("QUIT")||command.equalsIgnoreCase("QUIT GAME")){
            //Ask the player "Are you sure you want to quit?" Prompt for Y/N
            isPlaying = false;
            //view.printGameOver();
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
