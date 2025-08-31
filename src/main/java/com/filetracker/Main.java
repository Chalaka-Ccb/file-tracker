package com.filetracker; // <-- Note: just com.filetracker, NOT com.filetracker.core

import com.filetracker.core.*;
import com.filetracker.models.Snapshot;

import java.io.IOException;
import java.util.Scanner;

/**
 * Main
 * -----
 * The entry point of the application.
 * Provides a command-line interface for interacting with the file tracker.
 * Handles user input and coordinates between different components.
 */
public class Main {

    private static SnapshotManager snapshotManager = new SnapshotManager();
    private static DiffEngine diffEngine = new DiffEngine();
    private static Restore restoreEngine = new Restore();
    private static Compression compressionEngine = new Compression();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("==========================================");
        System.out.println("    File Tracker - Lightweight VCS");
        System.out.println("==========================================");

        boolean running = true;
        while (running) {
            printMenu();
            String command = scanner.nextLine().trim();

            try {
                switch (command.toLowerCase()) {
                    case "snapshot":
                        takeSnapshotCommand();
                        break;
                    case "diff latest":
                        diffLatestCommand();
                        break;
                    case "diff":
                        diffSpecificCommand();
                        break;
                    case "list history":
                        listHistoryCommand();
                        break;
                    case "restore":
                        restoreCommand();
                        break;
                    case "compress":
                        compressCommand();
                        break;
                    case "exit":
                        running = false;
                        System.out.println("Exiting File Tracker. Goodbye!");
                        break;
                    case "help":
                        printHelp();
                        break;
                    default:
                        System.out.println("Unknown command. Type 'help' for available commands.");
                }
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
                e.printStackTrace();
            }
            System.out.println();
        }
        scanner.close();
    }

    private static void printMenu() {
        System.out.println("\nAvailable commands:");
        System.out.println("  snapshot       - Take a snapshot of a directory");
        System.out.println("  diff latest    - Compare two most recent snapshots");
        System.out.println("  diff           - Compare specific snapshots by index");
        System.out.println("  list history   - Show snapshot timeline");
        System.out.println("  restore        - Restore directory to a snapshot");
        System.out.println("  compress       - Compress snapshots (delta storage)");
        System.out.println("  help           - Show this help menu");
        System.out.println("  exit           - Exit the application");
        System.out.print("\nEnter command: ");
    }

    private static void printHelp() {
        System.out.println("\n=== File Tracker Help ===");
        System.out.println("snapshot: Prompts for directory path and creates a new snapshot");
        System.out.println("diff latest: Shows changes between two most recent snapshots");
        System.out.println("diff: Prompts for two snapshot indices to compare");
        System.out.println("list history: Displays all snapshots with IDs and timestamps");
        System.out.println("restore: Prompts for snapshot ID and directory to restore to");
        System.out.println("compress: Compresses snapshots using delta encoding");
        System.out.println("exit: Quits the application");
    }

    private static void takeSnapshotCommand() throws IOException {
        System.out.print("Enter directory path to snapshot: ");
        String directoryPath = scanner.nextLine().trim();

        Snapshot snapshot = snapshotManager.takeSnapshot(directoryPath);
        System.out.println("Snapshot #" + snapshot.getSnapshotId() + " created successfully!");
    }

    private static void diffLatestCommand() {
        if (snapshotManager.getSnapshotCount() < 2) {
            System.out.println("Need at least 2 snapshots to compare.");
            return;
        }

        Snapshot older = snapshotManager.getSecondLatestSnapshot();
        Snapshot newer = snapshotManager.getLatestSnapshot();

        var differences = diffEngine.compare(older, newer);
        diffEngine.printDiffReport(differences, older.getSnapshotId(), newer.getSnapshotId());
    }

    private static void diffSpecificCommand() {
        if (snapshotManager.getSnapshotCount() < 2) {
            System.out.println("Need at least 2 snapshots to compare.");
            return;
        }

        snapshotManager.printHistory();

        System.out.print("Enter first snapshot index: ");
        int index1 = Integer.parseInt(scanner.nextLine());
        System.out.print("Enter second snapshot index: ");
        int index2 = Integer.parseInt(scanner.nextLine());

        Snapshot snap1 = snapshotManager.getSnapshot(index1);
        Snapshot snap2 = snapshotManager.getSnapshot(index2);

        var differences = diffEngine.compare(snap1, snap2);
        diffEngine.printDiffReport(differences, snap1.getSnapshotId(), snap2.getSnapshotId());
    }

    private static void listHistoryCommand() {
        snapshotManager.printHistory();
    }

    private static void restoreCommand() throws IOException {
        if (snapshotManager.getSnapshotCount() == 0) {
            System.out.println("No snapshots available to restore from.");
            return;
        }

        snapshotManager.printHistory();
        System.out.print("Enter snapshot index to restore: ");
        int index = Integer.parseInt(scanner.nextLine());

        System.out.print("Enter target directory to restore to: ");
        String targetDir = scanner.nextLine().trim();

        Snapshot snapshot = snapshotManager.getSnapshot(index);
        restoreEngine.restoreSnapshot(targetDir, snapshot);
    }

    private static void compressCommand() throws IOException {
        if (snapshotManager.getSnapshotCount() == 0) {
            System.out.println("No snapshots to compress.");
            return;
        }

        System.out.print("Enter storage directory for compressed data: ");
        String storageDir = scanner.nextLine().trim();

        // Compress all snapshots in the timeline
        for (int i = 0; i < snapshotManager.getSnapshotCount(); i++) {
            Snapshot snapshot = snapshotManager.getSnapshot(i);
            compressionEngine.compressSnapshot(snapshotManager, snapshot, storageDir);
        }

        System.out.println("Compression completed successfully!");
    }
}