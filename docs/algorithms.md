# Algorithms Used

## 1. DFS Directory Traversal
We use **Depth-First Search (DFS)** to go through all files in a folder.  
It helps us find every file, even inside subfolders.  
This way, no file is missed when we take a snapshot.

---

## 2. BST Insertion/Search (Per File)
Each file is stored inside a **Binary Search Tree (BST)**.
- **Insertion** → adds a file in the correct place.
- **Search** → quickly finds a file if it exists.

This makes storing and looking up files much faster than a simple list.

---

## 3. In-order Traversal for Sorted Snapshot Comparison
We use **in-order traversal** on the BST to list all files in order (alphabetical).  
This makes comparing snapshots easier because both lists are sorted before we check for changes.

---

## 4. Linked List Insertion (Timeline)
Every snapshot is added at the end of a **linked list**.  
This keeps snapshots in the correct order (like a timeline).  
It’s simple and quick to add a new snapshot.

---

## 5. Difference Generation Algorithm
When comparing two snapshots, we check:
- Files only in **old snapshot** → *Deleted*
- Files only in **new snapshot** → *Added*
- Files in both but changed → *Updated*

This tells us exactly what changed between snapshots.

---

## 6. Restore Algorithm
We can go back to a past snapshot by restoring the files recorded in that snapshot.  
This is useful if the user wants to undo changes and get the older version of files.

---

## 7. Compression (Delta Storage)
Instead of saving full files every time, we only save the **differences (deltas)**.  
This saves storage space because unchanged files don’t get stored again.
