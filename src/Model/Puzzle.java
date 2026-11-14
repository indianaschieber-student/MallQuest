package Model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Puzzle {
    public static final int MAX_ATTEMPTS = 4; // PZFR2: kicked out after 4 wrong tries

    private final String id;
    private final String roomId;
    private final String description;
    private final List<PuzzleStep> puzzleSteps;
    private final String correctText;
    private final String incorrectText;
    private final String hint;
    private boolean solved = false;

    //New puzzle constructor, stores the prompts in an arraylist
    public Puzzle(String id, String roomId, String description, List<PuzzleStep> puzzleSteps, String correctText,
                  String incorrectText, String hint) {
        this.id = id;
        this.roomId = roomId;
        this.description = description;
        this.puzzleSteps = puzzleSteps;
        this.correctText = correctText;
        this.incorrectText = incorrectText;
        this.hint = hint;
    }

    //Turns one line of Puzzles.txt into a Puzzle, Modified by Indiana to store inputPrompts in
    // an ArrayList instead of a String, also added an ArrayList of answer choices
    public static Puzzle fromLine(String line) {
        String[] p = line.split("\\|", -1); // -1 keeps empty fields

        String[] prompts = p[3].split("\\*", -1);
        String[] correctAnswers = p[4].split(";", -1);
        String[] choicesByStep;
        choicesByStep = p[8].split("\\\\", -1); // groups separated by \
        List<PuzzleStep> puzzleSteps = new ArrayList<>();

        //Copilot helped me fix this method. Originally I had tried to use the .get method to retrieve the
        //puzzle step objects from the list, but this creates a null pointer. Instead, new PuzzleSteps are instantiated
        // with a prompt, solution, and answer choice and then are added to the list of puzzle steps
        for (int i = 0; i < prompts.length; i++) {
            List<String> choices = new ArrayList<>();
            for (String choice : choicesByStep[i].split(",", -1)) {
                choices.add(choice.trim());
            }
            PuzzleStep step = new PuzzleStep(
                    prompts[i].replace("\\", "\n")
                            .replaceAll("(?m)^[ \\t]+|[ \\t]+$", ""), //Trims whitespace around answer choices
                    choices,
                    correctAnswers[i].trim()
            );
            puzzleSteps.add(step);
        }

        return new Puzzle(p[0].trim(), p[1].trim(), p[2].trim(), puzzleSteps, p[5].trim(), p[6].trim(), p[7].trim());
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

    //Attempt and match method modified by Indy after adding PuzzleSteps
    public boolean attempt(List<String> answers) {
        if (solved) {
            return false;
        }

        if (matches(answers)) {
            solved = true;
        }
        return solved;
    }

    private boolean matches(List<String> answers) {
        if (answers.size() != puzzleSteps.size()) {
            return false;
        }

        for (int i = 0; i < puzzleSteps.size(); i++) {
            String expected = (puzzleSteps.get(i).getCorrectAnswer());
            String actual = (answers.get(i));

            if (!actual.equalsIgnoreCase(expected)) {
                return false;
            }
        }
        return true;
    }

    /*private static String normalize(String answer) {
        return answer.trim().replace('_', ' ').toLowerCase(Locale.ROOT);
    }*/

    public boolean isSolved() { return solved; }
    public void restoreSolved(boolean solved) { this.solved = solved; }

    public String getId() { return id; }
    public String getRoomId() { return roomId; }
    public String getDescription() { return description; }
    public List<PuzzleStep> getPuzzleSteps() { return puzzleSteps; }
    public String getCorrectText() { return correctText; }
    public String getIncorrectText() { return incorrectText; }
    public String getHint() { return hint; }

    @Override
    public String toString() {
        return id + " (" + roomId + ")";
    }

}
