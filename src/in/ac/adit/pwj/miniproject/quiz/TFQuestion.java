package in.ac.adit.pwj.miniproject.quiz;

public class TFQuestion extends Question {

    public TFQuestion(String questionText, String correctAnswer) {
        super(questionText, correctAnswer);
    }

    @Override
    public void display() {
        System.out.println(questionText + " (True/False)");
    }

    @Override
    public boolean checkAnswer(String userAnswer) {
        return userAnswer.trim().equalsIgnoreCase(correctAnswer);
    }
}
