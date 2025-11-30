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
    private CoursePlanner planner;
    private GridPane calendarGrid;
    private VBox calendarInfoPane;
    
    // ADDED PROFILE
    private ImageView profileImageView;
    private static final double PROFILE_IMAGE_SIZE = 80;
    private static final String DEFAULT_PROFILE_PATH = "src/img/default-profile.png";

    
    public StudentDashboard(Student student) {
        this.currentStudent = student;
        this.fileManager = new FileManager();
        this.root = new BorderPane();
        this.scene = new Scene(root, 1280, 720);
        
        planner = new CoursePlanner();
        planner.setEnrolledCourses(currentStudent.getEnrolledCourses());
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
        
        tabPane.getTabs().addAll(courseListTab, enlistmentTab, calendarTab);
        
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
        
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setStyle("-fx-background-color: " + WHITE + ";");
        calendarGrid = new GridPane();
        calendarGrid.setGridLinesVisible(true);
        calendarGrid.setHgap(5);
        calendarGrid.setVgap(5);
        calendarGrid.setPadding(new Insets(10));
        calendarGrid.setStyle(
            "-fx-background-color: " + WHITE + ";" +
            "-fx-grid-lines-visible: true;"
        );
        
        scrollPane.setContent(calendarGrid);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        
        setupGrid();
        planner.getEnrolledCourses().clear();
        planner.getEnrolledCourses().addAll(currentStudent.getEnrolledCourses());
        fillCalendar();
        
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
        
        content.getChildren().addAll(titleLabel, scrollPane, calendarInfoPane);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
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
        planner.getEnrolledCourses().clear();
        planner.getEnrolledCourses().addAll(currentStudent.getEnrolledCourses());
        
        enlistmentManager = new EnlistmentManager(planner, tableView, this);
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
    
    private void setupGrid() {
        String[] days = {"Mon", "Tue", "Wed", "Thu", "Fri"};
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
            if (r != null && r == 0) return false;
            if (c != null && c == 0) return false;
            return true;
        });
        
        List<Course> enrolled = planner.getEnrolledCourses();
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
            
            int start24 = convertTo24Hour(startRaw, c.getSection());
            int end24 = convertTo24Hour(endRaw, c.getSection());
            
            if (start24 < 0 || end24 < 0) {
                continue;
            }
            
            int startRow = timeToRow(start24);
            int endRow = timeToRow(end24);
            int rowSpan = endRow - startRow;
            
            if (rowSpan <= 0) {
                rowSpan = 1;
            }
            
            for (String rawDay : expandDays(c.getDays())) {
                int col = dayToColumn(rawDay);
                
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
    
    private int dayToColumn(String day) {
        switch (day) {
            case "Mon": return 0;
            case "Tue": return 1;
            case "Wed": return 2;
            case "Thu": return 3;
            case "Fri": return 4;
            default: return -1;
        }
    }
    
    public static boolean isLab(String section) {
        return section.contains("-");
    }
    
    public static boolean isLecture(String section) {
        return !isLab(section);
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
            } else if (hour >= 1 && hour <= 5) {
                return hour + 12;
            } else {
                return -1;
            }
        }
    }
    
    private int timeToRow(int hour24) {
        return hour24 - 6;
    }
    
    public static List<String> expandDays(String dayString) {
        dayString = dayString.trim();
        List<String> days = new ArrayList<>();
        if (dayString.equalsIgnoreCase("TTh")) {
            days.add("Tue");
            days.add("Thu");
        } else if (dayString.equalsIgnoreCase("WF")) {
            days.add("Wed");
            days.add("Fri");
        } else {
            switch (dayString.substring(0, 1).toUpperCase()) {
                case "M": days.add("Mon"); break;
                case "T": days.add("Tue"); break;
                case "W": days.add("Wed"); break;
                case "F": days.add("Fri"); break;
            }
        }
        return days;
    }
    
    private void showCourseInfo(Course course) {
        calendarInfoPane.getChildren().clear();
        
        Label titleLabel = new Label("Course Information");
        titleLabel.setStyle(
            "-fx-font-family: " + FONT_FAMILY + ";" +
            "-fx-font-size: 16px;" +
            "-fx-font-weight: 700;" +
            "-fx-text-fill: " + ICS_BLUE + ";"
        );
        
        String infoStyle = "-fx-font-family: " + FONT_FAMILY + ";" +
                          "-fx-font-size: 13px;" +
                          "-fx-text-fill: " + DARK_TEXT + ";";
        
        calendarInfoPane.getChildren().addAll(
            titleLabel,
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
    
    private void handleLogout() {
        currentStudent.getEnrolledCourses().clear();
        currentStudent.getEnrolledCourses().addAll(planner.getEnrolledCourses());
        
        ArrayList<Student> users = fileManager.load(FileManager.getSavePath());
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getEmail().equals(currentStudent.getEmail())) {
                users.set(i, currentStudent);
                break;
            }
        }
        
        System.out.println("Logging out...");
        System.out.println("Saving " + currentStudent.getEnrolledCourses().size() + " courses.");
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


}