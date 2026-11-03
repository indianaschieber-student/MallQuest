package Model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Puzzle {
    public static final int MAX_ATTEMPTS = 4; // PZFR2: kicked out after 4 wrong tries

    private final String id;
    private final String roomId;
    private final String description;
    private final String inputPrompt;
    private final List<String> correctSolution;
    private final String correctText;
    private final String incorrectText;
    private final String hint;

    private int attempts = 0;
    private boolean solved = false;

    public Puzzle(String id, String roomId, String description, String inputPrompt,
                  List<String> correctSolution, String correctText,
                  String incorrectText, String hint) {
        this.id = id;
        this.roomId = roomId;
        this.description = description;
        this.inputPrompt = inputPrompt;
        this.correctSolution = correctSolution;
        this.correctText = correctText;
        this.incorrectText = incorrectText;
        this.hint = hint;
    }

    // Turns one line of Puzzles.txt into a Puzzle
    public static Puzzle fromLine(String line) {
        String[] p = line.split("\\|", -1); // -1 keeps empty fields
        if (p.length != 8) {
            throw new IllegalArgumentException("Bad puzzle line: " + line);
        }
        List<String> solution = new ArrayList<>();
        for (String part : p[4].split(";")) {
            // PZ16's answer has a note in parentheses; drop it so only "Waiver_Form" is compared
            solution.add(part.replaceAll("\\s*\\(.*\\)$", "").trim());
        }
        return new Puzzle(p[0].trim(), p[1].trim(), p[2].trim(), p[3].trim(),
                          solution, p[5].trim(), p[6].trim(), p[7].trim());
    }

    // Reads the whole file and returns a map of puzzleId -> Puzzle
    public static Map<String, Puzzle> loadFromFile(String path) throws IOException {
        Map<String, Puzzle> puzzles = new LinkedHashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue; // skip blank lines and comments
                }
                Puzzle puzzle = fromLine(line);
                puzzles.put(puzzle.getId(), puzzle);
            }
        }
        return puzzles;
    }

    // Checks the player's answers (in order) against the solution. Counts as one attempt.
    public boolean attempt(List<String> answer) {
        if (solved || isLockedOut()) {
            return false;
        }
        attempts++;
        if (matches(answer)) {
            solved = true;
        }
        return solved;
    }

    public boolean attempt(String... answer) {
        return attempt(Arrays.asList(answer));
    }

    private boolean matches(List<String> answer) {
        if (answer.size() != correctSolution.size()) {
            return false;
        }
        for (int i = 0; i < correctSolution.size(); i++) {
            if (!normalize(answer.get(i)).equals(normalize(correctSolution.get(i)))) {
                return false;
            }
        }
        return true;
    }

    private static String normalize(String s) {
        return s.trim().replace('_', ' ').toLowerCase();
    }

    public boolean isLockedOut() { return attempts >= MAX_ATTEMPTS && !solved; }
    public int getAttempts() { return attempts; }
    public int getAttemptsLeft() { return Math.max(0, MAX_ATTEMPTS - attempts); }
    public void resetAttempts() { attempts = 0; } // for when the player is kicked out and comes back
    public boolean isSolved() { return solved; }

    public String getId() { return id; }
    public String getRoomId() { return roomId; }
    public String getDescription() { return description; }
    public String getInputPrompt() { return inputPrompt; }
    public List<String> getCorrectSolution() { return correctSolution; }
    public String getCorrectText() { return correctText; }
    public String getIncorrectText() { return incorrectText; }
    public String getHint() { return hint; }

    @Override
    public String toString() {
        return id + " (" + roomId + ")";
    }
}
