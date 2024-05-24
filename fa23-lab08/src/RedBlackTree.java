import java.util.Stack;

public class RedBlackTree<T extends Comparable<T>> {

    /* Root of the tree. */
    RBTreeNode<T> root;

    static class RBTreeNode<T> {

        final T item;
        boolean isBlack;
        RBTreeNode<T> left;
        RBTreeNode<T> right;

        /* Creates a RBTreeNode with item ITEM and color depending on ISBLACK
           value. */
        RBTreeNode(boolean isBlack, T item) {
            this(isBlack, item, null, null);
        }

        /* Creates a RBTreeNode with item ITEM, color depending on ISBLACK
           value, left child LEFT, and right child RIGHT. */
        RBTreeNode(boolean isBlack, T item, RBTreeNode<T> left,
                   RBTreeNode<T> right) {
            this.isBlack = isBlack;
            this.item = item;
            this.left = left;
            this.right = right;
        }
    }

    /* Creates an empty RedBlackTree. */
    public RedBlackTree() {
        root = null;
    }

    /* Creates a RedBlackTree from a given 2-3 TREE. */
    public RedBlackTree(TwoThreeTree<T> tree) {
        Node<T> ttTreeRoot = tree.root;
        root = buildRedBlackTree(ttTreeRoot);
    }

    /* Builds a RedBlackTree that has isometry with given 2-3 tree rooted at
       given node R, and returns the root node. */
    RBTreeNode<T> buildRedBlackTree(Node<T> r) {
        if (r == null) {
            return null;
        }

        if (r.getItemCount() == 1) {
            RBTreeNode<T> left = buildRedBlackTree(r.getChildAt(0));
            RBTreeNode<T> right = buildRedBlackTree(r.getChildAt(1));
            return new RBTreeNode<T>(true, r.getItemAt(0), left, right);
        } else {
            RBTreeNode<T> subRoot = new RBTreeNode<T>(true, r.getItemAt(1));
            subRoot.left = new RBTreeNode<T>(false, r.getItemAt(0));
            subRoot.left.left = buildRedBlackTree(r.getChildAt(0));
            subRoot.left.right = buildRedBlackTree(r.getChildAt(1));
            subRoot.right = buildRedBlackTree(r.getChildAt(2));
            return subRoot;
        }
    }

    /* Flips the color of node and its children. Assume that NODE has both left
       and right children. */
    void flipColors(RBTreeNode<T> node) {
        if (node == null) {
            return;
        } else if (node.left == null || node.right == null) {
            return;
        } else if (node.left.isBlack || node.right.isBlack) {
            return;
        }
        node.isBlack = false;
        node.left.isBlack = true;
        node.right.isBlack = true;

    }

    /* Rotates the given node to the right. Returns the new root node of
       this subtree. For this implementation, make sure to swap the colors
       of the new root and the old root!*/
    RBTreeNode<T> rotateRight(RBTreeNode<T> node) {
        if (node == null || node.left == null) {
            return null;
        }

//      see  https://fa23.datastructur.es/materials/lab/lab08/#llrb-insertion-summary
        var newNode = node.left;
        swapColor(node, newNode);
        var leftPart = node.left.left;
        var midPart = node.left.right;
        var rightPart = node.right;
        var isRoot = node == this.root;
        var parentInfo = findParent(this.root, null, node.item);
        newNode.left = leftPart;
        newNode.right = node;
        node.left = midPart;
        node.right = rightPart;
        if (isRoot) {
            this.root = newNode;
        } else if (this.root != null) {
            var parent = parentInfo.parent;
            if (parentInfo.side < 0) {
                parent.left = newNode;
            } else if (parentInfo.side > 0) {
                parent.right = newNode;
            }
        }
        return newNode;
    }

    /* Rotates the given node to the left. Returns the new root node of
       this subtree. For this implementation, make sure to swap the colors
       of the new root and the old root! */
    RBTreeNode<T> rotateLeft(RBTreeNode<T> node) {
        if (node == null || node.right == null) {
            return null;
        }

        var newNode = node.right;
        swapColor(node, newNode);
        var leftPart = node.left;
        var midPart = node.right.left;
        var rightPart = node.right.right;
        var isRoot = node == this.root;
        var parentInfo = findParent(this.root, null, node.item);
        newNode.left = node;
        newNode.right = rightPart;
        node.left = leftPart;
        node.right = midPart;
        if (isRoot) {
            this.root = newNode;
        } else if (this.root != null) {
            var parent = parentInfo.parent;
            if (parentInfo.side < 0) {
                parent.left = newNode;
            } else if (parentInfo.side > 0) {
                parent.right = newNode;
            }
        }
        return newNode;
    }

    void swapColor(RBTreeNode<T> node1, RBTreeNode<T> node2) {
        var color1 = node1.isBlack;
        node1.isBlack = node2.isBlack;
        node2.isBlack = color1;
    }

    public void insert(T item) {
        root = insert(root, item);
        root.isBlack = true;
    }

    static class PreNodeInfo {
        RBTreeNode node;
        int cmp;

        public PreNodeInfo(RBTreeNode node, int cmp) {
            this.node = node;
            this.cmp = cmp;
        }
    }

    private static final Stack<PreNodeInfo> stack = new Stack<>();

    /* Inserts the given node into this Red Black Tree. Comments have been provided to help break
     * down the problem. For each case, consider the scenario needed to perform those operations.
     * Make sure to also review the other methods in this class! */
    private RBTreeNode<T> insert(RBTreeNode<T> node, T item) {
        // Insert (return) new red leaf node.
        if (node == null) {
            return new RBTreeNode<>(false, item);
        }
        // Handle normal binary search tree insertion.
        int comp = item.compareTo(node.item);
        stack.push(new PreNodeInfo(node, comp));
        if (comp == 0) {
            return node; // do nothing.
        } else if (comp < 0) {
            node.left = insert(node.left, item);
        } else {
            node.right = insert(node.right, item);
        }

        var newNode = node;

//        node is black
        if (isBlack(node)) {

            if (isBlack(node.left) && isRed(node.right)) {
                newNode = rotateLeft(node);
            } else if (isRed(node.left) && isRed(node.right)) {
                flipColors(node);
            }
        } else {
            //        node is red
            if (isRed(node.left)) {
                var parent = findParent(root, null, node.item).parent;
                newNode = rotateRight(parent);
                flipColors(newNode);

                stack.pop();
                var cmp = stack.peek().cmp;
                if (cmp < 0) {
                    return parent.left;
                } else {
                    return parent.right;
                }
            } else if (isRed(node.right)) {
                var parent = findParent(root, null, node.item).parent;
                rotateLeft(node);
                newNode = rotateRight(parent);
                flipColors(newNode);

                stack.pop();
                var cmp = stack.peek().cmp;
                if (cmp < 0) {
                    return parent.left;
                } else {
                    return parent.right;
                }
            }
        }

        stack.pop();
        if (stack.isEmpty()) {
            return this.root;
        }
//        查看上一个节点的左右走向
        var preNodeInfo = stack.peek();
        if (preNodeInfo.cmp < 0) {
            return preNodeInfo.node.left;
        }
        return preNodeInfo.node.right;
    }

    private ParentInfo findParent(RBTreeNode<T> currentNode, ParentInfo parentInfo, T target) {
        if (currentNode == null) {
            return null;
        }
        if (this.root.item.compareTo(target) == 0) {
            return null;
        }
        var result = currentNode.item.compareTo(target);
        if (result == 0) {
            return parentInfo;
        } else if (result > 0) {
            return findParent(currentNode.left,
                    new ParentInfo(currentNode, -1), target);
        } else {
            return findParent(currentNode.right,
                    new ParentInfo(currentNode, 1), target);
        }
    }

    class ParentInfo {
        RBTreeNode<T> parent;
        int side;

        ParentInfo(RBTreeNode<T> parent, int side) {
            this.parent = parent;
            this.side = side;
        }
    }

    private boolean isBlack(RBTreeNode<T> node) {
        return !isRed(node);
    }

    /* Returns whether the given node is red. Null nodes (children of leaf
       nodes) are automatically considered black. */
    private boolean isRed(RBTreeNode<T> node) {
        return node != null && !node.isBlack;
    }

}
