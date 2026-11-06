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
        printMessage("Enter your name:");
        return scanner.nextLine().trim();
    }

    /*public void printRoomDescription(String roomDescription) {
        System.out.println(roomDescription);
    }*/


}
