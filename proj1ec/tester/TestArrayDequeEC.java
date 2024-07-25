package tester;

import edu.princeton.cs.introcs.StdRandom;
import org.junit.Test;
import student.StudentArrayDeque;

import static org.junit.Assert.assertEquals;

public class TestArrayDequeEC {

    @Test
    public void test() {
        StudentArrayDeque<Integer> studentDeque = new StudentArrayDeque<>();
        ArrayDequeSolution<Integer> arrayDeque = new ArrayDequeSolution<>();
        String msg = "";

        for (int i = 0; i < 10; i += 1) {
            double numberBetweenZeroAndOne = StdRandom.uniform();

            if (numberBetweenZeroAndOne < 0.5) {
                studentDeque.addLast(i);
                arrayDeque.addLast(i);
                msg += ("addLast(" + i + ")\n");
            } else {
                studentDeque.addFirst(i);
                arrayDeque.addFirst(i);
                msg += ("addFirst(" + i + ")\n");
            }
        }

        studentDeque.printDeque();
        System.out.println(arrayDeque.toString());
        msg += ("size()\n");
        assertEquals(msg, arrayDeque.size(), studentDeque.size());

        for (int i = 0; i < 10; i += 1) {
            double numberBetweenZeroAndOne = StdRandom.uniform();

            if (numberBetweenZeroAndOne < 0.5) {
                var item1 = studentDeque.removeLast();
                var item2 = arrayDeque.removeLast();
                msg += ("removeLast()\n");
                assertEquals(msg, item2, item1);
                System.out.println("-------------");
                System.out.println("removeLast()" + item1);
                studentDeque.printDeque();
                System.out.println(arrayDeque.toString());
            } else {
                var item1 = studentDeque.removeFirst();
                var item2 = arrayDeque.removeFirst();
                msg += ("removeFirst()\n");
                assertEquals(msg, item2, item1);
                System.out.println("-------------");
                System.out.println("removeFirst()" + item1);
                studentDeque.printDeque();
                System.out.println(arrayDeque.toString());
            }
            msg += ("size()\n");
            assertEquals(msg, arrayDeque.size(), studentDeque.size());
        }
    }

}
