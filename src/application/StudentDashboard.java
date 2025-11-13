package application;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import handler.FileManager;
import model.Course;
import model.Student;
import java.util.List;
import java.util.stream.Collectors;

// Main dashboard view for students to browse course offerings and program curriculum

public class StudentDashboard {
    private Scene scene;
    private Stage stage;
    private BorderPane root;
    private Student currentStudent;
    private FileManager fileManager;

    public StudentDashboard(Student student) {
        this.currentStudent = student;
        this.fileManager = new FileManager();
        this.root = new BorderPane();
        this.scene = new Scene(root, 1280, 720);
        
        setProperties();
    }

    // Sets up the main dashboard layout with header and tabs
    private void setProperties() {
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #f5f5f5;");

        // Setup header with welcome message and logout button
     // Setup header with welcome message and logout button
        HBox topBox = new HBox(20);
        topBox.setPadding(new Insets(0, 0, 10, 0));

        VBox welcomeBox = new VBox(5);
        Label welcomeLabel = new Label("Welcome, " + currentStudent.getFullName() + "!");
        welcomeLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        Label programLabel = new Label(currentStudent.getProgram());
        programLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666;");
        welcomeBox.getChildren().addAll(welcomeLabel, programLabel);

        Button logoutButton = new Button("Logout");
        logoutButton.setOnAction(e -> handleLogout());

        topBox.getChildren().addAll(welcomeBox, logoutButton);
        root.setTop(topBox);


        // Setup tab pane with Enlistment and Course List tabs
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        Tab enlistmentTab = new Tab("Enlistment");
        enlistmentTab.setContent(createEnlistmentContent());

        Tab courseListTab = new Tab("Course List");
        courseListTab.setContent(createCourseListContent());

        tabPane.getTabs().addAll(enlistmentTab, courseListTab);
        root.setCenter(tabPane);
    }

    // Creates the enlistment tab showing course offerings with schedule information
    private VBox createEnlistmentContent() {
        VBox content = new VBox(10);
        content.setPadding(new Insets(20));

        Label titleLabel = new Label("Course Offerings - 1S 2025-2026");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // filter dropdown
        HBox filterBox = new HBox(10);
        filterBox.setAlignment(Pos.CENTER_LEFT);
        
        Label filterLabel = new Label("Filter by Program:");
        ComboBox<String> programCombo = new ComboBox<>();
        programCombo.getItems().addAll(
            "All Programs",
            "BS Computer Science",
            "MS Computer Science",
            "Master of Information Technology",
            "PhD Computer Science"
        );
        programCombo.setValue(currentStudent.getProgram());
        
        filterBox.getChildren().addAll(filterLabel, programCombo);

        // Course offerings table setup
        TableView<Course> tableView = new TableView<>();
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Course, String> codeCol = new TableColumn<>("Course Code");
        codeCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getCourseCode()));

        TableColumn<Course, String> titleCol = new TableColumn<>("Course Title");
        titleCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getCourseTitle()));
        titleCol.setPrefWidth(250);

        TableColumn<Course, String> unitsCol = new TableColumn<>("Units");
        unitsCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(String.valueOf(data.getValue().getUnits())));
        unitsCol.setMaxWidth(60);

        TableColumn<Course, String> sectionCol = new TableColumn<>("Section");
        sectionCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getSection()));
        sectionCol.setMaxWidth(80);

        TableColumn<Course, String> timesCol = new TableColumn<>("Times");
        timesCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getTimes()));

        TableColumn<Course, String> daysCol = new TableColumn<>("Days");
        daysCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getDays()));
        daysCol.setMaxWidth(100);

        TableColumn<Course, String> roomsCol = new TableColumn<>("Rooms");
        roomsCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getRooms()));

        tableView.getColumns().addAll(codeCol, titleCol, unitsCol, sectionCol, timesCol, daysCol, roomsCol);

        // Load course offerings and setup filtering
        List<Course> allOfferings = fileManager.loadCourseOfferings();
        
        programCombo.setOnAction(e -> {
            tableView.getItems().clear();
            String selectedProgram = programCombo.getValue();
            
            if (selectedProgram.equals("All Programs")) {
                tableView.getItems().addAll(allOfferings);
            } else {
                List<Course> programCourses = fileManager.loadProgramCourses(selectedProgram);
                List<String> programCourseCodes = programCourses.stream()
                        .map(Course::getCourseCode)
                        .collect(Collectors.toList());
                
                List<Course> filteredOfferings = allOfferings.stream()
                        .filter(course -> programCourseCodes.contains(course.getCourseCode()))
                        .collect(Collectors.toList());
                
                tableView.getItems().addAll(filteredOfferings);
            }
        });
        
        programCombo.fireEvent(new javafx.event.ActionEvent());

        content.getChildren().addAll(titleLabel, filterBox, tableView);
        VBox.setVgrow(tableView, Priority.ALWAYS);

        return content;
    }

    // Creates the course list tab showing program curriculum with descriptions
    private VBox createCourseListContent() {
        VBox content = new VBox(10);
        content.setPadding(new Insets(20));

        Label titleLabel = new Label("Program Courses");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Program selector dropdown
        HBox filterBox = new HBox(10);
        filterBox.setAlignment(Pos.CENTER_LEFT);
        
        Label filterLabel = new Label("Select Program:");
        ComboBox<String> programCombo = new ComboBox<>();
        programCombo.getItems().addAll(
            "BS Computer Science",
            "MS Computer Science",
            "Master of Information Technology",
            "PhD Computer Science"
        );
        programCombo.setValue(currentStudent.getProgram());
        
        filterBox.getChildren().addAll(filterLabel, programCombo);

        // Program courses table setup
        TableView<Course> tableView = new TableView<>();
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Course, String> codeCol = new TableColumn<>("Course Code");
        codeCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getCourseCode()));
        codeCol.setPrefWidth(120);

        TableColumn<Course, String> titleCol = new TableColumn<>("Course Title");
        titleCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getCourseTitle()));
        titleCol.setPrefWidth(300);

        TableColumn<Course, String> unitsCol = new TableColumn<>("Units");
        unitsCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(String.valueOf(data.getValue().getUnits())));
        unitsCol.setMaxWidth(60);

        TableColumn<Course, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getDescription()));
        descCol.setPrefWidth(500);

        tableView.getColumns().addAll(codeCol, titleCol, unitsCol, descCol);

        // Load program courses based on selected program
        programCombo.setOnAction(e -> {
            tableView.getItems().clear();
            String selectedProgram = programCombo.getValue();
            List<Course> programCourses = fileManager.loadProgramCourses(selectedProgram);
            tableView.getItems().addAll(programCourses);
        });
        
        programCombo.fireEvent(new javafx.event.ActionEvent());

        content.getChildren().addAll(titleLabel, filterBox, tableView);
        VBox.setVgrow(tableView, Priority.ALWAYS);

        return content;
    }

    // Handles logout by returning to login screen
    private void handleLogout() {
        System.out.println("Logging out...");
        LoginView loginView = new LoginView();
        loginView.setStage(stage);
    }

    public void setStage(Stage stage) {
        this.stage = stage;
        this.stage.setTitle("ICS Registration Planner - Dashboard");
        this.stage.setScene(this.scene);
        this.stage.show();
    }

    public Scene getScene() {
        return scene;
    }
}
