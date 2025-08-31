package com.filetracker.models;

import java.nio.file.Path;
import java.nio.file.Files;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HexFormat;
import java.io.Serializable;

/**
 * FileMetadata
 * ----------------------
 * This class represents the metadata of a single file in a snapshot.
 * It stores:
 *  - file path (relative to the snapshotted directory) - used as BST key
 *  - absolute file path - used for restore operations
 *  - file size (in bytes)
 *  - last modified timestamp
 *  - file hash (SHA-256) to detect content changes
 */
public class FileMetadata implements Serializable {
    private static final long serialVersionUID = 1L;
    private String filePath;        // Relative path (used as key in BST)
    private String absolutePath;    // Absolute path (used for restore)
    private long fileSize;          // Size of file in bytes
    private long lastModified;      // Last modified time in milliseconds since epoch
    private String fileHash;        // SHA-256 hash of the file contents

    /**
     * Constructor for FileMetadata.
     * Extracts size, lastModified, and hash from a given file path.
     * Calculates the relative path based on the provided base directory.
     *
     * @param path Path object of the file
     * @param baseDir Base directory path for calculating relative path
     * @throws IOException if file cannot be read
     */
    public FileMetadata(Path path, Path baseDir) throws IOException {
        this.absolutePath = path.toString();
        this.filePath = baseDir.relativize(path).toString(); // Calculate relative path
        this.fileSize = Files.size(path);
        this.lastModified = Files.getLastModifiedTime(path).toMillis();
        this.fileHash = calculateFileHash(path);
    }

    /**
     * Calculates SHA-256 hash of a file.
     * Used to detect if file content has changed.
     *
     * @param path Path to the file
     * @return Hexadecimal string of hash
     * @throws IOException if file cannot be read
     */
    private String calculateFileHash(Path path) throws IOException {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] fileBytes = Files.readAllBytes(path);
            byte[] hashBytes = digest.digest(fileBytes);

            // Convert byte array to hex string (Java 17+ has HexFormat)
            return HexFormat.of().formatHex(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found!", e);
        }
    }

    // ---------- Getters ---------- //
    public String getFilePath() {
        return filePath;
    }

    public String getAbsolutePath() {
        return absolutePath;
    }

    public long getFileSize() {
        return fileSize;
    }

    public long getLastModified() {
        return lastModified;
    }

    public String getFileHash() {
        return fileHash;
    }

    @Override
    public String toString() {
        return "FileMetadata{" +
                "relativePath='" + filePath + '\'' +
                ", absolutePath='" + absolutePath + '\'' +
                ", size=" + fileSize +
                ", lastModified=" + Instant.ofEpochMilli(lastModified) +
                ", hash='" + fileHash + '\'' +
                '}';
    }
}