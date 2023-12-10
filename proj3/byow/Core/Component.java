package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

public class Component {
    private Space parent;
    private Position position;
    private TETile representation;
    private World world;
    public Component(Position position, Space parent, World w) {
        this.parent = parent;
        this.position = position;
        this.world = w;
    }
    public Component(Position position, Space parent) {
        this(position, parent, parent.world());
    }
    public Component(Position position, World w) {
        this(position, null, w);
    }
    public TETile rep() {
        return representation;
    }
    public Space parent() {
        return parent;
    }
    public Position position() {
        return position;
    }

    public void setRep(TETile r) {
        this.representation = r;
    }
    public boolean floor() {
        return this instanceof Floor;
    }
    public boolean wall() {
        return this instanceof Wall;
    }
    public boolean nothing() {
        return this instanceof Nothing;
    }
    public boolean door() {
        return this instanceof Door;
    }
    public World world() {
        return world;
    };
    public int probeForHallSpan(ut.Direction d, int span) {
        World w = world;
        Position origin = position();
        //first check if you should just probe for a door
        Position doorCheck = origin.moveDirection(d);
        if (!doorCheck.outOfBounds() && w.getComponentByPosition(doorCheck).wall() && this.wall()) {
            Wall probewall = (Wall) this;
            //Wall probeWall = (Wall) w.getComponentByPosition(doorCheck);
            return probewall.probeForDoor(d, span) * -1;
            //return -1 so that the upstream command can differentiate walls & doors
        }
        //start from the counter-clockwise direction in front of you and probe for the span that you can go.
        Position curr = origin.moveDirection(d).moveDirection(ut.counterclock(d));
        int res = 0;
        //check that the first two positions are actually empty
        //that's the bare minimum to have a hallway
        for (int i = 0; i < 2; i++) {
            if (curr.outOfBounds() || !w.getComponentByPosition(curr).nothing()) {
                return 0;
            }
            // now you move clockwise to where you're expanding
            curr = curr.moveDirection(ut.clockwise(d));
        }
        //now probe the actual span
        for (int i = 0; i < span; i++) {
            if (curr.outOfBounds() || !w.getComponentByPosition(curr).nothing()) {
                break;
            }
            res += 1;
            curr = curr.moveDirection(ut.clockwise(d));
        }
        return res;
    }
    @Override
    public String toString() {
        return this.getClass().getSimpleName() + " @ " + position.toString();
    }
    public int hashCode() {
        int hash = 0;
        switch(this.getClass().getSimpleName()) {
            case "Nothing": hash = 1;
            break;
            case "Door": hash = 2;
            break;
            case "Floor": hash = 3;
            break;
            case "Wall": hash = 4;
            break;
        }
        return position.x() * 1000 + position.y() * 100 + hash;
    }

    public boolean equals(Object c) {
        if (c == null) {
            return false;
        } else if (c.getClass() != this.getClass()) {
            return false;
        } else {
            Component comp = (Component) c;
            return comp.position().equals(this.position());
        }
    }
}
