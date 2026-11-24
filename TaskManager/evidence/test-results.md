# Task Manager test results

Apache NetBeans 24 opened the project and launched the application with JDK 23.0.2. The automated Swing suite ran through the bundled Ant with the same JDK. All seven lab cases and three additional regression checks passed.

| ID | Case | Expected result | Observed result |
|---|---|---|---|
| TC01 | Add a valid task | The task is added | Task stored with its title, description and Pending status. Outer title spaces were trimmed. **Pass** |
| TC02 | Add with an empty title | Validation appears | “Title is required.” appeared and the store stayed empty. **Pass** |
| TC03 | View all tasks | All tasks are displayed | The table displayed both tasks and their states, including In Progress. **Pass** |
| TC04 | View an empty list | An empty-state message appears | The table had no rows and displayed “No tasks yet. Add a task to get started.” **Pass** |
| TC05 | Update an existing task | Updated details appear | Sorting the table still targeted the correct task. Title, description and Completed status were saved. **Pass** |
| TC06 | Update a missing task | The error is handled gracefully | A selected task removed from the store before Update Task produced “That task no longer exists.” and the table refreshed. **Pass** |
| TC07 | Delete an existing task | The task is removed | The store and table both became empty and “Task deleted.” appeared. **Pass** |
| TC08 | Add a whitespace-only title | Validation appears | The title was rejected and no task was created. **Pass** |
| TC09 | Update without selecting a row | Selection guidance appears | “Select a task first.” appeared and both rows remained. **Pass** |
| TC10 | Update a task to a blank title | Validation preserves the previous details | Validation appeared and the task kept its original title. **Pass** |

The screenshots were captured from the Swing window during the tests. TC06 simulates a stale selection by removing its task from the store immediately before pressing **Update Task**.
