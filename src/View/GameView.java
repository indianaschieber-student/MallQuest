package View;


import java.util.Scanner;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class GameView {
    private final Scanner scanner = new Scanner(System.in);

    //Constructor for the GameView
    public GameView(){

    }
    //Helper method to bold certain text, returns a String that will be in bold
    public String formatBold(String message) {
        return("\u001B[1m" + message + "\u001B[0m");
    }

    //This is a very useful helper method that will format long lines of text for Item and Room descriptions
    // from the .txt files into neat blocks of text
    public static String wrapText(String text) {
        StringBuilder result = new StringBuilder();
        String[] words = text.split("\\s+");
        int currentLineLength = 0;

        for (String word : words) {
            // If adding the next word exceeds the limit, start a new line
            if (currentLineLength + word.length() > 100) {//100 here represents the character limit per line.
                result.append("\n");                     //Once a line has 100 characters, move to the next line
                currentLineLength = 0;
            } else if (currentLineLength > 0) {
                result.append(" ");
                currentLineLength++;
            }
            result.append(word);
            currentLineLength += word.length();
        }
        return result.toString();
    }


    //Generic print message method
    public void printMessage(String msg) {
        System.out.println(msg);
    }
    //A method for printing room descriptions. Splits the Description into sentences,
    // and prints each sentence on its own line
    public void printWelcome(String playerName){
        printMessage("Hello, " + playerName + "!\n" +
                "Welcome to Mall Quest, the Text-Based Adventure Game where you play as a child lost in a shopping mall.\n" +
                "You must solve puzzles in order to collect 10 Escape Room Stamps and leave through the Exit on the top floor of the mall.\n" +
                "Enter the name of an adjacent room to walk to it, or enter 'Help' to see a list of player commands.\n" +
                "We here at Kirva Shopping Mall wish you the best of luck on your Mall Quest!");
    }

    //These two methods are used to display a room name and description when the player moves rooms
    // The text for using the look command is slightly different
    public void printRoomName(String roomName, boolean isVisited){
        if (isVisited == true){
            printMessage("+++++++++++++++++++++++++++++++++++++++++++++++++++++++");
            printMessage("You are currently in: " + formatBold(roomName) + ".");
            printMessage("This room seems familiar...");
        }
        else {
            printMessage("+++++++++++++++++++++++++++++++++++++++++++++++++++++++");
            printMessage("New room discovered!");
            printMessage("You are currently in: " + formatBold(roomName) + ".");
        }

    }
    public void printRoomDescription(String roomDescription) {
        printMessage("+++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        //This is specifically for the Hallways, which have the Adjacent rooms listed in separate lines on the SRS
        if(roomDescription.contains("\\")){
            String [] lines = roomDescription.split("\\\\");
                for (String string : lines){
                    printMessage(wrapText(string));
                }
        }
        else printMessage(wrapText(roomDescription));
        printMessage("+++++++++++++++++++++++++++++++++++++++++++++++++++++++");
    }


    //STATUS
    public void printPlayerStatus(String name, String hp, int level, String xpToNextLevel, int baseAttack, int roomsVisited, int puzzlesCompleted){
        printMessage("Player: " + formatBold(name) +
                "\nLevel: " + level + ", XP to Next Level: " + xpToNextLevel +
                "\nHP: " + hp +
                "\nBase Attack: " + baseAttack +
                "\nRooms Visited: " + roomsVisited +
                "\nPuzzles Completed: " + puzzlesCompleted );
    }

    //A method to return a Room's Description
    //EXPLORE command
    public void printExploreCommand(String monsterName, boolean hasPuzzle, List<String> itemNames, Map<String,String> exitNames, String roomName){
        printMessage("You are currently in: " + formatBold(roomName) + ".");
        if(!(monsterName.isEmpty())){
            printMessage("There is an enemy in this room: " + monsterName + "! Use the 'INSPECT MONSTER' command to learn more!");
        }
        else{
            printMessage("There are no enemies in this room... for now.");
        }

        if (hasPuzzle == true){
            printMessage("This room has a puzzle! Use the 'INSPECT PUZZLE' command to learn more!");
        }
        else{
            printMessage("There are no puzzles in this room for you to solve.");
        }

        if (itemNames.isEmpty()){
            printMessage("There are no items in this room that are of interest to you.");
        }
        else{
            printMessage("The following items can be found in this room: ");
            for(int n = 1;n <= itemNames.size();n++) {
                if (n < itemNames.size()) {
                    System.out.print(itemNames.get(n - 1) + ", ");
                } else {
                    System.out.print(itemNames.get(n - 1) + ".");
                }
            }
            System.out.println();
        }

        printMessage(formatBold("Adjacent Rooms: "));
        for (Map.Entry<String, String> entry : exitNames.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }

    }

    //A method to return a list of all the Main Rooms in the game (not counting back hallways) and which floor they are on
    //MAP command
    public void printMap(Map<String,String> floor1Rooms, Map<String,String> floor2Rooms, Map<String,String>groundFloorRooms){
        printMessage("Enter either a room's name or ID to travel to that room, if you are in an adjacent room." +
                "\nThe Main Hallways connect to every other room on their corresponding floor.");
        printMessage(formatBold("Ground Floor Rooms: "));
        for (Map.Entry<String,String> entry : groundFloorRooms.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
        printMessage(formatBold("Floor 1 Rooms: "));
        for (Map.Entry<String,String> entry : floor1Rooms.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
        printMessage(formatBold("Floor 2 Rooms: "));
        for (Map.Entry<String,String> entry : floor2Rooms.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
        printMessage("+++++++++++++++++++++++++++++++++++++++++++++++++++++++");
    }

    //Displays current Room Name and Description
    //LOOK command
    public void printLook(String roomName, String roomDescription, boolean isVisited){
        if (isVisited == true){
            printMessage("+++++++++++++++++++++++++++++++++++++++++++++++++++++++");
            printMessage("You are currently in: " + formatBold(roomName) + ".");
            printMessage("You have visited this room before.");
        }
        else {
            printMessage("+++++++++++++++++++++++++++++++++++++++++++++++++++++++");
            printMessage("You are currently in: " + formatBold(roomName) + ".");
            printMessage("This is your first time visiting this room.");
        }
        printRoomDescription(roomDescription);
    }

    //INVENTORY command
    public void printInvnetory(List<String> inventory){

    }

    //TAKE/PICKUP [Item Name] command
    public void printPickupItem(String itemName){

    }

    //DROP [Item Name] command
    public void printDropItem(String itemName){

    }

    //READ/EXAMINE [Item Name] command
    public void printItemDescription(String itemName, String itemDescription){

    }

    //USE [Item Name] command
    public void printUseItem(String itemName){

    }

    //Helper method for Item commands; prompts the player for the name of an item if they try to use, examine, take,
    // or drop without specifying an item name
    public String promptForItemName(){
        printMessage("Enter the name of an item: "); //This can be modified if necessary to work with DROP, USE, EQUIP, etc.
        return scanner.nextLine().trim().toUpperCase();
    }

    //INSPECT PUZZLE
    public void printPuzzleDescription(String roomName, String puzzleDescription){
        printMessage("Puzzle for " + roomName + ": " +
        "\n" + wrapText(puzzleDescription));
        printMessage("+++++++++++++++++++++++++++++++++++++++++++++++++++++++");
    }

    //I figured we'd want to see the Monster's name, description, level, HP, and attck?
    //Feel free to change the signature to whatever you feel is appropriate
    //INSPECT MONSTER
    public void printMonsterDescription(String monsterName, String monsterDescription, int lvl, int maxHp, String roomName){
        printMessage("Monster in " + roomName + ": " +
                "\n" + monsterName + "  Level: " + lvl + ", HP: " + maxHp +
                "\n" + wrapText(monsterDescription));
        printMessage("+++++++++++++++++++++++++++++++++++++++++++++++++++++++");
    }

    //HINT command
    public void printPuzzleHint(String puzzleHint){
        printMessage(puzzleHint);
        printMessage("+++++++++++++++++++++++++++++++++++++++++++++++++++++++");
    }

    //SOLVE or SOLVE PUZZLE command
    public String promptForPuzzleSolution(String puzzlePrompt){
        printMessage(puzzlePrompt);
        String input = scanner.nextLine().trim();
        String answer = input;
        return answer;
    }

    //FIGHT

    //SAVE

    //QUIT
    public boolean promptForQuit() {
        //Initiates a loop the player can't exit until they enter yes or no
        boolean validAnswer = false;
        while (!validAnswer) {
            printMessage("Are you sure you want to quit the game?");
            String input = scanner.nextLine().trim();
            String answer = switch(input.toUpperCase()) {
                case "Y" ->
                    answer = "YES";
                case "N" ->
                    answer = "NO";
                default ->
                    answer = input;
            };
            if (answer.equalsIgnoreCase("YES")) {
                return true;
            } else if (answer.equalsIgnoreCase("NO")) {
                return false;
            } else {
                System.out.println("Invalid input. Please Type 'Yes' or 'No' (Y/N)");
                continue;
            }
        }
        return false;
    }

    //A Method to print a List of Game commands when the player enters "HELP"
    //HELP command
    public void printCommandList(){
        printMessage("Enter a Command or its Corresponding Number (Commands are not Case-Sensitve)." +
                "\n(1)STATUS or CHECK STATUS: Displays relevant information about the player including HP, Level, and Base Attack." +
                "\n(2)EXPLORE: Displays the names of any items, monsters, or puzzles present in the current room; " +
                "\n also displays a list of adjacent rooms that the player can travel to from their current room." +
                "\n(3)MAP: Displays a List of all the rooms in the game, and which floor they are on." +
                "\n(4)LOOK or LOOK AROUND: Displays the description of the player's current room." +
                "\n(5)INVENTORY: Displays a list of all items in the player's inventory." +
                "\n(6)PICKUP [Item Name] or TAKE [Item Name]: Allows the player to add an item to their inventory, if that item is in the player's current room." +
                "\n(7)DROP [Item Name]: Removes an Item from the player's inventory, placing the item in the player's current room." +
                "\n(8)READ [Item Name] or EXAMINE [Item Name]: Displays an item's description." +
                "\n(9)USE [Item Name]: Allows the player to use consumable items to restore HP, if the item is present in their inventory. Item is removed from inventory after use." +
                "\n(10)INSPECT PUZZLE or INSPECT MONSTER: Displays a description for the monster or puzzle for the player's current room, " +
                "\n or a description for both if the player types 10 or INSPECT without specifying monster or puzzle." +
                "\n(11)SOLVE or SOLVE PUZZLE: Prompts the player for a solution to the puzzle in the player's current room, if there is a puzzle present." +
                "\n(12)HINT or REQUEST HINT: Gives the player a hint to the puzzle in the player's current room, if there is a puzzle present." +
                "\n(13)FIGHT or FIGHT MONSTER: Begin combat with the monster in the player's current room, if there is a monster present." +
                "\n(14)SAVE or SAVE GAME: Saves the user's current session." +
                "\n(15)QUIT or QUIT GAME: Ends the user's current session." +
                "\n(16)HELP: Displays a list of valid game commands.");
        printMessage("+++++++++++++++++++++++++++++++++++++++++++++++++++++++");
    }

    //Displays GAME OVER message
    public void printGameOver(){
        printMessage("+++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        printMessage("We Hope you've enjoyed your shopping experience at " +
                          "\nKuvira Mall! Please Give Us 5 Stars on Doodle Reviews!" +
                        "\n=======================GAME OVER=======================");
    }

    public String promptForPlayerName(){
        printMessage("Greetings, Shopper! What is your name?");
        return scanner.nextLine().trim();
    }
    //Prompts the user for a command, while not engaged in combat or solving a puzzle
    //Returns the player input as a String, all uppercase, that then goes to the parseCommand method in the Controller
    public String promptForCommand(){
        printMessage("Enter the name of the room you want to navigate to, or enter another command:");
        return scanner.nextLine().trim().toUpperCase();
    }

}
