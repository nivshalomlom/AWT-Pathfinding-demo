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

    /***
     * A class to hold a point and its type, used for transferring the pathfindinf log between the backend and frontend
     */
    public static class Visitor {

        private Point point;
        private GridConstants.TILE_TYPES type;

        /**
         * Creates a new visitor
         * @param point - the point that's been visited
         * @param type - it's type visited/path
         */
        public Visitor(Point point, GridConstants.TILE_TYPES type) {
            this.point = point;
            this.type = type;
        }

        public Point getPoint() {
            return point;
        }

        public void setPoint(Point point) {
            this.point = point;
        }

        public GridConstants.TILE_TYPES getType() {
            return type;
        }

        public void setType(GridConstants.TILE_TYPES type) {
            this.type = type;
        }

    }

}
