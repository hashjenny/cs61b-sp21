package deque;

import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque<T> {

    private final Comparator<T> comparator;

    public MaxArrayDeque(Comparator<T> c) {
        this.comparator = c;
    }

    public T max() {
        if (isEmpty()) {
            return null;
        }
        T result = null;
        for (T item : this) {
            if (result == null) {
                result = item;
            }
            if (this.comparator.compare(item, result) > 0) {
                result = item;
            }
        }
        return result;
    }

    public T max(Comparator<T> c) {
        if (isEmpty()) {
            return null;
        }
        T result = null;
        for (T item : this) {
            if (result == null) {
                result = item;
            }
            if (c.compare(item, result) > 0) {
                result = item;
            }
        }
        return result;
    }


}
