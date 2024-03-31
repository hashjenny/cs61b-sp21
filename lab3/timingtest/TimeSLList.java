package timingtest;

import edu.princeton.cs.algs4.Stopwatch;

/**
 * Created by hug.
 */
public class TimeSLList {
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
        timeGetLast();
    }

    public static void timeGetLast() {
        AList<Integer> nList = new AList<>();
        AList<Double> tList = new AList<>();
        AList<Integer> opList = new AList<>();

        for (int i = 1; i <= 128; i*=2) {
            SLList<Integer> lst = new SLList<>();

            nList.addLast(i*1000);
            for (int j = 0; j < i * 1000; j++) {
                lst.addLast(1);
            }
            Stopwatch stopwatch = new Stopwatch();
            for (int j = 0; j < 10000; j++) {
                lst.getLast();
            }
            tList.addLast(stopwatch.elapsedTime());
            opList.addLast(10000);

        }
        printTimingTable(nList, tList, opList);

    }

}

/*
            N     time (s)        # ops  microsec/op
------------------------------------------------------------
        1000         0.02        10000         1.60
        2000         0.03        10000         2.60
        4000         0.05        10000         5.20
        8000         0.10        10000        10.30
       16000         0.21        10000        20.60
       32000         0.41        10000        41.00
       64000         0.82        10000        81.60
      128000         1.64        10000       164.00
* */