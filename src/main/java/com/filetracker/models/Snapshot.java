package com.filetracker.models;

import com.filetracker.core.BST;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.Serializable;


public class Snapshot implements Serializable {
    private static final long serialVersionUID = 1L;
    private int snapshotId;          // Unique ID for snapshot
    private LocalDateTime timestamp; // Time when snapshot was taken
    private BST<FileMetadata> fileTree; // BST holding files of this snapshot


    public Snapshot(int snapshotId) {
        this.snapshotId = snapshotId;
        this.timestamp = LocalDateTime.now(); // Set current time
        this.fileTree = new BST<>(); // Empty BST initially
    }

    // --------- Methods --------- //


    public void addFile(FileMetadata fileMetadata) {
        fileTree.insert(fileMetadata.getFilePath(), fileMetadata);
    }


    public BST<FileMetadata> getFileTree() {
        return fileTree;
    }

    public int getSnapshotId() {
        return snapshotId;
    }

    public String getFormattedTimestamp() {
        return timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    @Override
    public String toString() {
        return "Snapshot{" +
                "ID=" + snapshotId +
                ", timestamp=" + getFormattedTimestamp() +
                ", totalFiles=" + fileTree.size() +
                '}';
    }
}