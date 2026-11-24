package oel.tasks;

import java.util.Objects;

/**
 * An immutable task with a stable identifier and editable task details.
 *
 * @param id stable identifier
 * @param title required nonblank title
 * @param description optional description, represented by an empty string when omitted
 * @param status workflow state
 * @author OEL Task Manager
 */
public record Task(long id, String title, String description, TaskStatus status) {
    /**
     * Creates a task and validates its details.
     *
     * @param id stable identifier
     * @param title required nonblank title
     * @param description optional description
     * @param status required workflow state
     * @throws IllegalArgumentException if the title is missing or blank
     * @throws NullPointerException if the description or status is missing
     */
    public Task {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Title is required.");
        }
        title = title.strip();
        Objects.requireNonNull(description, "Description is required.");
        Objects.requireNonNull(status, "Status is required.");
    }
}
