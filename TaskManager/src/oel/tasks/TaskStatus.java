package oel.tasks;

/**
 * Workflow states that can be assigned to a task.
 *
 * @author OEL Task Manager
 */
public enum TaskStatus {
    /** A task that has not started. */
    PENDING("Pending"),
    /** A task currently being worked on. */
    IN_PROGRESS("In Progress"),
    /** A task that has been finished. */
    COMPLETED("Completed");

    /** Label displayed in the editor and table. */
    private final String label;

    /**
     * Creates a workflow state with a display label.
     *
     * @param label human-readable state name
     */
    TaskStatus(String label) {
        this.label = label;
    }

    /**
     * Returns the state label.
     *
     * @return human-readable state name
     */
    @Override
    public String toString() {
        return label;
    }
}
