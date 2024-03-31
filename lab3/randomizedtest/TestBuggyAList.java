package randomizedtest;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Assert;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {
  // YOUR TESTS HERE
    @Test
    public void testThreeAddThreeRemove(){
        AListNoResizing<Integer> correctAList = new AListNoResizing<>();
        BuggyAList<Integer> buggyAList = new BuggyAList<>();
        correctAList.addLast(3);
        correctAList.addLast(4);
        correctAList.addLast(5);

        buggyAList.addLast(3);
        buggyAList.addLast(4);
        buggyAList.addLast(5);

        for (int i = 0; i < 3; i++) {
            Assert.assertEquals(correctAList.removeLast(), buggyAList.removeLast());
        }
    }

    @Test
    public void randomizedTest(){
        AListNoResizing<Integer> correctAList = new AListNoResizing<>();
        BuggyAList<Integer> buggyAList = new BuggyAList<>();

        int N = 5000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 4);
            switch (operationNumber) {
                case 0:
                    // addLast
                    int randVal = StdRandom.uniform(0, 100);
                    correctAList.addLast(randVal);
                    buggyAList.addLast(randVal);
                    System.out.printf("correctAList: addLast(" + randVal + ")");
                    System.out.println("---- buggyAList: addLast("+ randVal + ")");
                    break;
                case 1:
                    // size
                    int correctSize = correctAList.size();
                    int buggySize = buggyAList.size();
                    System.out.printf("correctAList: size: " + correctSize);
                    System.out.println("---- buggyAList: size: " + buggySize);
                    Assert.assertEquals(correctSize, buggySize);
                    break;
                case 2:
                    // getLast
                    if (correctAList.size() == 0) {
                        continue;
                    }
                    var correctLast = correctAList.getLast();
                    var buggyLast = buggyAList.getLast();
                    System.out.printf("correctAList: last(" + correctLast + ")");
                    System.out.println("---- buggyAList: last(" + buggyLast + ")");
                    Assert.assertEquals(correctLast, buggyLast);
                    break;
                case 3:
                    // removeLast
                    if (correctAList.size() == 0) {
                        continue;
                    }
                    var correctRemoved = correctAList.removeLast();
                    var buggyRemoved = buggyAList.removeLast();
                    System.out.printf("correctAList: removeLast(" + correctRemoved + ")");
                    System.out.println("---- buggyAList: removeLast(" + buggyRemoved + ")");
                    Assert.assertEquals(correctRemoved, buggyRemoved);
            }

        }
    }
}
