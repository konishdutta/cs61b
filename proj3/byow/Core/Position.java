package byow.Core;

import byow.TileEngine.Tileset;


public class Position {
    private int x;
    private int y;
    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }
    public int x() {
        return x;
    }
    public int y() {
        return y;
    }
    //directional pivots
    public Position north() {
        Position res = new Position(x, y + 1);
        return res;
    }
    public Position south() {
        Position res = new Position(x, y - 1);
        return res;
    }
    public Position east() {
        Position res = new Position(x - 1, y);
        return res;
    }
    public Position west() {
        Position res = new Position(x + 1, y);
        return res;
    }

    public Position moveDirection(Ut.Direction d) {
        Position res = null;
        switch (d) {
            case NORTH: res = new Position(x, y + 1);
            break;
            case SOUTH: res = new Position(x, y - 1);
            break;
            case EAST: res = new Position(x + 1, y);
            break;
            case WEST: res = new Position(x - 1, y);
            break;
            default: break;
        }
        return res;
    }

    public boolean outOfBounds() {
        return x >= Engine.WIDTH
                || y >= Engine.HEIGHT
                || x < 0 || y < 0;
    }

    public double calculateDistance(Position other) {
        int x1 = this.x;
        int y1 = this.y;
        int x2 = other.x;
        int y2 = other.y;
        return Math.sqrt(Math.pow((x2 - x1), 2) + Math.pow((y2 - y1), 2));
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    @Override
    public boolean equals(Object c) {
        if (c == null) {
            return false;
        } else if (c.getClass() != this.getClass()) {
            return false;
        } else {
            Position comp = (Position) c;
            return comp.x == this.x && comp.y == this.y;
        }
    }
    @Override
    public int hashCode() {
        return x * 100 + y;
    }
}
