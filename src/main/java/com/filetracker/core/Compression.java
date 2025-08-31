package com.filetracker.core;

import com.filetracker.models.FileMetadata;
import com.filetracker.models.Snapshot;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static jdk.internal.jshell.tool.Selector.FormatAction.ADDED;
import static sun.font.TextLineComponent.UNCHANGED;

/**
 * Compression
 * ------------
 * Provides delta compression functionality to save storage space.
 * Instead of storing complete snapshots, only stores differences (deltas)
 * from the previous snapshot.
 * This is the second key enhancement to the basic tracking tool.
 */
public class Compression {

    /**
     * Represents a delta between two snapshots - what changed.
     */
    public static class Delta {
        public List<FileMetadata> addedFiles = new ArrayList<>();
        public List<String> deletedFilePaths = new ArrayList<>();
        public List<FileMetadata> updatedFiles = new ArrayList<>();

        public boolean isEmpty() {
            return addedFiles.isEmpty() && deletedFilePaths.isEmpty() && updatedFiles.isEmpty();
        }
    }

    /**
     * Creates a delta (difference) between two consecutive snapshots.
     * This is the core of the compression algorithm.
     *
     * @param previousSnapshot The older snapshot (can be null for first snapshot).
     * @param currentSnapshot  The newer snapshot to compress.
     * @return A Delta object containing only the changes.
     */
    public Delta createDelta(Snapshot previousSnapshot, Snapshot currentSnapshot) {
        Delta delta = new Delta();
        DiffEngine diffEngine = new DiffEngine();

        // Use our existing DiffEngine to find changes
        List<DiffEngine.DiffResult> changes = diffEngine.compare(previousSnapshot, currentSnapshot);

        for (DiffEngine.DiffResult change : changes) {
            switch (change.getChangeType()) {
                case ADDED:
                    delta.addedFiles.add(change.getNewFileMeta());
                    break;
                case DELETED:
                    delta.deletedFilePaths.add(change.getFilePath());
                    break;
                case UPDATED:
                    delta.updatedFiles.add(change.getNewFileMeta());
                    break;
                case UNCHANGED:
                    // Skip unchanged files for delta compression
                    break;
            }
        }

        return delta;
    }

    /**
     * Applies a delta to a base snapshot to reconstruct the full snapshot.
     *
     * @param baseSnapshot The base snapshot to apply changes to.
     * @param delta        The delta containing changes to apply.
     * @return A new Snapshot representing the reconstructed state.
     */
    public Snapshot applyDelta(Snapshot baseSnapshot, Delta delta) {
        // Create a new snapshot that will be the reconstructed version
        Snapshot reconstructed = new Snapshot(baseSnapshot.getSnapshotId() + 1); // New ID

        // First, copy all files from the base snapshot (except those that were deleted/updated)
        BST<FileMetadata> baseTree = baseSnapshot.getFileTree();
        baseTree.inOrderTraversal((filePath, fileMeta) -> {
            // Only keep files that weren't deleted or updated in the delta
            if (!delta.deletedFilePaths.contains(filePath) &&
                    !containsFilePath(delta.updatedFiles, filePath)) {
                reconstructed.addFile(fileMeta);
            }
        });

        // Then, add all added and updated files from the delta
        for (FileMetadata addedFile : delta.addedFiles) {
            reconstructed.addFile(addedFile);
        }
        for (FileMetadata updatedFile : delta.updatedFiles) {
            reconstructed.addFile(updatedFile);
        }

        return reconstructed;
    }

    /**
     * Helper method to check if a list of FileMetadata contains a specific file path.
     */
    private boolean containsFilePath(List<FileMetadata> files, String filePath) {
        for (FileMetadata file : files) {
            if (file.getFilePath().equals(filePath)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Serializes a Delta object to a file for storage.
     *
     * @param delta     The delta to serialize.
     * @param deltaPath The file path where to store the delta.
     * @throws IOException If serialization fails.
     */
    public void serializeDelta(Delta delta, String deltaPath) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(deltaPath))) {
            oos.writeObject(delta);
        }
    }

    /**
     * Deserializes a Delta object from a file.
     *
     * @param deltaPath The file path where the delta is stored.
     * @return The deserialized Delta object.
     * @throws IOException            If file reading fails.
     * @throws ClassNotFoundException If deserialization fails.
     */
    public Delta deserializeDelta(String deltaPath) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(deltaPath))) {
            return (Delta) ois.readObject();
        }
    }

    /**
     * Compresses a snapshot by storing only the delta from the previous snapshot.
     * If it's the first snapshot, stores it completely.
     *
     * @param snapshotManager The snapshot manager containing the timeline.
     * @param snapshot        The snapshot to compress.
     * @param storagePath     The directory where to store compressed data.
     * @throws IOException If compression fails.
     */
    public void compressSnapshot(SnapshotManager snapshotManager, Snapshot snapshot, String storagePath) throws IOException {
        Path storageDir = Paths.get(storagePath);
        Files.createDirectories(storageDir);

        int snapshotId = snapshot.getSnapshotId();
        String snapshotFilePath = storageDir.resolve("snapshot_" + snapshotId + ".dat").toString();

        if (snapshotId == 1) {
            // First snapshot - store completely
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(snapshotFilePath))) {
                oos.writeObject(snapshot);
            }
        } else {
            // Subsequent snapshots - store delta
            Snapshot previousSnapshot = snapshotManager.getSnapshot(snapshotId - 2); // 0-based index
            Delta delta = createDelta(previousSnapshot, snapshot);

            String deltaFilePath = storageDir.resolve("delta_" + snapshotId + ".dat").toString();
            serializeDelta(delta, deltaFilePath);

            // For demonstration, also store info about compression ratio
            System.out.println("Snapshot #" + snapshotId + " compressed. Delta contains: " +
                    delta.addedFiles.size() + " added, " +
                    delta.deletedFilePaths.size() + " deleted, " +
                    delta.updatedFiles.size() + " updated files.");
        }
    }

    /**
     * Decompresses a snapshot by applying all deltas from the beginning.
     *
     * @param snapshotId  The ID of the snapshot to decompress.
     * @param storagePath The directory where compressed data is stored.
     * @return The fully reconstructed Snapshot.
     * @throws IOException            If decompression fails.
     * @throws ClassNotFoundException If deserialization fails.
     */
    public Snapshot decompressSnapshot(int snapshotId, String storagePath) throws IOException, ClassNotFoundException {
        Path storageDir = Paths.get(storagePath);

        if (snapshotId == 1) {
            // First snapshot - load directly
            String snapshotFilePath = storageDir.resolve("snapshot_1.dat").toString();
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(snapshotFilePath))) {
                return (Snapshot) ois.readObject();
            }
        }

        // For subsequent snapshots, we need to build from base + all deltas
        Snapshot current = decompressSnapshot(1, storagePath); // Start with base

        for (int i = 2; i <= snapshotId; i++) {
            String deltaFilePath = storageDir.resolve("delta_" + i + ".dat").toString();
            Delta delta = deserializeDelta(deltaFilePath);
            current = applyDelta(current, delta);
        }

        return current;
    }
}