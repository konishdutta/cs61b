package byow.Core;

public class VerticalHallway extends Room {
    private int direction;
    public VerticalHallway(World w, int width, int iter, int direction, Door door) {
        super(w, 1, width);
        this.direction = direction;
        int newHeight = probe(width, iter, direction, 0, door.getPosition());
        //try again if your probe fails and you're wide
        if (newHeight == 0 && width == 4) {
            setWidth(3);
            newHeight = probe(width(), iter, direction, 0, door.getPosition());
        }
        this.setHeight(Math.max(height(), newHeight));
        int x = door.getPosition().x() - 1;
        int y = door.getPosition().y() + direction;
        if (direction == -1) {
            y -= (newHeight - 1);
        }
        Position newPosition = new Position(x, y);
        this.setPosition(newPosition);
    }

    public void draw() {
        super.draw();
        if (height() > 1) {
            searchForDoors(); //blow open doors - will create dupe doors for now
        }
    }
    public int probe(int width, int iter, int direction, int currSize, Position currPosition) {
        // start from one to the left
        int x = currPosition.x() - 1;
        int y = currPosition.y() + direction;
        if (iter == 0) {
            return currSize;
        }
        for (int i = 0; i < width; i++) {
            Position candidate = new Position(x + i, y);
            if (candidate.outOfBounds() || candidate.returnSpace(world()) != null) {
                return currSize;
            }
        }
        x = currPosition.x();
        y = currPosition.y() + direction;
        Position next = new Position(x, y);
        return probe(width, iter - 1, direction, currSize + 1, next);
    }

    private void searchForDoors() {
        //check the bottom for doors
        int bottomX = this.getPosition().x() + 1;
        int bottomY = this.getPosition().y() - 1;
        //check the top for doors
        int topX = this.getPosition().x() + 1;
        int topY = this.getPosition().y() + height();

        int iter = this.width() - 2; //Subtracting 2 from loop length to offset corners
        for (int i = 0; i < iter; i++) {
            Position bottomCandidate = new Position(bottomX + i, bottomY);
            Position topCandidate = new Position(topX + i, topY);

            //check if it's out of bounds
            if (bottomCandidate.outOfBounds() && topCandidate.outOfBounds()) {
                continue;
            }
            if (!bottomCandidate.outOfBounds()) {
                Space whatsDown = bottomCandidate.returnSpace(world());
                if (whatsDown != null && !bottomCandidate.isCorner(world(), "vertical")) {
                    //it's not a corner, which means it's a wall or a door. Blow it open!
                    Door newDoor = new Door(world(), bottomCandidate, whatsDown, direction);
                    //Blow our door open as well!
                    Position us = new Position(bottomCandidate.x(), this.getPosition().y());
                    Door ourDoor = new Door(world(), us, this, direction);
                }

            }
            if (!topCandidate.outOfBounds()) {
                Space whatsUp = topCandidate.returnSpace(world());
                if (whatsUp != null && !topCandidate.isCorner(world(), "vertical")) {
                    //it's not a corner, which means it's a wall or a door. Blow it open!
                    Door newDoor = new Door(world(), topCandidate, whatsUp, direction);
                    //Blow our door open as well!
                    Position us = new Position(topCandidate.x(), this.getPosition().y() + height() - 1);
                    Door ourDoor2 = new Door(world(), us, this, direction);
                }
            }
        }
        world().drawAllDoors();
    }

}
