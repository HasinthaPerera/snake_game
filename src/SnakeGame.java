import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class SnakeGame extends JPanel implements ActionListener, KeyListener {
    private final int TILE_SIZE = 20;
    private final int WIDTH = 400;
    private final int HEIGHT = 400;

    private ArrayList<Point> snake = new ArrayList<>();
    private Point food;
    private String direction = "RIGHT";
    private Timer timer;
    private int score = 0;
    private int highScore = 0;

    // 🔹 Game states: 0 = Menu, 1 = Playing, 2 = Game Over
    private int state = 0;
    private int mode = 1; // 1 = Classic, 2 = Free, 3 = Obstacle

    // Obstacle (for mode 3)
    private Rectangle obstacle = new Rectangle(WIDTH / 2 - 40, HEIGHT / 2 - 40, 80, 80);

    // 🔹 Buttons
    private JButton restartBtn, menuBtn, quitBtn;
    private JFrame frame;

    public SnakeGame(JFrame frame) {
        this.frame = frame;

        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);

        timer = new Timer(120, this);
        timer.start();
        setFocusable(true);
        addKeyListener(this);

        // Initialize buttons but don’t add yet
        restartBtn = new JButton("Restart");
        menuBtn = new JButton("Menu");
        quitBtn = new JButton("Quit");

        restartBtn.addActionListener(e -> restartGame());
        menuBtn.addActionListener(e -> backToMenu());
        quitBtn.addActionListener(e -> System.exit(0));
    }

    private void initGame(int selectedMode) {
        snake.clear();
        snake.add(new Point(5, 5));
        direction = "RIGHT";
        score = 0;
        mode = selectedMode;
        spawnFood();
        state = 1; // Playing
        hideButtons();
    }

    private void spawnFood() {
        int x = (int) (Math.random() * (WIDTH / TILE_SIZE));
        int y = (int) (Math.random() * (HEIGHT / TILE_SIZE));
        food = new Point(x, y);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (state == 0) { // Menu Screen
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 28));
            g.drawString("SNAKE GAME", WIDTH / 2 - 90, HEIGHT / 2 - 100);

            g.setFont(new Font("Arial", Font.PLAIN, 20));
            g.drawString("Choose Mode:", WIDTH / 2 - 70, HEIGHT / 2 - 40);
            g.drawString("1 - Classic Mode", WIDTH / 2 - 90, HEIGHT / 2);
            g.drawString("2 - Free Mode", WIDTH / 2 - 90, HEIGHT / 2 + 30);
            g.drawString("3 - Obstacle Mode", WIDTH / 2 - 90, HEIGHT / 2 + 60);
            return;
        }

        if (state == 2) { // Game Over Screen
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 30));
            g.drawString("GAME OVER", WIDTH / 2 - 90, HEIGHT / 2 - 40);

            g.setFont(new Font("Arial", Font.PLAIN, 20));
            g.drawString("Score: " + score, WIDTH / 2 - 50, HEIGHT / 2);
            g.drawString("High Score: " + highScore, WIDTH / 2 - 70, HEIGHT / 2 + 30);

            showButtons();
            return;
        }

        // --- Playing state ---
        if (mode == 3) { // Draw obstacle
            g.setColor(Color.GRAY);
            g.fillRect(obstacle.x, obstacle.y, obstacle.width, obstacle.height);
        }

        // Draw food
        g.setColor(Color.RED);
        g.fillRect(food.x * TILE_SIZE, food.y * TILE_SIZE, TILE_SIZE, TILE_SIZE);

        // Draw snake
        g.setColor(Color.GREEN);
        for (Point p : snake) {
            g.fillRect(p.x * TILE_SIZE, p.y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
        }

        // Draw score & high score
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("Score: " + score, 10, 20);
        g.drawString("High Score: " + highScore, WIDTH - 140, 20);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (state != 1) return; // Only update if playing

        Point head = new Point(snake.get(0));
        switch (direction) {
            case "UP" -> head.y--;
            case "DOWN" -> head.y++;
            case "LEFT" -> head.x--;
            case "RIGHT" -> head.x++;
        }

        // Free mode (teleport through walls)
        if (mode == 2) {
            if (head.x < 0) head.x = WIDTH / TILE_SIZE - 1;
            else if (head.x >= WIDTH / TILE_SIZE) head.x = 0;
            if (head.y < 0) head.y = HEIGHT / TILE_SIZE - 1;
            else if (head.y >= HEIGHT / TILE_SIZE) head.y = 0;
        }

        // Eating food
        if (head.equals(food)) {
            snake.add(0, head);
            score++;
            if (score > highScore) highScore = score;
            spawnFood();
        } else {
            snake.add(0, head);
            snake.remove(snake.size() - 1);
        }

        // Wall collision (Classic + Obstacle)
        if (mode != 2) {
            if (head.x < 0 || head.x >= WIDTH / TILE_SIZE || head.y < 0 || head.y >= HEIGHT / TILE_SIZE) {
                gameOver();
            }
        }

        // Self collision
        for (int i = 1; i < snake.size(); i++) {
            if (head.equals(snake.get(i))) {
                gameOver();
            }
        }

        // Obstacle collision
        if (mode == 3) {
            Rectangle headRect = new Rectangle(head.x * TILE_SIZE, head.y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            if (headRect.intersects(obstacle)) {
                gameOver();
            }
        }

        repaint();
    }

    private void gameOver() {
        state = 2;
        timer.stop();
        repaint();
    }

    private void restartGame() {
        initGame(mode);
        timer.start();
    }

    private void backToMenu() {
        state = 0;
        hideButtons();
        repaint();
    }

    private void showButtons() {
        if (restartBtn.getParent() == null) {
            JPanel buttonPanel = new JPanel();
            buttonPanel.setOpaque(false);
            buttonPanel.add(restartBtn);
            buttonPanel.add(menuBtn);
            buttonPanel.add(quitBtn);
            frame.add(buttonPanel, BorderLayout.SOUTH);
            frame.revalidate();
        }
    }

    private void hideButtons() {
        frame.getContentPane().removeAll();
        frame.add(this, BorderLayout.CENTER);
        frame.revalidate();
        frame.repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (state == 0) { // Menu navigation
            if (e.getKeyCode() == KeyEvent.VK_1) {
                initGame(1);
                timer.start();
            } else if (e.getKeyCode() == KeyEvent.VK_2) {
                initGame(2);
                timer.start();
            } else if (e.getKeyCode() == KeyEvent.VK_3) {
                initGame(3);
                timer.start();
            }
        } else if (state == 1) { // Playing
            switch (e.getKeyCode()) {
                case KeyEvent.VK_UP -> {
                    if (!direction.equals("DOWN")) direction = "UP";
                }
                case KeyEvent.VK_DOWN -> {
                    if (!direction.equals("UP")) direction = "DOWN";
                }
                case KeyEvent.VK_LEFT -> {
                    if (!direction.equals("RIGHT")) direction = "LEFT";
                }
                case KeyEvent.VK_RIGHT -> {
                    if (!direction.equals("LEFT")) direction = "RIGHT";
                }
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}
    @Override
    public void keyTyped(KeyEvent e) {}

    public static void main(String[] args) {
        JFrame frame = new JFrame("Snake Game");
        SnakeGame game = new SnakeGame(frame);
        frame.setLayout(new BorderLayout());
        frame.add(game, BorderLayout.CENTER);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
