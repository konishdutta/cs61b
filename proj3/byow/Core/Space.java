package byow.Core;
import java.util.*;
import byow.TileEngine.Tileset;

public class Space {
    private int height;
    private int width;
    private int id;
    private Position position;
    private World world;
    private Set<Space> adjSpaces;

    public Space(World w, int height, int width) {
        this.height = height;
        this.width = width;
        this.position = null;
        this.world = w;
        this.adjSpaces = new HashSet<>();
        this.id = w.spaceList.size();
        w.spaceList.add(this);
    }

    public int height() {
        return height;
    }
    public int width() {
        return width;
    }
    public void setHeight(int h) {
        this.height = h;
    }
    public void setWidth(int w) {
        this.width = w;
    }
    public void setPosition(Position p) {
        this.position = p;
    }
    public Position getPosition() {
        return position;
    }
    public void setID(int id) {
        this.id = id;
    }

    public int id() {
        return id;
    }
    public World world() {
        return world;
    }
    public void draw() {
        Position bottomLeft = getPosition();
        int x = bottomLeft.x();
        int y = bottomLeft.y();
        // Draw everything as a floor
        for (int h = 0; h < this.height(); h++) {
            for (int w = 0; w < this.width(); w++) {
                Position curr = new Position(x + w, y + h);
                Floor f = new Floor(curr, this);
                world().build(f);
            }
        }
        // Draw the walls
        for (int top = 0; top < this.width(); top++) {
            Position b = new Position(x + top, y);
            Position t = new Position(x + top, y + this.height() - 1);
            Wall bottomW = new Wall(b, this);
            Wall topW = new Wall(t, this);
            world().build(bottomW);
            world().build(topW);
        }
        for (int side = 0; side < this.height(); side++) {
            Position l = new Position(x, y + side);
            Position r = new Position(x + this.width() - 1, y + side);
            Wall leftW = new Wall(l, this);
            Wall rightW = new Wall(r, this);
            world().build(leftW);
            world().build(rightW);
        }
    }

    public void connect(Space s) {
        s.adjSpaces.add(this);
        this.adjSpaces.add(s);
    }
    public Set<Space> dfs() {
        Set<Space> visited = new HashSet<>();
        dfsHelper(visited);
        return visited;
    }
    private void dfsHelper(Set<Space> visited) {
        if (visited.contains(this)) {
            return;
        }
        visited.add(this);
        for (Space s : adjSpaces) {
            s.dfsHelper(visited);
        }
    }
    public Position getWallStart(ut.Direction d) {
        Position start = null;
        switch(d) {
            case NORTH:
                start = new Position(position.x() + 1, position.y() + height - 1);
                break;
            case SOUTH:
                start = new Position(position.x() + width - 2, position.y());
                break;
            case EAST:
                start = new Position(position.x() + width - 1, position.y() + height - 2);
                break;
            case WEST:
                start = new Position(position.x(), position.y() + 1);
                break;
        }
        return start;
    }

    public Position quickHall(ut.Direction d, Set<Space> visited) {
        int iter = 0;
        switch(d) {
            case NORTH, SOUTH:
                iter = width;
                break;
            case EAST, WEST:
                iter = height;
                break;
        }
        Space currNeighbor = null;
        int count = 0;
        Position currPos = getWallStart(d);
        for (int i = 0; i < iter - 2; i++) {
            Space newNeighbor = world().neighbor(currPos, d);
            if (newNeighbor != null &&
                    newNeighbor.equals(currNeighbor) && !visited.contains(newNeighbor)) {
                count += 1;
            }
            if (count == 2) {
                currPos = currPos.moveDirection(ut.counterclock(d));
                return currPos;
            }
            currNeighbor = newNeighbor;
            currPos = currPos.moveDirection(ut.clockwise(d));
        }
        return null;
    }
    public Position findRandomEmptyPosition() {
        int blX = this.position.x();
        int blY = this.position.y();
        int failOver = 0;
        Position candidatePos = null;
        while (candidatePos == null && failOver < 10) {
            int randX = world().randNum(1, width);
            int randY = world().randNum(1, height);
            candidatePos = new Position(blX + randX, blY + randY);
            if (world.getComponentByPosition(candidatePos) instanceof Floor &&
                    !(world.getComponentByPosition(candidatePos) instanceof LightSource)) {
                return candidatePos;
            }
            failOver += 1;
        }
        return null;
    }
    public Hallway checkWallsForHalls(Set<Space> visited) {
        ut.Direction d = ut.Direction.NORTH;
        for (int i = 0; i < 4; i++) {
            Position p = quickHall(d, visited);
            if (p != null) {
                Wall oldWall = (Wall) world.getComponentByPosition(p);
                Door newDoor = new Door(oldWall);
                Hallway newHall = newDoor.launchHallway(d, 1, Engine.WIDTH);
                return newHall;
            }
            d = ut.clockwise(d);
        }
        return null;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + " #" +
                id + ": " + width + " x " + height +
                " at " + this.position;
    }
    @Override
    public int hashCode() {
        return this.id;
    }
    @Override
    public boolean equals(Object c) {
        if (c == null) {
            return false;
        } else if (c.getClass() != this.getClass()) {
            return false;
        } else {
            Space comp = (Space) c;
            return comp.id == id;
        }
    }
}
