package oel.tasks;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Holds tasks in insertion order for the current application session.
 *
 * @author OEL Task Manager
 */
public final class TaskStore {
    /** Tasks keyed by stable identifier. */
    private final Map<Long, Task> tasks = new LinkedHashMap<>();
    /** Identifier reserved for the next successful addition. */
    private long nextId = 1;

    /** Creates an empty task store. */
    public TaskStore() {
    }

    /**
     * Adds a validated task.
     *
     * @param title required title
     * @param description optional description
     * @param status workflow state
     * @return the added task
     */
    public Task add(String title, String description, TaskStatus status) {
        Task task = new Task(nextId, title, description, status);
        tasks.put(nextId++, task);
        return task;
    }

    /**
     * Returns a read-only snapshot of all tasks.
     *
     * @return tasks in insertion order
     */
    public List<Task> all() {
        return List.copyOf(tasks.values());
    }

    /**
     * Replaces the details of an existing task.
     *
     * @param id identifier of the task to edit
     * @param title new title
     * @param description new description
     * @param status new workflow state
     * @return the updated task
     * @throws NoSuchElementException if the task does not exist
     */
    public Task update(long id, String title, String description, TaskStatus status) {
        requireTask(id);
        Task task = new Task(id, title, description, status);
        tasks.put(id, task);
        return task;
    }

    /**
     * Deletes an existing task.
     *
     * @param id identifier of the task to delete
     * @throws NoSuchElementException if the task does not exist
     */
    public void delete(long id) {
        requireTask(id);
        tasks.remove(id);
    }

    /**
     * Checks that an identifier still exists.
     *
     * @param id task identifier
     * @throws NoSuchElementException if the task does not exist
     */
    private void requireTask(long id) {
        if (!tasks.containsKey(id)) {
            throw new NoSuchElementException("That task no longer exists.");
        }
    }
}
