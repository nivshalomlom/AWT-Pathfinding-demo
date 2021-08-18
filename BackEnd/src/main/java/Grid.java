import java.util.LinkedList;

/**
 * A class to represent the grid the pathfinding will take place on
 */
public class Grid {

    // the grid we will use for path finding
    private GridConstants.TILE_TYPES[][] grid;

    // source and destination points
    private int[] source;
    private int[] destination;

    // log of actions taken in this grid
    private LinkedList<String> actionLog;

    /**
     * A constructor to create a new grid of given dimensions
     * @param width - the desired width of the grid
     * @param height - the desired height of the grid
     */
    public Grid(int width, int height) {
        setupGrid(width, height);
    }

    /**
     * A method to get the width of the grid
     * @return the width of the grid
     */
    public int getWidth() {
        return this.grid.length;
    }

    /**
     * A method to get the width of the grid
     * @return the width of the grid
     */
    public int getHeight() {
        return this.grid[0].length;
    }

    /**
     * A method to check if a given set of coordinates is in the grid
     * @param x - the width index
     * @param y - the height index
     * @return true if (x, y) is in the grid, false otherwise
     */
    public boolean isInGrid(int x, int y) {
        if (x < 0 || x >= this.getWidth())
            return false;
        return y >= 0 && y < this.getHeight();
    }

    /**
     * Sets a tile in a given set of coordinates to a specific type <br>
     * (every tile's default value if empty)
     * @param x - the width index
     * @param y - the height index
     * @param tile_type - the type to set the tile to
     * @throws IndexOutOfBoundsException - if the given point (x, y) is not in the grid
     */
    public void setTileType(int x, int y, GridConstants.TILE_TYPES tile_type) throws IndexOutOfBoundsException {
        if (!this.isInGrid(x, y))
            throw new IndexOutOfBoundsException("(" + x + ", " + y + ") is not in the grid!");
        this.grid[x][y] = tile_type;
        this.actionLog.add(x + "," + y + "," + tile_type);
    }

    /**
     * Returns the type of the cell in the given set of coordinates
     * @param x - the width index
     * @param y - the height index
     * @return the type of the cell in the (x, y) location in the grid
     * @throws IndexOutOfBoundsException - if the given point (x, y) is not in the grid
     */
    public GridConstants.TILE_TYPES getTileType(int x, int y) {
        if (!this.isInGrid(x, y))
            throw new IndexOutOfBoundsException("(" + x + ", " + y + ") is not in the grid!");
        return this.grid[x][y];
    }

    /**
     * A method to reset the grid
     */
    private void setupGrid(int width, int height) {
        // create a new grid and fill it with empty tiles
        this.grid = new GridConstants.TILE_TYPES[width][height];
        for (int i = 0; i < this.getWidth(); i++)
            for (int j = 0; j < this.getHeight(); j++)
                this.grid[i][j] = GridConstants.TILE_TYPES.EMPTY;
        // delete source and destination
        this.source = null;
        this.destination = null;
        // reset log
        this.actionLog = new LinkedList<>();
    }

    /**
     * A method to return the source point that the pathfinding algorithm will start from
     * @return the source(start point) for the pathfinding algorithm
     */
    public int[] getSource() {
        return source;
    }

    /**
     * A method set return the source point that the pathfinding algorithm will start from
     * @param x - start width coordinate for the pathfinding algorithm
     * @param y - start height coordinate for the pathfinding algorithm
     */
    public void setSource(int x, int y) {
        this.source = new int[] {x, y};
        this.setTileType(x, y, GridConstants.TILE_TYPES.SOURCE);
    }

    /**
     * A method to return the destination point that the pathfinding algorithm will try to reach
     * @return the destination(end point) for the pathfinding algorithm
     */
    public int[] getDestination() {
        return destination;
    }

    /**
     * A method set return the destination point that the pathfinding algorithm will try to reach
     * @param x - end width coordinate for the pathfinding algorithm
     * @param y - end height coordinate for the pathfinding algorithm
     */
    public void setDestination(int x, int y) {
        this.destination = new int[] {x, y};
        this.setTileType(x, y, GridConstants.TILE_TYPES.DESTINATION);
    }

    /**
     * A method to clear any path markings from the grid
     */
    public void clearMarkings() {
        for (int i = 0; i < this.grid.length; i++)
            for (int j = 0; j < this.grid[0].length; j++)
                if (this.grid[i][j] == GridConstants.TILE_TYPES.PATH || this.grid[i][j] == GridConstants.TILE_TYPES.VISITED)
                    this.grid[i][j] = GridConstants.TILE_TYPES.EMPTY;
    }

    /**
     * A method to reset the grid to a empty state
     */
    public void clearGrid() {
        this.setupGrid(this.grid.length, this.grid[0].length);
    }

    // A method to get neighboring vertexes of a given vertex
    private LinkedList<int[]> getNeighbourWalls(int[] vertex) {
        LinkedList<int[]> output = new LinkedList<>();
        // Right
        if (vertex[0] + 1 < this.getWidth() && this.getTileType(vertex[0] + 1, vertex[1]) == GridConstants.TILE_TYPES.WALL)
            output.add(new int[] {vertex[0] + 1, vertex[1]});
        // Left
        if (vertex[0] > 0 && this.getTileType(vertex[0] - 1, vertex[1]) == GridConstants.TILE_TYPES.WALL)
            output.add(new int[] {vertex[0] - 1, vertex[1]});
        // Up
        if (vertex[1] + 1 < this.getHeight() && this.getTileType(vertex[0], vertex[1] + 1) == GridConstants.TILE_TYPES.WALL)
            output.add(new int[] {vertex[0], vertex[1] + 1});
        // Down
        if (vertex[1] > 0 && this.getTileType(vertex[0], vertex[1] - 1) == GridConstants.TILE_TYPES.WALL)
            output.add(new int[] {vertex[0], vertex[1] - 1});
        return output;
    }

    /**
     * A method to generate a maze on the grid using a version of Prim's algorithm
     */
    public void generateMaze() {
        // fill the grid with walls
        for (int i = 0; i < this.getWidth(); i++)
            for (int j = 0; j < this.getHeight(); j++)
                this.setTileType(i, j, GridConstants.TILE_TYPES.WALL);
        // generate a random starting point and set it to empty
        int[] startPoint = {UtilityMethods.RAND.nextInt(this.getWidth()), UtilityMethods.RAND.nextInt(this.getHeight())};
        this.setTileType(startPoint[0], startPoint[1], GridConstants.TILE_TYPES.EMPTY);
        // create a list of walls and add the starting point's walls to it
        LinkedList<int[]> walls = new LinkedList<>(this.getNeighbourWalls(startPoint));
        // save the last wall that was removed to set as destination
        int[] lastWall = null;
        while (!walls.isEmpty()) {
            // pick a random wall from the list and remove it
            int[] wall = walls.remove(UtilityMethods.RAND.nextInt(walls.size()));
            // check its neighbour tiles
            GridConstants.TILE_TYPES up = wall[1] + 1 < this.getHeight() ? this.getTileType(wall[0], wall[1] + 1) : null;
            GridConstants.TILE_TYPES down = wall[1]> 0 ? this.getTileType(wall[0], wall[1] - 1) : null;
            GridConstants.TILE_TYPES right = wall[0] + 1 < this.getWidth() ? this.getTileType(wall[0] + 1, wall[1]) : null;
            GridConstants.TILE_TYPES left = wall[0]> 0 ? this.getTileType(wall[0] - 1, wall[1]) : null;
            // check if we can create a horizontal path
            if (right != left && (right != null && left != null)) {
                if (this.getTileType(wall[0] + 1, wall[1]) == GridConstants.TILE_TYPES.WALL)
                    walls.addAll(this.getNeighbourWalls(lastWall = new int[] {wall[0] + 1, wall[1]}));
                else walls.addAll(this.getNeighbourWalls(lastWall = new int[] {wall[0] - 1, wall[1]}));

                this.setTileType(wall[0] + 1, wall[1], GridConstants.TILE_TYPES.EMPTY);
                this.setTileType(wall[0] - 1, wall[1], GridConstants.TILE_TYPES.EMPTY);
                this.setTileType(wall[0], wall[1], GridConstants.TILE_TYPES.EMPTY);
            }
            // check if we can create a vertical path
            if (up != down && (up != null && down != null)) {
                if (this.getTileType(wall[0], wall[1] + 1) == GridConstants.TILE_TYPES.WALL)
                    walls.addAll(this.getNeighbourWalls(lastWall = new int[] {wall[0], wall[1] + 1}));
                else walls.addAll(this.getNeighbourWalls(lastWall = new int[] {wall[0], wall[1] - 1}));

                this.setTileType(wall[0], wall[1] + 1, GridConstants.TILE_TYPES.EMPTY);
                this.setTileType(wall[0], wall[1] - 1, GridConstants.TILE_TYPES.EMPTY);
                this.setTileType(wall[0], wall[1], GridConstants.TILE_TYPES.EMPTY);
            }
        }
        // set start point and source and last wall to destination
        this.setSource(startPoint[0], startPoint[1]);
        this.setDestination(lastWall[0], lastWall[1]);
    }

    /**
     * A method to clear the action log
     */
    public void clearActionLog() {
        this.actionLog = new LinkedList<>();
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();
        for (int j = this.getHeight() - 1; j > -1; j--) {
            for (int i = 0; i < this.getWidth(); i++)
                output.append(this.getTileType(i, j)).append(" ");
            output.append("\n");
        }
        return output.toString();
    }
}
