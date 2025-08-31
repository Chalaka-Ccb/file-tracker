package com.filetracker.core;

import com.filetracker.models.FileMetadata;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

/**
 * FileUtils
 * ----------
 * Provides utility methods for working with files and directories.
 * Core functionality includes recursively traversing a directory tree
 * to collect file metadata.
 * This implements a Depth-First Search (DFS) algorithm.
 */
public class FileUtils {

    /**
     * Scans a given directory path recursively and collects metadata for all files.
     *
     * @param directoryPath The path of the directory to scan.
     * @param baseDir The base directory path for calculating relative paths.
     * @return A List of FileMetadata objects for every file found.
     * @throws IOException If an I/O error occurs during traversal.
     */
    public static List<FileMetadata> scanDirectory(String directoryPath, Path baseDir) throws IOException {
        List<FileMetadata> fileList = new ArrayList<>();
        Path startPath = Paths.get(directoryPath);

        // Check if the path exists and is a directory
        if (!Files.exists(startPath)) {
            throw new IOException("Error: The directory '" + directoryPath + "' does not exist.");
        }
        if (!Files.isDirectory(startPath)) {
            throw new IOException("Error: '" + directoryPath + "' is not a directory.");
        }

        // Use Files.walkFileTree for efficient and controlled recursion.
        // This is Java's built-in way to perform a DFS.
        Files.walkFileTree(startPath, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                // This method is called for every file found during the walk.
                if (attrs.isRegularFile()) { // Ignore symbolic links, directories, etc.
                    try {
                        FileMetadata metadata = new FileMetadata(file, baseDir);
                        fileList.add(metadata);
                    } catch (IOException e) {
                        // Log the error for a specific file but continue processing others
                        System.err.println("Could not read file: " + file + " - " + e.getMessage());
                    }
                }
                return FileVisitResult.CONTINUE; // Continue the traversal
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                // This method is called if a file could not be visited (e.g., permission denied).
                System.err.println("Failed to access: " + file + " - " + exc.getMessage());
                return FileVisitResult.CONTINUE; // Skip this file and continue
            }
        });

        return fileList;
    }

    /**
     * A simpler method to get a list of all file paths in a directory (without metadata).
     * This can be useful for debugging or other future functionalities.
     *
     * @param directoryPath The path to the directory.
     * @return A list of file paths as strings.
     * @throws IOException If an I/O error occurs.
     */
    public static List<String> listAllFiles(String directoryPath) throws IOException {
        List<String> paths = new ArrayList<>();
        Path startPath = Paths.get(directoryPath);

        Files.walkFileTree(startPath, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                if (attrs.isRegularFile()) {
                    paths.add(file.toString());
                }
                return FileVisitResult.CONTINUE;
            }
        });
        return paths;
    }
}