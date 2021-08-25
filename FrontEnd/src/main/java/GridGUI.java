import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.HashMap;
import java.util.Iterator;
import java.util.function.Consumer;

public class GridGUI extends Panel {

    private static final int width = 600;
    private static final int height = 600;

    // The canvas to draw the grid on
    private final GridCanvas gridCanvas;

    /**
     * A constructor to build a new GridGUI Frame
     * @param gridWidth - the width of the grid
     * @param gridHeight - the height of the grid
     */
    public GridGUI(int gridWidth, int gridHeight) {
        // Initialize the grid canvas
        this.gridCanvas = new GridCanvas(gridWidth, gridHeight);
        this.gridCanvas.setSize((GridConstants.tileWidth + 1) * gridWidth + 1, (GridConstants.tileHeight + 1) * gridHeight + 1);
        // Create scroll pane
        ScrollPane scrollGrid = new ScrollPane();
        scrollGrid.setSize(width, height);
        // Disable mouse wheel scrolling
        scrollGrid.setWheelScrollingEnabled(false);
        // Add the canvas
        scrollGrid.add(this.gridCanvas);
        this.add(scrollGrid);
        // Create algorithm dropdown menu
        JComboBox<String> algorithmsMenu = new JComboBox<>();
        algorithmsMenu.addItem("AStar");
        algorithmsMenu.addItem("Dijkstra");
        algorithmsMenu.addItem("DFS");
        algorithmsMenu.addItem("BFS");
        this.add(algorithmsMenu);
        // Create control buttons
        Button solveBtn = new Button("solve");
        Button genMazeBtn = new Button("generate maze");
        Button clearBtn = new Button("clear grid");
        // Add button functions
        solveBtn.addActionListener(e -> {
            int index = algorithmsMenu.getSelectedIndex();
            if (index == 0)
                this.gridCanvas.solve(PathfindingAlgorithms::AStar);
            else if (index == 1)
                this.gridCanvas.solve(PathfindingAlgorithms::Dijkstra);
            else if (index == 2)
                this.gridCanvas.solve(PathfindingAlgorithms::DFS);
            else if (index == 3)
                this.gridCanvas.solve(PathfindingAlgorithms::BFS);
        });
        genMazeBtn.addActionListener(e -> {
            this.gridCanvas.generateMaze();
        });
        clearBtn.addActionListener(e -> {
            this.gridCanvas.clear();
        });

        Label label = new Label("Currently drawing:");
        JComboBox<String> drawSelection = new JComboBox<>();
        drawSelection.addItem("Wall");
        drawSelection.addItem("Source");
        drawSelection.addItem("Destination");

        drawSelection.addActionListener(e -> {
            int index = drawSelection.getSelectedIndex();
            if (index == 0)
                this.gridCanvas.setCurrentlyDrawing(GridConstants.TILE_TYPES.WALL);
            else if (index == 1)
                this.gridCanvas.setCurrentlyDrawing(GridConstants.TILE_TYPES.SOURCE);
            else if (index == 2)
                this.gridCanvas.setCurrentlyDrawing(GridConstants.TILE_TYPES.DESTINATION);
        });

        // Add buttons to panel
        Panel controlPanel = new Panel();
        controlPanel.add(solveBtn);
        controlPanel.add(genMazeBtn);
        controlPanel.add(clearBtn);
        controlPanel.add(label);
        controlPanel.add(drawSelection);
        // Configure grid layout and add to main gui
        controlPanel.setLayout(new GridLayout(2, 3));
        this.add(controlPanel);
        // Set layout as grid
        this.setLayout(new GridLayout(3, 1));
        // Bind mouse wheel to zoom
        this.addMouseWheelListener(new MouseAdapter() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                gridCanvas.updateZoom(((int)-e.getPreciseWheelRotation()) * 0.1);
            }
        });
        // Set window size
        this.setSize(width, height);
    }

    /**
     * A method to create a maze on the grid and display it on the canvas
     */
    public void generateMaze() {
        this.gridCanvas.generateMaze();
    }

    /**
     * A class to handle drawing the grid on a canvas
     */
    private static class GridCanvas extends Canvas {

        // The grid this ui is showing
        private final Grid grid;

        // The zoom level
        private double zoom;

        // Timer for animations
        private Timer timer;

        // Grid width and height
        private final int gridWidth;
        private final int gridHeight;

        // current selected drawing option
        private GridConstants.TILE_TYPES currentlyDrawing;

        // a map of tiles that are currently present on the grid
        private HashMap<Point, GridConstants.TILE_TYPES> tiles;

        /**
         * A constructor to create a new grid canvas
         * @param gridWidth - the width of the grid
         * @param gridHeight - the height of the grid
         */
        public GridCanvas(int gridWidth, int gridHeight) {
            // Initialize parameters
            this.gridWidth = gridWidth;
            this.gridHeight = gridHeight;
            this.tiles = new HashMap<>();
            this.zoom = 1;
            // Initialize the grid
            this.grid = new Grid(gridWidth, gridHeight);
            // Set drawing option to wall (default)
            this.currentlyDrawing = GridConstants.TILE_TYPES.WALL;
            // Add mouse listener for user mouse clicks to draw
            this.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    // scale coordinates and create a new point
                    int x = (int) (e.getX() / (GridConstants.tileWidth * zoom));
                    int y = (int) (e.getY() / (GridConstants.tileHeight * zoom));
                    // check point is valid
                    if (grid.isInGrid(x, y)) {
                        Point key = new Point(x * GridConstants.tileWidth + 1, y * GridConstants.tileHeight + 1);
                        // update dictionary accordingly
                        if (tiles.containsKey(key)) {
                            tiles.remove(key);
                            grid.setTileType(x, y, GridConstants.TILE_TYPES.EMPTY);
                        }
                        else {
                            tiles.put(key, currentlyDrawing);
                            grid.setTileType(x, y, currentlyDrawing);
                        }
                        // update canvas
                        repaint();
                    }
                }
            });
        }

        // A method to draw the grid pattern on the canvas
        private void drawGridPattern(Graphics2D g2d) {
            // The grid limits
            int maxHeight = GridConstants.tileHeight * this.gridHeight;
            int maxWidth = GridConstants.tileWidth * this.gridWidth;
            // How many lines to draw in each direction to create grid pattern
            int verticalLineCount = this.gridWidth + 1;
            int horizontalLineCount = this.gridHeight + 1;
            // Draw vertical lines
            for (int i = 0; i < verticalLineCount; i++) {
                int x = GridConstants.tileWidth * i;
                g2d.drawLine(x, 0, x, maxHeight);
            }
            // Draw horizontal lines
            for (int i = 0; i < horizontalLineCount; i++) {
                int y = GridConstants.tileHeight * i;
                g2d.drawLine(0, y, maxWidth, y);
            }
        }

        // A method to fill all relevant tiles
        private void fillTiles(Graphics2D g2d) {
            // Check the tile map if any tile is present on the grid
            for (Point p : this.tiles.keySet()) {
                // If yes color that tile accordingly
                switch (this.tiles.get(p)) {
                    case WALL -> g2d.setColor(GridConstants.tileColors.get(GridConstants.TILE_TYPES.WALL));
                    case SOURCE -> g2d.setColor(GridConstants.tileColors.get(GridConstants.TILE_TYPES.SOURCE));
                    case DESTINATION -> g2d.setColor(GridConstants.tileColors.get(GridConstants.TILE_TYPES.DESTINATION));
                    case VISITED -> g2d.setColor(GridConstants.tileColors.get(GridConstants.TILE_TYPES.VISITED));
                    case PATH -> g2d.setColor(GridConstants.tileColors.get(GridConstants.TILE_TYPES.PATH));
                }
                g2d.fillRect((int)p.getX(), (int)p.getY(), GridConstants.tileWidth - 1, GridConstants.tileHeight - 1);
            }
        }

        @Override
        public void paint(Graphics g) {
            // Convert to 2d
            Graphics2D g2d = ((Graphics2D)g);
            // Set zoom scaling
            g2d.scale(zoom, zoom);
            // Draw grid patterns
            this.drawGridPattern(g2d);
            // Fill all relevant tiles
            this.fillTiles(g2d);
        }

        /**
         * A method to change the current drawing mode
         * @param currentlyDrawing - the new drawing mode
         */
        public void setCurrentlyDrawing(GridConstants.TILE_TYPES currentlyDrawing) {
            this.currentlyDrawing = currentlyDrawing;
        }

        // A show the contents of the grid on the canvas
        private void readGrid() {
            // Clear current tiles
            this.tiles = new HashMap<>();
            // Add all current tiles to map
            for (int i = 0; i < this.gridWidth; i++)
                for (int j = 0; j < this.gridHeight; j++) {
                    GridConstants.TILE_TYPES tile_type = GridConstants.TILE_TYPES.valueOf(this.grid.getTileType(i, j).name());
                    if (tile_type != GridConstants.TILE_TYPES.EMPTY)
                        this.tiles.put(new Point(i * GridConstants.tileWidth + 1, j * GridConstants.tileHeight + 1), tile_type);
                }
            this.repaint();
        }

        /**
         * A method to generate a maze and show it on the canvas
         */
        public void generateMaze() {
            this.grid.generateMaze();
            this.readGrid();
        }

        public void updateZoom(double zoom) {
            this.zoom += zoom;
            if (this.zoom < 0)
                this.zoom = 0;
            this.repaint();
        }

        public void resetZoom() {
            this.zoom = 1;
            this.repaint();
        }

        /**
         * A method to solve the given grid and show a animation of the result
         * @param pathfindingAlgorithm - the algorithm the use
         */
        public void solve(Consumer<Grid> pathfindingAlgorithm) {
            pathfindingAlgorithm.accept(this.grid);
            // get the log of visited notes
            Iterator<GridConstants.Visitor> pointIterator = this.grid.getVisitorLog().iterator();
            // set a timer for every 100 milliseconds
            this.timer =  new Timer(100, e -> {
                // get next visitor
                GridConstants.Visitor visitor = pointIterator.next();
                // get and scale the point to the grid
                Point p = visitor.getPoint();
                p.setLocation(p.getX() * GridConstants.tileWidth + 1, p.getY() * GridConstants.tileHeight + 1);
                // add to tile map
                tiles.put(p, visitor.getType());
                // update canvas
                repaint();
                // restart timer if needed
                if (pointIterator.hasNext())
                    timer.restart();
                else timer.stop();
            });
            //start the timer
            this.timer.start();

        }

        /**
         * A method completely clean the grid
         */
        public void clear() {
            this.grid.clearGrid();
            this.readGrid();
        }

        /**
         * A method to clear only path and visited markings of the grid
         */
        public void clearMarkings() {
            this.grid.clearMarkings();
            this.readGrid();
        }

    }

}
