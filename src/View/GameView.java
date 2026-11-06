package View;

import java.util.Scanner;

public class GameView {
    private final Scanner scanner = new Scanner(System.in);

    //Constructor for the GameView
    public GameView(){

    }
    //Generic print message method
    public void printMessage(String msg) {
        System.out.println(msg);
    }

    public String promptForPlayerName(){
        printMessage("Greetings, Shopper! What is your name?");
        return scanner.nextLine().trim();
    }
    //Prompts the user for a command, while not engaged in combat or solving a puzzle
    //Returns the player input as a String, all uppercase, that then goes to the parseCommand method in the Controller
    public String promptForCommand(){
        printMessage("Enter the name of the room you want to navigate to, type 'Explore' to see a list of adjacent rooms, or type 'Help' for a full list of commands.");
        return scanner.nextLine().trim().toUpperCase();
    }

    //A method for printing room descriptions. Splits the Description into sentences,
    // and prints each sentence on its own line
    public void printRoomDescription(String roomDescription) {
        printMessage("++++++++++++++++++++++++++++++++++++");
        String[] descArray = roomDescription.split("\\\\");
        for (String sentence : descArray) {
            printMessage(sentence);
        }
        printMessage("++++++++++++++++++++++++++++++++++++");
    }
    public void printRoomName(String roomName){
        printMessage("++++++++++++++++++++++++++++++++++++");
        printMessage("You are currently in: " + roomName + ".");
    }


}
