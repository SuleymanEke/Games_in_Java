package SnakeGame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.font.TextLayout;
import java.util.LinkedList;
import java.util.Random;


public class SnakeGame extends JPanel implements ActionListener {
    private final int width;
    private final int height;
    private final int cellSize;
    private final Random random = new Random();
    private static final int FRAME_RATE = 20;
    private boolean gameStarted = false;
    private boolean gameOver = false;
    private GamePoint food;
    private Direction direction = Direction.RIGHT;
    private Direction newDirection = Direction.RIGHT;
    private final LinkedList<GamePoint> snake = new LinkedList<>();

    public SnakeGame(final int width, final int height) {
        super();
        this.width = width;
        this.height = height;
        this.cellSize = width / (FRAME_RATE * 2);
        setPreferredSize(new Dimension(width, height));
        setBackground(Color.BLACK);
    }

    public void startGame() {
        resetGameData();
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        requestFocusInWindow();
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyEvent(e.getKeyCode());
            }
        });
        new Timer(1000 / FRAME_RATE, this).start();
    }

    private void handleKeyEvent(final int keyCode) {
        if (!gameStarted) {
            if (keyCode == KeyEvent.VK_SPACE) {
                gameStarted = true;
            }
        } else if (!gameOver) {
            switch (keyCode) {
                case KeyEvent.VK_UP:
                    newDirection = Direction.UP;
                    break;
                case KeyEvent.VK_DOWN:
                    newDirection = Direction.DOWN;
                    break;
                case KeyEvent.VK_LEFT:
                    newDirection = Direction.LEFT;
                    break;
                case KeyEvent.VK_RIGHT:
                    newDirection = Direction.RIGHT;
                    break;

            }
        }
    }

    private void resetGameData() {
        snake.clear();
        snake.add(new GamePoint(width / 2, height / 2));
        generateFood();
    }

    private void generateFood() {
        do {
            food = new GamePoint(random.nextInt(width / cellSize) * cellSize,
                    random.nextInt(height / cellSize) * cellSize);
        } while (snake.contains(food));
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        if (!gameStarted) {
            graphics.setColor(Color.WHITE);
            graphics.setFont(graphics.getFont().deriveFont(30F));
            int currentHeight = height / 4;
            final var graphics2D = (Graphics2D) graphics;
            final var frc = graphics2D.getFontRenderContext();
            final String message = "Oyunu Baslatmak Icin Tıkla";
            for (final var line : message.split("\n")) {
                final var layout = new TextLayout(line, graphics.getFont(), frc);
                final var bounds = layout.getBounds();
                final var targetWidth = (float) (width - bounds.getWidth()) / 2;
                layout.draw(graphics2D, targetWidth, currentHeight);
                currentHeight += graphics.getFontMetrics().getHeight();
            }
        } else {
            graphics.setColor(Color.cyan);
            graphics.fillRect(food.x, food.y, cellSize, cellSize);

            Color snakeColor = Color.GREEN;
            for (final var point : snake) {
                graphics.setColor(snakeColor);
                graphics.fillRect(point.x, point.y, cellSize, cellSize);
                final int newGreen = (int) Math.round(snakeColor.getGreen() * (0.95));
                snakeColor = new Color(0, newGreen, 0);
            }

            if (gameOver){

            }
        }
    }

    private void move() {
        direction = newDirection;
        final GamePoint currentHead = snake.getFirst();
        final GamePoint newHead = switch (direction) {
            case UP -> new GamePoint(currentHead.x, currentHead.y - cellSize);
            case DOWN -> new GamePoint(currentHead.x, currentHead.y + cellSize);
            case LEFT -> new GamePoint(currentHead.x - cellSize, currentHead.y);
            case RIGHT -> new GamePoint(currentHead.x + cellSize, currentHead.y);
        };
        snake.addFirst(newHead);
        if (newHead.equals(food)) {
            generateFood();
        } else if (checkCollision()) {
            gameOver = true;
            snake.removeFirst();
        } else {
            snake.removeLast();
        }
        direction = newDirection;
    }

    private boolean checkCollision() {
        final GamePoint head = snake.getFirst();
        final var invalidWidth = head.x < 0 || head.x >= width;
        final var invalidHeight = head.y < 0 || head.y >= height;
        return invalidWidth || invalidHeight;
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        if (gameStarted && !gameOver) {
            move();
        }
        repaint();
    }

    private record GamePoint(int x, int y) {
    }

    private enum Direction {
        UP, DOWN, LEFT, RIGHT
    }


}

