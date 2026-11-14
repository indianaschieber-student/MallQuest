package Model;

import java.util.List;
import java.util.ArrayList;

public class Room {
    private String roomID;
    private String roomName;
    private String roomDescription;
    private List<Item> roomInventory;
    private List<Room> roomExits;
    private boolean isVisited;
    private Puzzle roomPuzzle;
    private Monster roomMonster;//this is a single monster and not an ArrayList because each room can only have one monster


    //Two constructors. This is because the Room objects must first be instantiated before the connections between rooms can be established.
    //First, create the room objects with the first constructor, then go through the txt file again
    // and add connections, encounters, items, and puzzles.
    public Room(String roomId, String roomName, String roomDescription) {
        this.roomID = roomId;
        this.roomName = roomName;
        this.roomDescription = roomDescription;
        this.roomInventory = new ArrayList<>();
        this.roomExits = new ArrayList<>();
        this.roomPuzzle = null; //initialize the puzzle to null, since not all rooms will have a puzzle
        this.roomMonster = null; //initialize the monster to null, since not all rooms will have a monster
        this.isVisited = false; //this attribute defaults to false, indicating that the player hasn't visited the room yet
    }
    public Room(String roomID, String roomName, String roomDescription, List<Item> items, List<Room> rooms) {
        this.roomID = roomID;
        this.roomName = roomName;
        this.roomDescription = roomDescription;
        this.roomInventory = items;
        this.roomExits = rooms;
        this.roomPuzzle = null; //initialize the puzzle to null, since not all rooms will have a puzzle
        this.roomMonster = null; //initialize the monster to null, since not all rooms will have a monster\
        this.isVisited = false;
    }

    public boolean hasPuzzle() {
        return roomPuzzle != null;
    }
    public Puzzle getRoomPuzzle() {
        return roomPuzzle;
    }
    public void setRoomPuzzle(Puzzle puzzle) {
        this.roomPuzzle = puzzle;
    }
    public void setVisited(boolean visited) {
        this.isVisited = visited;
    }

    public boolean hasMonster() {
        return roomMonster != null;
    }
    public void removeMonster() {
        if (roomMonster != null) {
            roomMonster = null;
        }
    }
    public void addMonster(Monster monster) {
        if (roomMonster == null) {
            roomMonster = monster;
        }
    }
    public void setRoomMonster(Monster monster) {
        this.roomMonster = monster;
    }
    public Monster getRoomMonster(){
        return this.roomMonster;
    }

    public boolean isVisited(){
        return this.isVisited;
    }
    public void markVisited(){
        this.isVisited = true;
    }

    public String getRoomID(){
        return this.roomID;
    }

    public List<Room> getRoomExits(){
        return roomExits;
    }

    public String getRoomDescription(){
        return roomDescription;
    }
    public String getRoomName(){
        return roomName;
    }

    public List<Item> getRoomInventory(){
        return roomInventory;
    }
    public void addItemToRoom(Item item){
        roomInventory.add(item);
    }
    public void takeItemFromRoom(Item item){
        roomInventory.remove(item);
    }

    // Temporary inventory helpers inside Room.java

    public void addItem(Item item) {
        if (item != null) {
            this.roomInventory.add(item);
        }
    }

    public Item removeItem(String nameOrId) {
        for (Item item : this.roomInventory) {
            if (item.getId().equalsIgnoreCase(nameOrId) || item.getName().equalsIgnoreCase(nameOrId)) {
                this.roomInventory.remove(item);
                return item;
            }
        }
        return null;
    }

    public Item findItem(String nameOrId) {
        for (Item item : this.roomInventory) {
            if (item.getId().equalsIgnoreCase(nameOrId) || item.getName().equalsIgnoreCase(nameOrId)) {
                return item;
            }
        }
        return null;
    }
}
