package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

public class Wall extends Component {
    private static final TETile R = Tileset.WALL;
    private int id;
    public Wall(Position position, Space parent) {
        super(position, parent);
        this.setRep(R);
        this.id = parent.world().walls().size();
        parent.world().addWall(this);
    }
    public int id() {
        return id;
    }
    //provides an initial direction or null for a proposed door
    //gives a simple directional nod if there is a single adjoining floor
    // and if that single floor is behind you
    //does not guarantee safety
    public Ut.Direction doorDirection() {
        //we will test each direction, starting with north
        Ut.Direction currentDirection = Ut.Direction.NORTH;
        Position p = this.position();
        World w = parent().world();
        //initialize a loop that will run a maximum of four times for four directions
        for (int i = 0; i < 4; i++) {
            Position front = p.moveDirection(currentDirection); //look ahead
            Position back = p.moveDirection(Ut.inverse(currentDirection)); //look behind
            Position left = p.moveDirection(Ut.counterclock(currentDirection)); //look left
            Position right = p.moveDirection(Ut.clockwise(currentDirection)); //look right
            //check for out of bounds first so later calls to components don't error out
            if (front.outOfBounds() || back.outOfBounds()
                    || left.outOfBounds() || right.outOfBounds()) {
                return null;
            }
            Component frontC = w.getComponentByPosition(front);
            Component backC = w.getComponentByPosition(back);
            //if the thing behind you isn't a floor, we should stop
            Component leftC = w.getComponentByPosition(left);
            Component rightC = w.getComponentByPosition(right);
            Component[] components = {frontC, backC, leftC, rightC};
            int floorCount = 0;
            for (Component c : components) {
                if (c instanceof Floor) {
                    floorCount += 1;
                }
            }
            //if you only have 1 adjacent floor, this direction might work
            if (floorCount == 1 && backC instanceof Floor) {
                return currentDirection;
            }
            //try it in a clockwise direction if you fail
            currentDirection = Ut.clockwise(currentDirection);
        }
        //if you've tried everything and haven't found something good return null
        return null;
    }
    /*
    probe for the max span of a hall (which can be two)
    results:
    2 = max width of 2
    1 = max width of 1
    0 = no hallway possible
    -1 = direct door to another space
    -2 = double doors to another space
     */

    public int probeForDoor(Ut.Direction d, int span) {
        if (span == 0) {
            return 0;
        }
        //check where you are first
        Position origin = position();
        Position left = origin.moveDirection(Ut.counterclock(d));
        Position right = origin.moveDirection(Ut.clockwise(d));
        if (left.outOfBounds() || right.outOfBounds()) {
            return 0;
        }
        if (!world().getComponentByPosition(left).wall()
                || !world().getComponentByPosition(right).wall()) {
            return 0;
        }
        //move ahead
        Position ahead = origin.moveDirection(d);
        Position leftAhead = ahead.moveDirection(Ut.counterclock(d));
        Position rightAhead = ahead.moveDirection(Ut.clockwise(d));
        if (ahead.outOfBounds()
                || leftAhead.outOfBounds()
                || rightAhead.outOfBounds()) {
            return 0;
        }
        if (!world().getComponentByPosition(ahead).wall()
                || !world().getComponentByPosition(leftAhead).wall()
                || !world().getComponentByPosition(rightAhead).wall()) {
            Component c1 = world().getComponentByPosition(ahead);
            Component c2 = world().getComponentByPosition(leftAhead);
            Component c3 = world().getComponentByPosition(rightAhead);
            return 0;
        }
        Wall nextWall = (Wall) world().getComponentByPosition(right);
        return 1 + nextWall.probeForDoor(d, span - 1);
    }

    public int probeForDoor2(Ut.Direction d, int span) {
        Position origin = position();
        Position behind = origin.moveDirection(Ut.inverse(d));
        Position front = origin.moveDirection(d);
        Position frontTwo = front.moveDirection(d);
        World w = parent().world();
        //check out-of-bounds first
        if (origin.outOfBounds()
                || front.outOfBounds()
                || frontTwo.outOfBounds()) {
            return 0;
        } else {
            //check if it's a wall and a floor
            Component behindC = w.getComponentByPosition(behind);
            Component originC = w.getComponentByPosition(origin);
            Component frontC = w.getComponentByPosition(front);
            Component frontTwoC = w.getComponentByPosition(frontTwo);
            if (!(behindC.floor() && (originC.wall()
                    || originC.door())
                    && (frontC.wall()
                    || frontC.door())
                    && frontTwoC.floor())) {
                return 0;
            }
            //now check if the next rung over is a wall and recurse if you must
            Position next = origin.moveDirection(Ut.clockwise(d));
            //handles base case or out of bounds case before recursing
            if (next.outOfBounds()
                    || span == 1
                    || !(w.getComponentByPosition(next).wall())) {
                return 1;
            }
            Wall nextWall = (Wall) w.getComponentByPosition(next);
            return 1 + nextWall.probeForDoor(d, span - 1);
        }
    }
    public boolean isCorner() {
        Position curr = this.position();
        for (Ut.Direction d: Ut.Direction.values()) {
            Position probe = curr.moveDirection(d);
            if (probe.outOfBounds()) {
                continue;
            }
            Component c = world().getComponentByPosition(probe);
            if (c instanceof Floor) {
                return false;
            }
        }
        return true;
    }
}
