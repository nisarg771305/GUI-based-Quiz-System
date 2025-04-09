package in.ac.adit.pwj.miniproject.quiz;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        QuizManager manager = new QuizManager();

        // Load questions
        manager.loadQuestions("data/questions.csv");

        // Ask for user details
        System.out.print("Enter your name: ");
        String name = scanner.nextLine();

        System.out.print("Enter your enrollment number: ");
        String enroll = scanner.nextLine();

        // Combine to make a unique user ID
        String userId = enroll + " - " + name;

        // Start the quiz for the user
        manager.startQuiz(userId);

        // Save results
        manager.saveResults("data/results.csv");
    }
}
