package timingtest;
import edu.princeton.cs.algs4.Stopwatch;

/**
 * Created by hug.
 */
public class TimeAList {
    private static void printTimingTable(AList<Integer> Ns, AList<Double> times, AList<Integer> opCounts) {
        System.out.printf("%12s %12s %12s %12s\n", "N", "time (s)", "# ops", "microsec/op");
        System.out.printf("------------------------------------------------------------\n");
        for (int i = 0; i < Ns.size(); i += 1) {
            int N = Ns.get(i);
            double time = times.get(i);
            int opCount = opCounts.get(i);
            double timePerOp = time / opCount * 1e6;
            System.out.printf("%12d %12.2f %12d %12.2f\n", N, time, opCount, timePerOp);
        }
    }

    public static void main(String[] args) {
        timeAListConstruction();
    }

    public static void timeAListConstruction() {
        AList<Integer> lst = new AList<>();
        AList<Integer> nList = new AList<>();
        AList<Double> tList = new AList<>();
        AList<Integer> opList = new AList<>();

        int specialNumber = 1;
        Stopwatch stopwatch = new Stopwatch();

        for (int i = 1; i <= 128000; i++) {
            lst.addLast(1);
            if (i == specialNumber * 1000) {
                double timeInSeconds = stopwatch.elapsedTime();
                nList.addLast(lst.size());
                tList.addLast(timeInSeconds);
                opList.addLast(i);
                specialNumber *= 2;
            }
        }
        printTimingTable(nList, tList, opList);
    }
}

/* resize(size + 1) N = 128000
            N     time (s)        # ops  microsec/op
------------------------------------------------------------
        1000         0.00         1000         1.00
        2000         0.00         2000         1.50
        4000         0.01         4000         2.75
        8000         0.03         8000         3.75
       16000         0.11        16000         7.13
       32000         0.65        32000        20.28
       64000         1.24        64000        19.38
      128000         3.44       128000        26.90
* */

/* resize((int) (size * 1.01)) N = 128000
           N     time (s)        # ops  microsec/op
------------------------------------------------------------
        1000         0.00         1000         1.00
        2000         0.00         2000         0.50
        4000         0.00         4000         0.50
        8000         0.00         8000         0.25
       16000         0.00        16000         0.19
       32000         0.01        32000         0.19
       64000         0.01        64000         0.22
      128000         0.02       128000         0.15
* */