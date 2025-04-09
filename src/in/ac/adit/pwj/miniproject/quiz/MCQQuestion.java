package in.ac.adit.pwj.miniproject.quiz;

import java.util.List;

public class MCQQuestion extends Question {
    private List<String> options;

    public MCQQuestion(String questionText, List<String> options, String correctAnswer) {
        super(questionText, correctAnswer);
        this.options = options;
    }

    @Override
    public void display() {
        System.out.println(questionText);
        for (int i = 0; i < options.size(); i++) {
            System.out.println((char)('A' + i) + ". " + options.get(i));
        }
    }

    @Override
    public boolean checkAnswer(String userAnswer) {
        return userAnswer.trim().equalsIgnoreCase(correctAnswer);
    }
}
