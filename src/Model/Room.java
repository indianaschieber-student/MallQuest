package Model;

import java.util.List;
import java.util.ArrayList;

public class Room {
    private String roomID;
    private String roomName;
    private String roomDescription;
    private List<Monster> roomEncounters;
    private List<Item> roomInventory;
    private List<Room> roomExits;
    private Puzzle roomPuzzle;
    private Monster roomMonster;//this is a single monster and not an ArrayList because each room can only have one monster

    //Two constructors. This is because the Room objects must first be instantiated before the connections between rooms can be established.
    //First, create the room objects with the first constructor, then go through the txt file again
    // and add connections, encounters, items, and puzzles.
    public Room(String roomId, String roomName, String roomDescription) {
        this.roomID = roomId;
        this.roomName = roomName;
        this.roomDescription = roomDescription;
        this.roomEncounters = new ArrayList<>();
        this.roomInventory = new ArrayList<>();
        this.roomExits = new ArrayList<>();
        this.roomPuzzle = null; //initialize the puzzle to null, since not all rooms will have a puzzle
        this.roomMonster = null; //initialize the monster to null, since not all rooms will have a monster
    }
    public Room(String roomID, String roomName, String roomDescription, List<Monster> monsters, List<Item> items, List<Room> rooms) {
        this.roomID = roomID;
        this.roomName = roomName;
        this.roomDescription = roomDescription;
        this.roomEncounters = monsters;
        this.roomInventory = items;
        this.roomExits = rooms;
        this.roomPuzzle = null; //initialize the puzzle to null, since not all rooms will have a puzzle
        this.roomMonster = null; //initialize the monster to null, since not all rooms will have a monster
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
}
