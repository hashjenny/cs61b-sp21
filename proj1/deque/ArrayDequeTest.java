package deque;

import org.junit.*;
public class ArrayDequeTest {

    @Test
    public void addAndRemoveTest() {
        ArrayDeque<Integer> arr = new ArrayDeque<>();
        Assert.assertTrue(arr.isEmpty());
        arr.addLast(1);
        Assert.assertEquals(arr.size(), 1);
        arr.addFirst(2);
        Assert.assertEquals(arr.size(), 2);
        arr.addFirst(3);
        arr.addLast(4);
        Assert.assertEquals(arr.size(), 4);
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
    public void equalTest() {
        ArrayDeque<Integer> arr = new ArrayDeque<>();
        arr.addLast(1);
        arr.addFirst(2);
        arr.addLast(1);
        arr.addFirst(3);
        arr.addLast(1);
        arr.addLast(4);

        ArrayDeque<Integer> arr2 = new ArrayDeque<>();
        arr2.addLast(1);
        arr2.addFirst(2);
        arr2.addLast(1);
        arr2.addFirst(3);
        arr2.addLast(1);

        Assert.assertNotEquals(arr, arr2);

        arr2.addFirst(4);
        Assert.assertNotEquals(arr, arr2);

        arr2.removeFirst();
        arr2.addLast(4);
        Assert.assertEquals(arr, arr2);

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
