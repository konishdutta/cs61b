package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import edu.princeton.cs.algs4.StdDraw;

import java.awt.*;
import java.util.Random;

public class Engine {
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    private InputSource inputSource;
    public static final int WIDTH = 80;
    public static final int HEIGHT = 30;
    public World world;
    public enum gameState {
        MENU, SEEDING, PLAY, EXIT, LOAD
    }
    public gameState currState = gameState.MENU;
    private int seed = 0;
    public static void main(String[] args) {
        Engine e = new Engine();
        e.interactWithInputString("n455857754086099036s");
        //e.interactWithKeyboard();
    }
    public void exit() {
        StdDraw.clear(Color.BLACK);
        StdDraw.show();
        System.exit(0);
    }
    public void drawMenu() {
        StdDraw.setCanvasSize(WIDTH * 16, HEIGHT * 16); // Each cell is 16x16 pixels
        StdDraw.enableDoubleBuffering();
        StdDraw.clear(Color.BLACK);
        Font font = new Font("DialogInput", Font.BOLD, 60);
        StdDraw.setPenColor(Color.ORANGE);
        StdDraw.setFont(font);
        StdDraw.text(0.5, 0.65, "THESEUS");
        font = new Font("DialogInput", Font.PLAIN, 25);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.setFont(font);
        StdDraw.text(0.5, 0.50, "N - New Game");
        StdDraw.text(0.5, 0.425, "L - Load Game");
        StdDraw.text(0.5, 0.35, "Q - Quit");
        StdDraw.show();
    }
    public void drawSeed() {
        if (!(inputSource instanceof KeyboardInputSource)) {
            return;
        }
        StdDraw.clear(Color.BLACK);
        Font font = new Font("DialogInput", Font.BOLD, 60);
        StdDraw.setPenColor(Color.ORANGE);
        StdDraw.setFont(font);
        StdDraw.text(0.5, 0.65, "ENTER A NUMBER");
        font = new Font("DialogInput", Font.PLAIN, 25);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.setFont(font);
        StdDraw.text(0.5, 0.5, String.valueOf(seed));
        StdDraw.text(0.5, 0.35, "P - Play");
        StdDraw.show();
    }
    public void play() {
        this.world = new World(seed);
        world.startRandomGame();
        run();
    }

    public void run() {
        TETile[][] frame = world.getMap();
        //ter.renderSimpleLight(frame, world, 5);
        //ter.renderRayLight(frame, world, 5);
        //ter.renderFrame(frame);
    }

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */

    public void interactWithKeyboard() {
        ter.initialize(1, 1);
        drawMenu();
        this.inputSource = new KeyboardInputSource();
        while (currState != gameState.EXIT) {
            if (inputSource.possibleNextInput()) {
                char key = inputSource.getNextKey();
                processKey(key);
            }
        }
    }
    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     *
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     *
     * In other words, both of these calls:
     *   - interactWithInputString("n123sss:q")
     *   - interactWithInputString("lww")
     *
     * should yield the exact same world state as:
     *   - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] interactWithInputString(String input) {
        // TODO: Fill out this method so that it run the engine using the input
        // passed in as an argument, and return a 2D tile representation of the
        // world that would have been drawn if the same inputs had been given
        // to interactWithKeyboard().
        //
        // See proj3.byow.InputDemo for a demo of how you can make a nice clean interface
        // that works for many different input types.

        this.inputSource = new StringInputSource(input);
        while (inputSource.possibleNextInput()) {
            char key = inputSource.getNextKey();
            processKey(key);
            }
        return world.getMap();
    }
    public void processKey(char c) {
        switch (currState) {
            case MENU: menuStrokes(c);
            break;
            case SEEDING:
                seedStrokes(c);
                break;
            case PLAY:
                playStrokes(c);
                break;
            default: break;
        }
    }
    public void seedStrokes(char c) {
        switch (Character.toUpperCase(c)) {
            case 'P', 'S':
                currState = gameState.PLAY;
                play();
                break;
            default:
                if (!Character.isDigit(c)) {
                    drawSeed();
                }
                int i = c - '0';
                seed = seed * 10 + i;
                drawSeed();
                break;
        }
    }
    public void menuStrokes(char c) {
        switch (Character.toUpperCase(c)) {
            case 'N':
                currState = gameState.SEEDING;
                drawSeed();
                break;
            case 'L':
                currState = gameState.LOAD;
                break;
            case 'Q':
                currState = gameState.EXIT;
                exit();
                return;

            default:
                break;
        }
    }
    public void playStrokes(char c) {
        switch (Character.toUpperCase(c)) {
            case 'W':
                world.moveAvatar(ut.Direction.NORTH);
                run();
                break;
            case 'S':
                world.moveAvatar(ut.Direction.SOUTH);
                run();
                break;
            case 'A':
                world.moveAvatar(ut.Direction.WEST);
                run();
                break;
            case 'D':
                world.moveAvatar(ut.Direction.EAST);
                run();
                break;

            default:
                run();
                break;
        }
    }
}
