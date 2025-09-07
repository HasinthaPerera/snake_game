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
    private int score = 0; // 🔹 Score variable

    public SnakeGame() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        snake.add(new Point(5, 5));
        spawnFood();

        timer = new Timer(100, this);
        timer.start();
        setFocusable(true);
        addKeyListener(this);
    }

    private void spawnFood() {
        int x = (int) (Math.random() * (WIDTH / TILE_SIZE));
        int y = (int) (Math.random() * (HEIGHT / TILE_SIZE));
        food = new Point(x, y);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw food
        g.setColor(Color.RED);
        g.fillRect(food.x * TILE_SIZE, food.y * TILE_SIZE, TILE_SIZE, TILE_SIZE);

        // Draw snake
        g.setColor(Color.GREEN);
        for (Point p : snake) {
            g.fillRect(p.x * TILE_SIZE, p.y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
        }

        // 🔹 Draw score
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("Score: " + score, 10, 20);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
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
            score++; // 🔹 Increase score
            spawnFood();
        } else {
            snake.add(0, head);
            snake.remove(snake.size() - 1);
        }

        // Collision with walls
        if (head.x < 0 || head.x >= WIDTH / TILE_SIZE || head.y < 0 || head.y >= HEIGHT / TILE_SIZE) {
            timer.stop();
            JOptionPane.showMessageDialog(this, "Game Over!\nScore: " + score);
        }

        // Collision with itself
        for (int i = 1; i < snake.size(); i++) {
            if (head.equals(snake.get(i))) {
                timer.stop();
                JOptionPane.showMessageDialog(this, "Game Over!\nScore: " + score);
            }
        }

        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
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
