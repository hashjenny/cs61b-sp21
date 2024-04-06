package gh2;

import deque.LinkedListDeque;
import edu.princeton.cs.algs4.StdAudio;
import edu.princeton.cs.algs4.StdDraw;

public class GuitarHero {


    public static void main(String[] args) {
        final String keyboard = "q2we4r5ty7u8i9op-[=zxdcfvgbnjmk,.;/' ";

        LinkedListDeque<GuitarString> guitarStringDeque = new LinkedListDeque<>();
        for (int i = 0; i < 37; i++) {
            guitarStringDeque.addLast(new GuitarString(440.0 * Math.pow(2, (i - 24) / 12.0)));
        }


        while (true) {

            /* check if the user has typed a key; if so, process it */
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                var index = keyboard.indexOf(key);

                if (index >= 0) {
                    guitarStringDeque.get(index).pluck();
                }

            }

            double sample = 0.0;
            for (var guitarString: guitarStringDeque) {
                sample += guitarString.sample();
            }


            StdAudio.play(sample);

            for (var guitarString: guitarStringDeque) {
                guitarString.tic();
            }

        }
    }
}

