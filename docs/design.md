### Java folder structure
```
file-tracker/
│── README.md                # Project overview, usage, commands
│── .gitignore               # Ignore build/, logs/, .idea/, etc.
│── build.gradle / pom.xml   # (Optional if using Gradle/Maven)
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── filetracker/
│   │   │           ├── Main.java                # Entry point (CLI)
│   │   │           ├── core/
│   │   │           │   ├── SnapshotManager.java # Handles snapshots (LinkedList + BST)
│   │   │           │   ├── BST.java             # Binary Search Tree implementation
│   │   │           │   ├── LinkedList.java      # Linked List for timeline
│   │   │           │   ├── FileUtils.java       # File traversal + metadata extraction
│   │   │           │   ├── DiffEngine.java      # Snapshot comparison logic
│   │   │           │   ├── Restore.java         # Restore functionality
│   │   │           │   └── Compression.java     # Compression/delta storage
│   │   │           └── models/
│   │   │               ├── FileMetadata.java    # Metadata (path, size, timestamp, hash)
│   │   │               └── Snapshot.java        # Represents one snapshot (holds BST root)
│   │   └── resources/                           # Config files if needed
│   │
│   └── test/
│       └── java/
│           └── com/
│               └── filetracker/                # unit checking
│                   ├── BSTTest.java
│                   ├── LinkedListTest.java
│                   ├── SnapshotManagerTest.java
│                   ├── DiffEngineTest.java
│                   └── RestoreTest.java
│
├
│
├── docs/                    # Proposal, design notes, algorithm analysis
│   ├── design.md
│   └── algorithms.md
│
└── logs/                    # Debugging logs
└── tracker.log
```

