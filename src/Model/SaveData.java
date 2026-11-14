package Model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;


//The SaveData class is what stores the current state of in-game objects: The player, monsters, rooms,
//and puzzles
public class SaveData {
    private static final String VERSION = "1";
    private final Properties values;

    // Wrap an already populated set of save properties.
    private SaveData(Properties values) {
        this.values = values;
    }

    /*
     * Reads the current player and world state into named text properties.
     *
     * <p>Player properties contain identity, location, health, XP/level, counters, inventory
     * item IDs, and the equipped cosmetic ID. Room properties contain visit status, the IDs
     * of items still on the floor, and the current monster's mutable state. Puzzle properties
     * record whether each puzzle has been solved. The static descriptions, exits, and item
     * definitions are not copied because the game rebuilds those from its data files.</p>*/
    //Works with the save feature. Stores the in-game attributes as value key pairs using Properties.
    //Each instance of the SaveData class has its own Properties attribute, which functions like a HashMap or HashTable
    //with value key pairs. In this case, the key is the attribute, and the value is the corresponding ID, value, or String
    public static SaveData capture(GameModel model) {
        Properties values = new Properties();
        Player player = model.getPlayer();
        Room currentRoom = player.getPlayerRoom();
        Room previousRoom = player.getPreviousPlayerRoom();

        // Version the format so future code can detect saves it cannot interpret.
        values.setProperty("version", VERSION);

        // Save player progression and refer to rooms/items by ID, not Java object references.
        values.setProperty("player.name", player.getPlayerName());
        values.setProperty("player.currentRoom", currentRoom.getRoomID());
        values.setProperty("player.previousRoom", previousRoom == null ? "" : previousRoom.getRoomID());
        values.setProperty("player.maxHp", Integer.toString(player.getMaxHp()));
        values.setProperty("player.currentHp", Integer.toString(player.getCurrentHp()));
        values.setProperty("player.totalXp", Integer.toString(player.getTotalXp()));
        values.setProperty("player.currentXp", Integer.toString(player.getCurrentXp()));
        values.setProperty("player.xpToNextLevel", Integer.toString(player.getXpToNextLevel()));
        values.setProperty("player.level", Integer.toString(player.getPlayerLevel()));
        values.setProperty("player.baseAttack", Integer.toString(player.getBaseAttack()));
        values.setProperty("player.puzzlesSolved", Integer.toString(player.getPuzzlesSolved()));
        values.setProperty("player.monstersDefeated", Integer.toString(player.getMonstersDefeated()));
        values.setProperty("player.roomsVisited", Integer.toString(player.getRoomsVisited()));
        values.setProperty("player.inventory.count", Integer.toString(player.getInventory().size()));
        for (int i = 0; i < player.getInventory().size(); i++) {
            values.setProperty("player.inventory." + i, player.getInventory().get(i).getId());
        }
        CosmeticItem cosmetic = player.getEquippedCosmetic();
        values.setProperty("player.equippedCosmetic", cosmetic == null ? "" : cosmetic.getId());

        // Track the mutable contents of a room: items, monsters, and visited status
        values.setProperty("room.count", Integer.toString(model.getGameMap().size()));
        int roomIndex = 0;
        for (Map.Entry<String, Room> entry : model.getGameMap().entrySet()) {
            String roomId = entry.getKey();
            Room room = entry.getValue();
            String prefix = "room." + roomId + ".";
            values.setProperty("room.id." + roomIndex++, roomId);
            values.setProperty(prefix + "visited", Boolean.toString(room.isVisited()));
            values.setProperty(prefix + "item.count", Integer.toString(room.getRoomInventory().size()));
            for (int i = 0; i < room.getRoomInventory().size(); i++) {
                values.setProperty(prefix + "item." + i, room.getRoomInventory().get(i).getId());
            }
            Monster monster = room.getRoomMonster();
            values.setProperty(prefix + "monster.present", Boolean.toString(monster != null));
            if (monster != null) {
                values.setProperty(prefix + "monster.id", monster.getId());
                values.setProperty(prefix + "monster.hp", Integer.toString(monster.getHp()));
                values.setProperty(prefix + "monster.level", Integer.toString(monster.getLvl()));
                values.setProperty(prefix + "monster.status", monster.getStatus());
                values.setProperty(prefix + "monster.escapeBlocked", Boolean.toString(!monster.canEscape()));
            }
        }

        // Puzzle only needs to track if it's solved or not
        values.setProperty("puzzle.count", Integer.toString(model.getGamePuzzles().size()));
        int puzzleIndex = 0;
        for (Map.Entry<String, Puzzle> entry : model.getGamePuzzles().entrySet()) {
            values.setProperty("puzzle.id." + puzzleIndex++, entry.getKey());
            values.setProperty("puzzle." + entry.getKey() + ".solved",
                    Boolean.toString(entry.getValue().isSolved()));
        }
        return new SaveData(values);
    }

    //Saves properties from a save(txt file) into a SaveData object
    public static SaveData fromProperties(Properties values) {
        if (!VERSION.equals(values.getProperty("version"))) {
            throw new IllegalArgumentException("Unsupported or missing save-file version.");
        }
        return new SaveData(values);
    }

    /** Returns the player name for displaying this save slot in the startup menu. */
    public String getPlayerName() {
        return required("player.name");
    }

    //This method effectively rebuilds the game world to its previous state when the player reloads a save. After
    //the GameWorldBuilder rebuilds the world, this method modifies the in-game monsters, rooms, and puzzles to match
    //the game-state of the player's previous save.
    public void restore(GameModel model) {

        //Reset player attributes: Name, Inventory, etc.
        String playerName = getPlayerName();
        if (playerName.trim().isEmpty()) {
            throw new IllegalArgumentException("Save file contains an empty player name.");
        }
        String currentRoomId = required("player.currentRoom");
        Room currentRoom = model.getGameMap().get(currentRoomId);
        if (currentRoom == null) {
            throw new IllegalArgumentException("Save file references unknown room: " + currentRoomId);
        }
        String previousRoomId = required("player.previousRoom");
        Room previousRoom = previousRoomId.isEmpty() ? null : model.getGameMap().get(previousRoomId);
        if (!previousRoomId.isEmpty() && previousRoom == null) {
            throw new IllegalArgumentException("Save file references unknown previous room: " + previousRoomId);
        }
        int maxHp = readInteger("player.maxHp");
        int currentHp = readInteger("player.currentHp");
        int totalXp = readInteger("player.totalXp");
        int currentXp = readInteger("player.currentXp");
        int xpToNextLevel = readInteger("player.xpToNextLevel");
        int playerLevel = readInteger("player.level");
        int baseAttack = readInteger("player.baseAttack");
        int puzzlesSolved = readInteger("player.puzzlesSolved");
        int monstersDefeated = readInteger("player.monstersDefeated");
        int roomsVisited = readInteger("player.roomsVisited");
        if (maxHp <= 0 || currentHp < 0 || currentHp > maxHp || totalXp < 0 || currentXp < 0
                || xpToNextLevel <= 0 || playerLevel < 1 || baseAttack < 0 || puzzlesSolved < 0
                || monstersDefeated < 0 || roomsVisited < 0) {
            throw new IllegalArgumentException("Save file contains invalid player state.");
        }

        List<Item> inventory = new ArrayList<>();
        int inventoryCount = readCount("player.inventory.count");
        for (int i = 0; i < inventoryCount; i++) {
            inventory.add(requireItem(model, required("player.inventory." + i)));
        }
        String cosmeticId = required("player.equippedCosmetic");
        CosmeticItem cosmetic = null;
        if (!cosmeticId.isEmpty()) {
            Item item = requireItem(model, cosmeticId);
            if (!(item instanceof CosmeticItem)) {
                throw new IllegalArgumentException("Save file references a non-cosmetic equipped item: " + cosmeticId);
            }
            cosmetic = (CosmeticItem) item;
        }

        int roomCount = readCount("room.count");
        if (roomCount != model.getGameMap().size()) {
            throw new IllegalArgumentException("Save file does not contain state for every room.");
        }
        Set<String> restoredRoomIds = new HashSet<>();
        for (int i = 0; i < roomCount; i++) {
            String roomId = required("room.id." + i);
            Room room = model.getGameMap().get(roomId);
            if (room == null || !restoredRoomIds.add(roomId)) {
                throw new IllegalArgumentException("Save file references unknown room: " + roomId);
            }
            String prefix = "room." + roomId + ".";
            readBoolean(prefix + "visited");
            int itemCount = readCount(prefix + "item.count");
            for (int itemIndex = 0; itemIndex < itemCount; itemIndex++) {
                requireItem(model, required(prefix + "item." + itemIndex));
            }
            boolean monsterPresent = readBoolean(prefix + "monster.present");
            if (monsterPresent) {
                String monsterId = required(prefix + "monster.id");
                Monster template = model.getGameMonsters().get(monsterId);
                int monsterHp = readInteger(prefix + "monster.hp");
                int monsterLevel = readInteger(prefix + "monster.level");
                String monsterStatus = required(prefix + "monster.status");
                readBoolean(prefix + "monster.escapeBlocked");
                if (template == null || monsterHp < 0 || monsterHp > template.getMaxHp()
                        || monsterLevel < 1 || monsterStatus.isEmpty()) {
                    throw new IllegalArgumentException("Save file references unknown monster: " + monsterId);
                }
            }
        }
        int puzzleCount = readCount("puzzle.count");
        if (puzzleCount != model.getGamePuzzles().size()) {
            throw new IllegalArgumentException("Save file does not contain state for every puzzle.");
        }
        Set<String> restoredPuzzleIds = new HashSet<>();
        for (int i = 0; i < puzzleCount; i++) {
            String puzzleId = required("puzzle.id." + i);
            if (!model.getGamePuzzles().containsKey(puzzleId) || !restoredPuzzleIds.add(puzzleId)) {
                throw new IllegalArgumentException("Save file references unknown puzzle: " + puzzleId);
            }
            readBoolean("puzzle." + puzzleId + ".solved");
        }

        //Clears room inventories, then restores visited status, items, and monsters in room
        for (Room room : model.getGameMap().values()) {
            room.getRoomInventory().clear();
        }
        for (int i = 0; i < roomCount; i++) {
            String roomId = required("room.id." + i);
            Room room = model.getGameMap().get(roomId);
            String prefix = "room." + roomId + ".";
            room.setVisited(readBoolean(prefix + "visited"));
            int itemCount = readCount(prefix + "item.count");
            for (int itemIndex = 0; itemIndex < itemCount; itemIndex++) {
                room.addItem(requireItem(model, required(prefix + "item." + itemIndex)));
            }
            if (readBoolean(prefix + "monster.present")) {
                String monsterId = required(prefix + "monster.id");
                Monster template = model.getGameMonsters().get(monsterId);
                Monster monster = room.getRoomMonster();
                if (monster == null || !monster.getId().equals(monsterId)) {
                    monster = new Monster(template.getId(), template.getName(), template.getDescription(),
                            template.getLocationFound(), template.getLvl(), template.getMaxHp(), template.getAttacks());
                    room.setRoomMonster(monster);
                }
                monster.restoreState(readInteger(prefix + "monster.hp"),
                        readInteger(prefix + "monster.level"), required(prefix + "monster.status"),
                        readBoolean(prefix + "monster.escapeBlocked"));
            } else {
                room.removeMonster();
            }
        }
        //Restores puzzle solved status
        for (int i = 0; i < puzzleCount; i++) {
            String puzzleId = required("puzzle.id." + i);
            model.getGamePuzzles().get(puzzleId).restoreSolved(readBoolean("puzzle." + puzzleId + ".solved"));
        }
        for (Item item : model.getGameItems().values()) {
            if (item instanceof CosmeticItem) {
                ((CosmeticItem) item).unequip();
            }
        }
        if (cosmetic != null) {
            cosmetic.equip();
        }
        //Restores player status
        model.getPlayer().restoreState(playerName, currentRoom, previousRoom, maxHp, currentHp,
                totalXp, currentXp, xpToNextLevel, playerLevel, baseAttack, puzzlesSolved,
                monstersDefeated, roomsVisited, inventory, cosmetic);
    }

    /** Resolves a saved item ID against this game's item definitions. */
    private Item requireItem(GameModel model, String itemId) {
        Item item = model.getGameItems().get(itemId);
        if (item == null) {
            throw new IllegalArgumentException("Save file references unknown item: " + itemId);
        }
        return item;
    }

    /** Reads a required property, reporting which key is absent in an incomplete save. */
    private String required(String key) {
        String value = values.getProperty(key);
        if (value == null) {
            throw new IllegalArgumentException("Save file is missing required data: " + key);
        }
        return value;
    }

    /** Parses a required integer property and gives a save-specific error for invalid numbers. */
    private int readInteger(String key) {
        try {
            return Integer.parseInt(required(key));
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Save file contains an invalid number for " + key, exception);
        }
    }

    /** Parses a collection size and rejects negative or unreasonably large counts. */
    private int readCount(String key) {
        int count = readInteger(key);
        if (count < 0 || count > 10000) {
            throw new IllegalArgumentException("Save file contains an invalid count for " + key);
        }
        return count;
    }

    /** Parses a required boolean property without silently accepting other text. */
    private boolean readBoolean(String key) {
        String value = required(key);
        if (!"true".equalsIgnoreCase(value) && !"false".equalsIgnoreCase(value)) {
            throw new IllegalArgumentException("Save file contains an invalid boolean for " + key);
        }
        return Boolean.parseBoolean(value);
    }

    // Exposed package-locally so SaveManager can write the snapshot without exposing mutation publicly.
    Properties toProperties() {
        return values;
    }
}
