package oel.tasks;

import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import javax.imageio.ImageIO;
import javax.swing.*;

/** Runs the lab scenarios through Swing controls and verifies the resulting state. */
public final class TaskManagerTest {
    public static void main(String[] args) throws Exception {
        SwingUtilities.invokeAndWait(TaskManagerTest::exercise);
    }

    private static void exercise() {
        TaskStore store = new TaskStore();
        TaskManagerApp app = new TaskManagerApp(store);
        app.setVisible(true);
        try {
            List<Component> controls = descendants(app.getContentPane());
            JTable table = find(controls, JTable.class, item -> true);
            JTextField title = find(controls, JTextField.class, item -> true);
            JTextArea description = find(controls, JTextArea.class, item -> true);
            JComboBox<?> status = find(controls, JComboBox.class, item -> true);
            JButton add = find(controls, JButton.class, item -> item.getText().equals("Add Task"));
            JButton update = find(controls, JButton.class, item -> item.getText().equals("Update Task"));
            JButton delete = find(controls, JButton.class, item -> item.getText().equals("Delete Task"));

            check(table.getRowCount() == 0 && hasLabel(controls, "No tasks yet. Add a task to get started."),
                    "TC04 Empty list displays an instruction");
            capture(app, "01-empty");

            add.doClick();
            check(store.all().isEmpty() && hasLabel(controls, "Title is required."),
                    "TC02 Empty title displays validation and creates no task");
            capture(app, "02-validation");

            title.setText("  ");
            add.doClick();
            check(store.all().isEmpty() && hasLabel(controls, "Title is required."),
                    "TC08 Whitespace-only title is rejected");

            title.setText("  Write lab report  ");
            description.setText("Summarize the test results");
            add.doClick();
            check(store.all().equals(List.of(new Task(1, "Write lab report",
                    "Summarize the test results", TaskStatus.PENDING)))
                    && hasLabel(controls, "Task added."), "TC01 Valid task is added with a trimmed title");

            title.setText("Review Javadoc");
            description.setText("Open the generated API reference");
            status.setSelectedItem(TaskStatus.IN_PROGRESS);
            add.doClick();
            check(table.getRowCount() == 2 && table.getValueAt(0, 0).equals("Write lab report")
                    && table.getValueAt(1, 0).equals("Review Javadoc")
                    && table.getValueAt(1, 2) == TaskStatus.IN_PROGRESS,
                    "TC03 All tasks appear with their statuses");

            table.getRowSorter().toggleSortOrder(0);
            table.setRowSelectionInterval(0, 0);
            title.setText("Check Javadoc");
            description.setText("Review the class and method pages");
            status.setSelectedItem(TaskStatus.COMPLETED);
            update.doClick();
            check(store.all().get(1).equals(new Task(2, "Check Javadoc",
                    "Review the class and method pages", TaskStatus.COMPLETED))
                    && store.all().get(0).id() == 1 && hasLabel(controls, "Task updated."),
                    "TC05 Sorted selection updates the correct task and saves Completed status");
            capture(app, "03-updated-list");

            update.doClick();
            check(hasLabel(controls, "Select a task first.") && table.getRowCount() == 2,
                    "TC09 Update without selection displays guidance");

            table.setRowSelectionInterval(0, 0);
            title.setText("");
            update.doClick();
            check(store.all().get(1).title().equals("Check Javadoc") && hasLabel(controls, "Title is required."),
                    "TC10 Invalid update preserves existing details");
            store.delete(2);
            title.setText("Missing task");
            update.doClick();
            check(hasLabel(controls, "That task no longer exists.") && table.getRowCount() == 1,
                    "TC06 Update of a missing task displays an error and refreshes the list");

            table.setRowSelectionInterval(0, 0);
            delete.doClick();
            check(store.all().isEmpty() && table.getRowCount() == 0 && hasLabel(controls, "Task deleted."),
                    "TC07 Delete removes the selected task and refreshes the list");
            System.out.println("10 checks passed.");
        } finally {
            app.dispose();
        }
    }

    private static boolean hasLabel(List<Component> controls, String text) {
        return controls.stream().anyMatch(control -> control instanceof JLabel label && text.equals(label.getText()));
    }

    private static <T> T find(List<Component> controls, Class<T> type, Predicate<T> matches) {
        return controls.stream().filter(type::isInstance).map(type::cast).filter(matches).findFirst().orElseThrow();
    }

    private static List<Component> descendants(Container parent) {
        List<Component> result = new ArrayList<>();
        for (Component component : parent.getComponents()) {
            result.add(component);
            if (component instanceof Container child) {
                result.addAll(descendants(child));
            }
        }
        return result;
    }

    private static void check(boolean condition, String expectation) {
        if (!condition) {
            throw new AssertionError(expectation);
        }
        System.out.println("PASS " + expectation);
    }

    private static void capture(TaskManagerApp app, String name) {
        String destination = System.getProperty("taskmanager.screenshots");
        if (destination == null) {
            return;
        }
        JRootPane window = app.getRootPane();
        BufferedImage image = new BufferedImage(window.getWidth(), window.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();
        try {
            window.printAll(graphics);
            Path directory = Files.createDirectories(Path.of(destination));
            ImageIO.write(image, "png", directory.resolve(name + ".png").toFile());
        } catch (java.io.IOException ex) {
            throw new java.io.UncheckedIOException(ex);
        } finally {
            graphics.dispose();
        }
    }
}
