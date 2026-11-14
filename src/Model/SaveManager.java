package Model;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Properties;

/**
 * Reads and writes the game's three save slots as text files.
 *
 * <p>SaveManager handles file operations only. SaveData captures and restores the game state;
 * SaveManager stores that data in files named {@code slot1.txt}, {@code slot2.txt}, and
 * {@code slot3.txt} inside the configured save directory.</p>
 */
public class SaveManager {
    /** The number of selectable save profiles shown when the game starts. Per the SRS, it should be 3*/
    public static final int SLOT_COUNT = 3;
    private final Path saveDirectory;

    //Default Constructor that uses the "saves" folder as its working directory
    public SaveManager() {
        this(Paths.get("saves"));
    }

    /*
     * Creates a manager that uses the specified directory.
     *
     * <p>This constructor is useful when a caller or test needs to choose a particular
     * location for save files.</p>
     *
     * @param saveDirectory directory where the slot text files are stored
     */
    //Secondary constructor that allows for a different save directory, for testing purposes
    public SaveManager(Path saveDirectory) {
        this.saveDirectory = saveDirectory;
    }


    //Checks if a slot has a corresponding save file
    public boolean hasSave(int slot) {
        return Files.exists(slotPath(slot));
    }


    //throws IOException if the file exists but cannot be read or is invalid
    //throws IllegalArgumentException if the slot number is out of range
    //Return's the player's name for the save slot, or [New Save] if there is no corresponding Save Data
    public String getSlotName(int slot) throws IOException {
        Path path = slotPath(slot);
        if (!Files.exists(path)) {
            return "[New Save]";
        }
        return load(slot).getPlayerName();
    }

    /*
     * Reads a slot's text file and creates a SaveData snapshot from its properties.
     *
     * @param slot slot number, from 1 through {@link #SLOT_COUNT}
     * @return the save snapshot, ready to restore into a GameModel
     * @throws IOException if the slot is empty, cannot be read, or contains an unsupported
     *                     or invalid save format
     * @throws IllegalArgumentException if the slot number is out of range
     */
    public SaveData load(int slot) throws IOException {
        Path path = slotPath(slot);
        if (!Files.exists(path)) {
            throw new IOException("Save slot " + slot + " is empty.");
        }
        Properties values = new Properties();
        try (InputStream input = Files.newInputStream(path)) {
            values.load(input);
        }
        try {
            return SaveData.fromProperties(values);
        } catch (IllegalArgumentException exception) {
            throw new IOException("Save slot " + slot + " is invalid: " + exception.getMessage(), exception);
        }
    }

    /*
     * Writes a snapshot to the selected slot, creating the save directory if needed.
     *
     * <p>The data is first written to a temporary file and then moved over the slot file.
     * This reduces the chance that an interrupted write leaves a partially written save.
     * An atomic move is used when the file system supports it.</p>
     *
     * @param slot slot number, from 1 through {@link #SLOT_COUNT}
     * @param saveData snapshot to persist
     * @throws IOException if the directory or save file cannot be created or written
     * @throws IllegalArgumentException if the slot number is out of range
     */
    public void save(int slot, SaveData saveData) throws IOException {
        Path path = slotPath(slot);
        Files.createDirectories(saveDirectory);
        Path temporaryPath = path.resolveSibling(path.getFileName().toString() + ".tmp");
        try {
            try (OutputStream output = Files.newOutputStream(temporaryPath)) {
                saveData.toProperties().store(output, "MallQuest save data");
            }
            try {
                Files.move(temporaryPath, path, StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException exception) {
                Files.move(temporaryPath, path, StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            Files.deleteIfExists(temporaryPath);
        }
    }

    /*
     * Resolves a validated slot number to its text-file path.
     *
     * @param slot slot number, from 1 through {@link #SLOT_COUNT}
     * @return path to that slot's save file
     * @throws IllegalArgumentException if the slot number is out of range
     */
    private Path slotPath(int slot) {
        if (slot < 1 || slot > SLOT_COUNT) {
            throw new IllegalArgumentException("Save slot must be between 1 and " + SLOT_COUNT + ".");
        }
        return saveDirectory.resolve("slot" + slot + ".txt");
    }
}
