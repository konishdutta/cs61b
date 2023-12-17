package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import edu.princeton.cs.algs4.StdDraw;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Random;
import java.util.Scanner;

public class Engine {
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    private InputSource inputSource;
    public static final int WIDTH = 80;
    public static final int HEIGHT = 30;
    History save;
    public World world;
    public enum gameState {
        MENU, SEEDING, PLAY, EXIT, LOAD, LISTEN
    }
    private boolean zeusMode = false;
    public boolean stringMode = true;
    public gameState currState = gameState.MENU;
    private int seed = 0;
    public static void main(String[] args) {
        Engine e = new Engine();
        //e.interactWithInputString("n4805805086739915435s");
        //e.interactWithInputString("lwsd");
        e.interactWithKeyboard();
    }
    public void exit() {
        if (stringMode == false) {
            StdDraw.clear(Color.BLACK);
            StdDraw.show();
            System.exit(0);
        }
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
        if (stringMode == true) {
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
        StdDraw.text(0.5, 0.35, "S - Start");
        StdDraw.show();
    }
    public void play() {
        this.world = new World(seed);
        world.startRandomGame();
        init();

    }
    public void init() {
        if (stringMode == false) {
            ter.initialize(WIDTH, HEIGHT);
            run();
        }
    }

    public void run() {

        //ter.renderSimpleLight(frame, world, 5);
        TETile[][] frame = world.getMap();
        while (!inputSource.possibleNextInput()
                && stringMode == false) {
            if (!zeusMode) {
                ter.renderRayLight(frame, world);
                try {
                    Random random = new Random();
                    int sleep = 100 + random.nextInt(25);
                    Thread.sleep(sleep);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            } else {
                ter.renderFrame(frame);
            }

        }
        //ter.renderFrame(frame);
    }

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.wsw
     */

    public void interactWithKeyboard() {
        stringMode = false;
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
        this.inputSource = new StringInputSource(input);
        while (inputSource.possibleNextInput()) {
            char key = inputSource.getNextKey();
            processKey(key);
            }
        System.out.println(TETile.toString(world.getMap()));
        return world.getMap();
    }
    public void processKey(char c) {
        if (currState != gameState.MENU) {
            if (save == null) {
                save = new History();
            }
            save.addCommand(c);
            save.save();
        }
        switch (currState) {
            case MENU: menuStrokes(c);
            break;
            case SEEDING:
                seedStrokes(c);
                break;
            case PLAY:
                playStrokes(c);
                break;
            case LISTEN:
                listenStrokes(c);
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
    public void load() {
        File CWD = new File(System.getProperty("user.dir"));
        File saveFile = Paths.get(CWD.getPath(), "savefile.txt").toFile();
        if (saveFile.exists()) {
            try (Scanner reader = new Scanner(saveFile)) {
                String commands = reader.nextLine();
                commands = commands.substring(0, commands.length()-2);
                save = new History();
                save.addCommands(commands);
                currState = gameState.MENU;
                interactWithInputString(commands);
                currState = gameState.PLAY;
                if (stringMode == false) {
                    inputSource = new KeyboardInputSource();
                    init();
                }
            }
            catch (IOException e) {
            }
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
                load();
                break;
            case 'Q':
                currState = gameState.EXIT;
                exit();
                return;

            default:
                break;
        }
    }
    public void listenStrokes(char c) {
        switch (Character.toUpperCase(c)) {
            case 'Q':
                currState = gameState.EXIT;
                exit();
                return;
            default:
                currState = gameState.PLAY;
                break;
        }
    }
    public void playStrokes(char c) {
        switch (Character.toUpperCase(c)) {
            case 'W':
                world.moveAvatar(Ut.Direction.NORTH);
                run();
                break;
            case 'S':
                world.moveAvatar(Ut.Direction.SOUTH);
                run();
                break;
            case 'A':
                world.moveAvatar(Ut.Direction.WEST);
                run();
                break;
            case 'D':
                world.moveAvatar(Ut.Direction.EAST);
                run();
                break;
            case 'Z':
                zeusMode = !zeusMode;
                run();
                break;
            case ':':
                currState = gameState.LISTEN;
                break;

            default:
                run();
                break;
        }
    }
}
