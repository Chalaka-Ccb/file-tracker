package com.filetracker.core;

import com.filetracker.models.FileMetadata;
import com.filetracker.models.Snapshot;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * SnapshotManager
 * ---------------
 * The central class that manages the lifecycle of snapshots.
 * It maintains a timeline (LinkedList) of all snapshots and provides
 * methods to create new snapshots and access existing ones.
 * This class encapsulates the core business logic of the application.
 */
public class SnapshotManager {

    private LinkedList snapshotTimeline; // The chronological list of snapshots
    private int nextSnapshotId;          // Counter to assign unique IDs to snapshots

    /**
     * Constructor. Initializes an empty timeline.
     */
    public SnapshotManager() {
        this.snapshotTimeline = new LinkedList();
        this.nextSnapshotId = 1; // Start IDs from 1
    }

    /**
     * Takes a snapshot of the specified directory.
     * 1. Scans the directory using FileUtils.
     * 2. Creates a new Snapshot object.
     * 3. Populates the snapshot's BST with FileMetadata.
     * 4. Appends the snapshot to the timeline.
     *
     * @param directoryPath The path of the directory to snapshot.
     * @return The newly created Snapshot object.
     * @throws IOException If the directory cannot be scanned.
     */
    public Snapshot takeSnapshot(String directoryPath) throws IOException {
        System.out.println("Scanning directory: " + directoryPath);
        // 1. & 2. Scan directory and get list of files with metadata
        // We need to pass the base directory path to FileUtils so it can calculate relative paths.
        Path baseDirPath = Paths.get(directoryPath).toAbsolutePath();
        List<FileMetadata> files = FileUtils.scanDirectory(directoryPath, baseDirPath);

        // 3. Create a new snapshot
        Snapshot newSnapshot = new Snapshot(nextSnapshotId++);

        // 4. Insert each file's metadata into the snapshot's BST
        // CORRECTION: The addFile method only takes the FileMetadata object.
        // The Snapshot class internally uses file.getFilePath() as the key for the BST.
        for (FileMetadata file : files) {
            newSnapshot.addFile(file); // This is the correct call
        }

        // 5. Add the snapshot to the timeline
        snapshotTimeline.append(newSnapshot);

        System.out.println("Snapshot #" + newSnapshot.getSnapshotId() + " created at " + newSnapshot.getFormattedTimestamp() + " | Files: " + files.size());
        return newSnapshot;
    }

    /**
     * Retrieves a snapshot by its index in the timeline.
     *
     * @param index The index of the snapshot (0-based).
     * @return The Snapshot at the given index.
     * @throws IndexOutOfBoundsException if the index is invalid.
     */
    public Snapshot getSnapshot(int index) {
        return snapshotTimeline.get(index);
    }

    /**
     * Retrieves the most recent snapshot.
     *
     * @return The latest Snapshot, or null if no snapshots exist.
     */
    public Snapshot getLatestSnapshot() {
        return snapshotTimeline.getLast();
    }

    /**
     * Retrieves the second most recent snapshot.
     * Essential for the 'diff latest' command.
     *
     * @return The second-latest Snapshot, or null if less than 2 snapshots exist.
     */
    public Snapshot getSecondLatestSnapshot() {
        return snapshotTimeline.getSecondLast();
    }

    /**
     * @return The total number of snapshots taken.
     */
    public int getSnapshotCount() {
        return snapshotTimeline.size();
    }

    /**
     * Prints the entire history of snapshots to the console.
     */
    public void printHistory() {
        snapshotTimeline.printList();
    }

    /**
     * Gets the internal timeline (LinkedList) of snapshots.
     * This is provided for other components like the DiffEngine.
     *
     * @return The LinkedList timeline.
     */
    public LinkedList getSnapshotTimeline() {
        return snapshotTimeline;
    }
}