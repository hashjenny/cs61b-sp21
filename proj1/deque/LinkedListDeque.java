package deque;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class LinkedListDeque<T> implements Deque<T>, Iterable<T> {

    private static class Node<T> {
        private T value;
        private Node<T> prev;
        private Node<T> next;

        public Node(T item) {
            this.value = item;
            this.prev = null;
            this.next = null;
        }
    }

    private final Node<T> sentinel;

    private int size;

    public LinkedListDeque() {
        sentinel = new Node<>(null);
        sentinel.next = sentinel;
        sentinel.prev = sentinel;
        size = 0;
    }

    public void addFirst(T item) {
        Node<T> newNode = new Node<>(item);

        newNode.next = sentinel.next;
        sentinel.next.prev = newNode;
        sentinel.next = newNode;
        newNode.prev = sentinel;

        size++;
    }

    public void addLast(T item) {
        Node<T> newNode = new Node<>(item);

        newNode.prev = sentinel.prev;
        sentinel.prev.next = newNode;
        sentinel.prev = newNode;
        newNode.next = sentinel;

        size++;
    }

    public int size() {
        return size;
    }

    public void printDeque() {
        Node<T> current = sentinel;
        while (current.next != sentinel) {
            current = current.next;
            System.out.printf(current.value.toString() + " ");
        }
        System.out.println();
    }

    public T removeFirst() {
        if (isEmpty()) {
            return null;
        }
        var first = sentinel.next;
        sentinel.next = first.next;
        first.next.prev = sentinel;
        first.next = null;
        first.prev = null;
        size--;
        return first.value;
    }

    public T removeLast() {
        if (isEmpty()) {
            return null;
        }
        var last = sentinel.prev;
        sentinel.prev = last.prev;
        last.prev.next = sentinel;
        last.next = null;
        last.prev = null;
        size--;
        return last.value;
    }

    public T get(int index) {
        if (isEmpty() || index + 1 > size || index < 0) {
            return null;
        }
        var current = sentinel;
        while (index >= 0) {
            current = current.next;
            index--;
        }
        return current.value;
    }

    public T getRecursive(int index) {
        if (isEmpty() || index + 1 > size || index < 0) {
            return null;
        }
        return getNode(sentinel, index);
    }

    private T getNode(Node<T> node, int index) {
        if (index == 0) {
            return node.next.value;
        }
        index--;
        return getNode(node.next, index);
    }

    @Override
    public Iterator<T> iterator() {
        return new LinkedListIterator();
    }

    private class LinkedListIterator implements Iterator<T> {
        private Node<T> current;

        public LinkedListIterator() {
            current = sentinel;
        }

        @Override
        public boolean hasNext() {
            return current.next != sentinel;
        }

        @Override
        public T next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            current = current.next;
            return current.value;
        }
    }

    public boolean equals(Object o) {
        if (!(o instanceof LinkedListDeque)) {
            return false;
        }
        var obj = (LinkedListDeque<?>) o;
        if (size() != obj.size()) {
            return false;
        }
        for (int index = 0; index < size(); index++) {
            if (get(index) != obj.get(index)) {
                return false;
            }
        }
        return true;
    }

}
