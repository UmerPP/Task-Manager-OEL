package oel.tasks;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.List;
import java.util.NoSuchElementException;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 * Swing window for creating, viewing, editing and deleting tasks.
 *
 * @author OEL Task Manager
 */
public final class TaskManagerApp extends JFrame {
    /** Serialization version for the Swing frame. */
    private static final long serialVersionUID = 1L;
    /** Session task store. */
    private final TaskStore store;
    /** Tasks currently displayed in table model order. */
    private List<Task> visibleTasks = List.of();
    /** Title input. */
    private final JTextField title = new JTextField();
    /** Description input. */
    private final JTextArea description = new JTextArea(5, 22);
    /** Available workflow states. */
    private final JComboBox<TaskStatus> status = new JComboBox<>(TaskStatus.values());
    /** Inline feedback for successful actions and errors. */
    private final JLabel feedback = new JLabel(" ");
    /** Summary above the task list. */
    private final JLabel count = new JLabel();
    /** Read-only table model. */
    private final TaskTableModel rows = new TaskTableModel();
    /** View of current tasks. */
    private final JTable table = new JTable(rows);

    /**
     * Builds the window on the Swing event dispatch thread.
     *
     * @param store task store to display and edit
     */
    public TaskManagerApp(TaskStore store) {
        super("Task Manager");
        this.store = store;
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel content = new JPanel(new BorderLayout(20, 18));
        content.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        setContentPane(content);
        JLabel heading = new JLabel("Task Manager");
        heading.setFont(heading.getFont().deriveFont(Font.BOLD, 26f));
        content.add(heading, BorderLayout.NORTH);
        content.add(feedback, BorderLayout.SOUTH);

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFillsViewportHeight(true);
        table.setAutoCreateRowSorter(true);
        table.setRowHeight(26);
        JScrollPane list = new JScrollPane(table);
        list.setPreferredSize(new Dimension(475, 340));

        JPanel taskList = new JPanel(new BorderLayout(0, 8));
        taskList.add(count, BorderLayout.NORTH);
        taskList.add(list, BorderLayout.CENTER);
        content.add(taskList, BorderLayout.CENTER);

        JPanel editor = new JPanel();
        editor.setLayout(new BoxLayout(editor, BoxLayout.Y_AXIS));
        description.setLineWrap(true);
        description.setWrapStyleWord(true);
        addField(editor, "Title", title, title);
        addField(editor, "Description", new JScrollPane(description), description);
        addField(editor, "Status", status, status);
        JPanel buttons = new JPanel(new GridLayout(2, 2, 6, 6));
        buttons.setAlignmentX(LEFT_ALIGNMENT);
        JButton add = new JButton("Add Task");
        JButton update = new JButton("Update Task");
        JButton delete = new JButton("Delete Task");
        JButton clear = new JButton("Clear Form");
        buttons.add(add);
        buttons.add(update);
        buttons.add(delete);
        buttons.add(clear);
        editor.add(buttons);
        editor.add(Box.createVerticalGlue());
        content.add(editor, BorderLayout.EAST);

        table.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                Task task = selectedTask();
                if (task != null) {
                    title.setText(task.title());
                    description.setText(task.description());
                    status.setSelectedItem(task.status());
                }
            }
        });
        add.addActionListener(event -> change(false, false));
        update.addActionListener(event -> change(true, false));
        delete.addActionListener(event -> change(true, true));
        clear.addActionListener(event -> {
            clearForm();
            showFeedback("Form cleared.", false);
        });

        refresh();
        pack();
        setMinimumSize(getSize());
        setLocationRelativeTo(null);
    }

    /**
     * Places a labelled input in the editor.
     *
     * @param editor panel that contains the form
     * @param label label text
     * @param field component to display
     * @param input component to focus from the label
     */
    private static void addField(JPanel editor, String label, JComponent field, JComponent input) {
        JLabel caption = new JLabel(label);
        caption.setLabelFor(input);
        caption.setAlignmentX(LEFT_ALIGNMENT);
        field.setAlignmentX(LEFT_ALIGNMENT);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, field.getPreferredSize().height));
        editor.add(caption);
        editor.add(Box.createVerticalStrut(4));
        editor.add(field);
        editor.add(Box.createVerticalStrut(12));
    }

    /**
     * Performs a requested list mutation and reports the result.
     *
     * @param needsSelection whether the action targets an existing task
     * @param remove whether the action deletes the selected task
     */
    private void change(boolean needsSelection, boolean remove) {
        Task task = selectedTask();
        if (needsSelection && task == null) {
            showFeedback("Select a task first.", true);
            return;
        }

        try {
            TaskStatus state = (TaskStatus) status.getSelectedItem();
            String message;
            if (remove) {
                store.delete(task.id());
                message = "Task deleted.";
            } else if (needsSelection) {
                store.update(task.id(), title.getText(), description.getText(), state);
                message = "Task updated.";
            } else {
                store.add(title.getText(), description.getText(), state);
                message = "Task added.";
            }
            refresh();
            clearForm();
            showFeedback(message, false);
        } catch (IllegalArgumentException ex) {
            showFeedback(ex.getMessage(), true);
            title.requestFocusInWindow();
        } catch (NoSuchElementException ex) {
            refresh();
            clearForm();
            showFeedback(ex.getMessage(), true);
        }
    }

    /**
     * Reads the selected task, translating sorted table row indexes.
     *
     * @return the selected task or {@code null} if no row is selected
     */
    private Task selectedTask() {
        int row = table.getSelectedRow();
        return row < 0 ? null : visibleTasks.get(table.convertRowIndexToModel(row));
    }

    /** Updates the table and its task count from the store. */
    private void refresh() {
        table.clearSelection();
        visibleTasks = store.all();
        rows.setRowCount(0);
        for (Task task : visibleTasks) {
            rows.addRow(new Object[]{task.title(), task.description(), task.status()});
        }
        int size = visibleTasks.size();
        count.setText(size == 0 ? "No tasks yet. Add a task to get started."
                : size == 1 ? "1 task" : size + " tasks");
    }

    /** Clears the editor and restores Pending as the default status. */
    private void clearForm() {
        table.clearSelection();
        title.setText("");
        description.setText("");
        status.setSelectedItem(TaskStatus.PENDING);
        title.requestFocusInWindow();
    }

    /**
     * Shows a message below the editor and table.
     *
     * @param message text identifying the result
     * @param error whether the message describes a failed action
     */
    private void showFeedback(String message, boolean error) {
        feedback.setForeground(error ? new Color(165, 31, 38) : new Color(20, 105, 76));
        feedback.setText(message);
    }

    /**
     * Launches the task manager with an empty session.
     *
     * @param args command-line arguments, currently unused
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TaskManagerApp(new TaskStore()).setVisible(true));
    }

    /**
     * Read-only table model for tasks edited through the form.
     *
     * @author OEL Task Manager
     */
    private static final class TaskTableModel extends DefaultTableModel {
        /** Serialization version for the table model. */
        private static final long serialVersionUID = 1L;

        /** Creates the three task columns. */
        TaskTableModel() {
            super(new String[]{"Title", "Description", "Status"}, 0);
        }

        /**
         * Keeps table cells read-only.
         *
         * @param row model row index
         * @param column model column index
         * @return always {@code false}
         */
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    }
}
