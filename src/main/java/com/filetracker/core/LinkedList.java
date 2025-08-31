package com.filetracker.core;

import com.filetracker.models.Snapshot;

/**
 * LinkedList
 * -----------
 * A custom implementation of a singly Linked List to store Snapshot objects.
 * This list maintains a chronological timeline of snapshots.
 * New snapshots are appended to the end of the list.
 */
public class LinkedList {

    /**
     * Node
     * -----
     * Private inner class that represents a single node in the Linked List.
     */
    private class Node {
        Snapshot data; // The Snapshot object stored in this node
        Node next;     // Reference to the next node in the list

        Node(Snapshot data) {
            this.data = data;
            this.next = null;
        }
    }

    private Node head; // The first node in the list
    private Node tail; // The last node in the list (for efficient appending)
    private int size;  // The number of nodes (snapshots) in the list

    /**
     * Constructor. Initializes an empty Linked List.
     */
    public LinkedList() {
        head = null;
        tail = null;
        size = 0;
    }

    /**
     * Appends a new Snapshot to the END of the list.
     * This operation is O(1) constant time due to the 'tail' pointer.
     *
     * @param snapshot The Snapshot to be added to the timeline.
     */
    public void append(Snapshot snapshot) {
        Node newNode = new Node(snapshot);

        if (tail == null) {
            // If the list is empty, the new node becomes both head and tail.
            head = newNode;
            tail = newNode;
        } else {
            // Link the new node after the current tail and update the tail pointer.
            tail.next = newNode;
            tail = newNode;
        }
        size++;
    }

    /**
     * Returns the Snapshot at the specified position in the list.
     * This operation is O(n) linear time.
     *
     * @param index The index of the Snapshot to retrieve (0-based).
     * @return The Snapshot at the given index.
     * @throws IndexOutOfBoundsException if the index is out of range.
     */
    public Snapshot get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }

        Node current = head;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        return current.data;
    }

    /**
     * @return The number of snapshots in the timeline.
     */
    public int size() {
        return size;
    }

    /**
     * @return true if the list is empty, false otherwise.
     */
    public boolean isEmpty() {
        return head == null;
    }

    /**
     * Returns the last Snapshot in the list (the most recent one).
     * This operation is O(1) constant time.
     *
     * @return The last Snapshot, or null if the list is empty.
     */
    public Snapshot getLast() {
        if (tail == null) {
            return null;
        }
        return tail.data;
    }

    /**
     * Returns the second-last Snapshot in the list.
     * This is a helper method for the 'diff latest' command.
     * This operation is O(n) linear time.
     *
     * @return The second-last Snapshot, or null if the list has less than 2 items.
     */
    public Snapshot getSecondLast() {
        if (size < 2) {
            return null;
        }
        // Traverse to the node just before the tail.
        Node current = head;
        while (current.next != tail) {
            current = current.next;
        }
        return current.data;
    }

    /**
     * Prints the list of snapshots (ID and Timestamp) to the console.
     * Used for the 'list history' command.
     */
    public void printList() {
        if (isEmpty()) {
            System.out.println("No snapshots found.");
            return;
        }

        Node current = head;
        int index = 0;
        System.out.println("Snapshot History:");
        System.out.println("-----------------");
        while (current != null) {
            Snapshot snap = current.data;
            System.out.println("[" + index + "] " + snap.getFormattedTimestamp() + " → " + snap.getFileTree().size() + " files");
            current = current.next;
            index++;
        }
    }
}