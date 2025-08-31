package com.filetracker.core;

/**
 * BST (Binary Search Tree)
 * ----------------------
 * A custom implementation of a Binary Search Tree.
 * This tree stores key-value pairs, where the key is a String (file path)
 * and the value is of a generic type V (will be FileMetadata in our case).
 * The tree is sorted based on the natural ordering of the keys (lexicographical order for Strings).
 *
 * @param <V> the type of value to be stored in the tree nodes.
 */
public class BST<V> {

    /**
     * Node
     * -----
     * Private inner class that represents a single node in the BST.
     */
    private class Node {
        String key;   // The key used for sorting (file path)
        V value;      // The data associated with the key (FileMetadata)
        Node left;    // Reference to the left child node
        Node right;   // Reference to the right child node

        Node(String key, V value) {
            this.key = key;
            this.value = value;
            this.left = null;
            this.right = null;
        }
    }

    private Node root; // The root node of the tree
    private int size;  // Tracks the number of nodes in the tree

    /**
     * Constructor. Initializes an empty BST.
     */
    public BST() {
        root = null;
        size = 0;
    }

    /**
     * Inserts a new key-value pair into the BST.
     * If the key already exists, its value is updated.
     *
     * @param key   The key (file path) for the new node.
     * @param value The value (FileMetadata) for the new node.
     */
    public void insert(String key, V value) {
        root = insertRecursive(root, key, value);
    }

    /**
     * A helper method that recursively traverses the tree to find the correct
     * position for the new node and inserts it.
     *
     * @param current The current node being examined during recursion.
     * @param key     The key to insert.
     * @param value   The value to insert.
     * @return The new root of the subtree after insertion.
     */
    private Node insertRecursive(Node current, String key, V value) {
        // Base case: if we've found an empty spot, create a new node here.
        if (current == null) {
            size++;
            return new Node(key, value);
        }

        // Compare the new key with the current node's key.
        int comparison = key.compareTo(current.key);

        if (comparison < 0) {
            // New key is less than current key -> go left.
            current.left = insertRecursive(current.left, key, value);
        } else if (comparison > 0) {
            // New key is greater than current key -> go right.
            current.right = insertRecursive(current.right, key, value);
        } else {
            // Key already exists -> update the value.
            current.value = value;
        }

        // Return the current node (which may have new children)
        return current; // <-- THIS WAS THE BUG! We need to return current, not just let it fall through
    }

    /**
     * Searches for a value based on its key.
     *
     * @param key The key to search for.
     * @return The value associated with the key, or null if not found.
     */
    public V search(String key) {
        return searchRecursive(root, key);
    }

    /**
     * A helper method for recursive search.
     *
     * @param current The current node being examined.
     * @param key     The key to search for.
     * @return The value if found, null otherwise.
     */
    private V searchRecursive(Node current, String key) {
        if (current == null) {
            return null; // Key not found.
        }

        int comparison = key.compareTo(current.key);
        if (comparison == 0) {
            return current.value; // Key found!
        }
        return comparison < 0
                ? searchRecursive(current.left, key)  // Search left subtree.
                : searchRecursive(current.right, key); // Search right subtree.
    }

    /**
     * Performs an in-order traversal of the BST.
     * This will visit all nodes in sorted order (alphabetical order of file paths).
     * It accepts a "visitor" function that processes each node.
     * This is VITAL for the diff algorithm.
     *
     * @param visitor An object that implements the BSTVisitor interface,
     *                defining what to do with each node during traversal.
     */
    public void inOrderTraversal(BSTVisitor<V> visitor) {
        inOrderTraversalRecursive(root, visitor);
    }

    /**
     * Recursively performs the in-order traversal (Left -> Root -> Right).
     *
     * @param node    The current node.
     * @param visitor The visitor function to process the node.
     */
    private void inOrderTraversalRecursive(Node node, BSTVisitor<V> visitor) {
        if (node != null) {
            inOrderTraversalRecursive(node.left, visitor);  // Traverse left subtree
            visitor.visit(node.key, node.value);           // Process current node
            inOrderTraversalRecursive(node.right, visitor); // Traverse right subtree
        }
    }

    /**
     * @return The number of nodes (files) in this BST.
     */
    public int size() {
        return size;
    }

    /**
     * @return true if the tree is empty, false otherwise.
     */
    public boolean isEmpty() {
        return root == null;
    }
}