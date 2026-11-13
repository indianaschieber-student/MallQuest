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
    private int currentXp;
    private int xpToNextLevel;
    private int playerLevel;
    private int baseAttack;
    private int puzzlesSolved;
    private int monstersDefeated;
    private int roomsVisited;
    private CosmeticItem equippedCosmetic = null;

    //Constructor for the player. Base XP is set to 0 and Level is 1.
    // base HP is 100 and base attack is 10 at lvl 1
    // plus 10HP and plus 5 Attack per level
    //previousRoom is null by default, and so is the player's name. This is because the game should prompt
    // the player for their name only at the very start of a new session
    public Player (Room startingRoom){
        this.currentRoom = startingRoom;
        this.maxHp = 100;
        this.currentHp = this.maxHp;
        this.baseAttack = 5;
        this.playerLevel = 1;
        this.totalXp = 0;
        this.playerInventory = new ArrayList<>();
        this.roomsVisited = 1;
        this.monstersDefeated = 0;
        this.puzzlesSolved = 0;
        this.xpToNextLevel = 100;
    }

    public String getPlayerName(){
        return this.name;
    }
    public void setPlayerName(String name){
        if(name == null || name.isEmpty()){
            this.name ="Kid McRealName";
        }
        else{
            this.name = name;
        }
    }

    public int getMaxHp(){
        return this.maxHp;
    }
    public int getCurrentHp(){
        return this.currentHp;
    }
    public int getPlayerLevel(){ return this.playerLevel;}
    public int getCurrentXp(){
        return this.currentXp;
    }
    public int getXpToNextLevel(){
        return this.xpToNextLevel;
    }
    public int getBaseAttack() { return this.baseAttack;}
    public Room getPlayerRoom(){
        return this.currentRoom;
    }
    public Room getPreviousPlayerRoom(){
        return this.previousRoom;
    }
    public int getPuzzlesSolved(){ return this.puzzlesSolved;}
    public int getRoomsVisited(){ return this.roomsVisited;}
    public int getMonstersDefeated(){ return this.monstersDefeated;}
    public List<Item> getPlayerInventory(){ return this.playerInventory; }

    // --- INVENTORY METHODS FOR GAMECONTROLLER ---

    // Standard alias getter to support getInventory()
    public List<Item> getInventory() {
        return this.playerInventory;
    }

    // Getter and setter for equipped cosmetics
    public CosmeticItem getEquippedCosmetic() {
        return this.equippedCosmetic;
    }

    public void setEquippedCosmetic(CosmeticItem cosmetic) {
        this.equippedCosmetic = cosmetic;
    }
    public boolean hasEquippedCosmetic(){
        if (equippedCosmetic == null){
            return false;
        }
        else return true;
    }

    // Adds an item to the player's inventory
    public void addItem(Item item) {
        if (item != null) {
            this.playerInventory.add(item);
        }
    }

    // Removes an item matching by ID or Name
    public Item removeItemByName(String nameOrId) {
        for (Item item : this.playerInventory) {
            if (item.getId().equalsIgnoreCase(nameOrId) || item.getName().equalsIgnoreCase(nameOrId)) {
                this.playerInventory.remove(item);
                return item;
            }
        }
        return null;
    }

    // Overloaded removal method using an Item object reference directly
    public boolean removeItem(Item item) {
        return this.playerInventory.remove(item);
    }

    // Searches for an item in inventory by ID or Name without removing it
    public Item findItemInInventory(String nameOrId) {
        for (Item item : this.playerInventory) {
            if (item.getId().equalsIgnoreCase(nameOrId) || item.getName().equalsIgnoreCase(nameOrId)) {
                return item;
            }
        }
        return null;
    }

    // --- END INVENTORY METHODS ---

    //Removes a random consumable item from the player's inventory
    public String removeRandomConsumable() {
        List<Item> consumables = new ArrayList<>();
        //Check player inventory for consumables
        for (Item item : this.playerInventory) {
            if ((item.getId()).startsWith("CI")) {
                consumables.add(item);
            }
        }
        //if player inventory has no consumables, return false
        if (consumables.isEmpty()) {
            return "";
        }
        //Pull a random index from 0-(n-1) for a list of size n
        Item selected = consumables.get((int) (Math.random() * consumables.size()));
        this.playerInventory.remove(selected);
        return selected.getName();
    }

    public void increaseRoomsVisited(){this.roomsVisited += 1;}
    public void increasePuzzlesSolved(){this.puzzlesSolved ++;}
    public void increaseMonstersDefeated(){this.monstersDefeated ++;}

    //The method returns true if the player levels up, false if not.
    //Modified by Copilot to allow Multiple Level ups if the player obtains enough xp to level up twice
    public boolean gainXp(int xp) {
        if (xp <= 0) {
            return false;
        }

        totalXp += xp;
        currentXp += xp;

        boolean leveledUp = false;
        while (currentXp >= xpToNextLevel) {
            levelUp();
            leveledUp = true;
        }
        return leveledUp;
    }

    //I could be wrong, but I couldn't find anything in the SRS that states how much XP is required for level up.
    //First level up costs 100 Xp, every subsequent level cost 20 mor Xp
    public void levelUp(){
        this.playerLevel += 1;
        this.baseAttack += 5;
        this.maxHp += 10;
        this.currentHp = this.maxHp;
        this.currentXp -= this.xpToNextLevel;
        //Every time the player levels up, it will cost the player 20 more XP to level up next time
        this.xpToNextLevel += 20;
        return;
    }

    //method to attempt to move. Returns true for a valid move, false for an invalid move
    public boolean movePlayer(Room destination){

        //check to see if the destination room is adjacent to the current room
        if (this.currentRoom.getRoomExits().contains(destination)){
            Room tempRoom = this.currentRoom;
            this.currentRoom = destination;
            this.previousRoom = tempRoom; //Sets new room as current room, and their current room as previous room
            return true;
        }
        else return false;
    }


    // PLAYER COMBAT - Reduces player HP when attacked by a monster - KX
    public void takeDamage(int damage) {

        // Only accept positive damage
        if (damage > 0) {
            this.currentHp -= damage;

            // Prevent HP from dropping below zero
            if (this.currentHp < 0) {
                this.currentHp = 0;
            }
        }
    }

    // Setter for HP (used by consumable healing items)
    public void setHp(int hp) {
        this.currentHp = Math.min(hp, this.maxHp);
    }
}
