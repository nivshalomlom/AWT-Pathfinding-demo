public class Grid {

    // enum of grid tile types
    public enum TILE_TYPES {
        WALL,
        SOURCE,
        DESTINATION,
        EMPTY
    }

    // the grid we will use for path finding
    private TILE_TYPES[][] grid;

    /**
     * A constructor to create a new grid of given dimensions
     * @param width - the desired width of the grid
     * @param height - the desired height of the grid
     */
    public Grid(int width, int height) {
        resetGrid();
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
     * @param tile_type - the type to set the tile to: WALL/SOURCE/DESTINATION/EMPTY
     */
    public void setTileType(int x, int y, TILE_TYPES tile_type) {
        this.grid[x][y] = tile_type;
    }

    /**
     * A method to reset the grid
     */
    private void resetGrid() {
        // create a new grid and fill it with empty tiles
        this.grid = new TILE_TYPES[this.getWidth()][this.getHeight()];
        for (int i = 0; i < this.getWidth(); i++)
            for (int j = 0; j < this.getHeight(); j++)
                this.grid[i][j] = TILE_TYPES.EMPTY;
    }

}