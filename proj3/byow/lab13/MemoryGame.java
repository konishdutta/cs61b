package byow.lab13;

import byow.Core.RandomUtils;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.Color;
import java.awt.Font;
import java.util.Random;

public class MemoryGame {
    /** The width of the window of this game. */
    private int width;
    /** The height of the window of this game. */
    private int height;
    /** The current round the user is on. */
    private int round;
    /** The Random object used to randomly generate Strings. */
    private Random rand;
    /** Whether or not the game is over. */
    private boolean gameOver;
    /** Whether or not it is the player's turn. Used in the last section of the
     * spec, 'Helpful UI'. */
    private boolean playerTurn;
    /** The characters we generate random Strings from. */
    private static final char[] CHARACTERS = "abcdefghijklmnopqrstuvwxyz".toCharArray();
    /** Encouraging phrases. Used in the last section of the spec, 'Helpful UI'. */
    private static final String[] ENCOURAGEMENT = {"You can do this!", "I believe in you!",
                                                   "You got this!", "You're a star!", "Go Bears!",
                                                   "Too easy for you!", "Wow, so impressive!"};

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Please enter a seed");
            return;
        }

        long seed = Long.parseLong(args[0]);
        MemoryGame game = new MemoryGame(40, 40, seed);
        game.startGame();
    }

    public MemoryGame(int width, int height, long seed) {
        /* Sets up StdDraw so that it has a width by height grid of 16 by 16 squares as its canvas
         * Also sets up the scale so the top left is (0,0) and the bottom right is (width, height)
         */
        this.width = width;
        this.height = height;
        StdDraw.setCanvasSize(this.width * 16, this.height * 16);
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setXscale(0, this.width);
        StdDraw.setYscale(0, this.height);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();
        //Initialize random number generator
        this.rand = new Random(seed);
        startGame();
    }

    public String generateRandomString(int n) {
        //TODO: Generate random string of letters of length n
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < n; i++) {
            int randIndex = rand.nextInt(CHARACTERS.length);
            char nextChar = CHARACTERS[randIndex];
            res.append(nextChar);
        }
        return res.toString();
    }

    public void drawFrame(String s) {
        StdDraw.clear();
        Font font = new Font("Serif", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setPenColor();
        StdDraw.text(width / 2, height / 2, s);

        if (!gameOver) {
            StdDraw.line(0, height - 3, width, height - 3);
            Font upperFont = new Font("Monaco", Font.PLAIN, 20);
            StdDraw.setFont(upperFont);
            StdDraw.textLeft(0, height - 1, "Round: " + round);
            String platitude = ENCOURAGEMENT[rand.nextInt(ENCOURAGEMENT.length)];
            StdDraw.textRight(width,  height - 1, platitude);
            if (playerTurn) {
                StdDraw.text(width / 2, height - 1, "Type");
            } else {
                StdDraw.text(width / 2, height - 1, "Watch");
            }
        }

        StdDraw.show();

        //DONE: Take the string and display it in the center of the screen
        //TODO: If game is not over, display relevant game information at the top of the screen
    }

    public void flashSequence(String letters) {
        //TODO: Display each character in letters, making sure to blank the screen between letters
        for (int i = 0; i < letters.length(); i++) {
            String curr = String.valueOf(letters.charAt(i));
            drawFrame(curr);
            StdDraw.pause(1000);
            drawFrame("");
            StdDraw.pause(500);
        }
    }

    public String solicitNCharsInput(int n) {
        //TODO: Read n letters of player input
        StringBuilder res = new StringBuilder();
        while (res.length() < n) {
            if (StdDraw.hasNextKeyTyped()) {
                res.append(StdDraw.nextKeyTyped());
            }
        }
        return res.toString();
    }

    public void startGame() {
        //TODO: Set any relevant variables before the game starts
        round = 1;
        gameOver = false;
        while (!gameOver) {
            playerTurn = false;
            drawFrame("Round " + round);
            StdDraw.pause(1000);
            drawFrame("");
            StdDraw.pause(500);
            String res = generateRandomString(round);
            flashSequence(res);
            playerTurn = true;
            drawFrame("");
            String answer = solicitNCharsInput(round);
            if (answer.equals(res)) {
                round += 1;
            } else {
                gameOver = true;
                drawFrame("GAME OVER!");
                StdDraw.pause(1000);
                System.exit(0);
            }
        }
    }
}
