package Controller;

import Model.*;
import View.*;

import java.util.*;

public class GameController {
    private boolean isPlaying = false;
    private GameModel model = new GameModel();
    private GameView view = new GameView();

    // MFR4 - Randomly selects a monster attack during combat
    private Random combatRandom = new Random();

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

        }
        //Take/Pickup items
        if (command.startsWith("TAKE") || command.startsWith("PICKUP")) {

        }
        //Drop items
        if (command.startsWith("DROP")) {

        }
        //Read Lore Items; The SRS explicitly asked for a "READ" command,
        // but I think a generic "EXAMINE" command would make more sense.
        //That way, you can see the item descriptions for all items, not just lore items.
        if (command.startsWith("READ") || command.startsWith("EXAMINE")) {

        }
        //Interacts with an object in the room (e.g., puzzle elements, items).
        //This is what the SRS says. I'm assuming it's only for items in our inventory though
        //To be honest as I'm looking through the SRS a lot of the finer details of the game
        // seem very unclear, so just use your discretion on this one I guess
        if (command.startsWith("USE")) {

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
                loop.startPuzzleLoop(currentRoom.getRoomPuzzle(), currentRoom, view, 4);
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


        // MFR3 - Handles combat between the player and a monster
        private void combatLoop () {

            // Gets the player's current room
            Room room = model.getCurrentRoom();

            // Gets the monster inside that room
            Monster monster = room.getRoomMonster();

            // Gets the existing player from GameModel
            Player player = model.getPlayer();

            // Checks whether a monster exists in the room
            if (monster == null) {
                view.printMessage("There is no monster to fight here.");
                return;
            }

            // Checks whether the monster has already been defeated
            if (monster.isDefeated()) {
                view.printMessage(monster.getName() + " has already been defeated.");
                return;
            }

            // Announces the beginning of combat
            view.printMessage("You are now fighting " + monster.getName() + "!");

            // MFR3 - Keeps combat running until the monster is defeated
            // or the player successfully escapes
            boolean inCombat = true;

            while (inCombat) {

                // Display the monster's current HP
                view.printMessage(monster.getName() + " HP: " + monster.getHp() + "/" + monster.getMaxHp()
                );

                // TODO PLAYER #1:
                // Player.java needs getCurrentHp() and getMaxHp()
                // so we can display the player's HP here.

                // Ask the player what they want to do
                view.printMessage("Choose an action: ATTACK or RUN");

                String combatCommand = view.promptForCommand().trim().toUpperCase(Locale.ROOT);


                // MFR3 - Player attacks the monster
                if (combatCommand.equals("ATTACK")) {

                    // TODO PLAYER #2:
                    // Replace 10 with player.getBaseAttack()
                    // once your teammate adds the method.

                    int damage = 10;

                    // Reduce the monster's HP
                    monster.takeDamage(damage);

                    view.printMessage("You attacked " + monster.getName() + " and dealt " + damage + " damage!");

                    // Check if the monster has been defeated
                    if (monster.isDefeated()) {
                        view.printMessage("You defeated " + monster.getName() + "!");

                        // End combat
                        inCombat = false;
                    } else {
                        // MFR4 - Monster retaliates if it survived
                        monsterAttackTurn(monster, player);
                    }
                }

                // MFR3 - Player attempts to escape combat
                else if (combatCommand.equals("RUN")) {

                    // Check whether the monster allows escape
                    if (monster.canEscape()) {

                        view.printMessage("You successfully escaped from " + monster.getName() + "!");

                        // End combat
                        inCombat = false;
                    } else {
                        view.printMessage("You cannot escape from " + monster.getName() + "!");

                        // Monster retaliates after failed escape
                        monsterAttackTurn(monster, player);
                    }
                }

                // Handle invalid combat commands
                else {
                    view.printMessage("Invalid command. Please enter ATTACK or RUN.");
                }

            }

        }


        // MFR4 - Monster attacks the player during combat
        private void monsterAttackTurn (Monster monster, Player player){

            // Get all attacks assigned to this monster
            ArrayList<MonsterAttack> attacks = monster.getAttacks();

            // Check if the monster has any attacks
            if (attacks.isEmpty()) {
                view.printMessage(monster.getName() + " has no attacks!");
                return;
            }

            // Randomly select an attack from the monster's attack list
            int randomIndex = combatRandom.nextInt(attacks.size());
            MonsterAttack selectedAttack = attacks.get(randomIndex);

            // Get the selected attack's damage
            int damage = selectedAttack.getDamage();

            // Display the monster's attack
            view.printMessage(
                    monster.getName() + " used "
                            + selectedAttack.getName() + "!"
            );


            // TODO PLAYER #3:
            // Player.java needs takeDamage(int damage).
            // Replace the temporary message below with:
            // player.takeDamage(damage);

            view.printMessage("[TODO PLAYER #3] Monster would deal " + damage + " damage to the player.");

            // TODO PLAYER #4:
            // Once Player.java has getCurrentHp() and getMaxHp(),
            // display the player's remaining HP here.

            // TODO PLAYER #5:
            // Once Player.java has isDefeated(),
            // check whether the player has been defeated.

            // TODO MFR4:
            // Apply selectedAttack.getEffect() once the team
            // finishes deciding how status effects will work.
        }


    }
