package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.ArrayList;
import java.util.List;

public class Door extends Wall {
    Space adjoiningParent;
    TETile r = Tileset.FLOOR;
    public Door(Wall wall) {
        super(wall.position(), wall.parent());
        this.setRep(r);
        world().removeWall(wall);
        world().addDoor(this);
    }

    public void doubleDoor(ut.Direction d, int width) {
        Position curr = position();
        for (int i = 0; i < width; i++) {
            //be simple, make a new wall there, and then turn that into a door
            Component currC = world().getComponentByPosition(curr);
            Space currParent = currC.parent();
            Wall currWall = new Wall(curr, currParent);
            Door currDoor = new Door(currWall);

            Position newP = curr.moveDirection(d);
            Position pastP = newP.moveDirection(d);
            Component newC = world().getComponentByPosition(newP);
            Space newParent = newC.parent();
            if (!newP.outOfBounds() && !pastP.outOfBounds() && !world().getComponentByPosition(pastP).nothing()) {

                Wall newWall = new Wall(newP, newParent);
                Door newDoor = new Door(newWall);
            }
            this.adjoiningParent = newParent;
            currParent.connect(newParent);
            curr = curr.moveDirection(ut.clockwise(d));
        }
    }

    public Hallway launchHallway(ut.Direction d, int span, int size) {
        World w = world();
        int randSpan = w.randNum(1, span + 1);
        if (size == 0) {
            size = w.randNum(5, Engine.HEIGHT); // pick a random size of the hallway
        }
        Position curr = position();
        Component currComp = w.getComponentByPosition(curr);
        int currSize = 0;
        while (currSize < size && currComp.probeForHallSpan(d, randSpan) == randSpan) {
            currSize += 1;
            curr = curr.moveDirection(d);
            if (curr.outOfBounds()) {
                break;
            }
            currComp = w.getComponentByPosition(curr);
        }
        Hallway res = new Hallway(w, randSpan, currSize, d);
        res.setPosition(curr);
        return res;
    }


}
