package deque;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class ArrayDeque<T> implements Deque<T>, Iterable<T> {

    private int head;
    private int tail;

    private T[] array;

    @SuppressWarnings("unchecked")
    public ArrayDeque() {
        array = (T[]) new Object[8];
        head = -1;
        tail = -1;
    }

    @SuppressWarnings("unchecked")
    private void resize(int capacity) {
        if (capacity < 8) {
            capacity = 8;
        }
        T[] newArray = (T[]) new Object[capacity];
        int size = size();
        for (int i = 0; i < size; i++) {
            newArray[i] = this.get(i);
        }
        head = 0;
        tail = size - 1;
        this.array = newArray;
    }

    public void addFirst(T item) {
        if (size() == 0) {
            addItemToEmptyDeque(item);
            return;
        }
        if (size() == array.length) {
            resize(array.length + array.length / 3);
        }

        if (head == 0) {
            head = array.length - 1;
        } else {
            head--;
        }
        array[head] = item;
    }

    public void addLast(T item) {
        if (size() == 0) {
            addItemToEmptyDeque(item);
            return;
        }
        if (size() == array.length) {
            resize(array.length + array.length / 3);
        }

        if (tail == array.length - 1) {
            tail = 0;
        } else {
            tail++;
        }
        array[tail] = item;
    }

    private void addItemToEmptyDeque(T item) {
        array[0] = item;
        head = 0;
        tail = 0;
    }

    public int size() {
        if (head < 0 || tail < 0) {
            return 0;
        } else if (tail == head) {
            return 1;
        } else if (tail > head) {
            return tail - head + 1;
        } else {
            return (tail + 1) + (array.length - head);
        }
    }

    public void printDeque() {
        for (int i = 0; i < size(); i++) {
            System.out.printf(get(i) + " ");
        }
        System.out.println();
    }

    public T removeFirst() {
        if (size() == 0) {
            return null;
        }
        if (size() == 1) {
            return removeLastOne();
        }

        T item = array[head];
        if (head == array.length - 1) {
            head = 0;
        } else {
            head++;
        }


        if (size() < array.length / 4) {
            resize(array.length / 2);
        }

        return item;

    }

    public T removeLast() {
        if (size() == 0) {
            return null;
        }
        if (size() == 1) {
            return removeLastOne();
        }

        T item = array[tail];
        if (tail == 0) {
            tail = array.length - 1;
        } else {
            tail--;
        }


        if (size() < array.length / 4) {
            resize(array.length / 2);
        }

        return item;
    }

    private T removeLastOne() {
        T item = array[head];
        head = -1;
        tail = -1;
        return item;
    }

    public T get(int index) {
        if (index > size() - 1 || index < 0) {
            return null;
        }
        if (head + index >= array.length) {
            return array[index - (array.length - head)];
        }
        return array[head + index];
    }


    public boolean equals(Object o) {
        if (!(o instanceof Deque)) {
            return false;
        }
        var obj = (Deque<?>) o;
        if (this.size() != obj.size()) {
            return false;
        }
        for (int i = 0; i < size(); i++) {
            if (!this.get(i).equals(obj.get(i))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Iterator<T> iterator() {
        return new ArrayIterator();
    }

    private class ArrayIterator implements Iterator<T> {

        private int index;

        private ArrayIterator() {
            index = 0;
        }

        @Override
        public boolean hasNext() {
            return index < size();
        }

        @Override
        public T next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            T item = get(index);
            index++;
            return item;
        }
    }
}
