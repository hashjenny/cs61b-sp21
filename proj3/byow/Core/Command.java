package byow.Core;

import java.util.ArrayList;

public class Command {
    private final char key;
    private final long seed;
    private final ArrayList<Character> actions;
    private boolean isQuit;

    public char getKey() {
        return key;
    }

    public long getSeed() {
        return seed;
    }

    public ArrayList<Character> getActions() {
        return actions;
    }

    public boolean isQuit() {
        return isQuit;
    }

    public Command(String input) {
        var inputSeries = input.toCharArray();

        key = Character.toLowerCase(inputSeries[0]);

        var seedBuilder = new StringBuilder();
        var pos = 1;
        for (; pos < inputSeries.length; pos++) {
            if (Character.isDigit(inputSeries[pos])) {
                seedBuilder.append(inputSeries[pos]);
            } else {
                break;
            }
        }
        var seedStr = seedBuilder.toString();
        if (seedStr.isEmpty()) {
            seed = 0;
        } else {
            seed = Long.parseLong(seedStr);
        }

        actions = new ArrayList<>();
        isQuit = false;
        pos++;
        while (pos < inputSeries.length) {
            var c = inputSeries[pos];
            actions.add(c);
            pos++;
        }
    }
}
