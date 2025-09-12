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

    private int state = 0; // 0 = Menu, 1 = Playing, 2 = Game Over
    private int mode = 1;  // 1 = Classic, 2 = Free, 3 = Obstacle

    private Rectangle obstacle = new Rectangle(WIDTH / 2 - 40, HEIGHT / 2 - 40, 80, 80);

    private JButton classicBtn, freeBtn, obstacleBtn;
    private JButton restartBtn, menuBtn, quitBtn;
    private JFrame frame;

    class RoundedBorder implements javax.swing.border.Border {
        private int radius;
        public RoundedBorder(int radius) { this.radius = radius; }
        public Insets getBorderInsets(Component c) { return new Insets(this.radius+1, this.radius+1, this.radius+2, this.radius); }
        public boolean isBorderOpaque() { return false; }
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) { g.drawRoundRect(x, y, width-1, height-1, radius, radius); }
    }

    public SnakeGame(JFrame frame) {
        this.frame = frame;

        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);

        timer = new Timer(120, this);
        timer.start();
        setFocusable(true);
        addKeyListener(this);

        // Menu Buttons
        classicBtn = new JButton("Classic Mode");
        freeBtn = new JButton("Free Mode");
        obstacleBtn = new JButton("Obstacle Mode");
        classicBtn.addActionListener(e -> startGame(1));
        freeBtn.addActionListener(e -> startGame(2));
        obstacleBtn.addActionListener(e -> startGame(3));

        // Game Over Buttons
        restartBtn = new JButton("Restart");
        menuBtn = new JButton("Menu");
        quitBtn = new JButton("Quit");
        restartBtn.addActionListener(e -> restartGame());
        menuBtn.addActionListener(e -> backToMenu());
        quitBtn.addActionListener(e -> System.exit(0));

        // Style buttons with neon + hover
        styleButton(classicBtn, new Color(0, 255, 127), Color.BLACK);
        styleButton(freeBtn, new Color(0, 191, 255), Color.BLACK);
        styleButton(obstacleBtn, new Color(255, 140, 0), Color.BLACK);
        styleButton(restartBtn, new Color(255, 215, 0), Color.BLACK);
        styleButton(menuBtn, new Color(30, 144, 255), Color.BLACK);
        styleButton(quitBtn, new Color(255, 68, 68), Color.BLACK);
    }

    private void styleButton(JButton btn, Color bg, Color fg) {
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Arial", Font.BOLD, 16));
        btn.setOpaque(true);
        btn.setBorder(new RoundedBorder(15));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            Color originalBg = bg;
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) { btn.setBackground(bg.brighter()); }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) { btn.setBackground(originalBg); }
        });
    }

    private void startGame(int selectedMode) { initGame(selectedMode); timer.start(); }
    private void initGame(int selectedMode) {
        snake.clear();
        snake.add(new Point(5,5));
        direction = "RIGHT";
        score = 0;
        mode = selectedMode;
        spawnFood();
        state = 1;
        hideButtons();
    }

    private void spawnFood() {
        int x = (int)(Math.random()*(WIDTH/TILE_SIZE));
        int y = (int)(Math.random()*(HEIGHT/TILE_SIZE));
        food = new Point(x,y);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // Neon grid background
        g2.setColor(new Color(0,255,127,30));
        for (int x=0; x<WIDTH; x+=TILE_SIZE) g2.drawLine(x,0,x,HEIGHT);
        for (int y=0; y<HEIGHT; y+=TILE_SIZE) g2.drawLine(0,y,WIDTH,y);

        if(state==0) {
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial",Font.BOLD,28));
            g2.drawString("SNAKE GAME", WIDTH/2-90, HEIGHT/2-120);
            showMenuButtons();
            return;
        }

        if(state==2) {
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial",Font.BOLD,30));
            g2.drawString("GAME OVER", WIDTH/2-90, HEIGHT/2-80);
            g2.setFont(new Font("Arial",Font.PLAIN,20));
            g2.drawString("Score: "+score, WIDTH/2-50, HEIGHT/2-40);
            g2.drawString("High Score: "+highScore, WIDTH/2-70, HEIGHT/2-10);
            showGameOverButtons();
            return;
        }

        // Obstacle
        if(mode==3) {
            g2.setColor(Color.DARK_GRAY);
            g2.fillRect(obstacle.x, obstacle.y, obstacle.width, obstacle.height);
        }

        // Food
        g2.setColor(Color.RED);
        g2.fillRect(food.x*TILE_SIZE, food.y*TILE_SIZE, TILE_SIZE, TILE_SIZE);

        // Snake colors per mode
        Color mainColor, glowColor;
        if(mode==1) { mainColor=new Color(0,255,127); glowColor=new Color(0,255,127,100); }   // Green
        else if(mode==2) { mainColor=new Color(0,191,255); glowColor=new Color(0,191,255,100); } // Blue
        else { mainColor=new Color(255,140,0); glowColor=new Color(255,140,0,100); }            // Orange

        // Neon Snake
        g2.setStroke(new BasicStroke(2));
        for(Point p: snake) {
            int x=p.x*TILE_SIZE, y=p.y*TILE_SIZE;
            g2.setColor(glowColor);
            g2.fillRect(x-2,y-2,TILE_SIZE+4,TILE_SIZE+4);
            g2.setColor(mainColor);
            g2.fillRect(x,y,TILE_SIZE,TILE_SIZE);
        }

        // Score
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial",Font.BOLD,16));
        g2.drawString("Score: "+score,10,20);
        g2.drawString("High Score: "+highScore, WIDTH-140,20);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(state!=1) return;
        Point head = new Point(snake.get(0));
        switch(direction) {
            case "UP" -> head.y--;
            case "DOWN" -> head.y++;
            case "LEFT" -> head.x--;
            case "RIGHT" -> head.x++;
        }

        if(mode==2) { // Free mode teleport
            if(head.x<0) head.x=WIDTH/TILE_SIZE-1; else if(head.x>=WIDTH/TILE_SIZE) head.x=0;
            if(head.y<0) head.y=HEIGHT/TILE_SIZE-1; else if(head.y>=HEIGHT/TILE_SIZE) head.y=0;
        }

        if(head.equals(food)) { snake.add(0,head); score++; if(score>highScore) highScore=score; spawnFood(); }
        else { snake.add(0,head); snake.remove(snake.size()-1); }

        if(mode!=2 && (head.x<0 || head.x>=WIDTH/TILE_SIZE || head.y<0 || head.y>=HEIGHT/TILE_SIZE)) gameOver();
        for(int i=1;i<snake.size();i++) if(head.equals(snake.get(i))) gameOver();
        if(mode==3 && new Rectangle(head.x*TILE_SIZE, head.y*TILE_SIZE, TILE_SIZE, TILE_SIZE).intersects(obstacle)) gameOver();

        repaint();
    }

    private void gameOver() { state=2; timer.stop(); repaint(); }
    private void restartGame() { initGame(mode); timer.start(); }
    private void backToMenu() { state=0; hideButtons(); repaint(); }

    private void showMenuButtons() {
        frame.getContentPane().removeAll();
        JPanel panel = new JPanel(); panel.setOpaque(false); panel.setLayout(new GridLayout(3,1,10,10));
        panel.add(classicBtn); panel.add(freeBtn); panel.add(obstacleBtn);
        frame.add(this, BorderLayout.CENTER); frame.add(panel, BorderLayout.SOUTH);
        frame.revalidate(); frame.repaint();
    }

    private void showGameOverButtons() {
        frame.getContentPane().removeAll();
        JPanel panel = new JPanel(); panel.setOpaque(false);
        panel.add(restartBtn); panel.add(menuBtn); panel.add(quitBtn);
        frame.add(this, BorderLayout.CENTER); frame.add(panel, BorderLayout.SOUTH);
        frame.revalidate(); frame.repaint();
    }

    private void hideButtons() {
        frame.getContentPane().removeAll();
        frame.add(this, BorderLayout.CENTER);
        frame.revalidate(); frame.repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(state==1) {
            switch(e.getKeyCode()) {
                case KeyEvent.VK_UP -> { if(!direction.equals("DOWN")) direction="UP"; }
                case KeyEvent.VK_DOWN -> { if(!direction.equals("UP")) direction="DOWN"; }
                case KeyEvent.VK_LEFT -> { if(!direction.equals("RIGHT")) direction="LEFT"; }
                case KeyEvent.VK_RIGHT -> { if(!direction.equals("LEFT")) direction="RIGHT"; }
            }
        }
    }
    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}

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
