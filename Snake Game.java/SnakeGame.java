package com.company;

import java.util.*;
import java.io.*;

public class SnakeGame {
    private static final char SNAKE_CHAR = '\u25CF';
    private static final char FOOD_CHAR = '@';
    private int[][] grid;
    private List<int[]> snake;
    private int width;
    private int height;
    private Deque<Direction> directions;

    public void run() throws IOException {
        loadApples();

        Scanner scanner = new Scanner(System.in);
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("Welcome to the Snake Game!");
        Thread.sleep(1000);

        initGrid();
        placeFood();
        printInstructions();

        String line;
        while (!(line = reader.readLine()).equalsIgnoreCase("q")) {
            switch (line.toUpperCase()) {
                case "W":
                    directions.offerFirst(Direction.UP);
                    break;
                case "A":
                    directions.offerFirst(Direction.LEFT);
                    break;
                case "S":
                    directions.offerFirst(Direction.DOWN);
                    break;
                case "D":
                    directions.offerFirst(Direction.RIGHT);
                    break;
                default:
                    System.out.println("Invalid command.");
                    continue;
            }
            updateSnakePosition();
            checkCollisionsAndEndGameIfNeeded();
            clearScreen();
            render();
        }

        System.exit(0);
    }

    private void loadApples() throws FileNotFoundException {
        File file = new File("src/com/company/apples");
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextInt()) {
                int randX = scanner.nextInt();
                int randY = scanner.nextInt();
                System.out.printf("Adding (%d,%d)%n", randX, randY);
                Apples.getInstance().addApple(randX, randY);
            }
        }
    }

    private void initGrid() {
        width = 40;
        height = 20;

        grid = new int[height][width];
        snake = Arrays.asList(
                new int[]{height / 2, width / 2},
                new int[]{height / 2, width / 2 - 1}
        );

        dimensionsInitializer(grid, height, width);

        directions = new LinkedList<>();
        directions.addLast(Direction.RIGHT);
    }

    private void dimensionsInitializer(int[][] arr, int rows, int cols) {
        for (int i = 0; i < rows; ++i) {
            Arrays.fill(arr[i], 0);
        }
    }

    private void placeFood() {
        Random rnd = new Random();
        FoodGenerator generator = () -> rnd.nextInt(height) * width + rnd.nextInt(width);
        int pos = generator.generate();
        grid[pos / width][pos % width] = 1;
    }

    private void printInstructions() {
        System.out.println("Use WASD keys to control the snake. Type Q to quit.");
    }

    private void updateSnakePosition() {
        Direction dir = directions.pollFirst();
        int headX = snake.get(0)[0];
        int headY = snake.get(0)[1];

        int newHeadX;
        int newHeadY;

        switch (dir) {
            case UP:
                --headX;
                break;
            case DOWN:
                ++headX;
                break;
            case LEFT:
                --headY;
                break;
            case RIGHT:
                ++headY;
                break;
        }

        newHeadX = Math.min(Math.max(headX, 0), height - 1);
        newHeadY = Math.min(Math.max(headY, 0), width - 1);

        snake.add(0, new int[]{newHeadX, newHeadY});
    }

    private void checkCollisionsAndEndGameIfNeeded() {
        if (isSelfCollidingOrOutOfBounds()) {
            endGame();
            return;
        }

        if (isEatingItself()) {
            endGame();
            return;
        }

        if (isEatingFood()) {
            growSnake();
            placeFood();
        }
    }

    private boolean isSelfCollidingOrOutOfBounds() {
        int[] head = snake.get(0);
        return head[0] >= height || head[0] < 0 || head[1] >= width || head[1] < 0 ||
                snake.stream().anyMatch(cell -> Arrays.equals(cell, head) && cell != snake.get(0));
    }

    private boolean isEatingItself() {
        int[] head = snake.get(0);
        return snake.subList(1, snake.size()).contains(head);
    }

    private boolean isEatingFood() {
        int[] head = snake.get(0);
        return grid[head[0]][head[1]] == 1;
    }

    private void growSnake() {
        int lastIndex = snake.size() - 1;
        snake.add(lastIndex, snake.get(lastIndex).clone());
    }

    private void render() {
        StringBuilder sb = new StringBuilder();

        for (int[] arr : grid) {
            for (int num : arr) {
                sb.append((num == 1) ? FOOD_CHAR : ' ');
            }
            sb.append('\n');
        }

        for (int[] s : snake) {
            grid[s[0]][s[1]] = SNAKE_CHAR;
        }

        System.out.print(sb.toString());
        System.out.flush();
    }

    private void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    private void endGame() {
        System.out.println("Game Over!");
        System.exit(0);
    }

    enum Direction {
        UP(-1, 0),
        DOWN(1, 0),
        LEFT(0, -1),
        RIGHT(0, 1);

        private final int dx;
        private final int dy;

        Direction(int dx, int dy) {
            this.dx = dx;
            this.dy = dy;
        }
    }

    static class Apples implements Serializable {
        private static final long serialVersionUID = 1L;
        private Map<Integer, Integer> applesMap;

        private synchronized static Apples getInstance() {
            if (instance == null) {
                instance = new Apples();
            }
            return instance;
        }

        private Apples() {
            applesMap = new HashMap<>();
        }

        public synchronized void addApple(int x, int y) {
            applesMap.put(x, y);
        }

        @Override
        public String toString() {
            StringBuilder stringBuilder = new StringBuilder();
            for (var entry : applesMap.entrySet()) {
                stringBuilder.append(String.format("%d %d%n", entry.getKey(), entry.getValue()));
            }
            return stringBuilder.toString();
        }
    }

    private static Apples instance;

    public static void main(String[] args) throws InterruptedException, IOException {
        new SnakeGame().run();
    }
}