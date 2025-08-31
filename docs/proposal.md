#  Lightweight File Snapshot and Change Tracking Tool

---

##  Introduction
This project develops a **lightweight command-line tool** that tracks changes in a directory over time.  
It provides a simple, local alternative to complex version control systems by taking periodic snapshots of a folder’s state and generating difference reports.

The tool focuses on **efficiency** by using core data structures to organize file metadata and perform fast comparisons, without relying on external databases or libraries.

---

##  Functional Requirements

### 🔹 Input Requirements
1. `snapshot <directory_path>` → Captures and stores the current state of the directory.
2. `diff latest` → Compares the two most recent snapshots.
3. `diff <id1> <id2>` → Compares any two snapshots by their IDs.
4. `list history` → Displays the timeline of all snapshots with timestamps.
5. `restore <id>` → Restores the directory to a selected snapshot state.

---

### 🔹 Process Requirements
1. **Directory Traversal** → Recursively scan all files and subdirectories.
2. **File Metadata Extraction** → Collect path, size, modified time, and hash.
3. **BST Storage** → Insert file metadata into a Binary Search Tree (keyed by file path).
4. **Linked List Timeline** → Store snapshots sequentially in a Linked List to maintain chronological order.
5. **Comparison Engine** → Perform in-order traversal on BSTs to identify added, updated, or deleted files.
6. **Difference Reporting** → Generate structured output highlighting changes.
7. **Snapshot Compression** → Store only incremental differences to reduce memory usage.
8. **Snapshot Restore** → Copy files back to the directory to revert to an older state.

---

### 🔹 Output Requirements
1. **Snapshot Confirmation**
   ```text
   Snapshot #5 created at 2025-09-01 10:30 AM | Files: 214
   ```
   
2. **Diff Report**
    ```text
    Snapshot 4 → Snapshot 5
    Added:   file3.txt
    Updated: report.docx
    Deleted: old_notes.pdf
   ```
3. **History List**
   ```text
   [1] 2025-08-01 12:00PM → 120 files
   [2] 2025-08-10 02:15PM → 123 files
   [3] 2025-08-15 06:45PM → 126 files
   ```
   
4. **Restore**
    ```text
   Directory restored to Snapshot #3 (2025-08-15 06:45PM)
    ```
   
5. **Error Handling**
   ```text
   Error: Snapshot ID 10 not found
    ```


---




##  Data Structures Used and Justification

* **Binary Search Tree (BST)** →
  Each snapshot is stored as a BST with file paths as keys and metadata (size, timestamp, hash) as values.
  This enables efficient insertion, lookup, and sorted traversal for snapshot comparison.

* **Linked List of BSTs** →
  Snapshots are stored in a Linked List to represent a chronological timeline.
  Each node points to a BST, providing fast insertion of new snapshots and natural ordering.

**Why this combination?**
The **Linked List** efficiently models the progression of time, while the **BST** ensures that file metadata within each snapshot is stored in an ordered structure.
This allows quick lookup, traversal, and comparison of snapshots — the core functionality of the tool

---

## 🌟 Expected Benefits
- Provides an easy-to-use **local file change tracking system**.
- Eliminates the complexity of heavy version control tools for small-scale needs.
- Enables quick identification of **added, deleted, and updated** files.
- Offers the ability to **revert** to an earlier directory state when required.
- Efficient in terms of memory by storing **incremental changes**.  
