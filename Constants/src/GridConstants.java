import java.awt.*;
import java.util.Dictionary;
import java.util.Hashtable;

public class GridConstants {

    // each tile's width and height
    public final static int tileWidth = 30;
    public final static int tileHeight = 30;

    // enum of grid tile types
    public enum TILE_TYPES {
        WALL,
        EMPTY,
        VISITED,
        PATH,
        SOURCE,
        DESTINATION
    }

    // the color of each tile type
    public static Dictionary<TILE_TYPES, Color> tileColors = new Hashtable<>() {{
        put(TILE_TYPES.WALL, Color.GRAY);
        put(TILE_TYPES.VISITED, Color.ORANGE);
        put(TILE_TYPES.PATH, Color.BLUE);
        put(TILE_TYPES.SOURCE, Color.GREEN);
        put(TILE_TYPES.DESTINATION, Color.RED);
    }};

}
