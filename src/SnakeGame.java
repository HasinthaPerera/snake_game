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
    private int highScore = 0; // 🔹 High Score variable
    private boolean gameOver = false;

    public SnakeGame() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        initGame();

        timer = new Timer(150, this);
        timer.start();
        setFocusable(true);
        addKeyListener(this);
    }

    // 🔹 Initialize or Restart the Game
    private void initGame() {
        snake.clear();
        snake.add(new Point(5, 5));
        direction = "RIGHT";
        score = 0;
        gameOver = false;
        spawnFood();
    }

    private void spawnFood() {
        int x = (int) (Math.random() * (WIDTH / TILE_SIZE));
        int y = (int) (Math.random() * (HEIGHT / TILE_SIZE));
        food = new Point(x, y);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (gameOver) {
            // 🔹 Game Over Screen
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 30));
            g.drawString("GAME OVER", WIDTH / 2 - 90, HEIGHT / 2 - 40);

            g.setFont(new Font("Arial", Font.PLAIN, 20));
            g.drawString("Score: " + score, WIDTH / 2 - 50, HEIGHT / 2);
            g.drawString("High Score: " + highScore, WIDTH / 2 - 70, HEIGHT / 2 + 30);

            g.drawString("Press R to Restart", WIDTH / 2 - 80, HEIGHT / 2 + 70);
            g.drawString("Press Q to Quit", WIDTH / 2 - 70, HEIGHT / 2 + 100);
            return;
        }

        // Draw food
        g.setColor(Color.RED);
        g.fillRect(food.x * TILE_SIZE, food.y * TILE_SIZE, TILE_SIZE, TILE_SIZE);

        // Draw snake
        g.setColor(Color.GREEN);
        for (Point p : snake) {
            g.fillRect(p.x * TILE_SIZE, p.y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
        }

        // Draw score and high score
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("Score: " + score, 10, 20);
        g.drawString("High Score: " + highScore, WIDTH - 140, 20);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameOver) return;

        Point head = new Point(snake.get(0));
        switch (direction) {
            case "UP" -> head.y--;
            case "DOWN" -> head.y++;
            case "LEFT" -> head.x--;
            case "RIGHT" -> head.x++;
        }

        // Check collision with food
        if (head.equals(food)) {
            snake.add(0, head);
            score++;
            if (score > highScore) highScore = score; // 🔹 Update high score
            spawnFood();
        } else {
            snake.add(0, head);
            snake.remove(snake.size() - 1);
        }

        // Check wall collision
        if (head.x < 0 || head.x >= WIDTH / TILE_SIZE || head.y < 0 || head.y >= HEIGHT / TILE_SIZE) {
            gameOver = true;
            timer.stop();
        }

        // Check self collision
        for (int i = 1; i < snake.size(); i++) {
            if (head.equals(snake.get(i))) {
                gameOver = true;
                timer.stop();
            }
        }

        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (!gameOver) {
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
        } else {
            // 🔹 Restart or Quit
            if (e.getKeyCode() == KeyEvent.VK_R) {
                initGame();
                timer.start();
                repaint();
            } else if (e.getKeyCode() == KeyEvent.VK_Q) {
                System.exit(0);
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}
    @Override
    public void keyTyped(KeyEvent e) {}

    public static void main(String[] args) {
        JFrame frame = new JFrame("Snake Game");
        SnakeGame game = new SnakeGame();
        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
