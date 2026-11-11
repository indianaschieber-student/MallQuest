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
        boolean isVisited = (model.getCurrentRoom()).isVisited();
        view.printRoomName(model.getCurrentRoomName(), isVisited);
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
        while (isPlaying == true) {

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
            case "F1H":
                return "Floor 1 Hallway";
            case "F2H":
                return "Floor 2 Hallway";
            case "FGH":
                return "Ground Floor Hallway";
            case "F1AM":
                return "Abercrombie And Mitch";
            case "F1FS":
                return "Foot Stalker";
            case "F1JS":
                return "Tiffany & Jo";
            case "F1BB":
                return "Buy Better";
            case "FGMB":
                return "A Million Books";
            case "F1DP":
                return "Daisy Penny";
            case "FGCX":
                return "Chublix";
            case "FGMFC":
                return "Kavris Food Court";
            case "FGIG":
                return "Infinite Grind";
            case "F2SH":
                return "Sky-High Doom";
            case "FGFF":
                return "Fatal Flora";
            case "F2KMT":
                return "Kavris Movie Theater";
            case "F2TD":
                return "Token of Despair";
            case "F1SOB":
                return "Salon of Beauty";
            case "F1BS":
                return "Big Cutz";
            case "F1OG":
                return "Old But Gold";
            case "FGCVS":
                return "C-Vous-Say Pharmacy";
            case "FGTU":
                return "Toys B Us";
            case "F2AS":
                return "All Sportz";
            case "FGCSI":
                return "Informational Service Room";
            case "B01H":
                return "Buy Better Back Hallway";
            case "B02H":
                return "Old But Gold Back Hallway";
            case "B03H":
                return "Kavris Food Court Back Hallway";
            case "B04H":
                return "Foot Stalker Back Hallway";
            case "B05H":
                return "Chublix Back Hallway";
            case "BO6H":
                return "Infinite Grind Back Hallway";
            case "B07H":
                return "Sky-High Doom Back Hallway";
            case "B08H":
                return "All Sportz Back Hallway";
            case "B09H":
                return "Toys B Us Back Hallway";
            case "B10H":
                return "Big Cutz Back Hallway";
            case "EX01":
                return "The Exit";
            default:
                return command;
        }
    }

    //Same deal here, I used GitHub Copilot to create this switch statement that allows the user to enter
    // a number instead of typing out the full command
    public String numberCommand(String command) {
        switch (command.trim()) {
            case "1":
                return "STATUS";
            case "2":
                return "EXPLORE";
            case "3":
                return "MAP";
            case "4":
                return "LOOK";
            case "5":
                return "INVENTORY";
            case "6":
                return "PICKUP";
            case "7":
                return "DROP";
            case "8":
                return "EXAMINE";
            case "9":
                return "USE";
            case "10":
                return "INSPECT";
            case "11":
                return "SOLVE";
            case "12":
                return "HINT";
            case "13":
                return "FIGHT";
            case "14":
                return "SAVE";
            case "15":
                return "QUIT";
            case "16":
                return "HELP";
            default:
                return command;
        }
    }

    //Parse commmand method for general use, while not in combat or solving a puzzle
    public void parseCommand(String command) {
        //Convert numbers into their corresponding commands
        command = numberCommand(command);
        //Create a Room object for player's current Room, useful for many of these methods
        Room currentRoom = model.getCurrentRoom();
        //Check to see if the command the player entered is the name of an adjacent room, or its room id
        if (model.movePlayerToRoom(roomAbbreviations(command))) {
            printCurrentRoom();
            return;
        }

        //Displays player's Name, HP, Level, XP to next level, currently equipped items, base attack
        //As per the SRS: Items collected, Quests completed (I decided to do puzzles solved because there are no quests),
        // and number of rooms visited. I figured I'd also add number of monsters defeated, if we have time to implement.
        if (command.equalsIgnoreCase("STATUS") || command.equalsIgnoreCase("CHECK STATUS")) {
            Player player = model.getPlayer();

            String hp = player.getCurrentHp() + "/" + player.getMaxHp();
            String xp = player.getCurrentXp() + "/" + player.getXpToNextLevel();
            view.printPlayerStatus(player.getPlayerName(), hp, player.getPlayerLevel(), xp,
                    player.getBaseAttack(), player.getRoomsVisited(), player.getPuzzlesSolved());
            return;
        }

        //SRS says "Inspect" but I think "Explore" makes more sense
        //Shows Monsters, Puzzles, Items, and Adjacent Rooms for the player's current room
        //Also, the Inspect command can likely be used on Monsters, Puzzles, and Items and not rooms
        if (command.equalsIgnoreCase("EXPLORE")) {
            String monsterName = "";
            if (currentRoom.getRoomMonster() != null) {
                Monster monster = currentRoom.getRoomMonster();
                monsterName = monster.getName();
            }
            boolean hasPuzzle = currentRoom.hasPuzzle();
            List<Item> items = currentRoom.getRoomInventory();
            List<String> itemNames = new ArrayList<>();
            for (Item item : items) {
                itemNames.add(item.getName());
            }
            List<Room> exits = currentRoom.getRoomExits();
            Map<String, String> exitNames = new HashMap<>();
            for (Room tempRoom : exits) {
                exitNames.put(tempRoom.getRoomID(), tempRoom.getRoomName());
            }
            view.printExploreCommand(monsterName, hasPuzzle, itemNames, exitNames, currentRoom.getRoomName());
            return;
        }
        //Displays a list of all rooms and which floor they are on
        if (command.equalsIgnoreCase("MAP")) {
            Map<String, Room> rooms = model.getGameMap();
            Map<String, String> floor1Rooms = new HashMap<>();
            Map<String, String> floor2Rooms = new HashMap<>();
            Map<String, String> groundFloorRooms = new HashMap<>();
            for (Map.Entry<String, Room> entry : rooms.entrySet()) {
                if (entry.getKey().startsWith("F1")) {
                    Room tempRoom = entry.getValue();
                    floor1Rooms.put(entry.getKey(), tempRoom.getRoomName());
                } else if (entry.getKey().startsWith("F2")) {
                    Room tempRoom = entry.getValue();
                    floor2Rooms.put(entry.getKey(), tempRoom.getRoomName());
                } else if (entry.getKey().startsWith("FG")) {
                    Room tempRoom = entry.getValue();
                    groundFloorRooms.put(entry.getKey(), tempRoom.getRoomName());
                }
            }
            view.printMap(floor1Rooms, floor2Rooms, groundFloorRooms);
            return;
        }
        //Re-prints a Room's Description
        if (command.equalsIgnoreCase("LOOK") || command.equalsIgnoreCase("LOOK AROUND")) {
            view.printLook(currentRoom.getRoomName(), currentRoom.getRoomDescription(), currentRoom.isVisited());
            return;
        }

        //"SEARCH": Checks the room for hidden items or puzzle hints.
        //This is what is written in the SRS for this command, but I honestly don't know what they mean by this.
        //I don't think this command serves any purpose so we can probably just ignore it

        //Check the player's inventory
        if (command.equalsIgnoreCase("INVENTORY")) {
            List<Item> inventory = player.getInventory();
            if (inventory == null || inventory.isEmpty()) {
                view.printMessage("Your inventory is empty.");
            } else {
                view.printMessage("--- Your Inventory ---");
                for (Item item : inventory) {
                    view.printMessage("- " + item.getName() + " [" + item.getCategory() + "] (" + item.getId() + ")");
                }
            }
            return;
        }
        
        //Take/Pickup items (e.g., "PICKUP W001" or "TAKE Water Bottle")
        if (command.startsWith("TAKE") || command.startsWith("PICKUP")) {
            String target = command.substring(command.indexOf(" ") + 1).trim();
            if (target.isEmpty() || target.equalsIgnoreCase("TAKE") || target.equalsIgnoreCase("PICKUP")) {
                view.printMessage("What would you like to take/pickup?");
                return;
            }
            Item itemToTake = currentRoom.removeItem(target);
            if (itemToTake != null) {
                player.addItem(itemToTake);
                view.printMessage("You picked up: " + itemToTake.getName());
            } else {
                view.printMessage("You don't see that item here.");
            }
            return;
        }
        
        //Drop items (e.g., "DROP W001")
        if (command.startsWith("DROP")) {
            String target = command.substring(4).trim();
            if (target.isEmpty()) {
                view.printMessage("What would you like to drop?");
                return;
            }
            Item itemToDrop = player.removeItemByName(target);
            if (itemToDrop != null) {
                currentRoom.addItem(itemToDrop);
                view.printMessage("You dropped: " + itemToDrop.getName());
            } else {
                view.printMessage("You aren't carrying that item.");
            }
            return;
        }
        
        //Read Lore Items / Examine items (e.g., "EXAMINE LI001" or "READ map")
        if (command.startsWith("READ") || command.startsWith("EXAMINE")) {
            String target = command.substring(command.indexOf(" ") + 1).trim();
            if (target.isEmpty() || target.equalsIgnoreCase("READ") || target.equalsIgnoreCase("EXAMINE")) {
                view.printMessage("What would you like to read or examine?");
                return;
            }
            Item item = player.findItemInInventory(target);
            if (item == null) {
                // If not in inventory, check room items
                item = currentRoom.findItem(target);
            }

            if (item != null) {
                if (item instanceof LoreItem) {
                    LoreItem lore = (LoreItem) item;
                    view.printMessage(lore.provideInfo());
                } else {
                    view.printMessage(item.getName() + ": " + item.getDescription());
                }
            } else {
                view.printMessage("You don't have or see that item to examine.");
            }
            return;
        }
        
        //Interacts with an object / consumable in the inventory (e.g., "USE CI001")
        if (command.startsWith("USE")) {
            String target = command.substring(3).trim();
            if (target.isEmpty()) {
                view.printMessage("What would you like to use?");
                return;
            }
            Item item = player.findItemInInventory(target);
            if (item != null) {
                if (item instanceof Consumable) {
                    Consumable consumable = (Consumable) item;
                    consumable.consume(player);
                    view.printMessage("You used " + consumable.getName() + " and restored health!");
                    if (consumable.isSingleUse()) {
                        player.removeItem(item);
                    }
                } else {
                    view.printMessage("You cannot use " + item.getName() + " like that.");
                }
            } else {
                view.printMessage("You don't have that item in your inventory.");
            }
            return;
        }

        //This block of code may be a little tricky. The SRS is somewhat unclear, but I think we need a command to inspect
        // monsters and puzzles because there are two FR's for this. However, the Functional Requirements don't seem
        // to have parity on how they want to enter commands. The monster FRs say you can use a number instead of typing a command
        if (command.startsWith("INSPECT")) {
            //I think we could also have a generic INSPECT command that will just display the puzzle and monster information,
            // it would seem redundant to ask the player if they want to inspect a puzzle or monster, but
            //Block of code to inspect monster: view the monster's name, text description, and difficulty level and any
            // other relevant information
            Puzzle puzzle = currentRoom.getRoomPuzzle();
            Monster monster = currentRoom.getRoomMonster();
            String subcommand = command.substring(7).trim();
            if (subcommand.equalsIgnoreCase("MONSTER")) {
                if (monster == null) {
                    view.printMessage("There are no monsters in this room.");
                    return;
                } else {
                    view.printMonsterDescription(monster.getName(), monster.getDescription(),
                            monster.getLvl(), monster.getMaxHp(), currentRoom.getRoomName());
                }
            }

            //Block of code to inspect puzzle: Presumably returns a puzzle's description
            else if (subcommand.equalsIgnoreCase("PUZZLE")) {
                if (puzzle == null) {
                    view.printMessage("There are no puzzles in this room for you to solve.");
                }
                else{
                    view.printPuzzleDescription(currentRoom.getRoomName(), puzzle.getDescription());
                }
            }

            else if ((subcommand == null) || (subcommand.isBlank())) {
                if (monster == null) {
                    view.printMessage("There are no monsters in this room.");
                } else {
                    view.printMonsterDescription(monster.getName(), monster.getDescription(),
                            monster.getLvl(), monster.getMaxHp(), currentRoom.getRoomName());
                }
                if (puzzle == null) {
                    view.printMessage("There are no puzzles in this room for you to solve.");
                }
                else{
                    view.printPuzzleDescription(currentRoom.getRoomName(), puzzle.getDescription());
                }

            }

            else {
                view.printMessage("Command not recognized. Use '10', 'Inspect', 'Inspect Monster', or 'Inspect Puzzle'.");
            }
            return;
        }




            //The SRS didn't specify that you have to be in the puzzle to request a hint, and this makes sense
            //The Hint option should tell you where to find the lore item with the puzzle solution, or tell you
            // to examine the item in your inventory if you have already picked it up.
            if (command.equalsIgnoreCase("HINT") || command.equalsIgnoreCase("REQUEST HINT")) {
                //Pulls the Hint from the current Room Puzzle
                if (currentRoom.hasPuzzle()) {
                    String hint = (currentRoom.getRoomPuzzle()).getHint();
                    view.printPuzzleHint(hint);
                    return;
                } else {
                    view.printMessage("There is no puzzle in this room!");
                }

            }

            //Solve puzzle command. We may have to back through the SRS and look, but I'm not sure if the player
            // should ask for a hint while solving a puzzle, or before they solve the puzzle
            // If so, there can be a separate method parsePuzzleCommand to handle that interaction
            if (command.equalsIgnoreCase("SOLVE") || command.equalsIgnoreCase("SOLVE PUZZLE")) {
                PuzzleLoop loop = new PuzzleLoop();
                loop.startPuzzleLoop(currentRoom.getRoomPuzzle(), currentRoom, view, 4, model.getPlayer(), model.getGameItems());
            }

            //Fight monster starts the combat loop. From there, they have a combat interface that may require its own methods
            if (command.equalsIgnoreCase("FIGHT")) {
                combatLoop();
                return;

            }

            if (command.equalsIgnoreCase("SAVE") || command.equalsIgnoreCase("SAVE GAME")) {

            }

            if (command.equalsIgnoreCase("QUIT") || command.equalsIgnoreCase("QUIT GAME")) {
                //Ask the player "Are you sure you want to quit?" Prompt for Y/N
                boolean quit = view.promptForQuit();
                if (quit) {
                    isPlaying = false;
                    view.printGameOver();
                    return;
                } else {
                    return;
                }

            }

            //Prints a list of commands
            if (command.equalsIgnoreCase("HELP")) {
                view.printCommandList();
                return;
            }

            //This block should go at the very bottom, if the command is not recognized anywhere else
            else {
                view.printMessage("Command not recognized. Type 'Explore' to see a list of adjacent rooms, or 'Help' to " +
                        "see a full list of commands.");
                view.printMessage("++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
            }

        }

        public void startNewGame () {

        }

        public void loadGame () {

        }





}
