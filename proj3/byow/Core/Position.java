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

    public Position moveDirection(ut.Direction d) {
        Position res = null;
        switch(d) {
            case NORTH: res = new Position(x, y + 1);
            break;
            case SOUTH: res = new Position(x, y - 1);
            break;
            case EAST: res = new Position(x + 1, y);
            break;
            case WEST: res = new Position(x - 1, y);
            break;
        }
        return res;
    }

    //give a position and return the room that's there
    public Space returnSpace(World w) {
        if (w.map[x][y] == Tileset.NOTHING) {
            return null;
        }
        for (Space s:w.spaceList) {
            int bot = s.getPosition().y();
            int left = s.getPosition().x();
            int top = bot + s.height() - 1;
            int right = left + s.width() - 1;
            if (y >= bot && y <= top && x >= left && x <= right) {
                return s;
            }
        }
        return null;
    }
    public boolean outOfBounds() {
        return x >= Engine.WIDTH ||
                y >= Engine.HEIGHT ||
                x < 0 ||
                y < 0;
    }
    //Check if the positions row/col is empty and inbounds,
    //treating given position as a midpoint for n iterations
    public boolean checkRow(int n, World w) {
        for (int i = 0; i <= n; i++) {
            Position left = new Position(x - i, y);
            Position right = new Position(x + i, y);
            if (left.outOfBounds() || right.outOfBounds()) {
                return false;
            } else if (left.returnSpace(w) != null || right.returnSpace(w) != null) {
                return false;
            }
        }

        return true;
    }

    public boolean checkCol(int n, World w) {
        for (int i = 0; i <= n; i++) {
            Position bottom = new Position(x, y - i);
            Position top = new Position(x, y + i);
            if (bottom.outOfBounds() || top.outOfBounds()) {
                return false;
            } else if (bottom.returnSpace(w) != null || top.returnSpace(w) != null) {
                return false;
            }
        }
        return true;
    }
    public boolean isCorner(World w, String direction) {
        if (w.map[x][y] != Tileset.WALL) {
            return false;
        }
        int critSum = 0;
        switch(direction) {
            case "vertical":
                if (w.map[x + 1][y] == Tileset.WALL) {
                    critSum += 1;
                }
                if (w.map[x - 1][y] == Tileset.WALL) {
                    critSum += 1;
                }
                break;
            case "horizontal":
                if (w.map[x][y - 1] == Tileset.WALL) {
                    critSum += 1;
                }
                if (w.map[x][y + 1] == Tileset.WALL) {
                    critSum += 1;
                }
                break;
            case "default":
                int vertSum = 0;
                int horSum = 0;
                if (w.map[x][y - 1] == Tileset.WALL) {
                    vertSum += 1;
                }
                if (w.map[x][y + 1] == Tileset.WALL) {
                    vertSum += 1;
                }
                if (w.map[x + 1][y] == Tileset.WALL) {
                    horSum += 1;
                }
                if (w.map[x - 1][y] == Tileset.WALL) {
                    horSum += 1;
                }
                return horSum > 0 && vertSum > 0;
        }
        return critSum == 1;
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
