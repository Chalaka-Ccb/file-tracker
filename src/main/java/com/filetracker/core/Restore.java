package com.filetracker.core;

import com.filetracker.models.FileMetadata;
import com.filetracker.models.Snapshot;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

/**
 * Restore
 * -------
 * Provides functionality to restore a directory to the state captured in a previous snapshot.
 * This is one of the key enhancements to the basic tracking tool.
 * Algorithm:
 * 1. Identify files to delete (files present now but not in the snapshot).
 * 2. Identify files to copy (files present in the snapshot).
 * 3. Perform the deletion and copying.
 */
public class Restore {

    /**
     * Restores the target directory to the state stored in the given snapshot.
     *
     * @param targetDirectory The path of the directory to restore.
     * @param snapshot        The snapshot to restore to.
     * @throws IOException If an I/O error occurs during file operations.
     */
    public void restoreSnapshot(String targetDirectory, Snapshot snapshot) throws IOException {
        Path targetPath = Paths.get(targetDirectory);

        // Validate target directory
        if (!Files.exists(targetPath) || !Files.isDirectory(targetPath)) {
            throw new IOException("Error: Target directory '" + targetDirectory + "' does not exist or is not a directory.");
        }

        System.out.println("Preparing to restore directory to Snapshot #" + snapshot.getSnapshotId() + "...");

        // **Algorithm Step 1: Plan Deletions**
        // Get a list of all files CURRENTLY in the target directory.
        // We will later delete any file that is not in the snapshot.
        List<Path> currentFiles = new ArrayList<>();
        Files.walkFileTree(targetPath, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                if (attrs.isRegularFile()) {
                    currentFiles.add(file);
                }
                return FileVisitResult.CONTINUE;
            }
        });

        // **Algorithm Step 2: Plan Copies & Identify Deletions**
        // We need to know which files from the current directory to keep.
        // We will use the snapshot's BST to check each current file.
        List<Path> filesToDelete = new ArrayList<>();
        BST<FileMetadata> snapshotTree = snapshot.getFileTree();

        for (Path currentFile : currentFiles) {
            // Get the relative path of the current file compared to the target directory
            // This is the key used in the snapshot BST
            String relativePath = targetPath.relativize(currentFile).toString();

            // If the snapshot does NOT contain this relative path, mark it for deletion
            if (snapshotTree.search(relativePath) == null) {
                filesToDelete.add(currentFile);
            }
        }

        // **Algorithm Step 3: Execute Deletions**
        System.out.println("Removing files not present in snapshot...");
        for (Path fileToDelete : filesToDelete) {
            try {
                Files.deleteIfExists(fileToDelete);
                System.out.println("  DELETED: " + targetPath.relativize(fileToDelete));
            } catch (IOException e) {
                System.err.println("  Failed to delete: " + fileToDelete + " - " + e.getMessage());
            }
        }

        // **Algorithm Step 4: Execute Copy/Overwrite**
        // Traverse the snapshot's BST and copy every file to the target directory.
        System.out.println("Copying files from snapshot...");
        snapshotTree.inOrderTraversal((filePath, fileMeta) -> {
            try {
                // Use the ABSOLUTE path stored in the metadata to find the source file
                Path sourceFilePath = Paths.get(fileMeta.getAbsolutePath());
                Path destinationFilePath = targetPath.resolve(filePath); // Build the path in the target directory

                // Create parent directories if they don't exist
                Files.createDirectories(destinationFilePath.getParent());
                // Copy the file, overwriting if it already exists
                Files.copy(sourceFilePath, destinationFilePath, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("  COPIED: " + filePath);
            } catch (IOException e) {
                System.err.println("  Failed to copy: " + filePath + " - " + e.getMessage());
            }
        });

        System.out.println("Restore to Snapshot #" + snapshot.getSnapshotId() + " completed successfully.");
    }
}