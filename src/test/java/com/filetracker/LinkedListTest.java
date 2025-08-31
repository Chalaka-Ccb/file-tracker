package com.filetracker;

import com.filetracker.core.LinkedList;
import com.filetracker.models.Snapshot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class LinkedListTest {

    private LinkedList list;
    private Snapshot snap1, snap2, snap3;

    @BeforeEach
    public void setUp() {
        list = new LinkedList();
        snap1 = new Snapshot(1);
        snap2 = new Snapshot(2);
        snap3 = new Snapshot(3);
    }

    @Test
    public void testAppendAndSize() {
        assertEquals(0, list.size());

        list.append(snap1);
        assertEquals(1, list.size());

        list.append(snap2);
        assertEquals(2, list.size());
    }

    @Test
    public void testGet() {
        list.append(snap1);
        list.append(snap2);
        list.append(snap3);

        assertEquals(snap1, list.get(0));
        assertEquals(snap2, list.get(1));
        assertEquals(snap3, list.get(2));
    }

    @Test
    public void testGetOutOfBounds() {
        list.append(snap1);

        assertThrows(IndexOutOfBoundsException.class, () -> list.get(1));
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(-1));
    }

    @Test
    public void testIsEmpty() {
        assertTrue(list.isEmpty());

        list.append(snap1);
        assertFalse(list.isEmpty());
    }

    @Test
    public void testGetLast() {
        assertNull(list.getLast());

        list.append(snap1);
        assertEquals(snap1, list.getLast());

        list.append(snap2);
        assertEquals(snap2, list.getLast());
    }

    @Test
    public void testGetSecondLast() {
        assertNull(list.getSecondLast());

        list.append(snap1);
        assertNull(list.getSecondLast()); // Only one element

        list.append(snap2);
        assertEquals(snap1, list.getSecondLast());

        list.append(snap3);
        assertEquals(snap2, list.getSecondLast());
    }
}
