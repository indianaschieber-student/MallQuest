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
    //A method for printing room descriptions. Splits the Description into sentences,
    // and prints each sentence on its own line
    public void printRoomDescription(String roomDescription) {
        String[] descArray = roomDescription.split("\\.");
        for (String sentence : descArray) {
            System.out.println(sentence);
        }
    }


}
