package com.filetracker.models;

import java.nio.file.Path;
import java.nio.file.Files;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HexFormat;
import java.io.Serializable;


public class FileMetadata implements Serializable {
    private static final long serialVersionUID = 1L;
    private String filePath;        // Relative path (used as key in BST)
    private String absolutePath;    // Absolute path (used for restore)
    private long fileSize;          // Size of file in bytes
    private long lastModified;      // Last modified time in milliseconds since epoch
    private String fileHash;        // SHA-256 hash of the file contents


    public FileMetadata(Path path, Path baseDir) throws IOException {
        this.absolutePath = path.toString();
        this.filePath = baseDir.relativize(path).toString(); // Calculate relative path
        this.fileSize = Files.size(path);
        this.lastModified = Files.getLastModifiedTime(path).toMillis();
        this.fileHash = calculateFileHash(path);
    }


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