import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;
import java.util.Random;

public class SnakeGame extends JPanel implements KeyListener {
    private static final int GRID_SIZE = 20;
    private static final int GRID_WIDTH = 30;
    private static final int GRID_HEIGHT = 20;

    private ArrayList<Point> snake;
    private ArrayList<Point> obstacles;
    private Point food;
    private int direction;
    private boolean gameOver;
    private boolean gameRunning;
    private int score;
    private Timer obstacleTimer;

    public SnakeGame() {
        snake = new ArrayList<>();
        snake.add(new Point(GRID_WIDTH / 2, GRID_HEIGHT / 2));
        obstacles = new ArrayList<>();
        food = createFood();
        direction = KeyEvent.VK_RIGHT;
        gameOver = false;
        gameRunning = true;
        score = 0;

        setPreferredSize(new Dimension(GRID_SIZE * GRID_WIDTH, GRID_SIZE * GRID_HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);

        obstacleTimer = new Timer(10000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addObstacle();
            }
        });
        obstacleTimer.start();

        Timer gameTimer = new Timer(100, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (gameRunning) {
                    move();
                    checkCollision();
                    repaint();
                }
            }
        });
        gameTimer.start();
    }

    private Point createFood() {
        Random random = new Random();
        int x = random.nextInt(GRID_WIDTH);
        int y = random.nextInt(GRID_HEIGHT);
        return new Point(x, y);
    }

    private void move() {
        int headX = snake.get(0).x;
        int headY = snake.get(0).y;

        if (direction == KeyEvent.VK_LEFT) {
            headX = (headX - 1 + GRID_WIDTH) % GRID_WIDTH;
        } else if (direction == KeyEvent.VK_RIGHT) {
            headX = (headX + 1) % GRID_WIDTH;
        } else if (direction == KeyEvent.VK_UP) {
            headY = (headY - 1 + GRID_HEIGHT) % GRID_HEIGHT;
        } else if (direction == KeyEvent.VK_DOWN) {
            headY = (headY + 1) % GRID_HEIGHT;
        }

        snake.add(0, new Point(headX, headY));
        if (headX == food.x && headY == food.y) {
            updateScore();
            food = createFood();
        } else {
            snake.remove(snake.size() - 1);
        }
    }

    private void checkCollision() {
        int headX = snake.get(0).x;
        int headY = snake.get(0).y;

        for (Point obstacle : obstacles) {
            if (obstacle.x == headX && obstacle.y == headY) {
                obstacles.remove(obstacle);
                score -= 5;
                break;
            }
        }

        for (int i = 1; i < snake.size(); i++) {
            if (snake.get(i).x == headX && snake.get(i).y == headY) {
                gameOver = true;
                gameRunning = false;
                break;
            }
        }
    }

    private void updateScore() {
        score++;
    }

    private void addObstacle() {
        Random random = new Random();
        int x = random.nextInt(GRID_WIDTH);
        int y = random.nextInt(GRID_HEIGHT);
        obstacles.add(new Point(x, y));
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (gameRunning) {
            g.setColor(Color.GREEN);
            for (Point p : snake) {
                g.fillRect(p.x * GRID_SIZE, p.y * GRID_SIZE, GRID_SIZE, GRID_SIZE);
            }

            g.setColor(Color.RED);
            for (Point obstacle : obstacles) {
                g.fillRect(obstacle.x * GRID_SIZE, obstacle.y * GRID_SIZE, GRID_SIZE, GRID_SIZE);
            }

            g.setColor(Color.YELLOW);
            g.fillRect(food.x * GRID_SIZE, food.y * GRID_SIZE, GRID_SIZE, GRID_SIZE);

            g.setColor(Color.WHITE);
            g.drawString("Score: " + score, 10, 15);
        } else {
            g.setColor(Color.WHITE);
            g.drawString("Game Over", getWidth() / 2 - 40, getHeight() / 2);
            g.drawString("Score: " + score, getWidth() / 2 - 30, getHeight() / 2 + 20);
            g.drawString("Press 'R' to Play Again", getWidth() / 2 - 60, getHeight() / 2 + 40);
        }
    }

    public void keyPressed(KeyEvent e) {
        int newDirection = e.getKeyCode();
        if (gameRunning && ((newDirection == KeyEvent.VK_LEFT && direction != KeyEvent.VK_RIGHT) ||
                (newDirection == KeyEvent.VK_RIGHT && direction != KeyEvent.VK_LEFT) ||
                (newDirection == KeyEvent.VK_UP && direction != KeyEvent.VK_DOWN) ||
                (newDirection == KeyEvent.VK_DOWN && direction != KeyEvent.VK_UP))) {
            direction = newDirection;
        }
        if (!gameRunning && e.getKeyCode() == KeyEvent.VK_R) {
            resetGame();
        }
    }

    public void keyReleased(KeyEvent e) {}

    public void keyTyped(KeyEvent e) {}

    private void resetGame() {
        snake.clear();
        snake.add(new Point(GRID_WIDTH / 2, GRID_HEIGHT / 2));
        obstacles.clear();
        food = createFood();
        direction = KeyEvent.VK_RIGHT;
        gameOver = false;
        gameRunning = true;
        score = 0;
        repaint();
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Snake Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new SnakeGame());
        frame.pack();
        frame.setVisible(true);
    }
}
