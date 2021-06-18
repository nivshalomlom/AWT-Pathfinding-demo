import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class PathfindingAlgorithms {

    // constants
    private static final PathfindingAlgorithms DFS = new PathfindingAlgorithms(PathfindingAlgorithms::DFS_code, "DFS");

    // The algorithm method
    private final BiFunction<Grid, int[], Boolean> algorithm;

    // The algorithm name
    private final String name;

    /**
     * A private constructor to create a new pathfinding algorithm <br>
     * (the constructor is private because this class should be mainly constants)
     * @param algorithm - the code of the algorithm, a method that accepts a grid and path finding in it
     * @param name - the name of the algorithm
     */
    private PathfindingAlgorithms(BiFunction<Grid, int[], Boolean> algorithm, String name) {
        this.algorithm = algorithm;
        this.name = name;
    }

    // TODO find a way to randomize directions efficiently

    private static final List<int[]> DIRECTIONS = Arrays.asList(new int[] {1, 0}, new int[] {-1, 0}, new int[] {0, 1}, new int[] {0, -1});

    private static Boolean DFS_code(Grid grid, int[] source) {
        // check if destination is reached
        int[] destination = grid.getDestination();
        if (source[0] == destination[0] && source[1] == destination[1])
            return true;

        // keep looking
        for (int[] direction : DIRECTIONS) {
            int[] neighbor = new int[] {source[0] + direction[0], source[1] + direction[1]};

            if (!grid.isInGrid(neighbor[0], neighbor[1])
                    || grid.getTileType(neighbor[0], neighbor[1]) == Grid.TILE_TYPES.VISITED
                    || grid.getTileType(neighbor[0], neighbor[1]) == Grid.TILE_TYPES.WALL)
                continue;

            // mark neighbor tile as visited
            grid.setTileType(source[0], source[1], Grid.TILE_TYPES.VISITED);

            // go to neighbor tile
            if (DFS_code(grid, neighbor)) {
                grid.setTileType(neighbor[0], neighbor[1], Grid.TILE_TYPES.PATH);
                return true;
            }
        }
        return false;
    }

}
