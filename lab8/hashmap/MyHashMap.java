package hashmap;

import java.util.*;

/**
 * A hash table-backed Map implementation. Provides amortized constant time
 * access to elements via get(), remove(), and put() in the best case.
 * <p>
 * Assumes null keys will never be inserted, and does not resize down upon remove().
 *
 * @author YOUR NAME HERE
 */
public class MyHashMap<K, V> implements Map61B<K, V> {

    @Override
    public void clear() {
        this.buckets = createTable(this.length);
        for (int i = 0; i < this.length; i++) {
            this.buckets[i] = createBucket();
        }
    }

    @Override
    public boolean containsKey(K key) {
        int index = getHashIndex(key);
        Collection<Node> bucket = buckets[index];
        if (bucket == null || bucket.isEmpty()) {
            return false;
        }
        for (Node node : bucket) {
            if (node.key.equals(key)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public V get(K key) {
        int index = getHashIndex(key);
        Collection<Node> bucket = buckets[index];
        if (bucket == null || bucket.isEmpty()) {
            return null;
        }
        for (Node node : bucket) {
            if (node.key.equals(key)) {
                return node.value;
            }
        }
        return null;
    }

    private Node getNode(K key) {
        int index = getHashIndex(key);
        Collection<Node> bucket = buckets[index];
        if (bucket == null || bucket.isEmpty()) {
            return null;
        }
        for (Node node : bucket) {
            if (node.key.equals(key)) {
                return node;
            }
        }
        return null;
    }

    private int getHashIndex(K key) {
        int code = key.hashCode();
        if (code < 0) {
            code = -code;
        }
        return code % this.length;
    }

    @Override
    public int size() {
        int counter = 0;
        for (Collection<Node> bucket : buckets) {
            counter += bucket.size();
        }
        return counter;
    }

    @Override
    public void put(K key, V value) {
        Node node = this.getNode(key);
        if (node != null) {
            node.value = value;
        } else {
            int index = getHashIndex(key);
            Collection<Node> bucket = buckets[index];
            bucket.add(createNode(key, value));
        }
        if (this.getLoadFactor() > this.maxLoad) {
            extendBuckets((int) (this.length * 1.5));

        }
    }

    private void extendBuckets(int newLength) {
        var lst = new LinkedList<Node>();
        for (var bucket : this.buckets) {
            lst.addAll(bucket);
        }

        this.clear();
        this.length = newLength;
        this.buckets = createTable(newLength);
        for (int i = 0; i < this.length; i++) {
            this.buckets[i] = createBucket();
        }
        for (var item : lst) {
            this.put(item.key, item.value);
        }
    }

    @Override
    public Set<K> keySet() {
        Set<K> set = new HashSet<>();
        for (Collection<Node> bucket : buckets) {
            for (Node node : bucket) {
                set.add(node.key);
            }
        }
        return set;
    }

    @Override
    public V remove(K key) {
        Node node = this.getNode(key);
        if (node == null) {
            return null;
        }
        int index = getHashIndex(key);
        V v = node.value;
        this.buckets[index].remove(node);
        return v;
    }

    @Override
    public V remove(K key, V value) {
        Node node = this.getNode(key);
        if (node == null) {
            return null;
        }
        if (!node.value.equals(value)) {
            return null;
        }
        int index = getHashIndex(key);
        this.buckets[index].remove(node);
        return value;
    }

    @Override
    public Iterator<K> iterator() {
        return new NodeIterator(this.buckets);
    }

    class NodeIterator implements Iterator<K> {

        Iterator<K> iter;

        public NodeIterator(Collection<Node>[] buckets) {
            HashSet<K> set = new HashSet<>();
            for (Collection<Node> bucket : buckets) {
                for (Node node : bucket) {
                    set.add(node.key);
                }
            }
            iter = set.iterator();
        }

        @Override
        public boolean hasNext() {
            return iter.hasNext();
        }

        @Override
        public K next() {
            return iter.next();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */
    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    /* Instance Variables */
    private Collection<Node>[] buckets;
    // You should probably define some more!
    private int length;
    private final double maxLoad;

    /**
     * Constructors
     */
    public MyHashMap() {
        this(16, 0.75);
    }

    public MyHashMap(int initialSize) {
        this(initialSize, 0.75);
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad     maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        this.length = initialSize;
        this.buckets = createTable(this.length);
        this.maxLoad = maxLoad;
        for (int i = 0; i < this.length; i++) {
            this.buckets[i] = createBucket();
        }
    }

    private double getLoadFactor() {
        return this.size() * 1.0 / this.length;
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return new Node(key, value);
    }

    /**
     * Returns a data structure to be a hash table bucket
     * <p>
     * The only requirements of a hash table bucket are that we can:
     * 1. Insert items (`add` method)
     * 2. Remove items (`remove` method)
     * 3. Iterate through items (`iterator` method)
     * <p>
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     * <p>
     * Override this method to use different data structures as
     * the underlying bucket type
     * <p>
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {
        return new LinkedList<>();
    }

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     * <p>
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    private Collection<Node>[] createTable(int tableSize) {
        return new Collection[tableSize];

    }


}
