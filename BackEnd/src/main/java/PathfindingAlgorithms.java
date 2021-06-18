import java.util.function.BiFunction;

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
     * @param source - the start/source point to work from
     * @return true if found path, false otherwise (marks path on grid if found)
     */
    public static Boolean DFS_code(Grid grid, int[] source) {
        // if destination is found return true
        if (grid.getTileType(source[0], source[1]) == Grid.TILE_TYPES.DESTINATION)
            return true;
        // else mark tile as visited
        if (grid.getTileType(source[0], source[1]) != Grid.TILE_TYPES.SOURCE)
            grid.setTileType(source[0], source[1], Grid.TILE_TYPES.VISITED);
        // randomize search order
        DIRECTION[] directions = {DIRECTION.UP, DIRECTION.DOWN, DIRECTION.LEFT, DIRECTION.RIGHT};
        UtilityMethods.shuffleArray(directions);
        // check nearby tiles
        for (DIRECTION direction : directions) {
            int[] neighbor = getNeighbor(source, direction);
            // check neighbor tile is traversable
            Grid.TILE_TYPES neighbor_type = grid.getTileType(neighbor[0], neighbor[1]);
            if (!grid.isInGrid(neighbor[0], neighbor[1]) || neighbor_type == Grid.TILE_TYPES.WALL || neighbor_type == Grid.TILE_TYPES.VISITED)
                continue;
            // go to neighbor and return true if destination found
            if (DFS_code(grid, neighbor)) {
                grid.setTileType(source[0], source[1], Grid.TILE_TYPES.PATH);
                return true;
            }
        }
        // return false if dead end found
        return false;
    }

}
