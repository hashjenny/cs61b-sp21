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
        if (node == this.root) {
            return;
        }
        flipColors(findParent(this.root, null, node.item).parent);
    }

    /* Rotates the given node to the right. Returns the new root node of
       this subtree. For this implementation, make sure to swap the colors
       of the new root and the old root!*/
    RBTreeNode<T> rotateRight(RBTreeNode<T> node) {
        if (node == null || node.left == null) {
            return null;
        }

//      see  https://fa23.datastructur.es/materials/lab/lab08/#llrb-insertion-summary
        var isRoot = node == this.root;
        var leftNode = node.left;
        var leftPart = node.left.left;
        var midPart = node.left.right;
        var rightPart = node.right;
        var parentInfo = findParent(this.root, null, node.item);

//      rotateToRightNode => the root node rotate to right
        var rotateToRightNode = new RBTreeNode<>(leftNode.isBlack, node.item);
        rotateToRightNode.left = midPart;
        rotateToRightNode.right = rightPart;

//      node => the left of the root node rotate to right, become a new root node
        node = new RBTreeNode<>(node.isBlack, leftNode.item);
        node.left = leftPart;
        node.right = rotateToRightNode;
        if (isRoot) {
            this.root = node;
        } else if (this.root != null){
            var parent = parentInfo.parent;
            if (parentInfo.side <0) {
                parent.left = node;
            } else if (parentInfo.side > 0) {
                parent.right = node;
            }
        }
        return node;
    }

    /* Rotates the given node to the left. Returns the new root node of
       this subtree. For this implementation, make sure to swap the colors
       of the new root and the old root! */
    RBTreeNode<T> rotateLeft(RBTreeNode<T> node) {
        if (node == null || node.right == null) {
            return null;
        }

//      see  https://fa23.datastructur.es/materials/lab/lab08/#llrb-insertion-summary
        var isRoot = node == this.root;
        var rightNode = node.right;
        var leftPart = node.left;
        var midPart = node.right.left;
        var rightPart = node.right.right;
        var parentInfo = findParent(this.root, null, node.item);

        var rotateToLeftNode = new RBTreeNode<>(rightNode.isBlack, node.item);
        rotateToLeftNode.left = leftPart;
        rotateToLeftNode.right = midPart;

        node = new RBTreeNode<>(node.isBlack, rightNode.item);
        node.left = rotateToLeftNode;
        node.right = rightPart;

        if (isRoot) {
            this.root = node;
        } else if (this.root != null){
            var parent = parentInfo.parent;
            if (parentInfo.side <0) {
                parent.left = node;
            } else if (parentInfo.side > 0) {
                parent.right = node;
            }
        }
        return node;
    }

    public void insert(T item) {
        root = insert(root, item);
        root.isBlack = true;
    }

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
        if (comp == 0) {
            return node; // do nothing.
        } else if (comp < 0) {
            node.left = insert(node.left, item);
        } else {
            node.right = insert(node.right, item);
        }

//        node is black
        if (isBlack(node)) {

            if (isBlack(node.left) && isRed(node.right)) {
                rotateLeft(node);
            }
            if (isRed(node.left) && isRed(node.right)) {
                flipColors(node);
            }
        } else {
            //        node is red
            if (isRed(node.left)) {
                var parent = findParent(root, null, node.item).parent;
                var newNode = rotateRight(parent);
                flipColors(newNode);
            }

            if (isRed(node.right)) {
                var newNode = rotateLeft(node);
                newNode = rotateRight(findParent(this.root, null, newNode.item).parent);
                flipColors(newNode);
            }
        }

        return this.root;
//        if (comp < 0)
//            return node.left;
//        else
//            return node.right;//fix this return statement
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
