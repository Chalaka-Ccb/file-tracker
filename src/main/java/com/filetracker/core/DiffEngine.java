package com.filetracker.core;

import com.filetracker.models.FileMetadata;
import com.filetracker.models.Snapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * DiffEngine
 * -----------
 * Responsible for comparing two snapshots and identifying differences.
 * It uses a simultaneous in-order traversal algorithm to compare the two BSTs,
 * efficiently categorizing files as Added, Deleted, or Updated.
 */
public class DiffEngine {

    /**
     * Represents a single change between two snapshots.
     */
    public static class DiffResult {
        public enum ChangeType {
            ADDED, DELETED, UPDATED, UNCHANGED
        }

        private final String filePath;
        private final ChangeType changeType;
        private final FileMetadata oldFileMeta; // Can be null for ADDED files
        private final FileMetadata newFileMeta; // Can be null for DELETED files

        // Constructor is private. Use factory methods below.
        private DiffResult(String filePath, ChangeType changeType, FileMetadata oldFileMeta, FileMetadata newFileMeta) {
            this.filePath = filePath;
            this.changeType = changeType;
            this.oldFileMeta = oldFileMeta;
            this.newFileMeta = newFileMeta;
        }

        // Factory methods for creating different types of DiffResults
        public static DiffResult added(FileMetadata newFile) {
            return new DiffResult(newFile.getFilePath(), ChangeType.ADDED, null, newFile);
        }

        public static DiffResult deleted(FileMetadata oldFile) {
            return new DiffResult(oldFile.getFilePath(), ChangeType.DELETED, oldFile, null);
        }

        public static DiffResult updated(FileMetadata oldFile, FileMetadata newFile) {
            return new DiffResult(oldFile.getFilePath(), ChangeType.UPDATED, oldFile, newFile);
        }

        public static DiffResult unchanged(FileMetadata file) {
            return new DiffResult(file.getFilePath(), ChangeType.UNCHANGED, file, file);
        }

        // Getters
        public String getFilePath() { return filePath; }
        public ChangeType getChangeType() { return changeType; }
        public FileMetadata getOldFileMeta() { return oldFileMeta; }
        public FileMetadata getNewFileMeta() { return newFileMeta; }

        @Override
        public String toString() {
            switch (changeType) {
                case ADDED:
                    return "ADDED:   " + filePath + " (Size: " + newFileMeta.getFileSize() + " bytes)";
                case DELETED:
                    return "DELETED: " + filePath;
                case UPDATED:
                    return "UPDATED: " + filePath + " (Size: " + oldFileMeta.getFileSize() + " -> " + newFileMeta.getFileSize() + " bytes, Hash changed)";
                case UNCHANGED:
                    return "UNCHANGED: " + filePath;
                default:
                    return filePath + ": " + changeType;
            }
        }
    }

    /**
     * Compares two snapshots and returns a list of differences.
     * This is the core comparison algorithm using in-order traversal.
     *
     * @param snapshotA The older snapshot (can be null to simulate an empty snapshot).
     * @param snapshotB The newer snapshot.
     * @return A List of DiffResult objects detailing all changes.
     */
    public List<DiffResult> compare(Snapshot snapshotA, Snapshot snapshotB) {
        List<DiffResult> differences = new ArrayList<>();

        // Use the BST's public inOrderTraversal method to get sorted lists
        List<FileMetadata> listA = new ArrayList<>();
        List<FileMetadata> listB = new ArrayList<>();

        if (snapshotA != null && !snapshotA.getFileTree().isEmpty()) {
            snapshotA.getFileTree().inOrderTraversal((key, value) -> listA.add(value));
        }
        if (snapshotB != null && !snapshotB.getFileTree().isEmpty()) {
            snapshotB.getFileTree().inOrderTraversal((key, value) -> listB.add(value));
        }

        // Perform a merge-like operation on the two sorted lists
        int i = 0, j = 0;
        while (i < listA.size() && j < listB.size()) {
            FileMetadata fileA = listA.get(i);
            FileMetadata fileB = listB.get(j);

            int comp = fileA.getFilePath().compareTo(fileB.getFilePath());
            if (comp < 0) {
                // File exists only in snapshot A -> it was deleted
                differences.add(DiffResult.deleted(fileA));
                i++;
            } else if (comp > 0) {
                // File exists only in snapshot B -> it was added
                differences.add(DiffResult.added(fileB));
                j++;
            } else {
                // File exists in both -> check if it was updated
                if (!fileA.getFileHash().equals(fileB.getFileHash())) {
                    differences.add(DiffResult.updated(fileA, fileB));
                } else {
                    differences.add(DiffResult.unchanged(fileA)); // Optional: usually we skip unchanged
                }
                i++;
                j++;
            }
        }

        // Add any remaining files from listA (deleted)
        while (i < listA.size()) {
            differences.add(DiffResult.deleted(listA.get(i)));
            i++;
        }
        // Add any remaining files from listB (added)
        while (j < listB.size()) {
            differences.add(DiffResult.added(listB.get(j)));
            j++;
        }

        return differences;
    }

    /**
     * Prints a formatted diff report to the console.
     *
     * @param diffResults The list of differences from the compare() method.
     * @param snapshotAId The ID of the older snapshot (for the report header).
     * @param snapshotBId The ID of the newer snapshot.
     */
    public void printDiffReport(List<DiffResult> diffResults, int snapshotAId, int snapshotBId) {
        System.out.println("\n==========================================");
        System.out.println("DIFF REPORT: Snapshot #" + snapshotAId + " → Snapshot #" + snapshotBId);
        System.out.println("==========================================");

        boolean changesFound = false;

        for (DiffResult result : diffResults) {
            // Typically, we skip UNCHANGED files in the report
            if (result.getChangeType() != DiffResult.ChangeType.UNCHANGED) {
                System.out.println(result);
                changesFound = true;
            }
        }

        if (!changesFound) {
            System.out.println("No changes detected.");
        }
        System.out.println("==========================================\n");
    }
}