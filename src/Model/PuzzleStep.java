package Model;

import java.util.List;
//import java.util.Arraylist;

//This is a Helper class I created to store the prompts, answers, and answer choices for each step of the puzzle
public class PuzzleStep {
    private String prompt;
    private List<String> choices;
    private String correctAnswer;

    public PuzzleStep(String prompt, List<String> choices, String correctAnswer){
        this.prompt = prompt;
        this.choices = choices;
        this.correctAnswer = correctAnswer;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public List<String> getChoices() {
        return choices;
    }

    public void setChoices(List<String> choices) {
        this.choices = choices;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

}
