package game2048;

import java.util.Formatter;
import java.util.Observable;


/** The state of a game of 2048.
 *  @author TODO: YOUR NAME HERE
 */
public class Model extends Observable {
    /** Current contents of the board. */
    private Board board;
    /** Current score. */
    private static int score;
    /** Maximum score so far.  Updated when game ends. */
    private int maxScore;
    /** True iff game is ended. */
    private boolean gameOver;

    /* Coordinate System: column C, row R of the board (where row 0,
     * column 0 is the lower-left corner of the board) will correspond
     * to board.tile(c, r).  Be careful! It works like (x, y) coordinates.
     */

    /** Largest piece value. */
    public static final int MAX_PIECE = 2048;

    /** A new 2048 game on a board of size SIZE with no pieces
     *  and score 0. */
    public Model(int size) {
        board = new Board(size);
        score = maxScore = 0;
        gameOver = false;
    }

    /** A new 2048 game where RAWVALUES contain the values of the tiles
     * (0 if null). VALUES is indexed by (row, col) with (0, 0) corresponding
     * to the bottom-left corner. Used for testing purposes. */
    public Model(int[][] rawValues, int score, int maxScore, boolean gameOver) {
        int size = rawValues.length;
        board = new Board(rawValues, score);
        this.score = score;
        this.maxScore = maxScore;
        this.gameOver = gameOver;
    }

    /** Return the current Tile at (COL, ROW), where 0 <= ROW < size(),
     *  0 <= COL < size(). Returns null if there is no tile there.
     *  Used for testing. Should be deprecated and removed.
     *  */
    public Tile tile(int col, int row) {
        return board.tile(col, row);
    }

    /** Return the number of squares on one side of the board.
     *  Used for testing. Should be deprecated and removed. */
    public int size() {
        return board.size();
    }

    /** Return true iff the game is over (there are no moves, or
     *  there is a tile with value 2048 on the board). */
    public boolean gameOver() {
        checkGameOver();
        if (gameOver) {
            maxScore = Math.max(score, maxScore);
        }
        return gameOver;
    }

    /** Return the current score. */
    public int score() {
        return score;
    }

    /** Return the current maximum game score (updated at end of game). */
    public int maxScore() {
        return maxScore;
    }

    /** Clear the board to empty and reset the score. */
    public void clear() {
        score = 0;
        gameOver = false;
        board.clear();
        setChanged();
    }

    /** Add TILE to the board. There must be no Tile currently at the
     *  same position. */
    public void addTile(Tile tile) {
        board.addTile(tile);
        checkGameOver();
        setChanged();
    }

    /** Tilt the board toward SIDE. Return true iff this changes the board.
     *
     * 1. If two Tile objects are adjacent in the direction of motion and have
     *    the same value, they are merged into one Tile of twice the original
     *    value and that new value is added to the score instance variable
     * 2. A tile that is the result of a merge will not merge again on that
     *    tilt. So each move, every tile will only ever be part of at most one
     *    merge (perhaps zero).
     * 3. When three adjacent tiles in the direction of motion have the same
     *    value, then the leading two tiles in the direction of motion merge,
     *    and the trailing tile does not.
     * */
    public boolean tilt(Side side) {
        boolean changed;
        changed = false;
        Board b = this.board;
        int max_size = b.size();

        for (int i = 0; i < max_size; i = i + 1) {
            Tile[] partition = getPartition(this.board, side, i);
            if (side == Side.NORTH){
                if (NorthLogic(partition, i, this.board) == true) {
                changed = true;
            }}

            if (side == Side.SOUTH){
                if (SouthLogic(partition, i, this.board) == true) {
                    changed = true;
                }}

            if (side == Side.EAST){
                if (EastLogic(partition, i, this.board) == true) {
                    changed = true;
                }}

            if (side == Side.WEST){
                if (WestLogic(partition, i, this.board) == true) {
                    changed = true;
                }}

        }

        checkGameOver();
        if (changed) {
            setChanged();
        }
        return changed;
    }

    public static Boolean NorthLogic(Tile[] partition, int partNum, Board b) {
        /*
        We get a partition that we need to iterate over.
        - the first null is our place
        - when we merge, we need to track the merge_place
        - if a place is not null, but there is a place to track, we need to move the tile there and decrement the place
        - if the previous and the current are the same, we need to merge
         */

        boolean changed = false;
        int place = -1;
        int merge_place = -1;
        Tile prev = null;

        // we move backwards when it's north
        for (int i = partition.length - 1; i >= 0; i = i - 1) {

            // track the first null
            if (partition[i] == null && place == -1) {
                place = i; // the first time we encounter a null, track it
            }

            // logic for handling merges
            else if (prev != null && partition[i] != null && prev.value() == partition[i].value()) {
                    b.move(partNum, merge_place, partition[i]);
                    prev = null;
                    changed = true;
                    score += partition[i].value();
                    place = merge_place - 1; //since a merge happened, the next null place is after this
                }

            // non-null tile has a null place behind it
            else if (place != -1 && partition[i] != null) {
                changed = true;
                //move the tile to the first null position
                b.move(partNum, place, partition[i]);
                prev = partition[i];
                merge_place = place;
                place = place - 1;
            }

            // non null tile doesn't have null place behind it
            else if (partition[i] != null) {
                prev = partition[i];
                merge_place = i; // this is where the next merge could happen
            }

            }
        return changed;
    }

    public static Boolean SouthLogic(Tile[] partition, int partNum, Board b) {
        /*
        We get a partition that we need to iterate over.
        - the first null is our place
        - when we merge, we need to track the merge_place
        - if a place is not null, but there is a place to track, we need to move the tile there and decrement the place
        - if the previous and the current are the same, we need to merge
         */

        boolean changed = false;
        int place = -1;
        int merge_place = -1;
        Tile prev = null;

        // we move backwards when it's north
        for (int i = 0; i < partition.length; i = i + 1) {

            // track the first null
            if (partition[i] == null && place == -1) {
                place = i; // the first time we encounter a null, track it
            }

            // logic for handling merges
            else if (prev != null && partition[i] != null && prev.value() == partition[i].value()) {
                b.move(partNum, merge_place, partition[i]);
                prev = null;
                changed = true;
                score += partition[i].value();
                place = merge_place + 1; //since a merge happened, the next null place is after this
            }

            // non-null tile has a null place behind it
            else if (place != -1 && partition[i] != null) {
                changed = true;
                //move the tile to the first null position
                b.move(partNum, place, partition[i]);
                prev = partition[i];
                merge_place = place;
                place = place + 1;
            }

            // non null tile doesn't have null place behind it
            else if (partition[i] != null) {
                prev = partition[i];
                merge_place = i; // this is where the next merge could happen
            }

        }
        return changed;
    }

    public static Boolean EastLogic(Tile[] partition, int partNum, Board b) {
        /*
        We get a partition that we need to iterate over.
        - the first null is our place
        - when we merge, we need to track the merge_place
        - if a place is not null, but there is a place to track, we need to move the tile there and decrement the place
        - if the previous and the current are the same, we need to merge
         */

        boolean changed = false;
        int place = -1;
        int merge_place = -1;
        Tile prev = null;

        // we move backwards when it's north
        for (int i = partition.length - 1; i >= 0; i = i - 1) {

            // track the first null
            if (partition[i] == null && place == -1) {
                place = i; // the first time we encounter a null, track it
            }

            // logic for handling merges
            else if (prev != null && partition[i] != null && prev.value() == partition[i].value()) {
                b.move(merge_place, partNum, partition[i]);
                prev = null;
                changed = true;
                score += partition[i].value();
                place = merge_place - 1; //since a merge happened, the next null place is after this
            }

            // non-null tile has a null place behind it
            else if (place != -1 && partition[i] != null) {
                changed = true;
                //move the tile to the first null position
                b.move(place, partNum, partition[i]);
                prev = partition[i];
                merge_place = place;
                place = place - 1;
            }

            // non null tile doesn't have null place behind it
            else if (partition[i] != null) {
                prev = partition[i];
                merge_place = i; // this is where the next merge could happen
            }

        }
        return changed;
    }

    public static Boolean WestLogic(Tile[] partition, int partNum, Board b) {
        /*
        We get a partition that we need to iterate over.
        - the first null is our place
        - when we merge, we need to track the merge_place
        - if a place is not null, but there is a place to track, we need to move the tile there and decrement the place
        - if the previous and the current are the same, we need to merge
         */

        boolean changed = false;
        int place = -1;
        int merge_place = -1;
        Tile prev = null;

        // we move backwards when it's north
        for (int i = 0; i < partition.length; i = i + 1) {

            // track the first null
            if (partition[i] == null && place == -1) {
                place = i; // the first time we encounter a null, track it
            }

            // logic for handling merges
            else if (prev != null && partition[i] != null && prev.value() == partition[i].value()) {
                b.move(merge_place, partNum, partition[i]);
                prev = null;
                changed = true;
                score += partition[i].value();
                place = merge_place + 1; //since a merge happened, the next null place is after this
            }

            // non-null tile has a null place behind it
            else if (place != -1 && partition[i] != null) {
                changed = true;
                //move the tile to the first null position
                b.move(place, partNum, partition[i]);
                prev = partition[i];
                merge_place = place;
                place = place + 1;
            }

            // non null tile doesn't have null place behind it
            else if (partition[i] != null) {
                prev = partition[i];
                merge_place = i; // this is where the next merge could happen
            }

        }
        return changed;
    }

    /*
    Gets the partition for a given tilt.
    For example, if tilted north, it captures each column in order of consideration.
     */
    public static Tile[] getPartition(Board b, Side side, int i) {
        int len = b.size();
        Tile[] partition = new Tile[len];

        // direction will indicate which way we iterate the partition
        if (side == Side.NORTH) {
            for (int k = 0; k < len; k = k + 1) {
                partition[k] = b.tile(i, k); // North should be partitioned on col
            }
        }

        if (side == Side.SOUTH) {
            for (int k = len - 1; k >= 0; k = k - 1) {
                partition[k] = b.tile(i, k); // South should be partitioned on col
            }
        }

        if (side == Side.WEST) {
            for (int k = 0; k < len; k = k + 1) {
                partition[k] = b.tile(k, i);
            }
        }

        if (side == Side.EAST) {
            for (int k = len - 1; k >= 0; k = k - 1) {
                partition[k] = b.tile(k, i); // North should be partitioned on col
            }
        }


        return partition;
    }

    /** Checks if the game is over and sets the gameOver variable
     *  appropriately.
     */
    private void checkGameOver() {
        gameOver = checkGameOver(board);
    }

    /** Determine whether game is over. */
    private static boolean checkGameOver(Board b) {
        return maxTileExists(b) || !atLeastOneMoveExists(b);
    }

    /** Returns true if at least one space on the Board is empty.
     *  Empty spaces are stored as null.
     * */
    public static boolean emptySpaceExists(Board b) {
        for (int row = 0; row < b.size(); row = row + 1) {
            for (int col = 0; col < b.size(); col = col + 1) {
                if(b.tile(row, col) == null) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns true if any tile is equal to the maximum valid value.
     * Maximum valid value is given by MAX_PIECE. Note that
     * given a Tile object t, we get its value with t.value().
     */
    public static boolean maxTileExists(Board b) {
        for (int row = 0; row < b.size(); row = row + 1) {
            for (int col = 0; col < b.size(); col = col + 1) {
                if(b.tile(row, col) != null && b.tile(row, col).value() == MAX_PIECE) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns true if there are any valid moves on the board.
     * There are two ways that there can be valid moves:
     * 1. There is at least one empty space on the board.
     * 2. There are two adjacent tiles with the same value.
     */
    public static boolean atLeastOneMoveExists(Board b) {
        /*
        Approach:
        1) Check for an empty space in the board - we already have emptySpaceExists
        2) Check for neighbors with the same value
            - iterate over the board, get the tile
            - for each tile, iterate 1 above, 1 below, 1 to the left, and 1 to the right
            - know when the board's limit is hit and bounce out of it
         */
        //use emptySpaceExists abstraction
        if (emptySpaceExists(b) == true) {
            return true;
        }
        //iterate over every tile and check the neighboring values
        for (int row = 0; row < b.size(); row = row + 1) {
            for (int col = 0; col < b.size(); col = col + 1) {
                if (checkNeighborValues(b.tile(col, row), b)) {
                    return true;
                }
            }
        }
        return false;
    }

    /*
    Returns true if a neighboring tile has the same value as Tile t.
     */
    public static boolean checkNeighborValues(Tile t, Board b) {
        int max_size = b.size();
        int row = t.row();
        int col = t.col();
        int val = t.value();

        //check above
        if (row + 1 < max_size && b.tile(col, row + 1).value() == val) {
            return true;
        }
        //check below
        if (row - 1 >= 0 && b.tile(col, row - 1).value() == val) {
            return true;
        }
        //check right
        if (col + 1 < max_size && b.tile(col  + 1, row).value() == val) {
            return true;
        }
        //check left
        if (col - 1 >= 0 && b.tile(col - 1, row).value() == val) {
            return true;
        }

        return false;
    }

    @Override
     /** Returns the model as a string, used for debugging. */
    public String toString() {
        Formatter out = new Formatter();
        out.format("%n[%n");
        for (int row = size() - 1; row >= 0; row -= 1) {
            for (int col = 0; col < size(); col += 1) {
                if (tile(col, row) == null) {
                    out.format("|    ");
                } else {
                    out.format("|%4d", tile(col, row).value());
                }
            }
            out.format("|%n");
        }
        String over = gameOver() ? "over" : "not over";
        out.format("] %d (max: %d) (game is %s) %n", score(), maxScore(), over);
        return out.toString();
    }

    @Override
    /** Returns whether two models are equal. */
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        } else if (getClass() != o.getClass()) {
            return false;
        } else {
            return toString().equals(o.toString());
        }
    }

    @Override
    /** Returns hash code of Model’s string. */
    public int hashCode() {
        return toString().hashCode();
    }
}
