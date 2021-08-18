import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;

public class GridGUI extends Frame {

    // The canvas to draw the grid on
    private GridCanvas gridCanvas;

    /**
     * A constructor to build a new GridGUI Frame
     * @param gridWidth - the width of the grid
     * @param gridHeight - the height of the grid
     */
    public GridGUI(int gridWidth, int gridHeight) {
        // Initialize the grid canvas
        this.gridCanvas = new GridCanvas(gridWidth, gridHeight);
        this.add(this.gridCanvas);
        // Create listener for close button
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });
        // Set window size
        this.setSize(GridConstants.tileWidth * gridWidth, GridConstants.tileHeight * gridHeight);
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
        private Grid grid;

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
            // Initialize the grid
            this.grid = new Grid(gridWidth, gridHeight);
            // Set drawing option to wall (default)
            this.currentlyDrawing = GridConstants.TILE_TYPES.WALL;
            // Add mouse listener for user mouse clicks to draw
            this.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    // scale coordinates
                    int x = (e.getX() / GridConstants.tileWidth) * GridConstants.tileWidth + 1;
                    int y = (e.getY() / GridConstants.tileHeight) * GridConstants.tileHeight + 1;
                    // create a new point
                    Point key = new Point(x, y);
                    // update dictionary accordingly
                    if (tiles.containsKey(key))
                        tiles.remove(key);
                    else
                        tiles.put(key, currentlyDrawing);
                    // update canvas
                    repaint();
                }
            });
        }

        @Override
        public void paint(Graphics g) {
            // The grid limits
            int maxHeight = GridConstants.tileHeight * this.gridHeight;
            int maxWidth = GridConstants.tileWidth * this.gridWidth;
            // How many lines to draw in each direction to create grid pattern
            int verticalLineCount = this.gridWidth + 1;
            int horizontalLineCount = this.gridHeight + 1;
            // Draw vertical lines
            for (int i = 0; i < verticalLineCount; i++) {
                int x = GridConstants.tileWidth * i;
                g.drawLine(x, 0, x, maxHeight);
            }
            // Draw horizontal lines
            for (int i = 0; i < horizontalLineCount; i++) {
                int y = GridConstants.tileHeight * i;
                g.drawLine(0, y, maxWidth, y);
            }
            // Check the tile map if any tile is present on the grid
            for (Point p : this.tiles.keySet()) {
                // If yes color that tile accordingly
                switch (this.tiles.get(p)) {
                    case WALL -> g.setColor(GridConstants.tileColors.get(GridConstants.TILE_TYPES.WALL));
                    case SOURCE -> g.setColor(GridConstants.tileColors.get(GridConstants.TILE_TYPES.SOURCE));
                    case DESTINATION -> g.setColor(GridConstants.tileColors.get(GridConstants.TILE_TYPES.DESTINATION));
                    case VISITED -> g.setColor(GridConstants.tileColors.get(GridConstants.TILE_TYPES.VISITED));
                    case PATH -> g.setColor(GridConstants.tileColors.get(GridConstants.TILE_TYPES.PATH));
                }
                g.fillRect((int)p.getX(), (int)p.getY(), GridConstants.tileWidth - 1, GridConstants.tileHeight - 1);
            }
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

    }

}
