package byow.Core;

import byow.TileEngine.Tileset;

public class Space {
    private int height;
    private int width;
    private int id;
    private Position position;
    private World world;

    public Space(World w, int height, int width) {
        if (height != 0) {
            this.id = w.spaceList.size();
            w.spaceList.add(this);
        } else {
            w.proposedDoors.add((Door) this);
        }
        this.height = height;
        this.width = width;
        this.position = null;
        this.world = w;
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
    public boolean checkPosition(Space s, Position p) {
        for (int h = 0; h < this.height; h++) {
            for (int w = 0; w < this.width; w++) {
                if (p.x() + w >= Engine.WIDTH ||
                        p.y() + h >= Engine.HEIGHT ||
                        world.map[p.x() + w][p.y() + h] != Tileset.NOTHING) {
                    return false;
                }
            }
        }
        return true;
    }
    public void draw() {
        Position p = getPosition();
        int x = p.x();
        int y = p.y();
        // Draw everything as a floor
        for (int h = 0; h < this.height(); h++) {
            for (int w = 0; w < this.width(); w++) {
                world().map[x + w][y + h] = Tileset.FLOOR;
            }
        }
        // Draw the walls
        for (int top = 0; top < this.width(); top++) {
            world().map[x + top][y] = Tileset.WALL;
            world().map[x + top][y + this.height() - 1] = Tileset.WALL;
        }
        for (int side = 0; side < this.height(); side++) {
            world().map[x][y + side] = Tileset.WALL;
            world().map[x + this.width() - 1][y + side] = Tileset.WALL;
        }
    }
    public Door findDoor() {
        // finds a random door on some non-corner wall
        Door candidate = null;
        int currX = this.position.x();
        int currY = this.position.y();
        boolean safeDoor = false;
        while (!safeDoor) {
            world.proposedDoors.clear();
            // randomly generate S, N, W, E direction
            int direction = (1 + World.RANDOM.nextInt(2)) * (World.RANDOM.nextBoolean() ? -1 : 1);
            // find some random offset for a non-corner tile
            int randWidth = 1 + World.RANDOM.nextInt(this.width - 3);
            int randHeight = 1 + World.RANDOM.nextInt(this.height - 3);
            Position doorPos = null;
            switch (direction) {
                case -1:
                    doorPos = new Position(currX + randWidth, currY);
                    break;
                case 1:
                    doorPos = new Position(currX + randWidth, currY + this.height - 1);
                    break;
                case -2:
                    doorPos = new Position(currX, currY + randHeight);
                    break;
                case 2:
                    doorPos = new Position(currX + this.width - 1, currY + randHeight);
                    break;
            }
            candidate = new Door(world, doorPos, this, direction);
            safeDoor = candidate.safe();
        }
        boolean doubleDoor = false;
        if (world.proposedDoors.size() > 1) {
            doubleDoor = true;
        }
        world.drawAllDoors();
        if (doubleDoor) {
            return null;
        }
        return candidate;
    }

    public boolean isCorner(Position p) {
        Position bottomLeft = position;
        Position bottomRight = new Position(position.x() + width - 1, position.y());
        Position topLeft = new Position(position.x(), position.y() + height - 1);
        Position topRight = new Position(position.x() + width - 1, position.y() + height - 1);
        if (p.equals(bottomLeft) || p.equals(bottomRight)|| p.equals(topLeft) || p.equals(topRight)) {
            return true;
        }
        return false;
    }
    @Override
    public String toString() {
        return this.getClass().getSimpleName() + " #" +
                id + ": " + width + " x " + height +
                " at " + this.position;
    }
}
