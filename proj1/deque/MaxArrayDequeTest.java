package deque;

import org.junit.*;

import java.util.Comparator;

public class MaxArrayDequeTest {

    @Test
    public void maxTest() {
        MaxArrayDeque<Integer> arr = new MaxArrayDeque<>(new DequeItemComparator<>());
        for (int i = 0; i < 16; i++) {
            arr.addFirst(i);
        }

        arr.printDeque();
        Assert.assertEquals((int) arr.max(), 15);

    }

    @Test
    public void maxTest2() {
        MaxArrayDeque<Double> arr = new MaxArrayDeque<>(new DequeItemComparator<>());
        for (int i = 0; i < 16; i++) {
            arr.addFirst((double)i);
        }

        arr.printDeque();
        System.out.println(arr.max());
    }

    @Test
    public void maxTest3() {
        MaxArrayDeque<Integer> arr = new MaxArrayDeque<>(new DequeItemComparator<>());
        for (int i = 0; i < 16; i++) {
            arr.addFirst(i);
        }
        arr.printDeque();
        Assert.assertEquals((int) arr.max(new DequeItemComparator<>()), 15);
    }

    private class DequeItemComparator<T> implements Comparator<T> {
        @Override
        public int compare(T o1, T o2) {
            if (o1 instanceof Integer) {
                return (int) o1 - (int) o2;
            } else {
                return o1.hashCode() - o2.hashCode();
            }
        }
    }
}
