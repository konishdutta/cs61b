package byow.Core;

public class Hallway extends Room {
    private int size;
    private Ut.Direction direction;
    private int span;
    public Hallway(World w, int span, int size, Ut.Direction direction) {
        super(w, calculateHeight(direction, span, size), calculateWidth(direction, span, size));
        this.direction = direction;
        this.span = span;
        this.size = size;
    }
    private static int calculateWidth(Ut.Direction direction, int span, int size) {
        switch(direction) {
            case NORTH, SOUTH: return span + 2;
            case EAST, WEST: return size;
            default: throw new IllegalArgumentException("Invalid direction");
        }
    }
    private static int calculateHeight(Ut.Direction direction, int span, int size) {
        switch(direction) {
            case NORTH, SOUTH: return size;
            case EAST, WEST: return span + 2;
            default: throw new IllegalArgumentException("Invalid direction");
        }
    }
    @Override
    public void setPosition(Position p) {
        Position bottomLeft = null;
        switch(direction) {
            case NORTH:
                bottomLeft = new Position(p.x() - 1, p.y() - size + 1);
                break;
            case SOUTH:
                bottomLeft = new Position(p.x() - span, p.y());
                break;
            case EAST:
                bottomLeft = new Position(p.x() - size + 1, p.y() - span);
                break;
            case WEST:
                bottomLeft = new Position(p.x(), p.y() - 1);
                break;
        }
        super.setPosition(bottomLeft);
    }
    public void bulldozeForDoors(Position[] starts) {
        //expects a list of 2 positions from returnWallStarts()
        //1st value is a wall that moves in the same direction as the hall
        //2nd is the wall that moves in the reverse direction to blow a door
        for (Position p : starts) {
            Wall currWall = (Wall) world().getComponentByPosition(p);
            int probe = currWall.probeForDoor(direction, span);
            if (probe > 0) {
                Door d = new Door(currWall);
                d.doubleDoor(direction, probe);
            }
        }
    }

    //returns a list of two positions for the start of each wall that could house a door
    public Position[] returnWallStarts() {
        Position botLeft = this.getPosition();
        Position origin = null;
        Position oppWall = null;
        Position[] res;
        switch(direction) {
            case NORTH:
                origin = new Position(botLeft.x() + 1, botLeft.y() - 1);
                oppWall = new Position(botLeft.x() + 1, botLeft.y() + size - 1);
                break;
            case WEST:
                origin = new Position(botLeft.x() + size, botLeft.y() + 1);
                oppWall = new Position(botLeft.x(),botLeft.y() + 1);
                break;
            case SOUTH:
                origin = new Position(botLeft.x() + span, botLeft.y() + size);
                oppWall = new Position(botLeft.x() + span, botLeft.y());
                break;
            case EAST:
                origin = new Position(botLeft.x() - 1, botLeft.y() + span);
                oppWall = new Position(botLeft.x() + size - 1,botLeft.y() + span);
                break;
        }
        res = new Position[]{origin, oppWall};
        return res;
    }

    public Ut.Direction direction() {
        return direction;
    }
    public int span() {
        return span;
    }

}
