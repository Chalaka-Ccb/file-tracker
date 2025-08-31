package com.filetracker.core;

/**
 * BSTVisitor
 * -----------
 * A functional interface that defines a method to "visit" a node during a BST traversal.
 * This allows us to separate the traversal algorithm from the action we want to perform on each node.
 *
 * @param <V> the type of value stored in the BST nodes.
 */
@FunctionalInterface
public interface BSTVisitor<V> {
    /**
     * This method is called for every node visited during the traversal.
     *
     * @param key   The key of the node (file path).
     * @param value The value of the node (FileMetadata).
     */
    void visit(String key, V value);
}