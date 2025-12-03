package application;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import handler.FileManager;
import model.Course;
import model.CoursePlanner;
import model.Student;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javafx.scene.image.Image;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;

public class StudentDashboard {
    // ICS Color Palette
    private static final String ICS_BLUE = "#1753A0";
    private static final String ICS_YELLOW = "#F2ED0C";
    private static final String LIGHT_BLUE = "#4B8BDE";
    private static final String CREAM = "#F5F1E8";
    private static final String WHITE = "#FFFFFF";
    private static final String DARK_TEXT = "#2C2C2C";
    private static final String LIGHT_TEXT = "#666666";
    private static final String FONT_FAMILY = "'Poppins', 'Segoe UI', sans-serif";
    
    private Scene scene;
    private Stage stage;
    private BorderPane root;
    private Student currentStudent;
    private FileManager fileManager;
    private EnlistmentManager enlistmentManager;
    private GridPane calendarGrid;
    private VBox calendarInfoPane;
    private ScheduleManager scheduleManager; // NEW: added for multiple schedules
    private ScheduleComparisonView compareView; // NEW: added for comparison
    
    // NEW: removed planners
    // ADDED PROFILE
    private ImageView profileImageView;
    private static final double PROFILE_IMAGE_SIZE = 80;
    private static final String DEFAULT_PROFILE_PATH = "src/img/default-profile.png";

    
    public StudentDashboard(Student student) {
        this.currentStudent = student;
        this.fileManager = new FileManager();
        this.root = new BorderPane();
        this.scene = new Scene(root, 1280, 720);
        
        setProperties();
    }
    
    private void setProperties() {
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: " + CREAM + ";");
        
        // Setup header with welcome message and logout button
        HBox topBox = new HBox(20);
        topBox.setPadding(new Insets(15, 20, 15, 20));
        topBox.setStyle(
            "-fx-background-color: " + WHITE + ";" +
            "-fx-background-radius: 10;" +
            "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.1), 10, 0, 0, 2);"
        );
        
     // ADDED PROFILE SECTION
        VBox profileBox = new VBox(5);
        profileBox.setAlignment(Pos.CENTER);

        StackPane profileContainer = new StackPane();
        profileContainer.setPrefSize(PROFILE_IMAGE_SIZE, PROFILE_IMAGE_SIZE);
        profileContainer.setStyle(
            "-fx-background-color: " + CREAM + ";" +
            "-fx-border-color: " + ICS_BLUE + ";" +
            "-fx-border-width: 2px;"
        );

        // GET IMAGE VIEW
        profileImageView = new ImageView();
        try {
            String imagePath = currentStudent.getProfilePicturePath();
            if (imagePath == null || imagePath.isEmpty()) {
                imagePath = DEFAULT_PROFILE_PATH;
            }
            Image profileImage = new Image(imagePath);
            profileImageView.setImage(profileImage);
        } catch (Exception e) {
            try {
                Image defaultImage = new Image(DEFAULT_PROFILE_PATH);
                profileImageView.setImage(defaultImage);
            } catch (Exception ex) {
                System.out.println("Could not load profile images");
            }
        }
        profileImageView.setFitWidth(PROFILE_IMAGE_SIZE);
        profileImageView.setFitHeight(PROFILE_IMAGE_SIZE);
        profileImageView.setPreserveRatio(true);

        // Camera icon  WHEN HOVERED USING SVG FROM HERO ICONS
        SVGPath cameraIcon = new SVGPath();
        cameraIcon.setContent("M6.827 6.175A2.31 2.31 0 0 1 5.186 7.23c-.38.054-.757.112-1.134.175C2.999 7.58 2.25 8.507 2.25 9.574V18a2.25 2.25 0 0 0 2.25 2.25h15A2.25 2.25 0 0 0 21.75 18V9.574c0-1.067-.75-1.994-1.802-2.169a47.865 47.865 0 0 0-1.134-.175 2.31 2.31 0 0 1-1.64-1.055l-.822-1.316a2.192 2.192 0 0 0-1.736-1.039 48.774 48.774 0 0 0-5.232 0 2.192 2.192 0 0 0-1.736 1.039l-.821 1.316Z M16.5 12.75a4.5 4.5 0 1 1-9 0 4.5 4.5 0 0 1 9 0ZM18.75 10.5h.008v.008h-.008V10.5Z");
        cameraIcon.setFill(javafx.scene.paint.Color.WHITE);
        cameraIcon.setStroke(javafx.scene.paint.Color.WHITE);
        cameraIcon.setStrokeWidth(1.5);
        cameraIcon.setScaleX(2.5);
        cameraIcon.setScaleY(2.5);

        StackPane iconContainer = new StackPane(cameraIcon);
        iconContainer.setStyle(
            "-fx-background-color: rgba(0, 0, 0, 0.5);" +
            "-fx-padding: 10;"
        );
        iconContainer.setVisible(false);

        profileContainer.getChildren().addAll(profileImageView, iconContainer);

        // when hovered show cam icon 
        profileContainer.setOnMouseEntered(e -> {
            iconContainer.setVisible(true);
            profileContainer.setStyle(
                "-fx-cursor: hand;" +
                "-fx-background-color: " + CREAM + ";" +
                "-fx-border-color: " + ICS_BLUE + ";" +
                "-fx-border-width: 2px;"
            );
        });

        profileContainer.setOnMouseExited(e -> {
            iconContainer.setVisible(false);
            profileContainer.setStyle(
                "-fx-background-color: " + CREAM + ";" +
                "-fx-border-color: " + ICS_BLUE + ";" +
                "-fx-border-width: 2px;"
            );
        });

        profileContainer.setOnMouseClicked(e -> handleChangePhoto());

        profileBox.getChildren().add(profileContainer);
// END PROFILE SECTION ADDED

        
        VBox welcomeBox = new VBox(5);
        Label welcomeLabel = new Label("Welcome, " + currentStudent.getFullName() + "!");
        welcomeLabel.setStyle(
            "-fx-font-family: " + FONT_FAMILY + ";" +
            "-fx-font-size: 24px;" +
            "-fx-font-weight: 700;" +
            "-fx-text-fill: " + ICS_BLUE + ";"
        );
        
        Label programLabel = new Label(currentStudent.getProgram());
        programLabel.setStyle(
            "-fx-font-family: " + FONT_FAMILY + ";" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: 500;" +
            "-fx-text-fill: " + LIGHT_TEXT + ";"
        );
        welcomeBox.getChildren().addAll(welcomeLabel, programLabel);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Button logoutButton = new Button("Logout");
        styleButton(logoutButton, true);
        logoutButton.setOnAction(e -> handleLogout());
        
        topBox.getChildren().addAll(profileBox, welcomeBox, spacer, logoutButton);
        root.setTop(topBox);
        
     // NEW: initialize schedule management components
        this.compareView = new ScheduleComparisonView(currentStudent, this::onScheduleChanged);
        this.scheduleManager = new ScheduleManager(currentStudent, this::onScheduleChanged);
        
        // Setup tab pane
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.setStyle(
            "-fx-font-family: " + FONT_FAMILY + ";" +
            "-fx-background-color: " + WHITE + ";" +
            "-fx-border-radius: 10;" +
            "-fx-background-radius: 10;"
        );
        
        Tab courseListTab = new Tab("Course List");
        courseListTab.setContent(createCourseListContent());
        
        Tab enlistmentTab = new Tab("Enlistment");
        enlistmentTab.setContent(createEnlistmentContent());
        
        Tab calendarTab = new Tab("Calendar");
        calendarTab.setContent(createCalendarContent());
        
        Tab aboutTab = new Tab("About");
        aboutTab.setContent(createAboutContent());
        
     // NEW: added Compare Schedules tab
        Tab compareTab = new Tab("Compare Schedules");
        compareTab.setContent(compareView.createContent());
        
        tabPane.getTabs().addAll(aboutTab, courseListTab, enlistmentTab, calendarTab, compareTab);
        
        VBox centerContainer = new VBox(15);
        centerContainer.setPadding(new Insets(15, 0, 0, 0));
        centerContainer.getChildren().add(tabPane);
        VBox.setVgrow(tabPane, Priority.ALWAYS);
        
        root.setCenter(centerContainer);
    }
    
    private void styleButton(Button button, boolean isPrimary) {
        button.setPrefHeight(40);
        button.setPrefWidth(120);
        
        if (isPrimary) {
            button.setStyle(
                "-fx-font-family: " + FONT_FAMILY + ";" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: 600;" +
                "-fx-background-color: " + ICS_BLUE + ";" +
                "-fx-text-fill: " + WHITE + ";" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;"
            );
            
            button.setOnMouseEntered(e -> button.setStyle(
                "-fx-font-family: " + FONT_FAMILY + ";" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: 600;" +
                "-fx-background-color: " + LIGHT_BLUE + ";" +
                "-fx-text-fill: " + WHITE + ";" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;"
            ));
        } else {
            button.setStyle(
                "-fx-font-family: " + FONT_FAMILY + ";" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: 600;" +
                "-fx-background-color: transparent;" +
                "-fx-text-fill: " + ICS_BLUE + ";" +
                "-fx-background-radius: 8;" +
                "-fx-border-color: " + ICS_BLUE + ";" +
                "-fx-border-width: 2px;" +
                "-fx-border-radius: 8;" +
                "-fx-cursor: hand;"
            );
            
            button.setOnMouseEntered(e -> button.setStyle(
                "-fx-font-family: " + FONT_FAMILY + ";" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: 600;" +
                "-fx-background-color: " + CREAM + ";" +
                "-fx-text-fill: " + ICS_BLUE + ";" +
                "-fx-background-radius: 8;" +
                "-fx-border-color: " + ICS_BLUE + ";" +
                "-fx-border-width: 2px;" +
                "-fx-border-radius: 8;" +
                "-fx-cursor: hand;"
            ));
        }
        
        button.setOnMouseExited(e -> styleButton(button, isPrimary));
    }
    
    private VBox createCalendarContent() {
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: " + WHITE + ";");
        
        Label titleLabel = new Label("Weekly Schedule");
        titleLabel.setStyle(
            "-fx-font-family: " + FONT_FAMILY + ";" +
            "-fx-font-size: 20px;" +
            "-fx-font-weight: 700;" +
            "-fx-text-fill: " + ICS_BLUE + ";"
        );
        
        // NEW: Create split pane with calendar on left, info on right
        SplitPane splitPane = new SplitPane();
        splitPane.setDividerPositions(0.65);
        
        // LEFT SIDE: Calendar
        VBox leftPane = new VBox(10);
        leftPane.setPadding(new Insets(10));
        leftPane.setStyle("-fx-background-color: " + WHITE + ";");
        
        calendarGrid = new GridPane();
        calendarGrid.setGridLinesVisible(true);
        calendarGrid.setHgap(5);
        calendarGrid.setVgap(5);
        calendarGrid.setPadding(new Insets(10));
        calendarGrid.setStyle(
            "-fx-background-color: " + WHITE + ";" +
            "-fx-grid-lines-visible: true;"
        );
        
        ScrollPane calendarScroll = new ScrollPane(calendarGrid);
        calendarScroll.setFitToWidth(true);
        calendarScroll.setFitToHeight(true);
        calendarScroll.setStyle("-fx-background-color: " + WHITE + ";");
        
        setupGrid();
        fillCalendar();
        
        leftPane.getChildren().add(calendarScroll);
        VBox.setVgrow(calendarScroll, Priority.ALWAYS);
        
        // RIGHT SIDE: Course Information
        VBox rightPane = new VBox(10);
        rightPane.setPadding(new Insets(10));
        rightPane.setStyle("-fx-background-color: " + WHITE + ";");
        
        Label infoTitle = new Label("Course Information");
        infoTitle.setStyle(
            "-fx-font-family: " + FONT_FAMILY + ";" +
            "-fx-font-size: 18px;" +
            "-fx-font-weight: 700;" +
            "-fx-text-fill: " + ICS_BLUE + ";"
        );
        
        calendarInfoPane = new VBox(10);
        calendarInfoPane.setPadding(new Insets(15));
        calendarInfoPane.setStyle(
            "-fx-background-color: " + CREAM + ";" +
            "-fx-border-color: " + ICS_BLUE + ";" +
            "-fx-border-width: 2px;" +
            "-fx-border-radius: 8;" +
            "-fx-background-radius: 8;"
        );
        
        Label infoLabel = new Label("Select a course block to see details.");
        infoLabel.setStyle(
            "-fx-font-family: " + FONT_FAMILY + ";" +
            "-fx-font-size: 13px;" +
            "-fx-text-fill: " + LIGHT_TEXT + ";"
        );
        calendarInfoPane.getChildren().add(infoLabel);
        
        ScrollPane infoScroll = new ScrollPane(calendarInfoPane);
        infoScroll.setFitToWidth(true);
        infoScroll.setStyle("-fx-background-color: " + WHITE + ";");
        
        rightPane.getChildren().addAll(infoTitle, infoScroll);
        VBox.setVgrow(infoScroll, Priority.ALWAYS);
        
        splitPane.getItems().addAll(leftPane, rightPane);
        
        content.getChildren().addAll(titleLabel, splitPane);
        VBox.setVgrow(splitPane, Priority.ALWAYS);
        return content;
    }
    
    private VBox createEnlistmentContent() {
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: " + WHITE + ";");
        
        Label titleLabel = new Label("Course Offerings - 1S 2025-2026");
        titleLabel.setStyle(
            "-fx-font-family: " + FONT_FAMILY + ";" +
            "-fx-font-size: 20px;" +
            "-fx-font-weight: 700;" +
            "-fx-text-fill: " + ICS_BLUE + ";"
        );
        
        HBox filterBox = new HBox(10);
        filterBox.setAlignment(Pos.CENTER_LEFT);
        
        Label filterLabel = new Label("Filter by Program:");
        filterLabel.setStyle(
            "-fx-font-family: " + FONT_FAMILY + ";" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: 600;" +
            "-fx-text-fill: " + DARK_TEXT + ";"
        );
        
        ComboBox<String> programCombo = new ComboBox<>();
        programCombo.getItems().addAll(
            "BS Computer Science",
            "MS Computer Science",
            "Master of Information Technology",
            "PhD Computer Science"
        );
        programCombo.setValue(currentStudent.getProgram());
        programCombo.setStyle(
            "-fx-font-family: " + FONT_FAMILY + ";" +
            "-fx-font-size: 13px;"
        );
        
        filterBox.getChildren().addAll(filterLabel, programCombo);
        
        TableView<Course> tableView = new TableView<>();
        tableView.setStyle(
            "-fx-font-family: " + FONT_FAMILY + ";" +
            "-fx-background-color: " + WHITE + ";"
        );
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
        
        TableColumn<Course, String> timesCol = new TableColumn<>("Times");
        timesCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getTimes()));
        
        TableColumn<Course, String> daysCol = new TableColumn<>("Days");
        daysCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getDays()));
        daysCol.setMaxWidth(100);
        
        TableColumn<Course, String> roomsCol = new TableColumn<>("Rooms");
        roomsCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getRooms()));
        
        tableView.getColumns().addAll(codeCol, titleCol, unitsCol, sectionCol, timesCol, daysCol, roomsCol);
        
        List<Course> allOfferings = fileManager.loadCourseOfferings();
        
        programCombo.setOnAction(e -> {
            tableView.getItems().clear();
            String selectedProgram = programCombo.getValue();
            
            List<Course> programCourses = fileManager.loadProgramCourses(selectedProgram);
            List<String> programCourseCodes = programCourses.stream()
                    .map(Course::getCourseCode)
                    .collect(Collectors.toList());
            
            List<Course> filteredOfferings = allOfferings.stream()
                    .filter(course -> programCourseCodes.contains(course.getCourseCode()))
                    .collect(Collectors.toList());
            
            tableView.getItems().addAll(filteredOfferings);
        });
        
        programCombo.fireEvent(new javafx.event.ActionEvent());
     // NEW: pass List<Course> instead of CoursePlanner
        enlistmentManager = new EnlistmentManager(currentStudent.getActiveSchedule(), tableView, this);
        VBox enrolledPane = enlistmentManager.createEnlistmentPane();
        
        SplitPane splitPane = new SplitPane();
        splitPane.setDividerPositions(0.7);
        splitPane.getItems().addAll(tableView, enrolledPane);
        
        content.getChildren().addAll(titleLabel, filterBox, splitPane);
        VBox.setVgrow(splitPane, Priority.ALWAYS);
        return content;
    }
    
    private VBox createCourseListContent() {
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: " + WHITE + ";");
        
        Label titleLabel = new Label("Program Courses");
        titleLabel.setStyle(
            "-fx-font-family: " + FONT_FAMILY + ";" +
            "-fx-font-size: 20px;" +
            "-fx-font-weight: 700;" +
            "-fx-text-fill: " + ICS_BLUE + ";"
        );
        
        HBox filterBox = new HBox(10);
        filterBox.setAlignment(Pos.CENTER_LEFT);
        
        Label filterLabel = new Label("Select Program:");
        filterLabel.setStyle(
            "-fx-font-family: " + FONT_FAMILY + ";" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: 600;" +
            "-fx-text-fill: " + DARK_TEXT + ";"
        );
        
        ComboBox<String> programCombo = new ComboBox<>();
        programCombo.getItems().addAll(
            "BS Computer Science",
            "MS Computer Science",
            "Master of Information Technology",
            "PhD Computer Science"
        );
        programCombo.setValue(currentStudent.getProgram());
        programCombo.setStyle(
            "-fx-font-family: " + FONT_FAMILY + ";" +
            "-fx-font-size: 13px;"
        );
        
        filterBox.getChildren().addAll(filterLabel, programCombo);
        
        TableView<Course> tableView = new TableView<>();
        tableView.setStyle(
            "-fx-font-family: " + FONT_FAMILY + ";" +
            "-fx-background-color: " + WHITE + ";"
        );
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
        
        wrapColumnText(titleCol);
        wrapColumnText(descCol);
        
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
    
    private ScrollPane createAboutContent() {
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: " + WHITE + ";");

        Label titleLabel = new Label("Welcome to ICS DANGAL!");
        titleLabel.setStyle(
            "-fx-font-family: " + FONT_FAMILY + ";" +
            "-fx-font-size: 25px;" +
            "-fx-font-weight: 700;" +
            "-fx-text-fill: " + ICS_BLUE + ";"
        );

        Label overviewLabel = new Label("Overview");
        overviewLabel.setStyle(
            "-fx-font-family: " + FONT_FAMILY + ";" +
            "-fx-font-size: 20px;" +
            "-fx-font-weight: 700;" +
            "-fx-text-fill: " + ICS_BLUE + ";"
        );
        
        Label overviewText = new Label(
        	
            "DANGAL is a JavaFX-based course registration planner created for the students of the Institute of Computer Science (ICS). " +
            "It aims to provide learners a way to organize and plan their subjects for the First Semester of Academic Year 2025–2026 in a simple, interactive, and visually clear way.\n\n" +
            "The system provides a dynamic weekly schedule, a course-selection interface, smart features that guide users in choosing the correct courses for their degree programs. "+
            "Whether the student is taking BSCS, MSCS, MIT, or PhD Computer Science, DANGAL ensures that their planner only takes in valid courses for their curriculum. "+
            "DANGAL also includes other small quality of life features that can help improve user experience."+
            "This About page gives a complete overview of the system’s goals, functionalities, and how students can use it effectively.\n\n"
        );
        overviewText.setWrapText(true);
        overviewText.setStyle(
            "-fx-font-family: " + FONT_FAMILY + ";" +
            "-fx-font-size: 14px;" +
            "-fx-text-fill: " + DARK_TEXT + ";"
        );
        
        Label infoLabel = new Label("What this App does");
        infoLabel.setStyle(
            "-fx-font-family: " + FONT_FAMILY + ";" +
            "-fx-font-size: 20px;" +
            "-fx-font-weight: 700;" +
            "-fx-text-fill: " + ICS_BLUE + ";"
        );
        
        Label infoText = new Label(
        	
            "DANGAL helps students plan their subjects more easily by providing:\n"+
            
    		"\u2022 Program-restricted planning\n" +
    	    "   You can only add courses that belong to the degree program you selected during registration. This prevents mistakes like BSCS students adding MSCS or PhD courses.\n\n" +
    	    "\u2022 Visual weekly calendar\n" +
    	    "   When you add a course, its day and time slot appear on your weekly schedule. This helps you see how your classes fit together.\n\n" +
    	    "\u2022 Complete course information\n" +
    	    "   Before adding a course, students can view its complete details such as:\n"+
    	    "   \t\u2022code\n\t\u2022title\n\t\u2022description\n\t\u2022units\n\t\u2022schedule\n\t\u2022instructor\n\t\u2022program\n\n" +
    	    "\u2022 Automatic conflict checking\n" +
    	    "   The system checks for:\n"+
    	    "   \t\u2022Overlapping time slots\n\t\u2022Duplicate course entries\n\t\u2022Courses that do not belong to the student’s program\n\n" +
    	    "\u2022 Secure login system\n" +
    	    "   Students must register and create an account before using the planner.\n"+
    	    "	Each account stores:\n"+
    	    "	\u2022Username\n\t\u2022Password\n\t\u2022Selected Degree Program\n\t\u2022Planned Courses\n"+
    	    "This ensures a personalized and secure experience every time the user logs in.\n"
        );
        infoText.setStyle(
            "-fx-font-family: " + FONT_FAMILY + ";" +
            "-fx-font-size: 14px;" +
            "-fx-text-fill: " + DARK_TEXT + ";"
        );
        
        Label howToLabel = new Label("How to Use This App");
        howToLabel.setStyle(
            "-fx-font-family: " + FONT_FAMILY + ";" +
            "-fx-font-size: 20px;" +
            "-fx-font-weight: 700;" +
            "-fx-text-fill: " + ICS_BLUE + ";"
        );

        Label howToText = new Label(
            "1. Create an account and choose your program\n"+
            "2. Log in to open your dashboard\n"+
            "3. Browse the list of courses offered for 1st Semester AY 2025–2026.\n" +
            "4. Press 'Enlist' to place it on to your enlisted courses.\n" +
            "5. The course will appear automatically on the correct day and time slot in your weekly schedule.\n" +
            "6. Click a course to view its full details like units, schedule, and instructor.\n" +
            "7. You may edit or remove courses anytime.\n"+
            "DANGAL is designed so that students can freely experiment with schedules while staying within the academic rules of their program.\n"
            // dagadag pa kayo if may other stuff
        );
        howToText.setStyle(
                "-fx-font-family: " + FONT_FAMILY + ";" +
                "-fx-font-size: 14px;" +
                "-fx-text-fill: " + DARK_TEXT + ";"
        );
        
        Label featuresLabel = new Label("Important Features & Notes");
        featuresLabel.setStyle(
            "-fx-font-family: " + FONT_FAMILY + ";" +
            "-fx-font-size: 20px;" +
            "-fx-font-weight: 700;" +
            "-fx-text-fill: " + ICS_BLUE + ";"
        );

        Label featuresText = new Label(
            "\u2022 You may only add courses from your chosen degree program\n"+ //ano ulet sabi ni sir tungkol dito 
            "\u2022 The system automatically detects date and time conflicts to ensure a valid schedule.\n"+
            "\u2022 Course data comes from the official ICS list for AY 2025–2026\n"
        );
        featuresText.setStyle(
            "-fx-font-family: " + FONT_FAMILY + ";" +
            "-fx-font-size: 14px;" +
            "-fx-text-fill: " + DARK_TEXT + ";"
        );
        
        Label creditsLabel = new Label("Credits"); //di ata talaga to kasama sa about pero sinama ko narin for now padelete nalang kung ayaw nyo
        creditsLabel.setStyle(
            "-fx-font-family: " + FONT_FAMILY + ";" +
            "-fx-font-size: 20px;" +
            "-fx-font-weight: 700;" +
            "-fx-text-fill: " + ICS_BLUE + ";"
        );

        Label creditsText = new Label(
            "Team Name: this.getName()\n\n"+ 
            "Developers:\n"+
            "\u2022 MAGWILI, VINCE ROI SOLANO\n"+ 
            "\u2022 MANUEL, IVAN ERICK GONZALES\n"+
            "\u2022 NIÑO, BOBBY FROI SOLIVEN\n"+
            "\u2022 OLIVO, RAFAEL SAM GELISANGA\n"+
            "Instructor: CARL ANGELO G. ANGCANA\n"+
            "Course: CMSC22 UV-3L\n"+
            "Semester: First Semester, AY 2025–2026"
        );
        creditsText.setStyle(
                "-fx-font-family: " + FONT_FAMILY + ";" +
                "-fx-font-size: 14px;" +
                "-fx-text-fill: " + DARK_TEXT + ";"
        );

        
        content.getChildren().addAll(titleLabel, 
        		overviewLabel, overviewText,
        		infoLabel, infoText,
        		howToLabel, howToText,
        		featuresLabel, featuresText,
        		creditsLabel, creditsText //remove to kung ayaw nyo isama credits
        );
        
        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setPannable(true);
        scrollPane.setStyle("-fx-background: " + WHITE + "; -fx-border-color: transparent;");

        return scrollPane;
    }
    
    private void setupGrid() {
        String[] days = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        for (int i = 0; i < days.length; i++) {
            Label dayLabel = new Label(days[i]);
            dayLabel.setStyle(
                "-fx-font-family: " + FONT_FAMILY + ";" +
                "-fx-font-weight: 700;" +
                "-fx-font-size: 14px;" +
                "-fx-text-fill: " + ICS_BLUE + ";" +
                "-fx-alignment: center;"
            );
            dayLabel.setMinWidth(100);
            dayLabel.setAlignment(Pos.CENTER);
            calendarGrid.add(dayLabel, i + 1, 0);
        }
        
        for (int i = 0; i < 13; i++) {
            int hour = 7 + i;
            Label timeLabel = new Label(hour + ":00");
            timeLabel.setStyle(
                "-fx-font-family: " + FONT_FAMILY + ";" +
                "-fx-font-size: 12px;" +
                "-fx-text-fill: " + DARK_TEXT + ";"
            );
            timeLabel.setMinHeight(50);
            timeLabel.setAlignment(Pos.CENTER_LEFT);
            calendarGrid.add(timeLabel, 0, i + 1);
        }
    }
    
    private void fillCalendar() {
        calendarGrid.getChildren().removeIf(node -> {
            Integer r = GridPane.getRowIndex(node);
            Integer c = GridPane.getColumnIndex(node);

            // keeps header row (r = 0)
            if (r != null && r == 0) return false;

            // keeps header column (c = 0)
            if (c != null && c == 0) return false;

            // Remove everything else (course blocks)
            return true;
        });
        
        List<Course> enrolled = currentStudent.getActiveSchedule();
        for (Course c : enrolled) {
            String times = c.getTimes();
            if (times == null || times.trim().isEmpty() || times.equalsIgnoreCase("TBA")) {
                continue;
            }
            
            String[] parts = times.split("-");
            if (parts.length < 2) {
                continue;
            }
            
            String startRaw = parts[0].trim();
            String endRaw = parts[1].trim();
            
         // NEW: fixed 5-7pm lab classes
            int startHour = parseHour(startRaw);
            int endHour = parseHour(endRaw);
            if (startHour == -1 || endHour == -1) continue;
            
            int start24, end24;
            if (endHour < startHour) {
                start24 = startHour;
                end24 = (endHour == 12) ? 12 : endHour + 12;
            } else {
                if (startHour >= 7) {
                    start24 = startHour;
                    end24 = endHour;
                } else {
                    start24 = (startHour == 12) ? 12 : startHour + 12;
                    end24 = (endHour == 12) ? 12 : endHour + 12;
                }
            }
            
            int startRow = timeToRow(start24);
            int endRow = timeToRow(end24);
            int rowSpan = endRow - startRow;
            
            if (rowSpan <= 0) {
                rowSpan = 1;
            }
            
            for (String rawDay : expandDays(c.getDays())) {
                int col = dayToColumn(rawDay);
                if (col == -1) continue;
                
                Label courseBlock = new Label(c.getCourseCode() + "\n" + c.getSection());
                courseBlock.setStyle(
                    "-fx-background-color: " + LIGHT_BLUE + ";" +
                    "-fx-border-color: " + ICS_BLUE + ";" +
                    "-fx-border-width: 2px;" +
                    "-fx-text-fill: " + WHITE + ";" +
                    "-fx-font-family: " + FONT_FAMILY + ";" +
                    "-fx-font-size: 12px;" +
                    "-fx-font-weight: 600;" +
                    "-fx-alignment: center;" +
                    "-fx-background-radius: 5;" +
                    "-fx-border-radius: 5;"
                );
                courseBlock.setMinSize(100, 50 * rowSpan);
                courseBlock.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                courseBlock.setOnMouseClicked(e -> showCourseInfo(c));
                
                calendarGrid.add(courseBlock, col + 1, startRow + 1, 1, rowSpan);
            }
        }
    }
    
 // NEW: added saturday for grad classes
    private int dayToColumn(String day) {
        switch (day) {
            case "Mon": return 0;
            case "Tue": return 1;
            case "Wed": return 2;
            case "Thu": return 3;
            case "Fri": return 4;
            case "Sat": return 5;
            default: return -1;
        }
    }
    
    public static boolean isLab(String section) {
        return section.contains("-");
    }
    
    public static boolean isLecture(String section) {
        return !isLab(section);
    }

    // NEW: fixed 5-7pm lab classes
    private int parseHour(String timeStr) {
        try {
            String hourPart = timeStr.split(":")[0].trim();
            return Integer.parseInt(hourPart);
        } catch (Exception e) {
            return -1;
        }
    }

    public static int convertTo24Hour(String time, String section) {
        String hourPart = time.split(":")[0].trim();
        int hour;
        
        try {
            hour = Integer.parseInt(hourPart);
        } catch(NumberFormatException e){
            return -1;
        }
        
        boolean lab = isLab(section);
        
        if (lab) {
            if (hour >= 7 && hour <= 12) { 
                return hour; 
            } else if (hour >= 1 && hour <= 7) {
                return hour + 12; 
            } else {
                return -1;
            }
        } else {
            if (hour >= 7 && hour <= 12) {
                return hour; 
            } else if (hour >= 1 && hour <= 6) {
                return hour + 12; 
            } else {
                return -1;
            }
        }
    }

    private int timeToRow(int hour24) {
    	return hour24 - 6;
    }

    // FIXED: included Thursday and Saturday, and full date strings (Tues, Thurs)
    public static List<String> expandDays(String dayString) {
        dayString = dayString.trim();
        List<String> days = new ArrayList<>();

        if (dayString.equalsIgnoreCase("TTh")) {
            days.add("Tue");
            days.add("Thu");
        } else if (dayString.equalsIgnoreCase("WF")) {
            days.add("Wed");
            days.add("Fri");
        } else if (dayString.equalsIgnoreCase("M") || dayString.equalsIgnoreCase("Mon")) {
            days.add("Mon");
        } else if (dayString.equalsIgnoreCase("T") || dayString.equalsIgnoreCase("Tue") || dayString.equalsIgnoreCase("Tues")) {
            days.add("Tue");
        } else if (dayString.equalsIgnoreCase("W") || dayString.equalsIgnoreCase("Wed")) {
            days.add("Wed");
        } else if (dayString.equalsIgnoreCase("Th") || dayString.equalsIgnoreCase("Thu") || dayString.equalsIgnoreCase("Thurs")) {
            days.add("Thu");
        } else if (dayString.equalsIgnoreCase("F") || dayString.equalsIgnoreCase("Fri")) {
            days.add("Fri");
        } else if (dayString.equalsIgnoreCase("S") || dayString.equalsIgnoreCase("Sat")) {
            days.add("Sat");
        }

        return days;
    }
    
    private void showCourseInfo(Course course) {
        calendarInfoPane.getChildren().clear();
        
        // Header with title and back button
        HBox headerBox = new HBox();
        headerBox.setAlignment(Pos.CENTER_LEFT);
        
        Label titleLabel = new Label("Course Details");
        titleLabel.setStyle(
            "-fx-font-family: " + FONT_FAMILY + ";" +
            "-fx-font-size: 16px;" +
            "-fx-font-weight: 700;" +
            "-fx-text-fill: " + ICS_BLUE + ";"
        );
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        // Back button on the right
        Button backButton = new Button("← Back");
        backButton.setStyle(
            "-fx-font-family: " + FONT_FAMILY + ";" +
            "-fx-font-size: 12px;" +
            "-fx-font-weight: 600;" +
            "-fx-background-color: " + CREAM + ";" +
            "-fx-text-fill: " + ICS_BLUE + ";" +
            "-fx-background-radius: 6;" +
            "-fx-border-color: " + ICS_BLUE + ";" +
            "-fx-border-width: 1px;" +
            "-fx-border-radius: 6;" +
            "-fx-cursor: hand;" +
            "-fx-padding: 5 10 5 10;"
        );
        
        backButton.setOnMouseEntered(e -> backButton.setStyle(
            "-fx-font-family: " + FONT_FAMILY + ";" +
            "-fx-font-size: 12px;" +
            "-fx-font-weight: 600;" +
            "-fx-background-color: " + ICS_BLUE + ";" +
            "-fx-text-fill: " + WHITE + ";" +
            "-fx-background-radius: 6;" +
            "-fx-border-color: " + ICS_BLUE + ";" +
            "-fx-border-width: 1px;" +
            "-fx-border-radius: 6;" +
            "-fx-cursor: hand;" +
            "-fx-padding: 5 10 5 10;"
        ));
        
        backButton.setOnMouseExited(e -> backButton.setStyle(
            "-fx-font-family: " + FONT_FAMILY + ";" +
            "-fx-font-size: 12px;" +
            "-fx-font-weight: 600;" +
            "-fx-background-color: " + CREAM + ";" +
            "-fx-text-fill: " + ICS_BLUE + ";" +
            "-fx-background-radius: 6;" +
            "-fx-border-color: " + ICS_BLUE + ";" +
            "-fx-border-width: 1px;" +
            "-fx-border-radius: 6;" +
            "-fx-cursor: hand;" +
            "-fx-padding: 5 10 5 10;"
        ));
        
        backButton.setOnAction(e -> resetCourseInfo());
        
        headerBox.getChildren().addAll(titleLabel, spacer, backButton);
        
        String infoStyle = "-fx-font-family: " + FONT_FAMILY + ";" +
                          "-fx-font-size: 13px;" +
                          "-fx-text-fill: " + DARK_TEXT + ";";
        
        calendarInfoPane.getChildren().addAll(
            headerBox,
            new Separator(),
            createInfoLabel("Course Code: " + course.getCourseCode(), infoStyle),
            createInfoLabel("Title: " + course.getCourseTitle(), infoStyle),
            createInfoLabel("Units: " + course.getUnits(), infoStyle),
            createInfoLabel("Section: " + course.getSection(), infoStyle),
            createInfoLabel("Times: " + course.getTimes(), infoStyle),
            createInfoLabel("Days: " + course.getDays(), infoStyle),
            createInfoLabel("Rooms: " + course.getRooms(), infoStyle),
            createInfoLabel("Description: " + course.getDescription(), infoStyle)
        );
    }

    // RESET INFO PANE
    private void resetCourseInfo() {
        calendarInfoPane.getChildren().clear();
        
        Label infoLabel = new Label("Select a course block to see details.");
        infoLabel.setStyle(
            "-fx-font-family: " + FONT_FAMILY + ";" +
            "-fx-font-size: 13px;" +
            "-fx-text-fill: " + LIGHT_TEXT + ";"
        );
        calendarInfoPane.getChildren().add(infoLabel);
    }
    
    private Label createInfoLabel(String text, String style) {
        Label label = new Label(text);
        label.setStyle(style);
        label.setWrapText(true);
        return label;
    }
    
    public void refreshCalendar() {
        List<javafx.scene.Node> toRemove = new ArrayList<>();
        for (javafx.scene.Node node : calendarGrid.getChildren()) {
            Integer row = GridPane.getRowIndex(node);
            Integer col = GridPane.getColumnIndex(node);
            if ((row != null && row == 0) || (col != null && col == 0)) {
                continue;
            }
            toRemove.add(node);
        }
        
        for (javafx.scene.Node node : toRemove) {
            calendarGrid.getChildren().remove(node);
        }
        
        fillCalendar();
    }
    
    private void onScheduleChanged() {
        refreshCalendar();
        
        if (enlistmentManager != null) {
            enlistmentManager.updateEnrolledList(currentStudent.getActiveSchedule());
        }
        
        if (compareView != null) {
            compareView.onScheduleChanged(currentStudent.getActiveScheduleName());
        }
    }
    
    private void handleLogout() {
        ArrayList<Student> users = fileManager.load(FileManager.getSavePath());
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getEmail().equals(currentStudent.getEmail())) {
                users.set(i, currentStudent);
                break;
            }
        }
        
        System.out.println("Logging out...");
        System.out.println("Saving " + currentStudent.getActiveSchedule().size() + " courses.");
        fileManager.save(users, FileManager.getSavePath());
        
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
    
    public Student getCurrentStudent() {
        return currentStudent;
    }
    
    // ADDED METHOD TO HANDLE PROFILE PICTURE CHANGE
    // Source - https://stackoverflow.com/a
    // Posted by Renis1235, modified by community. See post 'Timeline' for change history
    // Retrieved 2025-11-30, License - CC BY-SA 4.0
    
    private void handleChangePhoto() {
        javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
        fileChooser.setTitle("Select Profile Picture");
        fileChooser.getExtensionFilters().add(
            new javafx.stage.FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );
        
        java.io.File selectedFile = fileChooser.showOpenDialog(stage);
        
        if (selectedFile != null) {
            try {
                final java.io.InputStream targetStream = new java.io.DataInputStream(
                    new java.io.FileInputStream(selectedFile)
                );
                Image newImage = new Image(targetStream);
                profileImageView.setImage(newImage);
                
                String imagePath = "file:" + selectedFile.getAbsolutePath();
                currentStudent.setProfilePicturePath(imagePath);
                
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText(null);
                alert.setContentText("Profile picture updated successfully!");
                alert.showAndWait();
                
            } catch (java.io.FileNotFoundException fileNotFoundException) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Failed to load image: " + fileNotFoundException.getMessage());
                alert.showAndWait();
            }
        }
    }
    
 // Wrap text in cell
    private <T> void wrapColumnText(TableColumn<T, String> column) {
        column.setCellFactory(tc -> {
            TableCell<T, String> cell = new TableCell<T, String>() {
                private final Text text = new Text();

                {
                    text.wrappingWidthProperty().bind(column.widthProperty().subtract(20));
                    text.setStyle(
                        "-fx-font-family: " + FONT_FAMILY + ";" +
                        "-fx-font-size: 12px;" +
                        "-fx-fill: " + DARK_TEXT + ";"
                    );
                    setGraphic(text);
                    setPrefHeight(Control.USE_COMPUTED_SIZE);
                    setPadding(new Insets(10, 10, 10, 10));
                }

                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        text.setText(null);
                    } else {
                        text.setText(item);
                    }
                }
            };
            return cell;
        });
    }

    
    // called when schedule changes
    public void notifyScheduleChanged() {
        if (compareView != null) {
            compareView.refreshView();
        }
    }
}