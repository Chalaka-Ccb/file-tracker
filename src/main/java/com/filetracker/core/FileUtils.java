package com.filetracker.core;

import com.filetracker.models.FileMetadata;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;


public class FileUtils {


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