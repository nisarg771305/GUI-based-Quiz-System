package in.ac.adit.pwj.miniproject.quiz;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class QuizApp extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private QuizManager quizManager;
    private List<Question> questions;
    private int currentQuestionIndex = 0;
    private List<String> userAnswers;
    private String userName;
    private JLabel questionLabel;
    private JPanel optionsPanel;
    private JButton nextButton;
    private ButtonGroup optionsGroup;
    private JLabel questionCountLabel;
    private int score = 0;

    public QuizApp() {
        // Initialize components
        quizManager = new QuizManager();
        userAnswers = new ArrayList<>();
        questions = new ArrayList<>();
        
        // Set up the main frame
        setTitle("Java Quiz Application");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Create main panel with card layout
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        add(mainPanel);
        
        // Create all screens
        createWelcomeScreen();
        createQuizScreen();
        createResultScreen();
        
        // Show the welcome screen first
        cardLayout.show(mainPanel, "welcome");
    }
    
    private void createWelcomeScreen() {
        JPanel welcomePanel = new JPanel();
        welcomePanel.setLayout(new BoxLayout(welcomePanel, BoxLayout.Y_AXIS));
        welcomePanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        
        // Add logo or title
        JLabel titleLabel = new JLabel("Java Programming Quiz");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Description
        JLabel descLabel = new JLabel("Test your Java knowledge with this quiz!");
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // User details form
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        JLabel nameLabel = new JLabel("Name:");
        JTextField nameField = new JTextField(20);
        
        JLabel enrollLabel = new JLabel("Enrollment Number:");
        JTextField enrollField = new JTextField(20);
        
        JButton startButton = new JButton("Start Quiz");
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        startButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String enroll = enrollField.getText().trim();
            
            if (name.isEmpty() || enroll.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Please enter both name and enrollment number", 
                    "Input Required", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Set user name and start quiz
            userName = enroll + " - " + name;
            loadQuestions();
            resetQuiz();
            cardLayout.show(mainPanel, "quiz");
        });
        
        formPanel.add(nameLabel);
        formPanel.add(nameField);
        formPanel.add(enrollLabel);
        formPanel.add(enrollField);
        
        // Add components to welcome panel
        welcomePanel.add(Box.createVerticalGlue());
        welcomePanel.add(titleLabel);
        welcomePanel.add(Box.createRigidArea(new Dimension(0, 20)));
        welcomePanel.add(descLabel);
        welcomePanel.add(Box.createRigidArea(new Dimension(0, 30)));
        welcomePanel.add(formPanel);
        welcomePanel.add(Box.createRigidArea(new Dimension(0, 20)));
        welcomePanel.add(startButton);
        welcomePanel.add(Box.createVerticalGlue());
        
        mainPanel.add(welcomePanel, "welcome");
    }
    
    private void createQuizScreen() {
        JPanel quizPanel = new JPanel(new BorderLayout(10, 10));
        quizPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Top panel for question number and timer
        JPanel topPanel = new JPanel(new BorderLayout());
        questionCountLabel = new JLabel("Question 1/14");
        topPanel.add(questionCountLabel, BorderLayout.WEST);
        
        // Center panel for question and options
        JPanel centerPanel = new JPanel(new BorderLayout(0, 20));
        
        // Question panel
        JPanel questionPanel = new JPanel(new BorderLayout());
        questionLabel = new JLabel("Question text goes here");
        questionLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        questionLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        questionPanel.add(questionLabel, BorderLayout.CENTER);
        
        // Options panel
        optionsPanel = new JPanel();
        optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));
        optionsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        centerPanel.add(questionPanel, BorderLayout.NORTH);
        centerPanel.add(optionsPanel, BorderLayout.CENTER);
        
        // Bottom panel for navigation buttons
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        nextButton = new JButton("Next");
        nextButton.addActionListener(e -> handleNextQuestion());
        bottomPanel.add(nextButton);
        
        quizPanel.add(topPanel, BorderLayout.NORTH);
        quizPanel.add(centerPanel, BorderLayout.CENTER);
        quizPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        mainPanel.add(quizPanel, "quiz");
    }
    
    private void createResultScreen() {
        JPanel resultPanel = new JPanel();
        resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.Y_AXIS));
        resultPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        JLabel titleLabel = new JLabel("Quiz Results");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Score panel
        JPanel scorePanel = new JPanel();
        scorePanel.setLayout(new BoxLayout(scorePanel, BoxLayout.Y_AXIS));
        scorePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        scorePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel userLabel = new JLabel("User: Not set");
        userLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel scoreLabel = new JLabel("Score: 0/0");
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 18));
        scoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Review panel (will be populated dynamically)
        JPanel reviewWrapperPanel = new JPanel(new BorderLayout());
        reviewWrapperPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JPanel reviewPanel = new JPanel();
        reviewPanel.setLayout(new BoxLayout(reviewPanel, BoxLayout.Y_AXIS));
        
        JScrollPane scrollPane = new JScrollPane(reviewPanel);
        scrollPane.setPreferredSize(new Dimension(600, 300));
        reviewWrapperPanel.add(scrollPane, BorderLayout.CENTER);
        
        JButton finishButton = new JButton("Finish");
        finishButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        finishButton.addActionListener(e -> {
            // Save results and go back to welcome screen
            quizManager.saveResults("data/results.csv");
            cardLayout.show(mainPanel, "welcome");
        });
        
        scorePanel.add(userLabel);
        scorePanel.add(Box.createRigidArea(new Dimension(0, 10)));
        scorePanel.add(scoreLabel);
        
        resultPanel.add(titleLabel);
        resultPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        resultPanel.add(scorePanel);
        resultPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        resultPanel.add(reviewWrapperPanel);
        resultPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        resultPanel.add(finishButton);
        
        mainPanel.add(resultPanel, "result");
    }
    
    private void loadQuestions() {
        quizManager.loadQuestions("data/questions.csv");
        try {
            // Access questions through reflection since questions is private in QuizManager
            java.lang.reflect.Field questionsField = QuizManager.class.getDeclaredField("questions");
            questionsField.setAccessible(true);
            questions = (List<Question>) questionsField.get(quizManager);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error loading questions: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void resetQuiz() {
        currentQuestionIndex = 0;
        userAnswers.clear();
        score = 0;
        displayCurrentQuestion();
    }
    
    private void displayCurrentQuestion() {
        if (currentQuestionIndex >= questions.size()) {
            return;
        }
        
        Question currentQuestion = questions.get(currentQuestionIndex);
        questionLabel.setText("<html><body style='width: 500px'>" + currentQuestion.getQuestionText() + "</body></html>");
        questionCountLabel.setText("Question " + (currentQuestionIndex + 1) + "/" + questions.size());
        
        // Clear previous options
        optionsPanel.removeAll();
        
        // Create new button group for radio buttons
        optionsGroup = new ButtonGroup();
        
        if (currentQuestion instanceof MCQQuestion) {
            MCQQuestion mcq = (MCQQuestion) currentQuestion;
            
            // Access options through reflection since options is private in MCQQuestion
            try {
                java.lang.reflect.Field optionsField = MCQQuestion.class.getDeclaredField("options");
                optionsField.setAccessible(true);
                List<String> options = (List<String>) optionsField.get(mcq);
                
                for (int i = 0; i < options.size(); i++) {
                    String option = options.get(i);
                    JRadioButton radioBtn = new JRadioButton((char)('A' + i) + ". " + option);
                    radioBtn.setActionCommand(String.valueOf((char)('A' + i)));
                    
                    optionsGroup.add(radioBtn);
                    
                    // Add some padding around each option
                    JPanel optionPanel = new JPanel(new BorderLayout());
                    optionPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
                    optionPanel.add(radioBtn, BorderLayout.WEST);
                    
                    optionsPanel.add(optionPanel);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (currentQuestion instanceof TFQuestion) {
            JRadioButton trueOption = new JRadioButton("True");
            JRadioButton falseOption = new JRadioButton("False");
            
            trueOption.setActionCommand("True");
            falseOption.setActionCommand("False");
            
            optionsGroup.add(trueOption);
            optionsGroup.add(falseOption);
            
            JPanel truePanel = new JPanel(new BorderLayout());
            truePanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
            truePanel.add(trueOption, BorderLayout.WEST);
            
            JPanel falsePanel = new JPanel(new BorderLayout());
            falsePanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
            falsePanel.add(falseOption, BorderLayout.WEST);
            
            optionsPanel.add(truePanel);
            optionsPanel.add(falsePanel);
            
            // Set button text
            nextButton.setText(currentQuestionIndex == questions.size() - 1 ? "Finish" : "Next");
        }
        
        optionsPanel.revalidate();
        optionsPanel.repaint();
    }
    
    private void handleNextQuestion() {
        // Check if an option is selected
        String selectedAnswer = getSelectedAnswer();
        if (selectedAnswer == null) {
            JOptionPane.showMessageDialog(this, 
                "Please select an answer", 
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Store the answer
        userAnswers.add(selectedAnswer);
        
        // Check if answer is correct
        if (questions.get(currentQuestionIndex).checkAnswer(selectedAnswer)) {
            score++;
        }
        
        currentQuestionIndex++;
        
        // If this was the last question, show results
        if (currentQuestionIndex >= questions.size()) {
            showResults();
        } else {
            displayCurrentQuestion();
        }
    }
    
    private String getSelectedAnswer() {
        ButtonModel selectedButton = optionsGroup.getSelection();
        return selectedButton == null ? null : selectedButton.getActionCommand();
    }
    
    private void showResults() {
        // Store results in QuizManager
        try {
            java.lang.reflect.Method method = QuizManager.class.getDeclaredMethod("storeResults", String.class, int.class, List.class);
            method.setAccessible(true);
            method.invoke(quizManager, userName, score, userAnswers);
        } catch (Exception e) {
            // If method doesn't exist, use maps directly
            try {
                java.lang.reflect.Field userScoresField = QuizManager.class.getDeclaredField("userScores");
                userScoresField.setAccessible(true);
                Map<String, Integer> userScores = (Map<String, Integer>) userScoresField.get(quizManager);
                userScores.put(userName, score);
                
                java.lang.reflect.Field userAnswersField = QuizManager.class.getDeclaredField("userAnswers");
                userAnswersField.setAccessible(true);
                Map<String, List<String>> userAnswersMap = (Map<String, List<String>>) userAnswersField.get(quizManager);
                userAnswersMap.put(userName, userAnswers);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        
        // Update result screen
        JPanel resultPanel = (JPanel) mainPanel.getComponent(2);
        
        // Update user and score labels
        JPanel scorePanel = (JPanel) resultPanel.getComponent(2);
        JLabel userLabel = (JLabel) scorePanel.getComponent(0);
        JLabel scoreLabel = (JLabel) scorePanel.getComponent(2);
        
        userLabel.setText("User: " + userName);
        scoreLabel.setText("Score: " + score + "/" + questions.size());
        
        // Update review section
        JPanel reviewWrapper = (JPanel) resultPanel.getComponent(4);
        JScrollPane scrollPane = (JScrollPane) reviewWrapper.getComponent(0);
        JPanel reviewPanel = (JPanel) scrollPane.getViewport().getView();
        
        reviewPanel.removeAll();
        
        // Add each question and answer
        for (int i = 0; i < questions.size(); i++) {
            Question q = questions.get(i);
            String userAns = userAnswers.get(i);
            boolean isCorrect = q.checkAnswer(userAns);
            
            JPanel questionReviewPanel = new JPanel();
            questionReviewPanel.setLayout(new BoxLayout(questionReviewPanel, BoxLayout.Y_AXIS));
            questionReviewPanel.setBorder(BorderFactory.createCompoundBorder(
                new EmptyBorder(10, 5, 10, 5),
                new LineBorder(Color.LIGHT_GRAY)
            ));
            
            JLabel qLabel = new JLabel("<html><b>Q" + (i + 1) + ":</b> " + q.getQuestionText() + "</html>");
            qLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            
            JLabel userAnsLabel = new JLabel("Your answer: " + userAns);
            userAnsLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            
            JLabel correctAnsLabel = new JLabel("Correct answer: " + q.getCorrectAnswer());
            correctAnsLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            
            // Change color based on correctness
            if (isCorrect) {
                userAnsLabel.setForeground(new Color(0, 128, 0)); // Dark green
            } else {
                userAnsLabel.setForeground(Color.RED);
            }
            
            questionReviewPanel.add(qLabel);
            questionReviewPanel.add(userAnsLabel);
            questionReviewPanel.add(correctAnsLabel);
            
            reviewPanel.add(questionReviewPanel);
            reviewPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        }
        
        reviewPanel.revalidate();
        reviewPanel.repaint();
        
        // Show result screen
        cardLayout.show(mainPanel, "result");
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            QuizApp app = new QuizApp();
            app.setVisible(true);
        });
    }
    
    // Helper method to store results - this will be called via reflection
    private void storeResults(String userName, int score, List<String> userAnswers) {
        try {
            java.lang.reflect.Field userScoresField = QuizManager.class.getDeclaredField("userScores");
            userScoresField.setAccessible(true);
            Map<String, Integer> userScores = (Map<String, Integer>) userScoresField.get(quizManager);
            userScores.put(userName, score);
            
            java.lang.reflect.Field userAnswersField = QuizManager.class.getDeclaredField("userAnswers");
            userAnswersField.setAccessible(true);
            Map<String, List<String>> userAnswersMap = (Map<String, List<String>>) userAnswersField.get(quizManager);
            userAnswersMap.put(userName, userAnswers);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}