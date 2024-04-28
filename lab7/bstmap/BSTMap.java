package bstmap;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V> {

    private BSTNode root;

    public BSTMap() {
        this.root = null;
    }

    @Override
    public void clear() {
        this.root = null;
    }

    @Override
    public boolean containsKey(K key) {
        return containsKey(this.root, key);
    }

    private boolean containsKey(BSTNode node, K key) {
        if (node == null) {
            return false;
        }
        int result = node.key.compareTo(key);
        if (result == 0) {
            return true;
        } else if (result > 0) {
            return containsKey(node.left, key);
        } else {
            return containsKey(node.right, key);
        }
    }

    @Override
    public V get(K key) {
        var target = getBSTNode(this.root, key);
        if (target != null) {
            return target.value;
        }
        return null;
    }

    private BSTNode getBSTNode(BSTNode node, K key) {
        if (node == null) {
            return null;
        }
        int result = node.key.compareTo(key);
        if (result == 0) {
            return node;
        } else if (result > 0) {
            return getBSTNode(node.left, key);
        } else {
            return getBSTNode(node.right, key);
        }
    }

    @Override
    public int size() {
        return getSize(this.root);
    }

    private int getSize(BSTNode node) {
        if (node == null) {
            return 0;
        }
        return 1 + getSize(node.left) + getSize(node.right);
    }

    @Override
    public void put(K key, V value) {
        if (this.root == null) {
            this.root = new BSTNode(key, value);
        } else {
            put(this.root, key, value);
        }
    }

    private BSTNode put(BSTNode node, K key, V value) {
        if (node == null) {
            return new BSTNode(key, value);
        }

        int result = node.key.compareTo(key);
        if (result == 0) {
            node.value = value;
        } else if (result > 0) {
            node.left = put(node.left, key, value);
        } else {
            node.right = put(node.right, key, value);
        }
        return node;
    }

    @Override
    public V remove(K key) {
        var item = getBSTNode(this.root, key);
        if (item == null) {
            return null;
        }
        var result = item.value;
        if (item.left == null && item.right == null) {
            if (item == this.root) {
                clear();
            } else {
                var parent = findParent(this.root, null, item);
                if (parent.left != null && parent.left.equals(item)) {
                    parent.left = null;
                } else if (parent.right != null && parent.right.equals(item)) {
                    parent.right = null;
                }
            }
        } else if (item.left != null && item.right != null) {
            replaceRightNodeInLeftTree(item);
        } else {
            if (item == this.root) {
                BSTNode newRoot;
                if (item.left != null) {
                    newRoot = this.root.left;
                    item.left = null;
                } else {
                    newRoot = this.root.right;
                    item.right = null;
                }
                this.root = newRoot;
            } else {
                var parent = findParent(this.root, null, item);
                if (parent.left != null && parent.left.equals(item) && item.left != null) {
                    parent.left = item.left;
                    item.left = null;
                }
                if (parent.left != null && parent.left.equals(item) && item.right != null) {
                    parent.left = item.right;
                    item.right = null;
                }
                if (parent.right != null && parent.right.equals(item) && item.left != null) {
                    parent.right = item.left;
                    item.left = null;
                }
                if (parent.right != null && parent.right.equals(item) && item.right != null) {
                    parent.right = item.right;
                    item.right = null;
                }
            }
        }
        return result;
    }

    private BSTNode findParent(BSTNode node, BSTNode parent, BSTNode item) {
        if (node == null) {
            return null;
        }
        var result = node.key.compareTo(item.key);
        if (result == 0) {
            return parent;
        } else if (result > 0) {
            return findParent(node.left, node, item);
        } else {
            return findParent(node.right, node, item);
        }
    }

    private void replaceRightNodeInLeftTree(BSTNode node) {
        var current = node.left;
        while (current.right != null) {
            current = current.right;
        }
        var newKey = current.key;
        var newValue = current.value;
        remove(current.key);
        node.key = newKey;
        node.value = newValue;
    }

    @Override
    public V remove(K key, V value) {
        var item = getBSTNode(this.root, key);
        if (item.value != value) {
            return null;
        }
        return remove(key);
    }

    @Override
    public Set<K> keySet() {
        Set<K> set = new HashSet<>();
        keySet(set, this.root);
        return set;
    }

    private void keySet(Set<K> set, BSTNode node) {
        if (node == null) {
            return;
        }
        set.add(node.key);
        keySet(set, node.left);
        keySet(set, node.right);
    }

    public void printInOrder() {
        printInOrder(this.root);
    }

    private void printInOrder(BSTNode node) {
        if (node == null) {
            return;
        }
        printInOrder(node.left);
        if (node == this.root) {
            System.out.printf("[root]<" + node.key + ": " + node.value + "> ");
        } else {
            System.out.printf("<" + node.key + ": " + node.value + "> ");
        }
        printInOrder(node.right);
    }

    @Override
    public Iterator<K> iterator() {
        return new BSTIterator(this.root);
    }

    private class BSTIterator implements Iterator<K> {

        private final Stack<BSTNode> stack;
        private BSTNode current;

        private void putLeftTree() {
            while (this.current != null) {
                stack.push(this.current);
                this.current = this.current.left;
            }
        }

        public BSTIterator(BSTNode root) {
            this.stack = new Stack<>();
            this.current = root;
            putLeftTree();
        }

        @Override
        public boolean hasNext() {
            return !stack.isEmpty();
        }

        @Override
        public K next() {
            this.current = stack.pop();
            K result = this.current.key;
            this.current = this.current.left;
            putLeftTree();
            return result;
        }
    }

    private class BSTNode {
        private K key;
        private V value;

        private BSTNode left;
        private BSTNode right;

        private BSTNode(K key, V value) {
            this.key = key;
            this.value = value;
            this.left = null;
            this.right = null;
        }
    }

}
