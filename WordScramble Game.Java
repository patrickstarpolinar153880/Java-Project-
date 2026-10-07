import javax.swing.*;
import java.awt.*;
import java.util.*;

public class WordScrambleGame {

    static JFrame frame;
    static JPanel panel;

    static JLabel title, info, word, score;
    static JTextField answer;

    static Random random = new Random();

    static String[] easy = {
        "computer", "mouse", "keyboard", "monitor", "printer",
        "screen", "cable", "server", "router", "network"
    };

    static String[] medium = {
        "software", "hardware", "database", "internet", "compiler",
        "website", "browser", "firewall", "wireless", "ethernet"
    };

    static String[] hard = {
        "algorithm", "cybersecurity", "virtualization", "architecture",
        "encryption", "information", "development", "authentication",
        "technology", "communication"
    };

    static String[] words;
    static boolean[] used;

    static int rounds;
    static int points;
    static int currentRound;
    static int currentScore;

    static String currentWord;
    static String playerName;

    static String[] names = new String[50];
    static int[] scores = new int[50];
    static int scoreCount = 0;

    static String recentPlayer = "";
    static int recentScore = 0;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> createWindow());
    }

    static void createWindow() {
        frame = new JFrame("Word Scramble");
        frame.setSize(600, 450);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        showMainMenu();
        frame.setVisible(true);
    }

    static void setupPanel() {
        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        panel.setBackground(new Color(25, 30, 40));

        frame.setContentPane(panel);
        panel.removeAll();
    }

    static JLabel makeTitle(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 30));
        label.setForeground(Color.CYAN);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    static JButton makeButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setMaximumSize(new Dimension(300, 50));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        return button;
    }

    static void showMainMenu() {
        setupPanel();

        title = makeTitle("WORD SCRAMBLE");
        panel.add(title);
        panel.add(Box.createVerticalStrut(15));

        JLabel sub = new JLabel("Java Edition");
        sub.setForeground(Color.WHITE);
        sub.setFont(new Font("Arial", Font.PLAIN, 18));
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(sub);

        panel.add(Box.createVerticalStrut(30));

        JButton play = makeButton("PLAY GAME");
        JButton scoresBtn = makeButton("VIEW SCORES");
        JButton exit = makeButton("EXIT");

        panel.add(play);
        panel.add(Box.createVerticalStrut(12));
        panel.add(scoresBtn);
        panel.add(Box.createVerticalStrut(12));
        panel.add(exit);

        play.addActionListener(e -> enterName());
        scoresBtn.addActionListener(e -> showScores());
        exit.addActionListener(e -> System.exit(0));

        panel.revalidate();
        panel.repaint();
    }

    static void enterName() {
        String name = JOptionPane.showInputDialog(
            frame, "Enter your name:"
        );

        if (name == null || name.trim().isEmpty())
            return;

        playerName = name.trim();
        showDifficulty();
    }

    static void showDifficulty() {
        setupPanel();

        panel.add(makeTitle("SELECT DIFFICULTY"));
        panel.add(Box.createVerticalStrut(30));

        JButton easyBtn = makeButton("EASY");
        JButton mediumBtn = makeButton("MEDIUM");
        JButton hardBtn = makeButton("HARD");
        JButton back = makeButton("BACK");

        panel.add(easyBtn);
        panel.add(Box.createVerticalStrut(10));
        panel.add(mediumBtn);
        panel.add(Box.createVerticalStrut(10));
        panel.add(hardBtn);
        panel.add(Box.createVerticalStrut(10));
        panel.add(back);

        easyBtn.addActionListener(e -> startGame(1));
        mediumBtn.addActionListener(e -> startGame(2));
        hardBtn.addActionListener(e -> startGame(3));
        back.addActionListener(e -> showMainMenu());

        panel.revalidate();
        panel.repaint();
    }

    static void startGame(int level) {

        if (level == 1) {
            words = easy;
            rounds = 10;
            points = 10;
        } else if (level == 2) {
            words = medium;
            rounds = 7;
            points = 20;
        } else {
            words = hard;
            rounds = 5;
            points = 30;
        }

        used = new boolean[words.length];
        currentRound = 0;
        currentScore = 0;

        showGame();
        nextWord();
    }

    static void showGame() {
        setupPanel();

        info = new JLabel();
        info.setForeground(Color.WHITE);
        info.setFont(new Font("Arial", Font.BOLD, 18));
        info.setAlignmentX(Component.CENTER_ALIGNMENT);

        word = makeTitle("");

        score = new JLabel();
        score.setForeground(Color.YELLOW);
        score.setFont(new Font("Arial", Font.BOLD, 18));
        score.setAlignmentX(Component.CENTER_ALIGNMENT);

        answer = new JTextField();
        answer.setFont(new Font("Arial", Font.PLAIN, 20));
        answer.setMaximumSize(new Dimension(300, 45));
        answer.setHorizontalAlignment(JTextField.CENTER);

        JButton submit = makeButton("SUBMIT");
        JButton back = makeButton("MAIN MENU");

        panel.add(info);
        panel.add(Box.createVerticalStrut(20));
        panel.add(word);
        panel.add(Box.createVerticalStrut(20));
        panel.add(score);
        panel.add(Box.createVerticalStrut(20));
        panel.add(answer);
        panel.add(Box.createVerticalStrut(15));
        panel.add(submit);
        panel.add(Box.createVerticalStrut(10));
        panel.add(back);

        submit.addActionListener(e -> checkAnswer());
        answer.addActionListener(e -> checkAnswer());

        back.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(
                frame,
                "Return to main menu?",
                "Confirm",
                JOptionPane.YES_NO_OPTION
            );

            if (choice == JOptionPane.YES_OPTION)
                showMainMenu();
        });

        panel.revalidate();
        panel.repaint();
    }

    static void nextWord() {

        if (currentRound == rounds) {
            finishGame();
            return;
        }

        int index;

        do {
            index = random.nextInt(words.length);
        } while (used[index]);

        used[index] = true;
        currentWord = words[index];

        word.setText(scramble(currentWord).toUpperCase());
        info.setText(
            "Round " + (currentRound + 1) + " / " + rounds
        );
        score.setText("Score: " + currentScore);

        answer.setText("");
        answer.requestFocus();

        currentRound++;
    }

    static String scramble(String text) {

        char[] letters = text.toCharArray();

        for (int i = 0; i < letters.length; i++) {
            int j = random.nextInt(letters.length);

            char temp = letters[i];
            letters[i] = letters[j];
            letters[j] = temp;
        }

        String result = new String(letters);

        if (result.equalsIgnoreCase(text) && text.length() > 1)
            return scramble(text);

        return result;
    }

    static void checkAnswer() {

        String guess = answer.getText().trim();

        if (guess.isEmpty())
            return;

        if (guess.equalsIgnoreCase(currentWord)) {
            currentScore += points;

            JOptionPane.showMessageDialog(
                frame,
                "Correct! +" + points + " points!"
            );
        } else {
            JOptionPane.showMessageDialog(
                frame,
                "Wrong!\nThe answer was: " + currentWord
            );
        }

        nextWord();
    }

    static void finishGame() {

        names[scoreCount] = playerName;
        scores[scoreCount] = currentScore;
        scoreCount++;

        recentPlayer = playerName;
        recentScore = currentScore;

        JOptionPane.showMessageDialog(
            frame,
            "Game Over!\n\nPlayer: " + playerName +
            "\nScore: " + currentScore
        );

        showMainMenu();
    }

    static void showScores() {
        setupPanel();

        panel.add(makeTitle("HIGH SCORES"));
        panel.add(Box.createVerticalStrut(20));

        Integer[] order = new Integer[scoreCount];

        for (int i = 0; i < scoreCount; i++)
            order[i] = i;

        Arrays.sort(order, (a, b) ->
            Integer.compare(scores[b], scores[a])
        );

        if (scoreCount == 0) {

            JLabel none = new JLabel("No scores yet.");
            none.setForeground(Color.WHITE);
            none.setFont(new Font("Arial", Font.PLAIN, 18));
            none.setAlignmentX(Component.CENTER_ALIGNMENT);

            panel.add(none);

        } else {

            for (int i : order) {

                JLabel row = new JLabel(
                    names[i] + " - " + scores[i] + " pts"
                );

                row.setForeground(Color.WHITE);
                row.setFont(
                    new Font("Arial", Font.BOLD, 18)
                );
                row.setAlignmentX(Component.CENTER_ALIGNMENT);

                panel.add(row);
                panel.add(Box.createVerticalStrut(8));
            }
        }

        panel.add(Box.createVerticalStrut(20));

        if (!recentPlayer.isEmpty()) {

            JLabel recent = new JLabel(
                "Recent: " + recentPlayer +
                " - " + recentScore + " pts"
            );

            recent.setForeground(Color.YELLOW);
            recent.setAlignmentX(Component.CENTER_ALIGNMENT);

            panel.add(recent);
        }

        panel.add(Box.createVerticalStrut(20));

        JButton back = makeButton("BACK");
        panel.add(back);

        back.addActionListener(e -> showMainMenu());

        panel.revalidate();
        panel.repaint();
    }
}
