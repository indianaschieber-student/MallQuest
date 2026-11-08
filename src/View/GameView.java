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
    public void printRoomDescription(String roomDescription) {
        printMessage("++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        //This is specifically for the Hallways, which have the Adjacent rooms listed in separate lines on the SRS
        if(roomDescription.contains("\\")){
            String [] lines = roomDescription.split("\\\\");
                for (String string : lines){
                    printMessage(wrapText(string));
                }
        }
        else printMessage(wrapText(roomDescription));
        printMessage("++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
    }
    public void printRoomName(String roomName){
        printMessage("++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        printMessage("You are currently in: " + formatBold(roomName) + ".");
    }
    //A method to return a Room's Description when the player uses the EXPLORE command
    public void printExploreCommand(String monsterName, boolean hasPuzzle, List<String> itemNames, Map<String,String> exitNames){
        if(!(monsterName.isEmpty())){
            printMessage("There is an enemy in this room: " + monsterName + "!");
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
        }

        printMessage(formatBold("Adjacent Rooms: "));
        for (Map.Entry<String, String> entry : exitNames.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }

    }

    //A method to return a list of all the Main Rooms in the game (not counting back hallways) when the player
    // uses the MAP command
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
        printMessage("++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
    }
    //A Method to print a List of Game commands when the player enters "HELP"
    //There was mention on the SRS of using a list of numbers to represent commands, so I added the numbers for now,
    // but I didn't implement it yet. We could probably implement this fairly easy if necessary, we would potentially
    // need to prompt the player for an Item Name if they try to use a number to pickup an item, for instance.
    public void printCommandList(){
        printMessage("(Commands are not Case-Sensitve)" +
                "\n(1)EXPLORE: Displays the names of any items, monsters, or puzzles present in the current room; " +
                "\n also displays a list of adjacent rooms that the player can travel to from their current room." +
                "\n(2)MAP: Displays a List of all the rooms in the game, and which floor they are on." +
                "\n(3)LOOK: Displays the description of the player's current room." +
                "\n(4)INVENTORY: Displays a list of all items in the player's inventory." +
                "\n(5)PICKUP [Item Name] or TAKE [Item Name]: Allows the player to add an item to their inventory, if that item is in the player's current room." +
                "\n(6)DROP [Item Name]: Removes an Item from the player's inventory, placing the item in the player's current room." +
                "\n(7)READ [Item Name] or EXAMINE [Item Name]: Displays an item's description." +
                "\n(8)USE [Item Name]: Allows the player to use consumable items to restore HP, if the item is present in their inventory. Item is removed from inventory after use." +
                "\n(9)INSPECT PUZZLE or INSPECT MONSTER: Displays a description for the monster or puzzle for the player's current room, or a description for both if neither is specified." +
                "\n(11)HINT or REQUEST HINT: Gives the player a hint to the puzzle in the player's current room, if there is a puzzle present." +
                "\n(10)SOLVE or SOLVE PUZZLE: Prompts the player for a solution to the puzzle in the player's current room, if there is a puzzle present." +
                "\n(12)FIGHT or FIGHT MONSTER: Begin combat with the monster in the player's current room, if there is a monster present." +
                "\n(13)SAVE or SAVE GAME: Saves the user's current session." +
                "\n(14)QUIT or QUIT GAME: Ends the user's current session.");
                printMessage("++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
    }

    //Displays GAME OVER message
    public void printGameOver(){

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
    //Prompts the player for the name of an item if they try to use, examine, take, or drop without specifying an item name

}
