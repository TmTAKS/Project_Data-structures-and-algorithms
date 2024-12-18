import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;

public class AlphabetGameWithRealTimeTimer {
    private JFrame frame;
    private JPanel mainPanel;
    private JLabel wordLabel, resultLabel, timerLabel;
    private JTextField answerField;
    private JButton playButton, viewScoresButton, exitButton;
    private JComboBox<String> difficultyComboBox;
    private ArrayList<String> usedWords = new ArrayList<>();
    private String selectedWord;
    private long startTime;
    private Timer timer;

    public AlphabetGameWithRealTimeTimer() {
        // สร้างหน้าต่างหลัก
        frame = new JFrame("Alphabet Game with Real-Time Timer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null);

        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(33, 47, 61));

        // หัวข้อ
        JLabel titleLabel = new JLabel("Alphabet Game");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 30));
        titleLabel.setForeground(new Color(244, 208, 63));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // กลาง (Scrambled Word)
        JPanel centerPanel = new JPanel(new GridLayout(5, 1, 10, 10));
        centerPanel.setBackground(new Color(33, 47, 61));

        wordLabel = new JLabel("Press 'Play' to Start!");
        wordLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        wordLabel.setForeground(Color.WHITE);
        wordLabel.setHorizontalAlignment(SwingConstants.CENTER);
        centerPanel.add(wordLabel);

        answerField = new JTextField();
        answerField.setFont(new Font("SansSerif", Font.PLAIN, 18));
        answerField.setHorizontalAlignment(JTextField.CENTER);
        answerField.setEnabled(false); // ปิดการใช้งานเริ่มต้น
        centerPanel.add(answerField);

        resultLabel = new JLabel("");
        resultLabel.setFont(new Font("SansSerif", Font.ITALIC, 18));
        resultLabel.setForeground(new Color(231, 76, 60));
        resultLabel.setHorizontalAlignment(SwingConstants.CENTER);
        centerPanel.add(resultLabel);

        timerLabel = new JLabel("Time: 0.0 seconds");
        timerLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        timerLabel.setForeground(new Color(46, 204, 113));
        timerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        centerPanel.add(timerLabel);

        difficultyComboBox = new JComboBox<>(new String[]{"Easy", "Medium", "Hard"});
        centerPanel.add(difficultyComboBox);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // ปุ่ม
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(new Color(33, 47, 61));
        playButton = new JButton("Play");
        viewScoresButton = new JButton("View Scores");
        exitButton = new JButton("Exit");
        styleButton(playButton);
        styleButton(viewScoresButton);
        styleButton(exitButton);

        buttonPanel.add(playButton);
        buttonPanel.add(viewScoresButton);
        buttonPanel.add(exitButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // เพิ่ม ActionListener
        playButton.addActionListener(e -> startGame());
        viewScoresButton.addActionListener(e -> viewScores());
        exitButton.addActionListener(e -> System.exit(0));

        frame.add(mainPanel);
        frame.setVisible(true);
    }

    private void startGame() {
        int difficulty = difficultyComboBox.getSelectedIndex() + 1;
        ArrayList<String> wordList = initializeWordLists(difficulty);
        selectedWord = getRandomWord(wordList, usedWords);

        if (selectedWord != null) {
            // สลับตัวอักษรของคำและแสดงผล
            String scrambledWord = scrambleWord(selectedWord);
            wordLabel.setText("Scrambled Word: " + scrambledWord);
            resultLabel.setText("");
            answerField.setText("");
            answerField.setEnabled(true); // เปิดใช้งานช่องคำตอบ

            // เริ่มจับเวลา
            startTime = System.currentTimeMillis();
            startRealTimeTimer();

            // เพิ่ม ActionListener เพื่อให้ผู้เล่นสามารถตอบคำถาม
            if (answerField.getActionListeners().length == 0) {
                answerField.addActionListener(e -> checkAnswer());
            }
        } else {
            wordLabel.setText("No words left!");
            timerLabel.setText("Time: 0.0 seconds");
        }
    }

    private void checkAnswer() {
        String userAnswer = answerField.getText().toUpperCase();

        if (userAnswer.equals(selectedWord.toUpperCase())) {
            // ตอบถูกต้อง
            stopRealTimeTimer();

            // คำนวณเวลาที่ใช้
            long endTime = System.currentTimeMillis();
            long timeTaken = endTime - startTime;
            resultLabel.setText("Correct! Time: " + (timeTaken / 1000.0) + " seconds");
            resultLabel.setForeground(new Color(46, 204, 113)); // สีเขียว

            // ถามผู้เล่นว่าต้องการบันทึกข้อมูลหรือไม่
            int choice = JOptionPane.showConfirmDialog(frame, "Do you want to save your score?", "Save Score", JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                // ให้กรอกชื่อผู้เล่น
                String playerName = JOptionPane.showInputDialog(frame, "Enter your name:", "Player Name", JOptionPane.PLAIN_MESSAGE);
                if (playerName != null && !playerName.trim().isEmpty()) {
                    savePlayerScore(playerName, difficultyComboBox.getSelectedIndex() + 1, timeTaken / 1000.0);
                    JOptionPane.showMessageDialog(frame, "Score saved successfully!");
                } else {
                    JOptionPane.showMessageDialog(frame, "Name cannot be empty. Score not saved.");
                }
            }

            answerField.setEnabled(false); // ปิดช่องคำตอบ
        } else {
            // ตอบผิด
            resultLabel.setText("Incorrect! Try again.");
            resultLabel.setForeground(new Color(231, 76, 60)); // สีแดง
        }
    }

    private void startRealTimeTimer() {
        timer = new Timer(100, e -> {
            long elapsedTime = System.currentTimeMillis() - startTime;
            timerLabel.setText("Time: " + (elapsedTime / 1000.0) + " seconds");
        });
        timer.start();
    }

    private void stopRealTimeTimer() {
        if (timer != null) {
            timer.stop();
        }
    }

    private void viewScores() {
        JFrame scoreFrame = new JFrame("Scores");
        scoreFrame.setSize(500, 400);
        scoreFrame.setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();

        // แสดงคะแนนในแต่ละระดับความยาก
        tabbedPane.addTab("Easy", new JScrollPane(createScoreTable(1)));
        tabbedPane.addTab("Medium", new JScrollPane(createScoreTable(2)));
        tabbedPane.addTab("Hard", new JScrollPane(createScoreTable(3)));

        scoreFrame.add(tabbedPane);
        scoreFrame.setVisible(true);
    }

    private JTable createScoreTable(int difficultyLevel) {
        DefaultTableModel model = new DefaultTableModel(new String[]{"Player", "Time (s)"}, 0);
        JTable table = new JTable(model);

        ArrayList<Score> scores = loadScores(difficultyLevel);

        // จัดเรียงคะแนนตามเวลา (น้อยไปมาก)
        scores.sort((s1, s2) -> Double.compare(s1.time, s2.time));

        for (Score score : scores) {
            model.addRow(new Object[]{score.playerName, score.time});
        }

        return table;
    }

    private ArrayList<Score> loadScores(int difficultyLevel) {
        ArrayList<Score> scores = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader("player_scores.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(", ");
                String playerName = parts[0].split(": ")[1];
                int difficulty = Integer.parseInt(parts[1].split(": ")[1]);
                double time = Double.parseDouble(parts[2].split(": ")[1].split(" ")[0]);

                if (difficulty == difficultyLevel) {
                    scores.add(new Score(playerName, difficulty, time));
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Error reading scores: " + e.getMessage());
        }

        return scores;
    }

    private void savePlayerScore(String playerName, int difficulty, double timeTaken) {
        try (FileWriter writer = new FileWriter("player_scores.txt", true)) {
            writer.write("Player: " + playerName + ", Difficulty: " + difficulty + ", Time: " + timeTaken + " seconds\n");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Error saving score: " + e.getMessage());
        }
    }

    private ArrayList<String> initializeWordLists(int difficulty) {
        ArrayList<String> easyWords = new ArrayList<>();
        ArrayList<String> mediumWords = new ArrayList<>();
        ArrayList<String> hardWords = new ArrayList<>();

        easyWords.add("JAVA");
        easyWords.add("CODE");
        easyWords.add("DESK");

        mediumWords.add("COMPUTER");
        mediumWords.add("PROGRAM");
        mediumWords.add("ALGORITHM");

        hardWords.add("STRUCTURE");
        hardWords.add("DATABASE");
        hardWords.add("DEVELOPER");

        return difficulty == 1 ? easyWords : difficulty == 2 ? mediumWords : hardWords;
    }

    private String getRandomWord(ArrayList<String> wordList, ArrayList<String> usedWords) {
        ArrayList<String> availableWords = new ArrayList<>();
        for (String word : wordList) {
            if (!usedWords.contains(word)) {
                availableWords.add(word);
            }
        }
        if (availableWords.isEmpty()) return null;
        Collections.shuffle(availableWords);
        String chosenWord = availableWords.get(0);
        usedWords.add(chosenWord);
        return chosenWord;
    }

    private String scrambleWord(String word) {
        ArrayList<Character> chars = new ArrayList<>();
        for (char c : word.toCharArray()) {
            chars.add(c);
        }
        Collections.shuffle(chars);
        StringBuilder scrambled = new StringBuilder();
        for (char c : chars) {
            scrambled.append(c);
        }
        return scrambled.toString();
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("SansSerif", Font.BOLD, 16));
        button.setBackground(new Color(41, 128, 185));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AlphabetGameWithRealTimeTimer::new);
    }

    // คลาสสำหรับเก็บข้อมูลคะแนน
    static class Score {
        String playerName;
        int difficulty;
        double time;

        public Score(String playerName, int difficulty, double time) {
            this.playerName = playerName;
            this.difficulty = difficulty;
            this.time = time;
        }
    }
}
