package deque;

import org.junit.*;
public class ArrayDequeTest {

    @Test
    public void addAndRemoveTest() {
        ArrayDeque<Integer> arr = new ArrayDeque<>();

        Assert.assertTrue(arr.isEmpty());
//        arr.print();

        arr.addLast(1);
        Assert.assertEquals(arr.size(), 1);
//        arr.print();
        arr.addFirst(2);
        Assert.assertEquals(arr.size(), 2);
//        arr.print();
        arr.addFirst(3);
        arr.addLast(4);
        Assert.assertEquals(arr.size(), 4);
        arr.print();

        arr.printDeque();
    }

    @Test
    public void printTest() {
        ArrayDeque<Integer> arr = new ArrayDeque<>();

        arr.print();
        arr.addLast(1);
        arr.print();
        arr.addFirst(2);
        arr.print();
        arr.addFirst(3);
        arr.print();
        arr.addLast(4);
        arr.print();

        arr.printDeque();
    }


    @Test
    public void iteratorTest() {
        ArrayDeque<Integer> arr = new ArrayDeque<>();
        arr.addLast(1);
        arr.addFirst(2);
        arr.addLast(1);
        arr.addFirst(3);
        arr.addLast(1);
        arr.addLast(4);
        for (var i: arr) {
            System.out.println(i);
        }
    }
}
