# Task Manager | Open-Ended Lab

A Java Swing to-do application for adding, viewing, updating and deleting tasks. Each task has a title, an optional description and a status of Pending, In Progress or Completed. The app stores tasks in memory for the current session. Closing the app clears the list.

## Run in Apache NetBeans

1. Use JDK 17 or newer. This project was built and run with Apache NetBeans 24 and JDK 23.0.2.
2. Choose **File → Open Project** and select the `TaskManager` folder that contains `build.xml` and `nbproject`.
3. Select **Task Manager** in Projects, then use **Run Project** from its right-click menu. If NetBeans prompts you to trust the build script, review the included `build.xml` before running it.
4. Use the same project menu for **Build**, **Test**, **Debug**, or **Generate Javadoc**. For a debugger walkthrough, set a breakpoint in `TaskStore.update`, start **Debug**, and use **Update Task** on a selected row.

With Ant on the command path, `ant clean jar` builds `build/task-manager.jar`, `ant test` runs the Swing checks, and `ant javadoc` regenerates the API pages. A graphical desktop is needed for the Swing tests. To launch the built JAR use `java -jar build/task-manager.jar`.

## Design

| Type | Responsibility |
|---|---|
| `Task` | Immutable record with a stable ID and a validated title |
| `TaskStatus` | Enum with the three workflow states |
| `TaskStore` | Adds, snapshots, updates, and deletes session tasks by ID |
| `TaskManagerApp` | Swing form, sortable table and inline feedback |
| `TaskManagerTest` | Drives Swing controls and checks store and window state |

The form uses stable IDs to update or delete the selected task even after the table is sorted. Invalid edits preserve the original task. An attempt to update a task that has disappeared shows an error and refreshes the table.

## Brief live demo

1. Run the project and point out the empty-list message. Click **Add Task** with an empty title to show validation.
2. Enter `Write lab report` and `Summarize the test results`. Keep **Pending** and click **Add Task**. Add a second task with **In Progress**.
3. Select the second row, change the title or description, choose **Completed**, then click **Update Task**. Point out the new values in the table.
4. Select a row and click **Delete Task**. Explain how the stable ID identifies the row and how the table refreshes after changes.
5. Show the API pages, run **Test**, and show the Git history.

## Lab evidence

- [Test cases and results](evidence/test-results.md)
- Screenshots of the [empty state](evidence/screenshots/01-empty.png), [title validation](evidence/screenshots/02-validation.png), and [updated task list](evidence/screenshots/03-updated-list.png)
- [Generated Javadoc HTML](docs/api/index.html), produced by the JDK Javadoc tool and reviewed in Chrome
- The delivery includes a Git bundle and a log of `git status`, `git log`, and `git diff`. To restore the history from the archive, run `git clone TaskManager-history.bundle TaskManager-with-history` from the extracted folder. To see the GUI revision, run `git log --oneline` then `git show 3a2057b` in that clone.

GitHub publication is pending a destination repository. To publish after creating an empty repository in your account, run `git remote add origin <repository-url>` and `git push -u origin main` from the local repository or the bundle clone.
