package Model;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class Player {
    private String name;
    private Room currentRoom;
    private Room previousRoom;
    private int maxHp;
    private int currentHp;
    private List<Item> playerInventory;
    private int totalXp;
    private int xpToNextLevel;
    private int playerLevel;
    private int baseAttack;

    //Constructor for the player. Base XP is set to 0 and Level is 1.
    // base HP is 100 and base attack is 10 at lvl 1
    // plus 10HP and plus 5 Attack per level
    //previousRoom is null by default, and so is the player's name. This is because the game should prompt
    // the player for their name only at the very start of a new session
    public Player (Room startingRoom){
        this.currentRoom = startingRoom;
        this.maxHp = 100;
        this.baseAttack = 5;
        this.playerLevel = 1;
        this.totalXp = 0;
        this.playerInventory = new ArrayList<>();
    }

    public String getPlayerName(){
        return this.name;
    }
    public void setPlayerName(String name){
        this.name = name;
    }

    //The method returns true if the player levels up, false if not. This may be redundant, in which case
    // I can change it to a void return type
    public boolean gainXp(int xp){
        this.totalXp += xp;
        this.xpToNextLevel += xp;
        if (this.xpToNextLevel >= 100){
            this.levelUp();
            return true;
        }
        else return false;
    }

    //I could be wrong, but I couldn't find anything in the SRS that states how much XP is required for level up.
    //For now, I set the value to 100XP. So, when the level up method is called the player's level, HP, and
    // base attack are adjusted. Then, the player's xpToNextLevel is reduced by 100, and the method will return the
    // level up message, notifying the player of their new level and stats
    public String levelUp(){
        this.playerLevel += 1;
        this.baseAttack += 5;
        this.maxHp += 10;
        this.xpToNextLevel -= 100;
        String levelUpMsg = "LEVEL UP! You are now Level " + this.playerLevel + "! Your Max HP has increased to "
                + this.maxHp + " and your base Attack has increased to " + this.baseAttack + "!";
        return levelUpMsg;
    }

    public Room getPlayerRoom(){
        return this.currentRoom;
    }
    public Room getPreviousPlayerRoom(){
        return this.previousRoom;
    }

    //method to attempt to move. Returns true for a valid move, false for an invalid move
    public boolean movePlayer(Room destination, Map<String, Room> gameMap){

        //check to see if the destination room is adjacent to the current room
        if (this.currentRoom.getRoomExits().contains(destination)){
            Room tempRoom = this.currentRoom;
            this.currentRoom = destination;
            this.previousRoom = tempRoom;
            return true;
        }
        else return false;
    }

}
