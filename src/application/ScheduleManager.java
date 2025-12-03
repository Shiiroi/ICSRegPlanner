package application;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;
import model.Student;
//NEW: class for managing schedules
public class ScheduleManager {
    private Student student;
    private Runnable onScheduleChanged;

    public ScheduleManager(Student student, Runnable onScheduleChanged) {
        this.student = student;
        this.onScheduleChanged = onScheduleChanged;
    }

    // Shows dialog to save current active schedule under a new name
    public void showSaveAsDialog() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Save Schedule");
        dialog.setHeaderText("Save your current schedule with a name");

        ButtonType saveBtn = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nameField = new TextField();
        nameField.setPromptText("e.g., Plan A");
        grid.add(new Label("Schedule Name:"), 0, 0);
        grid.add(nameField, 1, 0);

        dialog.getDialogPane().setContent(grid);
        Platform.runLater(() -> nameField.requestFocus());

        dialog.setResultConverter(button -> 
            (button == saveBtn) ? nameField.getText().trim() : null
        );

        dialog.showAndWait().ifPresent(name -> {
        	if (name.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Invalid Name", "Schedule name cannot be empty.");
                return;
            }
            if (student.getSavedScheduleNames().stream()
                    .anyMatch(existing -> existing.equalsIgnoreCase(name))) {
                showAlert(Alert.AlertType.WARNING, "Name Exists", 
                         "A schedule named '" + name + "' already exists.");
                return;
            }
            student.setSchedule(name, new ArrayList<>(student.getActiveSchedule()));
            showAlert(Alert.AlertType.INFORMATION, "Schedule Saved", 
                     "Schedule '" + name + "' has been saved.");
        });
    }

    // Shows dialog to create a brand new empty schedule and switch to it
    public void showNewScheduleDialog() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("New Schedule");
        dialog.setHeaderText("Create a new empty schedule");

        ButtonType createBtn = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createBtn, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nameField = new TextField();
        nameField.setPromptText("e.g., Backup Plan");
        grid.add(new Label("Schedule Name:"), 0, 0);
        grid.add(nameField, 1, 0);

        dialog.getDialogPane().setContent(grid);
        Platform.runLater(() -> nameField.requestFocus());

        dialog.setResultConverter(button -> 
            (button == createBtn) ? nameField.getText().trim() : null
        );

        dialog.showAndWait().ifPresent(name -> {
            if (name.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Invalid Name", "Schedule name cannot be empty.");
                return;
            }
            if (student.getSavedScheduleNames().stream()
                    .anyMatch(existing -> existing.equalsIgnoreCase(name))) {
                showAlert(Alert.AlertType.WARNING, "Name Exists", 
                         "A schedule named '" + name + "' already exists.");
                return;
            }
            student.setActiveSchedule(name);
            onScheduleChanged.run();
            showAlert(Alert.AlertType.INFORMATION, "New Schedule", 
                     "Created and activated: '" + name + "'");
        });
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    // Shows dialog to switch to another existing schedule
    public void showSwitchScheduleDialog() {
        Set<String> allSchedules = student.getSavedScheduleNames();
        if (allSchedules.size() <= 1) {
            showAlert(Alert.AlertType.INFORMATION, "No Other Schedules", 
                     "You only have one schedule. Create a new one first.");
            return;
        }

        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Switch Schedule");
        dialog.setHeaderText("Choose a schedule to load");

        ButtonType switchBtn = new ButtonType("Switch", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(switchBtn, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        ComboBox<String> scheduleCombo = new ComboBox<>();
        scheduleCombo.getItems().addAll(
            allSchedules.stream()
                .filter(name -> !name.equals(student.getActiveScheduleName()))
                .collect(Collectors.toList())
        );
        scheduleCombo.setEditable(false);

        grid.add(new Label("Schedule:"), 0, 0);
        grid.add(scheduleCombo, 1, 0);

        dialog.getDialogPane().setContent(grid);
        Platform.runLater(() -> scheduleCombo.show());

        dialog.setResultConverter(button -> 
            (button == switchBtn) ? scheduleCombo.getValue() : null
        );

        dialog.showAndWait().ifPresent(selected -> {
            if (selected != null && !selected.equals(student.getActiveScheduleName())) {
                student.setActiveSchedule(selected);
                onScheduleChanged.run();
                showAlert(Alert.AlertType.INFORMATION, "Schedule Loaded", 
                         "Switched to: '" + selected + "'");
            }
        });
    }
    // Shows dialog to delete a schedule
    public void showDeleteScheduleDialog() {
        Set<String> allSchedules = student.getSavedScheduleNames();
        
        if (allSchedules.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "No Schedules", "No schedules to delete!");
            return;
        }

        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Delete Schedule");
        dialog.setHeaderText("Select a schedule to delete");

        ButtonType deleteBtn = new ButtonType("Delete", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(deleteBtn, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        ComboBox<String> scheduleCombo = new ComboBox<>();
        scheduleCombo.getItems().addAll(allSchedules);
        
        // Pre-select current active schedule
        scheduleCombo.setValue(student.getActiveScheduleName());

        grid.add(new Label("Schedule:"), 0, 0);
        grid.add(scheduleCombo, 1, 0);

        dialog.getDialogPane().setContent(grid);
        Platform.runLater(() -> scheduleCombo.requestFocus());

        dialog.setResultConverter(button -> 
            (button == deleteBtn) ? scheduleCombo.getValue() : null
        );

        dialog.showAndWait().ifPresent(toDelete -> {
            if (toDelete == null) return;

            if (allSchedules.size() == 1) {
                showAlert(Alert.AlertType.WARNING, "Cannot Delete", "You must have at least one schedule.");
                return;
            }

            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirm Delete");
            confirm.setHeaderText("Delete schedule '" + toDelete + "'?");
            confirm.setContentText("This action cannot be undone.");

            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {

                    student.getSavedScheduleNames().remove(toDelete);
                    
                    // If deleting active schedule, switch to another one
                    if (toDelete.equals(student.getActiveScheduleName())) {
                        // Pick first available schedule
                        String newActive = student.getSavedScheduleNames().iterator().next();
                        student.setActiveSchedule(newActive);
                    }
                    
                    onScheduleChanged.run();
                    showAlert(Alert.AlertType.INFORMATION, "Deleted", "Schedule '" + toDelete + "' has been deleted.");
                }
            });
        });
    }
}