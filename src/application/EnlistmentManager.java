package application;

import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import model.Course;
import model.CoursePlanner;
import model.ProgramLevel;

public class EnlistmentManager {
    private TableView<Course> enrolledTable;
    private CoursePlanner planner;
    private TableView<Course> offeringsTable; 
    private ObservableList<Course> enrolledCourses;
    private StudentDashboard dashboard;
    private List<Course> enrolledList; // Direct reference to student's active schedule
    
    // Keep direct reference to the student's list
    public EnlistmentManager(List<Course> enrolledList, TableView<Course> offeringsTable, StudentDashboard dashboard) {
        this.enrolledList = enrolledList; // This is the ACTUAL student schedule
        this.offeringsTable = offeringsTable;
        this.dashboard = dashboard;
        this.planner = new CoursePlanner();
        this.planner.setEnrolledCourses(enrolledList); 
        this.enrolledCourses = FXCollections.observableArrayList(enrolledList); // ✅ Use the actual list
    }
    
    public VBox createEnlistmentPane() { 
        VBox content = new VBox(10);
        content.setPadding(new Insets(20));

        enrolledTable = new TableView<>(); 
        enrolledTable.setItems(enrolledCourses);
        enrolledTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        TableColumn<Course, String> codeCol = new TableColumn<>("Course Code");
        codeCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getCourseCode()));
        
        TableColumn<Course, String> secCol = new TableColumn<>("Section");
        secCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getSection()));
        
        TableColumn<Course, Integer> unitsCol = new TableColumn<>("Units");
        unitsCol.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getUnits()).asObject());
        
        enrolledTable.getColumns().addAll(codeCol, secCol, unitsCol);

        Button addButton = new Button("Add Course");
        Button removeButton = new Button("Remove Course");
        HBox buttonBox = new HBox(10, addButton, removeButton);
        buttonBox.setAlignment(Pos.CENTER);

        addButton.setOnAction(e -> addSelectedCourse());
        removeButton.setOnAction(e -> removeSelectedCourse());

        content.getChildren().addAll(new Label("Enrolled Courses:"), enrolledTable, buttonBox);
        VBox.setVgrow(enrolledTable, Priority.ALWAYS);

        refreshEnrolledTable();
        return content;
    }
    
    private void addSelectedCourse() {
        Course selected = offeringsTable.getSelectionModel().getSelectedItem();

        if (selected != null) {
            // Program level restriction check
            int studentLevel = ProgramLevel.getProgramLevel(dashboard.getCurrentStudent().getProgram());
            int courseLevel = ProgramLevel.getCourseLevel(selected.getCourseCode());

            if (courseLevel > studentLevel) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Enrollment Failed");
                alert.setHeaderText(null);
                alert.setContentText("This course is not available for your program.");
                alert.showAndWait();
                return;
            }

            // Conflict check
            if (planner.hasConflict(selected)) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Conflict");
                alert.setHeaderText(null);
                alert.setContentText("This course conflicts with your existing schedule!");
                alert.showAndWait();
                return;
            }

            // Check for section matching between lecture and lab
            boolean hasLecture = false;
            boolean hasLab = false;
            String existingSection = null;
            
            for (Course c : enrolledList) {
                if (c.getCourseCode().equals(selected.getCourseCode())) {
                    if (StudentDashboard.isLecture(c.getSection())) {
                        hasLecture = true;
                        existingSection = c.getSection();
                    } else if (StudentDashboard.isLab(c.getSection())) {
                        hasLab = true;
                        existingSection = c.getSection().split("-")[0]; // gets the section
                    }
                }
            }
            
            // Check if adding duplicate of same type
            if (StudentDashboard.isLecture(selected.getSection()) && hasLecture) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Duplicate Lecture");
                alert.setHeaderText(null);
                alert.setContentText("You are already enrolled in a lecture section for this course!");
                alert.showAndWait();
                return;
            }
            
            if (StudentDashboard.isLab(selected.getSection()) && hasLab) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Duplicate Lab");
                alert.setHeaderText(null);
                alert.setContentText("You are already enrolled in a lab section for this course!");
                alert.showAndWait();
                return;
            }
            
            // Check if sections match when adding lab to existing lecture or vice versa
            if (hasLecture && StudentDashboard.isLab(selected.getSection())) {
                String selectedBaseSection = selected.getSection().split("-")[0];
                if (!selectedBaseSection.equals(existingSection)) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Section Mismatch");
                    alert.setHeaderText(null);
                    alert.setContentText("Lab section must match your lecture section (" + existingSection + ")!");
                    alert.showAndWait();
                    return;
                }
            }
            
            if (hasLab && StudentDashboard.isLecture(selected.getSection())) {
                if (!selected.getSection().equals(existingSection)) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Section Mismatch");
                    alert.setHeaderText(null);
                    alert.setContentText("Lecture section must match your lab section (" + existingSection + ")!");
                    alert.showAndWait();
                    return;
                }
            }

            // Add to the actual student's list
            enrolledList.add(selected);
            planner.setEnrolledCourses(enrolledList);
            
            // Save back to student's schedule map
            dashboard.getCurrentStudent().setSchedule(
                dashboard.getCurrentStudent().getActiveScheduleName(), 
                new ArrayList<>(enrolledList)
            );
            
            System.out.println("Added: " + selected.getCourseCode() + " | " + selected.getSection());
            refreshEnrolledTable();
            dashboard.refreshCalendar();
            dashboard.notifyScheduleChanged();
        }
    }


    private void removeSelectedCourse() {
        Course selected = enrolledTable.getSelectionModel().getSelectedItem();
        
        if (selected != null) {
            // ✅ REMOVE FROM THE ACTUAL STUDENT'S LIST
            enrolledList.remove(selected);
            planner.setEnrolledCourses(enrolledList);
            
            // ✅ SAVE BACK TO STUDENT'S SCHEDULE MAP
            dashboard.getCurrentStudent().setSchedule(
                dashboard.getCurrentStudent().getActiveScheduleName(), 
                new ArrayList<>(enrolledList)
            );
            
            refreshEnrolledTable();
            dashboard.refreshCalendar();
            dashboard.notifyScheduleChanged(); // ✅ Notify comparison view
        }
    }

    private void refreshEnrolledTable() {
        enrolledCourses.setAll(enrolledList); // Use the actual list
    }
    
    public void updateEnrolledList(List<Course> newEnrolledList) {
        this.enrolledList = newEnrolledList;
        this.planner.setEnrolledCourses(newEnrolledList);
        refreshEnrolledTable();
    }
}