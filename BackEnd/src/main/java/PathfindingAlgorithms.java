import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

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
     */
    public static boolean DFS(Grid grid) {
        if (grid.getSource() == null || grid.getDestination() == null)
            return false;
        else {
            grid.clearVisitorLog();
            return DFS_code(grid, grid.getSource());
        }
    }

    // the algorithm recursive code
    private static boolean DFS_code(Grid grid, int[] source) {
        // if destination is found return true
        if (grid.getTileType(source[0], source[1]) == GridConstants.TILE_TYPES.DESTINATION)
            return true;
        // else mark tile as visited
        if (grid.getTileType(source[0], source[1]) == GridConstants.TILE_TYPES.EMPTY)
            grid.setTileType(source[0], source[1], GridConstants.TILE_TYPES.VISITED);
        // randomize search order
        DIRECTION[] directions = {DIRECTION.UP, DIRECTION.DOWN, DIRECTION.LEFT, DIRECTION.RIGHT};
        UtilityMethods.shuffleArray(directions);
        // check nearby tiles
        for (DIRECTION direction : directions) {
            int[] neighbor = getNeighbor(source, direction);
            // check neighbor tile is traversable
            if (!grid.isInGrid(neighbor[0], neighbor[1]) || (grid.getTileType(neighbor[0], neighbor[1]) != GridConstants.TILE_TYPES.EMPTY && grid.getTileType(neighbor[0], neighbor[1]) != GridConstants.TILE_TYPES.DESTINATION))
                continue;
            // go to neighbor and return true if destination found
            if (DFS_code(grid, neighbor)) {
                if (grid.getTileType(source[0], source[1]) == GridConstants.TILE_TYPES.VISITED)
                    grid.setTileType(source[0], source[1], GridConstants.TILE_TYPES.PATH);
                return true;
            }
        }
        // return false if dead end found
        return false;
    }

    /**
     * A implementation of the 'BFS' pathfinding algorithm
     * @param grid - the grid to work on
     * @return true if found path, false otherwise (marks path on grid if found)
     */
    public static boolean BFS(Grid grid) {
        grid.clearVisitorLog();
        // all possible search directions
        DIRECTION[] directions = {DIRECTION.UP, DIRECTION.DOWN, DIRECTION.LEFT, DIRECTION.RIGHT};
        // empty map for previous vertexes
        int[][][] prev = new int[grid.getWidth()][grid.getHeight()][2];
        // create a vertex queue containing the source vertex
        Queue<int[]> vertexQueue = new LinkedList<>();
        vertexQueue.add(grid.getSource());
        // get the source and vertex
        int[] source = grid.getSource();
        int[] destination = grid.getDestination();
        // check source and destination are defined
        if (source == null || destination == null)
            return false;
        while (!vertexQueue.isEmpty()) {
            int[] current = vertexQueue.poll();
            // check if destination reached
            if (Arrays.equals(current, destination)) {
                // trace path to source
                while (true) {
                    current = prev[current[0]][current[1]];
                    // if path invalid return false
                    if (current == null)
                        return false;
                    // if source found return true
                    if (Arrays.equals(current, source))
                        return true;
                    // mark current cell as visited
                    grid.setTileType(current[0], current[1], GridConstants.TILE_TYPES.PATH);
                }
            }
            // for neighbor of vertex
            for (DIRECTION direction : directions) {
                int[] neighbor = getNeighbor(current, direction);
                // check if neighbor valid
                if (!grid.isInGrid(neighbor[0], neighbor[1]) || grid.getTileType(neighbor[0], neighbor[1]) == GridConstants.TILE_TYPES.WALL || grid.getTileType(neighbor[0], neighbor[1]) == GridConstants.TILE_TYPES.VISITED)
                    continue;
                // add neighbor to queue
                vertexQueue.add(neighbor);
                // set prev of neighbor to current node
                prev[neighbor[0]][neighbor[1]] = current;
                // mark as visited if empty
                if (grid.getTileType(neighbor[0], neighbor[1]) == GridConstants.TILE_TYPES.EMPTY)
                    grid.setTileType(neighbor[0], neighbor[1], GridConstants.TILE_TYPES.VISITED);
            }
        }
        // return false if destination not found
        return false;
    }

    /**
     * A implementation of the 'Dijkstra' pathfinding algorithm
     * @param grid - the grid to work on
     * @return true if found path, false otherwise (marks path on grid if found)
     */
    public static boolean Dijkstra(Grid grid) {
        grid.clearVisitorLog();
        // all possible search directions
        DIRECTION[] directions = {DIRECTION.UP, DIRECTION.DOWN, DIRECTION.LEFT, DIRECTION.RIGHT};
        // vertex set
        HashSet<int[]> vertexes = new HashSet<>();
        // vertex distances
        double[][] distances = new double[grid.getWidth()][grid.getHeight()];
        // empty map for previous vertexes
        int[][][] prev = new int[grid.getWidth()][grid.getHeight()][2];
        // set every vertex's distance to POSITIVE_INFINITY and add it to the vertex set
        for (int i = 0; i < grid.getWidth(); i++)
            for (int j = 0; j < grid.getHeight(); j++) {
                distances[i][j] = Double.POSITIVE_INFINITY;
                if (grid.getTileType(i, j) != GridConstants.TILE_TYPES.WALL)
                    vertexes.add(new int[] {i, j});
            }
        // get the destination and source
        int[] source = grid.getSource();
        int[] destination = grid.getDestination();
        // check source and destination are defined
        if (source == null || destination == null)
            return false;
        // set source distance to 0
        distances[source[0]][source[1]] = 0;
        while (!vertexes.isEmpty()) {
            // find the vertex with the smallest distance
            int[] current = null;
            for (int[] vertex : vertexes)
                if (current == null || distances[vertex[0]][vertex[1]] < distances[current[0]][current[1]])
                    current = vertex;
            // remove it from the set
            vertexes.remove(current);
            // mark as visited if empty
            if (grid.getTileType(current[0], current[1]) == GridConstants.TILE_TYPES.EMPTY)
                grid.setTileType(current[0], current[1], GridConstants.TILE_TYPES.VISITED);
            // check if destination is reached
            if (Arrays.equals(current, destination)) {
                while (true) {
                    current = prev[current[0]][current[1]];
                    // if path stops before source
                    if (current == null)
                        return false;
                    // if source is reached
                    if (Arrays.equals(current, source))
                        return true;
                    // mark vertex as path
                    grid.setTileType(current[0], current[1], GridConstants.TILE_TYPES.PATH);
                }
            }
            // for each neighbor of the current vertex
            for (DIRECTION direction : directions) {
                int[] neighbor = getNeighbor(current, direction);
                // check if its valid
                if (!grid.isInGrid(neighbor[0], neighbor[1]) || grid.getTileType(neighbor[0], neighbor[1]) == GridConstants.TILE_TYPES.WALL)
                    continue;
                // if yes update it's distances if this is a better path
                double alt = distances[current[0]][current[1]] + 1;
                if (alt < distances[neighbor[0]][neighbor[1]]) {
                    distances[neighbor[0]][neighbor[1]] = alt;
                    prev[neighbor[0]][neighbor[1]] = current;
                }
                // mark as visited if empty
                if (grid.getTileType(neighbor[0], neighbor[1]) == GridConstants.TILE_TYPES.EMPTY)
                    grid.setTileType(neighbor[0], neighbor[1], GridConstants.TILE_TYPES.VISITED);
            }
        }
        // if destination is not reached
        return false;
    }

    /**
     * A method to compute the euclidean distance between 2 points
     * @param point1 - the first point
     * @param point2 - the second point
     * @return the distance between them
     */
    private static double distance(int[] point1, int[] point2) {
        int sum = 0;
        for (int i = 0; i < point1.length; i++)
            sum += Math.pow(point1[i] - point2[i], 2);
        return Math.sqrt(sum);
    }

    /**
     * A implementation of the 'A*' (A star) pathfinding algorithm
     * @param grid - the grid to work on
     * @return true if found path, false otherwise (marks path on grid if found)
     */
    public static boolean AStar(Grid grid) {
        grid.clearVisitorLog();
        // all possible search directions
        DIRECTION[] directions = {DIRECTION.UP, DIRECTION.DOWN, DIRECTION.LEFT, DIRECTION.RIGHT};
        // vertex set, initialized with the source
        HashSet<int[]> vertexes = new HashSet<>();
        vertexes.add(grid.getSource());
        // empty map for previous vertexes
        int[][][] prev = new int[grid.getWidth()][grid.getHeight()][2];
        // distance matrices, heuristic and weight based
        double[][] gDistances = new double[grid.getWidth()][grid.getHeight()];
        double[][] fDistances = new double[grid.getWidth()][grid.getHeight()];
        // initialize every cell in the matrices to POSITIVE_INFINITY
        for (int i = 0; i < grid.getWidth(); i++)
            for (int j = 0; j < grid.getHeight(); j++) {
                gDistances[i][j] = Double.POSITIVE_INFINITY;
                fDistances[i][j] = Double.POSITIVE_INFINITY;
            }
        // get the source and destination
        int[] source = grid.getSource();
        int[] destination = grid.getDestination();
        // check source and destination are defined
        if (source == null || destination == null)
            return false;
        // set source distances
        gDistances[source[0]][source[1]] = 0;
        fDistances[source[0]][source[1]] = distance(source, destination);
        while (!vertexes.isEmpty()) {
            // find vertex from vertex set with smallest fDistance
            int[] current = null;
            for (int[] vertex : vertexes)
                if (current == null || fDistances[vertex[0]][vertex[1]] < fDistances[current[0]][current[1]])
                    current = vertex;
            // check if destination is reached
            if (Arrays.equals(current, destination)) {
                while (true) {
                    current = prev[current[0]][current[1]];
                    // if path stops before source
                    if (current == null)
                        return false;
                    // if source is reached
                    if (Arrays.equals(current, source))
                        return true;
                    // mark vertex as path
                    grid.setTileType(current[0], current[1], GridConstants.TILE_TYPES.PATH);
                }
            }
            // remove current vertex from vertex set
            vertexes.remove(current);
            // mark as visited if empty
            if (grid.getTileType(current[0], current[1]) == GridConstants.TILE_TYPES.EMPTY)
                grid.setTileType(current[0], current[1], GridConstants.TILE_TYPES.VISITED);
            // expand current vertex neighbours
            for (DIRECTION direction : directions) {
                int[] neighbor = getNeighbor(current, direction);
                // check if neighbor valid
                if (!grid.isInGrid(neighbor[0], neighbor[1]) || grid.getTileType(neighbor[0], neighbor[1]) == GridConstants.TILE_TYPES.WALL)
                    continue;
                // check if alternative path to neighbor is better
                double alt = gDistances[current[0]][current[1]] + 1;
                if (alt < gDistances[neighbor[0]][neighbor[1]]) {
                    // if yes set prev for the neighbor to this current vertex
                    prev[neighbor[0]][neighbor[1]] = current;
                    // and update distances accordingly
                    gDistances[neighbor[0]][neighbor[1]] = alt;
                    fDistances[neighbor[0]][neighbor[1]] = alt + distance(neighbor, destination);
                    // then add it to the vertex set
                    vertexes.add(neighbor);
                    // mark as visited if empty
                    if (grid.getTileType(current[0], current[1]) == GridConstants.TILE_TYPES.EMPTY)
                        grid.setTileType(current[0], current[1], GridConstants.TILE_TYPES.VISITED);
                }
            }
        }
        // if destination not reached
        return false;
    }

}
