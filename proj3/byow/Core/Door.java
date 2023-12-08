package byow.Core;

import byow.TileEngine.Tileset;

public class Door extends Space {
    Space parent;
    int orient;
    public Door(World w, Position p, Space parent, int orient) {
        super(w, 0, 0);
        this.setPosition(p);
        this.parent = parent;
        this.orient = orient;

    }
    public void draw() {
        int x = getPosition().x();
        int y = getPosition().y();
        world().map[x][y] = Tileset.FLOOR;
    }
// Safe will create double doors when it detects them
    public boolean safe() {
        int x = getPosition().x();
        int y = getPosition().y();
        Position check = null;
        boolean sweep = false; //sweep checks if the space above is empty
        switch(Math.abs(orient)) {
            case 1:
                check = new Position(x, y + orient);
                sweep = check.checkRow(1, world());
                break;
            case 2:
                check = new Position(x + (orient / 2), y);
                sweep = check.checkCol(1, world());
                break;
        }
        //check for out of bounds
        if (check.outOfBounds()) {
            return false;
        }
        if (getPosition().isCorner(world(), "default")) {
            return false;
        }
        Space whatsThere = check.returnSpace(world());
        if (whatsThere == null) {
            return sweep;
        }
        //it's not empty so there is a corner or wall. double door if so.
        String direct = "";
        if (Math.abs(orient) == 2) {
            direct = "horizontal";
        } else {
            direct = "vertical";
        }
        if (!check.isCorner(world(), direct)) {
            Door doubleDoor = new Door(world(), check, whatsThere, this.orient * -1);
            System.out.println(world().map);
            return true;
        }
        return false;
    }

    public Space buildHallway(int n) {
        int cornerY = parent.getPosition().y();
        int cornerX = parent.getPosition().x() + parent.width() - 1;
        System.out.println("c:" + cornerY);
        int randWidth;
        // if a door is on the edge, we want to prevent 2-sized halls
        if (this.getPosition().x() + 1 == cornerX || this.getPosition().y() - 1 == cornerY) {
            randWidth = 3;
        } else {
            randWidth = 3 + World.RANDOM.nextInt(2);
        }
        Space hall = null;

        switch(Math.abs(orient)) {
            case 1:
                hall = new VerticalHallway(world(), randWidth, n, orient, this);
                break;
            case 2:
                hall = new HorizontalHallway(world(), randWidth, n, orient / 2, this);
                break;
        }
        return hall;
    }

}
