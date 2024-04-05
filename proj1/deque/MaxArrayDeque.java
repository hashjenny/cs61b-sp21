package deque;

import java.util.Arrays;
import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque<T> {

    Comparator<T> comparator;

    public MaxArrayDeque(Comparator<T> c) {
        this.comparator = c;
    }

    public T max() {
        if (isEmpty()) {
            return null;
        }
        T[] arr = (T[]) new Object[size()];
        for (int i = 0; i < size(); i++) {
            arr[i] = get(i);
        }
        return Arrays.stream(arr).max(comparator).get();
    }

    public T max(Comparator<T> c) {
        if (isEmpty()) {
            return null;
        }
        T[] arr = (T[]) new Object[size()];
        for (int i = 0; i < size(); i++) {
            arr[i] = get(i);
        }
        return Arrays.stream(arr).max(c).get();
    }


}
