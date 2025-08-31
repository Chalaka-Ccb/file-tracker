package com.filetracker;

import com.filetracker.core.BST;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BSTTest {

    private BST<String> tree;

    @BeforeEach
    public void setUp() {
        tree = new BST<>();
    }

    @Test
    public void testInsertAndSearch() {
        tree.insert("fileC.txt", "ContentC");
        tree.insert("fileA.txt", "ContentA");
        tree.insert("fileB.txt", "ContentB");

        assertEquals("ContentA", tree.search("fileA.txt"));
        assertEquals("ContentB", tree.search("fileB.txt"));
        assertEquals("ContentC", tree.search("fileC.txt"));
        assertNull(tree.search("nonexistent.txt"));
    }

    @Test
    public void testInsertDuplicateKey() {
        tree.insert("file1.txt", "Content1");
        tree.insert("file1.txt", "ContentUpdated");

        assertEquals("ContentUpdated", tree.search("file1.txt"));
    }

    @Test
    public void testSize() {
        assertEquals(0, tree.size());

        tree.insert("file1.txt", "Content1");
        assertEquals(1, tree.size());

        tree.insert("file2.txt", "Content2");
        assertEquals(2, tree.size());
    }

    @Test
    public void testIsEmpty() {
        assertTrue(tree.isEmpty());

        tree.insert("file1.txt", "Content1");
        assertFalse(tree.isEmpty());
    }

    @Test
    public void testInOrderTraversal() {
        tree.insert("Charlie", "C");
        tree.insert("Alpha", "A");
        tree.insert("Bravo", "B");

        java.util.List<String> result = new java.util.ArrayList<>();
        tree.inOrderTraversal((key, value) -> result.add(key + ":" + value));

        assertEquals(3, result.size());
        assertEquals("Alpha:A", result.get(0)); // Should be first (alphabetical)
        assertEquals("Bravo:B", result.get(1));
        assertEquals("Charlie:C", result.get(2));
    }
}
