package byow.Core;

public class HorizontalHallway extends Room {
    private int direction;
    public HorizontalHallway(World w, int height, int iter, int direction, Door door) {
        super(w, height, 1);
        this.direction = direction;
        int newWidth= probe(height, iter, direction, 0, door.getPosition());
        if (newWidth == 0 && height == 4) {
            setHeight(3);
            newWidth = probe(height(), iter, direction, 0, door.getPosition());
        }
        this.setWidth(Math.max(width(), newWidth));
        int x = door.getPosition().x() + direction;
        int y = door.getPosition().y() - 1;
        if (direction == -1) {
            x -= (newWidth - 1);
        }
        Position newPosition = new Position(x, y);
        this.setPosition(newPosition);
    }
    public int probe(int width, int iter, int direction, int currSize, Position currPosition) {
        // start from bottom
        int x = currPosition.x() + direction;
        int y = currPosition.y() - 1;
        if (iter == 0) {
            return currSize;
        }
        for (int i = 0; i < width; i++) {
            Position candidate = new Position(x, y + i);
            if (candidate.outOfBounds() || candidate.returnSpace(world()) != null) {
                return currSize;
            }
        }
        x = currPosition.x() + direction;
        y = currPosition.y();
        Position next = new Position(x, y);
        return probe(width, iter - 1, direction, currSize + 1, next);
    }
    public void draw() {
        super.draw();
        if (width() > 1) {
            searchForDoors(); //blow open doors - will create dupe doors for now
        }
    }
    private void searchForDoors() {
        //check the left for doors
        int leftX = this.getPosition().x() - 1;
        int leftY = this.getPosition().y() + 1;
        //check the right for doors
        int rightX = this.getPosition().x() + width();
        int rightY = this.getPosition().y() + 1;

        int iter = this.height() - 2; //Subtracting 2 from loop length to offset corners
        for (int i = 0; i < iter; i++) {
            Position leftCandidate = new Position(leftX, leftY + i);
            Position rightCandidate = new Position(rightX, rightY + i);

            //check if it's out of bounds
            if (leftCandidate.outOfBounds() && rightCandidate.outOfBounds()) {
                continue;
            }
            if (!leftCandidate.outOfBounds()) {
                Space whatsDown = leftCandidate.returnSpace(world());
                if (whatsDown != null && !leftCandidate.isCorner(world(), "horizontal")) {
                    //it's not a corner, which means it's a wall or a door. Blow it open!
                    Door newDoor = new Door(world(), leftCandidate, whatsDown, direction);
                    //Blow our door open as well!
                    Position us = new Position(this.getPosition().x(), leftCandidate.y());
                    Door ourDoor = new Door(world(), us, this, direction);
                }

            }
            if (!rightCandidate.outOfBounds()) {
                Space whatsUp = rightCandidate.returnSpace(world());
                if (whatsUp != null && !rightCandidate.isCorner(world(), "horizontal")) {
                    //it's not a corner, which means it's a wall or a door. Blow it open!
                    Door newDoor = new Door(world(), rightCandidate, whatsUp, direction);
                    //Blow our door open as well!
                    Position us = new Position(this.getPosition().x() + width() - 1, rightCandidate.y());
                    Door ourDoor2 = new Door(world(), us, this, direction);
                }
            }
        }
        world().drawAllDoors();
    }
}
