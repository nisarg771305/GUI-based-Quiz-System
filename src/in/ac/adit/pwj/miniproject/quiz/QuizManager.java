package in.ac.adit.pwj.miniproject.quiz;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class QuizManager {
    private List<Question> questions = new ArrayList<>();
    private Map<String, Integer> userScores = new ConcurrentHashMap<>();
    private Map<String, List<String>> userAnswers = new ConcurrentHashMap<>();

    public void loadQuestions(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                String type = parts[0];
                String questionText = parts[1];
                String correctAnswer = parts[2];

                if (type.equalsIgnoreCase("MCQ")) {
                    List<String> options = Arrays.asList(parts[3], parts[4], parts[5], parts[6]);
                    questions.add(new MCQQuestion(questionText, options, correctAnswer));
                } else if (type.equalsIgnoreCase("TF")) {
                    questions.add(new TFQuestion(questionText, correctAnswer));
                }
            }

        } catch (IOException e) {
            System.out.println("Error reading questions: " + e.getMessage());
        }
    }

    public void startQuiz(String userName) {
        QuizSession session = new QuizSession(userName);
        Thread quizThread = new Thread(session);
        quizThread.start();

        try {
            quizThread.join(); // Wait for this quiz to complete
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void saveResults(String filePath) {
        File file = new File(filePath);
        Set<String> existingUsers = new HashSet<>();

        // Load existing users to avoid duplication
        if (file.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length >= 2) {
                        existingUsers.add(parts[0]);
                    }
                }
            } catch (IOException e) {
                System.out.println("Error reading existing results.");
            }
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, true))) {
            for (Map.Entry<String, Integer> entry : userScores.entrySet()) {
                if (!existingUsers.contains(entry.getKey())) {
                    bw.write(entry.getKey() + "," + entry.getValue());
                    bw.newLine();
                }
            }
        } catch (IOException e) {
            System.out.println("Error saving results: " + e.getMessage());
        }
    }

    // Inner Class
    private class QuizSession implements Runnable {
        private String userName;
        private Scanner scanner = new Scanner(System.in);
        private List<String> answersGiven = new ArrayList<>();

        public QuizSession(String userName) {
            this.userName = userName;
        }

        @Override
        public void run() {
            int score = 0;
            System.out.println("\nWelcome " + userName + "! Your quiz is starting...");

            for (Question q : questions) {
                q.display();
                System.out.print("Your Answer: ");
                String userAnswer = scanner.nextLine();
                answersGiven.add(userAnswer);

                if (q.checkAnswer(userAnswer)) {
                    score++;
                }
            }

            userScores.put(userName, score);
            userAnswers.put(userName, answersGiven);

            // Score Display
            System.out.println("\n=== Quiz Completed ===");
            System.out.println("Your Score: " + score + "/" + questions.size());

            // Review Mode
            System.out.println("\n=== Review Answers ===");
            for (int i = 0; i < questions.size(); i++) {
                Question q = questions.get(i);
                System.out.println("\nQ" + (i + 1) + ": " + q.getQuestionText());
                System.out.println("Your Answer   : " + answersGiven.get(i));
                System.out.println("Correct Answer: " + q.getCorrectAnswer());
            }
        }
    }
}
