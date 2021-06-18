/**
 * A class that contains implementations of pathfinding algorithms
 */
public class PathfindingAlgorithms {

    // constants
    private enum DIRECTION {
        UP,
        DOWN,
        LEFT,
        RIGHT
    }

    /**
     * A method to get the neighbor of a source from a given direction
     * @param source - the source tile
     * @param direction - the neighbor direction (UP/DOWN/RIGHT/LEFT)
     * @return a array containing the coordinates of the neighbor from the given direction (returns the source if direction is null)
     */
    private static int[] getNeighbor(int[] source, DIRECTION direction)  {
        int[] neighbor = new int[] {source[0], source[1]};
        switch (direction) {
            case RIGHT -> {
                neighbor[0]++;
                return neighbor;
            }
            case LEFT -> {
                neighbor[0]--;
                return neighbor;
            }
            case UP -> {
                neighbor[1]++;
                return neighbor;
            }
            case DOWN -> {
                neighbor[1]--;
                return neighbor;
            }
            default -> {
                return source;
            }
        }
    }

    /**
     * A implementation of the 'depth first search' pathfinding algorithm
     * @param grid - the grid to work on
     * @return true if found path, false otherwise (marks path on grid if found)
     * @throws Exception if source or destination is not defined
     */
    public static Boolean DFS(Grid grid) throws Exception {
        if (grid.getSource() == null || grid.getDestination() == null)
            throw new Exception("Cannot run the DFS algorithm without source and destination points!");
        else return DFS_code(grid, grid.getSource());
    }

    // the algorithm recursive code
    public static Boolean DFS_code(Grid grid, int[] source) {
        // if destination is found return true
        if (grid.getTileType(source[0], source[1]) == Grid.TILE_TYPES.DESTINATION)
            return true;
        // else mark tile as visited
        if (grid.getTileType(source[0], source[1]) == Grid.TILE_TYPES.EMPTY)
            grid.setTileType(source[0], source[1], Grid.TILE_TYPES.VISITED);
        // randomize search order
        DIRECTION[] directions = {DIRECTION.UP, DIRECTION.DOWN, DIRECTION.LEFT, DIRECTION.RIGHT};
        UtilityMethods.shuffleArray(directions);
        // check nearby tiles
        for (DIRECTION direction : directions) {
            int[] neighbor = getNeighbor(source, direction);
            // check neighbor tile is traversable
            if (!grid.isInGrid(neighbor[0], neighbor[1]) || (grid.getTileType(neighbor[0], neighbor[1]) != Grid.TILE_TYPES.EMPTY && grid.getTileType(neighbor[0], neighbor[1]) != Grid.TILE_TYPES.DESTINATION))
                continue;
            // go to neighbor and return true if destination found
            if (DFS_code(grid, neighbor)) {
                if (grid.getTileType(source[0], source[1]) == Grid.TILE_TYPES.VISITED)
                    grid.setTileType(source[0], source[1], Grid.TILE_TYPES.PATH);
                return true;
            }
        }
        // return false if dead end found
        return false;
    }

}
