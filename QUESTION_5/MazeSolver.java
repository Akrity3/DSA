// A Maze Solver
// Functionality:
//  Graph (Adjacency List/Matrix): Use a graph to represent the maze where each cell is a node
// connected to its adjacent cells.
//  Stack (DFS) / Queue (BFS): Use a stack for Depth-First Search (DFS) or a queue for Breadth-First
// Search (BFS) to find a path from start to finish.
// GUI:
//  A grid-based maze where each cell is a node.
//  A start and end point for the player or algorithm.
//  Buttons to solve the maze using DFS or BFS.
//  A "Generate New Maze" button to create a randomized maze.
// Implementation:
// Initialization:
// 1. Generate a random maze using a grid where walls block movement.
// 2. Represent the maze as a graph (adjacency list or matrix).
// 3. Allow the user to choose a start and end point.
// 4. Display the maze in the GUI.
// Solving the Maze:
// While the path is not found:
// 1. Choose an algorithm:
// o If DFS is selected, use a stack.
// o If BFS is selected, use a queue.
// 2. Explore adjacent nodes:
// o If a path is found, mark it.
// o If a dead-end is reached, backtrack.
// 3. Highlight the solution path on the GUI.
// Game Over:
//  If the end point is reached, display a success message.
//  If no path exists, display a failure message.
// Data Structures:
//  Graph: Represent the maze where each cell is a node connected to adjacent walkable cells.
//  Queue: Used for BFS to find the shortest path.
//  Stack: Used for DFS to explore paths.
//  2D Array: Represents the grid-based maze.
// Additional Considerations:
//  Random Maze Generation: Use algorithms like Prim’s or Recursive Backtracking to generate
// new mazes dynamically.
//  User Input: Allow the player to manually navigate through the maze.
//  Path Animation: Visually show the algorithm exploring paths.
//  Scoring System: Award points based on efficiency (steps taken, time, etc.).





import javax.swing.*;
import javax.swing.Timer;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;
import java.util.Queue;

/**
 * Maze Solver Application using Java Swing
 * Features: Random maze generation, DFS/BFS pathfinding, interactive GUI
 * Data Structures: Graph (Adjacency List), Stack (DFS), Queue (BFS), 2D Array (Grid)
 */
public class MazeSolver extends JFrame {
    // Constants for maze dimensions and drawing
    private static final int MAZE_SIZE = 25;    // Number of cells in each dimension of the maze grid
    private static final int CELL_SIZE = 20;    // Pixel size of each cell on screen

    // Updated, vibrant, colorful palette for each cell state (no black or brown)
    private static final Color WALL_COLOR      = new Color(45, 197, 244);    // Bright sky blue for walls
    private static final Color PATH_COLOR      = new Color(255, 255, 134);   // Sunny yellow for paths
    private static final Color START_COLOR     = new Color(255, 105, 180);   // Hot pink for start point
    private static final Color END_COLOR       = new Color(123, 239, 178);   // Mint green for end point
    private static final Color VISITED_COLOR   = new Color(255, 153, 51);    // Orange for visited
    private static final Color SOLUTION_COLOR  = new Color(120, 77, 255);    // Vivid purple for solution path
    private static final Color EXPLORING_COLOR = new Color(0, 255, 242);     // Cyan for exploring
    private static final Color GRID_LINE_COLOR = new Color(244, 67, 54);     // Strong red for grid lines

    // Cell type identifiers for internal representation
    private static final int WALL = 0;
    private static final int PATH = 1;
    private static final int START = 2;
    private static final int END = 3;
    private static final int VISITED = 4;
    private static final int SOLUTION = 5;
    private static final int EXPLORING = 6;

    // Maze grid represented as a 2D int array
    private int[][] maze;
    // Graph representation: Each point maps to a list of adjacent walkable points
    private Map<Point, List<Point>> graph;
    // JPanel subclass for drawing the maze grid
    private MazePanel mazePanel;
    // Positions of start and end points in maze coordinates
    private Point startPoint;
    private Point endPoint;
    // Flags to indicate the user is setting the start or end point via mouse click
    private boolean isSettingStart = false;
    private boolean isSettingEnd = false;
    // Timer used for pathfinding animation
    private Timer animationTimer;
    // UI labels to show algorithm status and performance
    private JLabel statusLabel;
    private JLabel scoreLabel;
    // Variables to track number of steps and elapsed time during pathfinding
    private int steps = 0;
    private long startTime;

    /**
     * Constructor - Initialize the maze solver application with GUI components and a new maze.
     */
    public MazeSolver() {
        setTitle("Maze Solver - DFS & BFS Pathfinding");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        initializeComponents();   // Setup GUI components and listeners
        generateNewMaze();        // Generate a random maze on startup

        pack();
        setLocationRelativeTo(null);
        setResizable(false);
    }

    /**
     * Initialize the GUI panels and colorful control buttons.
     */
    private void initializeComponents() {
        // Maze display panel
        mazePanel = new MazePanel();
        add(mazePanel, BorderLayout.CENTER);

        // Control panel with colorful buttons for functions
        JPanel controlPanel = new JPanel(new FlowLayout());

        JButton generateBtn = new JButton("Generate New Maze");
        generateBtn.setBackground(new Color(255, 148, 120));   // Lively orange-pink
        generateBtn.setForeground(Color.BLUE);
        generateBtn.addActionListener(e -> generateNewMaze());

        JButton setStartBtn = new JButton("Set Start Point");
        setStartBtn.setBackground(new Color(255, 222, 89));    // Pastel yellow
        setStartBtn.setForeground(new Color(128, 0, 255));     // Violet
        setStartBtn.addActionListener(e -> {
            isSettingStart = true;
            isSettingEnd = false;
            statusLabel.setText("Click on a cell to set start point");
        });

        JButton setEndBtn = new JButton("Set End Point");
        setEndBtn.setBackground(new Color(107, 255, 173));     // Mint
        setEndBtn.setForeground(new Color(0, 140, 202));       // Azure
        setEndBtn.addActionListener(e -> {
            isSettingEnd = true;
            isSettingStart = false;
            statusLabel.setText("Click on a cell to set end point");
        });

        JButton solveWithDFS = new JButton("Solve with DFS");
        solveWithDFS.setBackground(new Color(255, 169, 255));  // Light magenta
        solveWithDFS.setForeground(new Color(0, 120, 120));    // Teal
        solveWithDFS.addActionListener(e -> solveMaze(true));  // true for DFS

        JButton solveWithBFS = new JButton("Solve with BFS");
        solveWithBFS.setBackground(new Color(97, 178, 255));   // Light blue
        solveWithBFS.setForeground(new Color(255, 51, 153));   // Pink
        solveWithBFS.addActionListener(e -> solveMaze(false)); // false for BFS

        JButton clearPath = new JButton("Clear Path");
        clearPath.setBackground(new Color(191, 255, 128));     // Spring green
        clearPath.setForeground(new Color(180, 6, 120));       // Deep rose
        clearPath.addActionListener(e -> clearSolution());

        controlPanel.add(generateBtn);
        controlPanel.add(setStartBtn);
        controlPanel.add(setEndBtn);
        controlPanel.add(solveWithDFS);
        controlPanel.add(solveWithBFS);
        controlPanel.add(clearPath);

        add(controlPanel, BorderLayout.NORTH);

        // Status and score panel
        JPanel statusPanel = new JPanel(new FlowLayout());
        statusLabel = new JLabel("Click 'Generate New Maze' to start");
        statusPanel.setBackground(new Color(174, 214, 241));      // Pastel blue background
        statusLabel.setForeground(new Color(203, 67, 53));        // Vivid magenta-red

        scoreLabel = new JLabel("Steps: 0 | Time: 0ms");
        scoreLabel.setForeground(new Color(42, 187, 155));        // Deep sea green

        statusPanel.add(statusLabel);
        statusPanel.add(Box.createHorizontalStrut(20));  // spacing between labels
        statusPanel.add(scoreLabel);

        add(statusPanel, BorderLayout.SOUTH);
    }

    /**
     * Generates a random colorful maze using recursive backtracking.
     */
    private void generateNewMaze() {
        maze = new int[MAZE_SIZE][MAZE_SIZE];
        graph = new HashMap<>();

        // Start with all walls (sky blue)
        for (int i = 0; i < MAZE_SIZE; i++) {
            for (int j = 0; j < MAZE_SIZE; j++) {
                maze[i][j] = WALL;
            }
        }

        // Generate path by recursive carving from cell (1,1)
        generateMazeRecursive(1, 1);

        // Default start (hot pink) and end (mint green) points
        startPoint = new Point(1, 1);
        endPoint = new Point(MAZE_SIZE - 2, MAZE_SIZE - 2);
        maze[startPoint.x][startPoint.y] = START;
        maze[endPoint.x][endPoint.y] = END;

        buildGraph();      // build the graph for pathfinding

        statusLabel.setText("New maze generated! Set start/end points and solve.");
        scoreLabel.setText("Steps: 0 | Time: 0ms");
        mazePanel.repaint();
    }

    /**
     * Recursively carve out maze paths using randomized DFS (backtracking).
     */
    private void generateMazeRecursive(int x, int y) {
        maze[x][y] = PATH;
        // Directions for carving out
        List<int[]> directions = Arrays.asList(
            new int[]{0, 2}, new int[]{2, 0}, new int[]{0, -2}, new int[]{-2, 0});
        Collections.shuffle(directions);  // Random order for colorful variety

        for (int[] dir : directions) {
            int newX = x + dir[0];
            int newY = y + dir[1];

            if (isValidCell(newX, newY) && maze[newX][newY] == WALL) {
                // Remove wall between current cell and neighbor
                maze[x + dir[0] / 2][y + dir[1] / 2] = PATH;
                generateMazeRecursive(newX, newY);
            }
        }
    }

    /**
     * Build an adjacency list graph out of walkable cells for pathfinding.
     */
    private void buildGraph() {
        graph.clear();

        for (int i = 0; i < MAZE_SIZE; i++) {
            for (int j = 0; j < MAZE_SIZE; j++) {
                if (maze[i][j] != WALL) {
                    Point current = new Point(i, j);
                    List<Point> neighbors = new ArrayList<>();

                    int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
                    for (int[] dir : directions) {
                        int newX = i + dir[0];
                        int newY = j + dir[1];
                        if (isValidCell(newX, newY) && maze[newX][newY] != WALL) {
                            neighbors.add(new Point(newX, newY));
                        }
                    }
                    graph.put(current, neighbors);
                }
            }
        }
    }

    /**
     * Helper: Returns true if (x, y) is within the maze grid bounds.
     */
    private boolean isValidCell(int x, int y) {
        return x >= 0 && x < MAZE_SIZE && y >= 0 && y < MAZE_SIZE;
    }

    /**
     * User triggers this method to solve the maze in colorful style.
     * @param useDFS true for DFS, false for BFS
     */
    private void solveMaze(boolean useDFS) {
        if (startPoint == null || endPoint == null) {
            JOptionPane.showMessageDialog(this, "Please set both start and end points!");
            return;
        }

        clearSolution();  // Remove previous path visuals
        startTime = System.currentTimeMillis();
        steps = 0;

        // Colorful status feedback to user
        if (useDFS) {
            statusLabel.setText("Solving with DFS (Depth-First Search)...");
            solveDFS();
        } else {
            statusLabel.setText("Solving with BFS (Breadth-First Search)...");
            solveBFS();
        }
    }

    /**
     * Solve maze using DFS with a stack. Colors dots as it goes.
     */
    private void solveDFS() {
        Stack<Point> stack = new Stack<>();
        Set<Point> visited = new HashSet<>();
        Map<Point, Point> parent = new HashMap<>();

        stack.push(startPoint);
        visited.add(startPoint);

        List<Point> explorationPath = new ArrayList<>();

        while (!stack.isEmpty()) {
            Point current = stack.pop();
            explorationPath.add(current);

            if (current.equals(endPoint)) {
                List<Point> solutionPath = reconstructPath(parent, startPoint, endPoint);
                animateSolution(explorationPath, solutionPath, "DFS");
                return;
            }

            List<Point> neighbors = graph.get(current);
            if (neighbors != null) {
                for (Point neighbor : neighbors) {
                    if (!visited.contains(neighbor)) {
                        visited.add(neighbor);
                        parent.put(neighbor, current);
                        stack.push(neighbor);
                    }
                }
            }
        }

        statusLabel.setText("No solution exists!");
        JOptionPane.showMessageDialog(this, "No path found from start to end!");
    }

    /**
     * Solve maze using BFS with a queue. Colors dots as it explores.
     */
    private void solveBFS() {
        Queue<Point> queue = new LinkedList<>();
        Set<Point> visited = new HashSet<>();
        Map<Point, Point> parent = new HashMap<>();

        queue.offer(startPoint);
        visited.add(startPoint);

        List<Point> explorationPath = new ArrayList<>();

        while (!queue.isEmpty()) {
            Point current = queue.poll();
            explorationPath.add(current);

            if (current.equals(endPoint)) {
                List<Point> solutionPath = reconstructPath(parent, startPoint, endPoint);
                animateSolution(explorationPath, solutionPath, "BFS");
                return;
            }

            List<Point> neighbors = graph.get(current);
            if (neighbors != null) {
                for (Point neighbor : neighbors) {
                    if (!visited.contains(neighbor)) {
                        visited.add(neighbor);
                        parent.put(neighbor, current);
                        queue.offer(neighbor);
                    }
                }
            }
        }

        statusLabel.setText("No solution exists!");
        JOptionPane.showMessageDialog(this, "No path found from start to end!");
    }

    /**
     * Reconstructs the solution path using parent references.
     */
    private List<Point> reconstructPath(Map<Point, Point> parent, Point start, Point end) {
        List<Point> path = new ArrayList<>();
        Point current = end;

        while (current != null) {
            path.add(current);
            current = parent.get(current);
        }

        Collections.reverse(path);
        return path;
    }

    /**
     * Animated path visualization: colors exploration and then solution path.
     */
    private void animateSolution(List<Point> explorationPath, List<Point> solutionPath, String algorithm) {
        final int[] index = {0};
        final boolean[] showingExploration = {true};

        animationTimer = new Timer(60, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (showingExploration[0]) {
                    if (index[0] < explorationPath.size()) {
                        Point p = explorationPath.get(index[0]);
                        if (!p.equals(startPoint) && !p.equals(endPoint)) {
                            maze[p.x][p.y] = EXPLORING;      // Cyan
                        }
                        steps++;
                        index[0]++;
                        mazePanel.repaint();

                        long currentTime = System.currentTimeMillis();
                        scoreLabel.setText("Steps: " + steps + " | Time: " + (currentTime - startTime) + "ms");
                    } else {
                        showingExploration[0] = false;
                        index[0] = 0;

                        // Make all explored dots orange
                        for (Point p : explorationPath) {
                            if (!p.equals(startPoint) && !p.equals(endPoint)) {
                                maze[p.x][p.y] = VISITED;
                            }
                        }
                    }
                } else {
                    if (index[0] < solutionPath.size()) {
                        Point p = solutionPath.get(index[0]);
                        if (!p.equals(startPoint) && !p.equals(endPoint)) {
                            maze[p.x][p.y] = SOLUTION;       // Bright purple
                        }
                        index[0]++;
                        mazePanel.repaint();
                    } else {
                        animationTimer.stop();
                        long totalTime = System.currentTimeMillis() - startTime;
                        statusLabel.setText(algorithm + " completed! Path found in " +
                                solutionPath.size() + " steps.");
                        scoreLabel.setText("Explored: " + explorationPath.size() +
                                " | Solution: " + solutionPath.size() +
                                " | Time: " + totalTime + "ms");

                        JOptionPane.showMessageDialog(MazeSolver.this,
                                algorithm + " Solution Found!\n" +
                                        "Exploration steps: " + explorationPath.size() + "\n" +
                                        "Solution path length: " + solutionPath.size() + "\n" +
                                        "Time taken: " + totalTime + "ms");
                    }
                }
            }
        });

        animationTimer.start();
    }

    /**
     * Remove pathfinding visualizations, restores color states.
     */
    private void clearSolution() {
        if (animationTimer != null && animationTimer.isRunning()) {
            animationTimer.stop();
        }

        for (int i = 0; i < MAZE_SIZE; i++) {
            for (int j = 0; j < MAZE_SIZE; j++) {
                if (maze[i][j] == VISITED || maze[i][j] == SOLUTION || maze[i][j] == EXPLORING) {
                    maze[i][j] = PATH;
                }
            }
        }

        // Restore colorful start and end dots
        if (startPoint != null) maze[startPoint.x][startPoint.y] = START;
        if (endPoint != null) maze[endPoint.x][endPoint.y] = END;

        statusLabel.setText("Path cleared. Ready to solve again.");
        scoreLabel.setText("Steps: 0 | Time: 0ms");
        mazePanel.repaint();
    }

    /**
     * MazePanel is a custom JPanel subclass responsible for drawing the maze grid in color.
     */
    private class MazePanel extends JPanel {
        public MazePanel() {
            setPreferredSize(new Dimension(MAZE_SIZE * CELL_SIZE, MAZE_SIZE * CELL_SIZE));
            setBackground(new Color(186, 225, 255));  // Light turquoise canvas

            // Mouse click to set start or endpoint, using color feedback
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    int x = e.getY() / CELL_SIZE;
                    int y = e.getX() / CELL_SIZE;

                    if (isValidCell(x, y) && maze[x][y] != WALL) {
                        if (isSettingStart) {
                            if (startPoint != null) {
                                maze[startPoint.x][startPoint.y] = PATH;
                            }
                            startPoint = new Point(x, y);
                            maze[x][y] = START;
                            isSettingStart = false;
                            statusLabel.setText("Start point set at (" + x + ", " + y + ")");
                            buildGraph();  // Update graph for new node
                        } else if (isSettingEnd) {
                            if (endPoint != null) {
                                maze[endPoint.x][endPoint.y] = PATH;
                            }
                            endPoint = new Point(x, y);
                            maze[x][y] = END;
                            isSettingEnd = false;
                            statusLabel.setText("End point set at (" + x + ", " + y + ")");
                            buildGraph();
                        }
                        repaint();
                    }
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            // Draw cells with their color states for a vibrant look
            for (int i = 0; i < MAZE_SIZE; i++) {
                for (int j = 0; j < MAZE_SIZE; j++) {
                    Color cellColor = getCellColor(maze[i][j]);
                    g.setColor(cellColor);
                    g.fillRect(j * CELL_SIZE, i * CELL_SIZE, CELL_SIZE, CELL_SIZE);

                    // Draw grid lines in vivid red (not black or brown)
                    g.setColor(GRID_LINE_COLOR);
                    g.drawRect(j * CELL_SIZE, i * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                }
            }
        }

        /**
         * Returns the color corresponding to a maze cell type.
         */
        private Color getCellColor(int cellType) {
            switch (cellType) {
                case WALL:      return WALL_COLOR;
                case PATH:      return PATH_COLOR;
                case START:     return START_COLOR;
                case END:       return END_COLOR;
                case VISITED:   return VISITED_COLOR;
                case SOLUTION:  return SOLUTION_COLOR;
                case EXPLORING: return EXPLORING_COLOR;
                default:        return PATH_COLOR;
            }
        }
    }

    /**
     * Main method to launch the MazeSolver application.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Use native platform look and feel
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

            } catch (Exception e) {
                e.printStackTrace();
            }
            new MazeSolver().setVisible(true);
        });
    }
}
