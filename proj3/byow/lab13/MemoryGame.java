package byow.lab13;

import byow.Core.RandomUtils;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.Color;
import java.awt.Font;
import java.util.Random;

public class MemoryGame {
    /**
     * The width of the window of this game.
     */
    private final int width;
    /**
     * The height of the window of this game.
     */
    private final int height;
    /**
     * The current round the user is on.
     */
    private int round;
    /**
     * The Random object used to randomly generate Strings.
     */
    private final Random rand;
    /**
     * Whether the game is over.
     */
    private boolean gameOver;
    /**
     * Whether it is the player's turn. Used in the last section of the
     * spec, 'Helpful UI'.
     */
    private boolean playerTurn;
    /**
     * The characters we generate random Strings from.
     */
    private static final char[] CHARACTERS = "abcdefghijklmnopqrstuvwxyz".toCharArray();
    /**
     * Encouraging phrases. Used in the last section of the spec, 'Helpful UI'.
     */
    private static final String[] ENCOURAGEMENT = {"You can do this!", "I believe in you!",
            "You got this!", "You're a star!", "Go Bears!",
            "Too easy for you!", "Wow, so impressive!"};

    public static void main(String[] args) throws InterruptedException {
        // if (args.length < 1) {
        //     System.out.println("Please enter a seed");
        //     return;
        // }

        // long seed = Long.parseLong(args[0]);
        long seed = System.currentTimeMillis();
        MemoryGame game = new MemoryGame(40, 40, seed);
        game.startGame();
    }

    public MemoryGame(int width, int height, long seed) {
        /* Sets up StdDraw so that it has a width by height grid of 16 by 16 squares as its canvas
         * Also sets up the scale so the top left is (0,0) and the bottom right is (width, height)
         */
        this.width = width;
        this.height = height;
        this.round = 1;
        StdDraw.setCanvasSize(this.width * 16, this.height * 16);
        Font font = new Font("Monaco", Font.BOLD, 23);
        StdDraw.setFont(font);
        StdDraw.setXscale(0, this.width);
        StdDraw.setYscale(0, this.height);
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.enableDoubleBuffering();

        //TODO: Initialize random number generator
        this.rand = new Random(seed);

    }

    public String generateRandomString(int n) {
        //TODO: Generate random string of letters of length n
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            sb.append(CHARACTERS[RandomUtils.uniform(rand, 26)]);
        }
        return sb.toString();
    }

    public void drawFrame(String s) {
        StdDraw.clear(Color.BLACK);
        //TODO: Take the string and display it in the center of the screen
        StdDraw.text((double) this.width / 2, (double) this.height / 2, s);
        //TODO: If game is not over, display relevant game information at the top of the screen
        if (!gameOver) {
            StdDraw.line(0, 38, 40, 38);
            StdDraw.text(4, 39, getLeftTitle());
            StdDraw.text(this.width / 2.0, 39, getMiddleTitle());
            StdDraw.text(33, 39, getRightTitle());
        }
        StdDraw.show();
    }

    public void flashSequence(String letters) throws InterruptedException {
        //TODO: Display each character in letters, making sure to blank the screen between letters
        playerTurn = false;
        var str = letters.split("");
        for (var s : str) {
            drawFrame(s);
            Thread.sleep(1000);
            StdDraw.clear(Color.BLACK);
            Thread.sleep(500);

        }
        playerTurn = true;
    }

    public String solicitNCharsInput(int n) {
        //TODO: Read n letters of player input
        var s = "";
        drawFrame("");
        while (n > 0) {
            if (StdDraw.hasNextKeyTyped()) {
                s += StdDraw.nextKeyTyped();
                drawFrame(s);
                n--;
            }
        }
        return s;
    }

    public void startGame() throws InterruptedException {
        //TODO: Set any relevant variables before the game starts
        var str = generateRandomString(this.round);
        //TODO: Establish Engine loop
        while (!gameOver) {
            flashSequence(str);
            var typedString = solicitNCharsInput(this.round);
            Thread.sleep(1000);
            if (typedString.equals(str)) {
                this.round += 1;
                str = generateRandomString(this.round);
            } else {
                gameOver = true;
                drawFrame("Game Over! You made it to round:" + this.round);
            }
        }
    }

    private String getLeftTitle() {
        return "Round: " + this.round;
    }

    private String getMiddleTitle() {
        return playerTurn ? "Type!" : "Watch!";
    }

    private String getRightTitle() {
        return ENCOURAGEMENT[RandomUtils.uniform(rand, ENCOURAGEMENT.length)];
    }

}
