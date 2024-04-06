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

//        arr.print();
        arr.addLast(1);
//        arr.print();
        arr.addFirst(2);
//        arr.print();
        arr.addFirst(3);
//        arr.print();
        arr.addLast(4);
//        arr.print();
        arr.printDeque();
    }

    @Test
    public void equalTest() {
        ArrayDeque<Integer> arr = new ArrayDeque<>();
        ArrayDeque<Integer> arr2 = new ArrayDeque<>();

        Assert.assertEquals(arr, arr2);
        Assert.assertTrue(arr.isEmpty() && arr2.isEmpty());

        arr.addLast(1);
        arr.addFirst(2);
        arr.addLast(1);
        arr.addFirst(3);
        arr.addLast(1);
        arr.addLast(4);

        arr2.addLast(1);
        arr2.addFirst(2);
        arr2.addLast(1);
        arr2.addFirst(3);
        arr2.addLast(1);

        Assert.assertNotEquals(arr, arr2);
        Assert.assertNotSame(arr, arr2);

        arr2.addFirst(4);
        Assert.assertNotEquals(arr, arr2);
        Assert.assertNotSame(arr, arr2);

        arr2.removeFirst();
        arr2.addLast(4);
        Assert.assertEquals(arr, arr2);
        Assert.assertEquals(arr.size(),arr2.size());
//        arr.printDeque();
//        arr2.printDeque();

    }

    @Test
    public void equalTest2() {
        ArrayDeque<Integer> arr = new ArrayDeque<>();
        LinkedListDeque<Integer> arr2 = new LinkedListDeque<>();

        Assert.assertTrue(arr.equals(arr2));
        Assert.assertTrue(arr.isEmpty() && arr2.isEmpty());

        arr.addLast(1);
        arr.addFirst(2);
        arr.addLast(1);
        arr.addFirst(3);
        arr.addLast(1);
        arr.addLast(4);

        arr2.addLast(1);
        arr2.addFirst(2);
        arr2.addLast(1);
        arr2.addFirst(3);
        arr2.addLast(1);

        Assert.assertFalse(arr.equals(arr2));

        arr2.addFirst(4);
        Assert.assertFalse(arr.equals(arr2));

        arr2.removeFirst();
        arr2.addLast(4);
        Assert.assertTrue(arr.equals(arr2));
        Assert.assertEquals(arr.size(),arr2.size());
//        arr.printDeque();
//        arr2.printDeque();

    }

    @Test
    public void resizeTest1(){
        ArrayDeque<Integer> arr = new ArrayDeque<>();
        for (int i = 0; i < 100; i++) {
            arr.addFirst(1);
//            System.out.println(arr.size() + "---" +arr.arraySize());
        }
    }

    @Test
    public void resizeTest2(){
        ArrayDeque<Integer> arr = new ArrayDeque<>();
        for (int i = 0; i < 100; i++) {
            arr.addFirst(1);
        }
        for (int i = 0; i < 100; i++) {
            arr.removeLast();
//            System.out.println(arr.size() + "---" +arr.arraySize());
        }
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
