# File Tracker – Simple File Version Control

A lightweight command-line tool that helps you track changes in your folders over time. Useful for personal projects, document versioning, or any directory you want to monitor.

## Quick Start

### Prerequisites
- Java 17 or later (Download from adoptium.net)
- Maven (Download from maven.apache.org)

### Installation & Running
1. Download or clone the project repository.
2. Open a command prompt in the project folder.
3. Run the application:
```bash
mvn compile exec:java

```
## First Time Usage

### Take your first snapshot:

-**Enter command**: snapshot
-Enter directory path to snapshot: C:\MyProject

---
Make some changes to your files and take another snapshot:

-**Enter command**: snapshot C:\MyProject

---

See what changed:

**Enter command**: diff latest

---

### Commands

- snapshot [path] – Capture current state of folder
- diff latest – Compare last two snapshots
- diff – Compare specific snapshots by entering their numbers
- list history – Show all snapshots taken
- restore – Revert files to previous state
- compress – Save storage space
- help – Show all commands
- exit – Close the application

---

### Tips

- Take snapshots regularly, especially after major changes or at the end of the day.
- Use descriptive comments for each snapshot.
- Compress old snapshots to save storage.
- Track important folders such as projects, documents, or config files.

## Troubleshooting


- "Java not found": Install Java 17+ from adoptium.net

- "Maven not found": Install Maven from maven.apache.org

- "Directory not found": Check path spelling and permissions

- Run help to see all commands.

- Ensure Java version is 17 or higher: java -version

- Ensure you have read access to the folders you want to track.

## Why Use This Tool?

- Simple – No complex setup or commands
- Local – All data stays on your computer
- Lightweight – Uses minimal resources
- Free – No costs or subscriptions
- Perfect for students, developers, writers, researchers, or anyone who wants to track file changes without learning Git.
