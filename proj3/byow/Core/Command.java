package byow.Core;

import java.util.ArrayList;

public class Command {
    private final char key;
    private long seed;
    private final ArrayList<Character> actions;

    public char getKey() {
        return key;
    }

    public long getSeed() {
        return seed;
    }

    public ArrayList<Character> getActions() {
        return actions;
    }

    public Command(String input) {
        var inputSeries = input.toCharArray();

        key = Character.toLowerCase(inputSeries[0]);
        var pos = 1;
        if (key == 'n') {
            var seedBuilder = new StringBuilder();
            for (; pos < inputSeries.length; pos++) {
                if (Character.isDigit(inputSeries[pos])) {
                    seedBuilder.append(inputSeries[pos]);
                } else {
                    // here, inputSeries[pos] is equal to 's'
                    pos++;
                    break;
                }
            }
            var seedStr = seedBuilder.toString();
            if (seedStr.isEmpty()) {
                seed = 0;
            } else {
                seed = Long.parseLong(seedStr);
            }
        }

        actions = new ArrayList<>();
        while (pos < inputSeries.length) {
            var c = inputSeries[pos];
            actions.add(c);
            pos++;
        }
    }
}
