package com.filetracker.core;


public class BST<V> {


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


    public BST() {
        root = null;
        size = 0;
    }


    public void insert(String key, V value) {
        root = insertRecursive(root, key, value);
    }


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


    public V search(String key) {
        return searchRecursive(root, key);
    }


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


    public void inOrderTraversal(BSTVisitor<V> visitor) {
        inOrderTraversalRecursive(root, visitor);
    }


    private void inOrderTraversalRecursive(Node node, BSTVisitor<V> visitor) {
        if (node != null) {
            inOrderTraversalRecursive(node.left, visitor);  // Traverse left subtree
            visitor.visit(node.key, node.value);           // Process current node
            inOrderTraversalRecursive(node.right, visitor); // Traverse right subtree
        }
    }


    public int size() {
        return size;
    }


    public boolean isEmpty() {
        return root == null;
    }
}