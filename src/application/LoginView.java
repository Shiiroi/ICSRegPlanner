package application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import handler.FileManager;
import model.Student;
import java.nio.file.Path;
import java.util.ArrayList;
// ADDED IMPORTS
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import java.util.List;
import javafx.scene.text.Text;
import javafx.scene.control.Control;



public class LoginView {
    // ICS Color Palette
    private static final String ICS_BLUE = "#1753A0";
    private static final String ICS_YELLOW = "#F2ED0C";
    private static final String LIGHT_BLUE = "#4B8BDE";
    private static final String CREAM = "#F5F1E8";
    private static final String WHITE = "#FFFFFF";
    private static final String DARK_TEXT = "#2C2C2C";
    private static final String LIGHT_TEXT = "#666666";
    
    private Stage stage;
    private Scene scene;
    
    // ADDED LEFT AND RIGHT CONTAINERS
    private SplitPane mainContainer;
    private VBox leftContainer;
    private VBox rightContainer;
    private GridPane grid;
    
    private TextField emailField;
    private PasswordField passwordField;
    private Label messageLabel;
    
    private FileManager fileManager;
    private ArrayList<Student> userList;
    private Path savePath;
    
    public LoginView() {
        this.fileManager = new FileManager();
        this.savePath = FileManager.getSavePath();
        this.userList = fileManager.load(savePath);
        
        if (userList == null) {
            userList = new ArrayList<>();
        }
        
        setProperties();
    }
    
    private void setProperties() {
    	// CHANGED FROM VBOX TO HBOX
        // Main container with gradient background
        mainContainer = new SplitPane();
        mainContainer.setStyle(
            "-fx-background-color: linear-gradient(to bottom, " + CREAM + ", " + WHITE + ");"
        );
        
        // LEFT SIDE LOGIN FORM
        leftContainer = new VBox(30);
        leftContainer.setAlignment(Pos.CENTER);
        leftContainer.setPadding(new Insets(40));
        leftContainer.setPrefWidth(550);
        leftContainer.setStyle(
            "-fx-background-color: linear-gradient(to bottom, " + CREAM + ", " + WHITE + ");"
        );
        
        
        // Load and display ICS logo
        try {
            Image logo = new Image("file:src/img/ics-logo.png");
            ImageView logoView = new ImageView(logo);
            logoView.setFitWidth(180);
            logoView.setPreserveRatio(true);
            // ADDED IMG TO LEFT CONTAINER RATHER THAN MAIN
            leftContainer.getChildren().add(logoView); 

        } catch (Exception e) {
            System.out.println("Logo not found: " + e.getMessage());
        }
        
        // Initialize grid layout with card design
        grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPadding(new Insets(35, 40, 35, 40));
        grid.setStyle(
            "-fx-background-color: " + WHITE + ";" +
            "-fx-background-radius: 15;" +
            "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.15), 20, 0, 0, 5);"
        );
        
        // Title with Poppins font
        Label titleLabel = new Label("ICS Registration Planner");
        titleLabel.setStyle(
            "-fx-font-family: 'Poppins', 'Segoe UI', sans-serif;" +
            "-fx-font-size: 32px;" +
            "-fx-font-weight: 700;" +
            "-fx-text-fill: " + ICS_BLUE + ";"
        );
        grid.add(titleLabel, 0, 0, 2, 1);
        
        // Initialize text fields with improved styling
        emailField = new TextField();
        emailField.setPromptText("Enter email");
        emailField.setPrefWidth(300);
        emailField.setStyle(
            "-fx-font-family: 'Poppins', 'Segoe UI', sans-serif;" +
            "-fx-font-size: 14px;" +
            "-fx-padding: 12px;" +
            "-fx-background-color: " + CREAM + ";" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: transparent;" +
            "-fx-border-radius: 8;" +
            "-fx-prompt-text-fill: " + LIGHT_TEXT + ";"
        );
        
        // Focus effect for email field
        emailField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                emailField.setStyle(
                    "-fx-font-family: 'Poppins', 'Segoe UI', sans-serif;" +
                    "-fx-font-size: 14px;" +
                    "-fx-padding: 12px;" +
                    "-fx-background-color: " + WHITE + ";" +
                    "-fx-background-radius: 8;" +
                    "-fx-border-color: " + ICS_YELLOW + ";" +
                    "-fx-border-width: 2px;" +
                    "-fx-border-radius: 8;" +
                    "-fx-prompt-text-fill: " + LIGHT_TEXT + ";"
                );
            } else {
                emailField.setStyle(
                    "-fx-font-family: 'Poppins', 'Segoe UI', sans-serif;" +
                    "-fx-font-size: 14px;" +
                    "-fx-padding: 12px;" +
                    "-fx-background-color: " + CREAM + ";" +
                    "-fx-background-radius: 8;" +
                    "-fx-border-color: transparent;" +
                    "-fx-border-radius: 8;" +
                    "-fx-prompt-text-fill: " + LIGHT_TEXT + ";"
                );
            }
        });
        
        passwordField = new PasswordField();
        passwordField.setPromptText("Enter password");
        passwordField.setPrefWidth(300);
        passwordField.setStyle(
            "-fx-font-family: 'Poppins', 'Segoe UI', sans-serif;" +
            "-fx-font-size: 14px;" +
            "-fx-padding: 12px;" +
            "-fx-background-color: " + CREAM + ";" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: transparent;" +
            "-fx-border-radius: 8;" +
            "-fx-prompt-text-fill: " + LIGHT_TEXT + ";"
        );
        
        // Focus effect for password field
        passwordField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                passwordField.setStyle(
                    "-fx-font-family: 'Poppins', 'Segoe UI', sans-serif;" +
                    "-fx-font-size: 14px;" +
                    "-fx-padding: 12px;" +
                    "-fx-background-color: " + WHITE + ";" +
                    "-fx-background-radius: 8;" +
                    "-fx-border-color: " + ICS_YELLOW + ";" +
                    "-fx-border-width: 2px;" +
                    "-fx-border-radius: 8;" +
                    "-fx-prompt-text-fill: " + LIGHT_TEXT + ";"
                );
            } else {
                passwordField.setStyle(
                    "-fx-font-family: 'Poppins', 'Segoe UI', sans-serif;" +
                    "-fx-font-size: 14px;" +
                    "-fx-padding: 12px;" +
                    "-fx-background-color: " + CREAM + ";" +
                    "-fx-background-radius: 8;" +
                    "-fx-border-color: transparent;" +
                    "-fx-border-radius: 8;" +
                    "-fx-prompt-text-fill: " + LIGHT_TEXT + ";"
                );
            }
        });
        
        // Initialize labels with Poppins font
        Label emailLabel = new Label("Email");
        emailLabel.setStyle(
            "-fx-font-family: 'Poppins', 'Segoe UI', sans-serif;" +
            "-fx-font-size: 13px;" +
            "-fx-font-weight: 600;" +
            "-fx-text-fill: " + DARK_TEXT + ";"
        );
        
        Label passwordLabel = new Label("Password");
        passwordLabel.setStyle(
            "-fx-font-family: 'Poppins', 'Segoe UI', sans-serif;" +
            "-fx-font-size: 13px;" +
            "-fx-font-weight: 600;" +
            "-fx-text-fill: " + DARK_TEXT + ";"
        );
        
        grid.add(emailLabel, 0, 2, 2, 1);
        grid.add(emailField, 0, 3, 2, 1);
        grid.add(passwordLabel, 0, 4, 2, 1);
        grid.add(passwordField, 0, 5, 2, 1);
        
        // Message label for errors
        messageLabel = new Label();
        messageLabel.setStyle(
            "-fx-font-family: 'Poppins', 'Segoe UI', sans-serif;" +
            "-fx-font-size: 12px;" +
            "-fx-text-fill: " + ICS_BLUE + ";" +
            "-fx-font-weight: 500;"
        );
        grid.add(messageLabel, 0, 6, 2, 1);
        
        // Initialize buttons with improved styling
        Button loginBtn = new Button("Login");
        styleButton(loginBtn, ICS_BLUE, WHITE, true);
        loginBtn.setPrefWidth(140); 
        
        Button registerBtn = new Button("Register");
        styleButton(registerBtn, ICS_BLUE, WHITE, true);
        registerBtn.setPrefWidth(140);
        
        Button exitButton = new Button("Exit");
        styleButton(exitButton, "transparent", ICS_BLUE, false);
        exitButton.setPrefWidth(140);
        exitButton.setStyle(
            exitButton.getStyle() +
            "-fx-border-color: " + ICS_BLUE + ";" +
            "-fx-border-width: 2px;" +
            "-fx-border-radius: 8;"
        );
        
        // Setup button layout - ALL BUTTONS IN ONE LINE
        HBox buttonBox = new HBox(10, loginBtn, registerBtn, exitButton);
        buttonBox.setAlignment(Pos.CENTER);
        grid.add(buttonBox, 0, 7, 2, 1);
        
        // Button event handlers
        loginBtn.setOnAction(e -> handleLogin());
        registerBtn.setOnAction(e -> handleRegister());
        exitButton.setOnAction(e -> System.exit(0));
        
        // CHANGED ADD TO MAIN CONTAINER TO LEFT CONAITNER
        // Add grid to left container
        leftContainer.getChildren().add(grid);

        // ==================== RIGHT SIDE CALENDAR ====================
        rightContainer = new VBox(20);
        rightContainer.setPadding(new Insets(40, 30, 40, 30));
        rightContainer.setStyle("-fx-background-color: " + WHITE + ";");

        // Academic Calendar Section
        Label calendarTitle = new Label("Academic Calendar 2025-2026");
        calendarTitle.setStyle(
            "-fx-font-family: 'Poppins', 'Segoe UI', sans-serif;" +
            "-fx-font-size: 20px;" +
            "-fx-font-weight: 700;" +
            "-fx-text-fill: " + ICS_BLUE + ";"
        );

        TableView<String[]> calendarTable = createCalendarTable();

        // Holidays Section
        Label holidaysTitle = new Label("Holidays");
        holidaysTitle.setStyle(
            "-fx-font-family: 'Poppins', 'Segoe UI', sans-serif;" +
            "-fx-font-size: 20px;" +
            "-fx-font-weight: 700;" +
            "-fx-text-fill: " + ICS_BLUE + ";"
        );

        TableView<String[]> holidaysTable = createHolidaysTable();

        // Add all elements to right container
        rightContainer.getChildren().addAll(calendarTitle, calendarTable, holidaysTitle, holidaysTable);

        // Wrap right container in ScrollPane
        ScrollPane scrollPane = new ScrollPane(rightContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(false);  
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);  
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);  
        scrollPane.setStyle(
            "-fx-background: " + WHITE + ";" +
            "-fx-background-color: " + WHITE + ";"
        );

        // ==================== ADD BOTH SIDES TO MAIN CONTAINER ====================
        mainContainer.getItems().addAll(leftContainer, scrollPane);
        mainContainer.setDividerPositions(0.5);
       

      
        
        // Create scene with wider width
        scene = new Scene(mainContainer, 1100, 700);

    }
    
    private void styleButton(Button button, String bgColor, String textColor, boolean isPrimary) {
        button.setPrefHeight(45);
        button.setStyle(
            "-fx-font-family: 'Poppins', 'Segoe UI', sans-serif;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: 600;" +
            "-fx-background-color: " + bgColor + ";" +
            "-fx-text-fill: " + textColor + ";" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;"
        );
        
        // Hover effect
        button.setOnMouseEntered(e -> {
            if (isPrimary) {
                button.setStyle(
                    "-fx-font-family: 'Poppins', 'Segoe UI', sans-serif;" +
                    "-fx-font-size: 14px;" +
                    "-fx-font-weight: 600;" +
                    "-fx-background-color: " + LIGHT_BLUE + ";" +
                    "-fx-text-fill: " + textColor + ";" +
                    "-fx-background-radius: 8;" +
                    "-fx-cursor: hand;"
                );
            } else if (bgColor.equals(ICS_YELLOW)) {
                button.setStyle(
                    "-fx-font-family: 'Poppins', 'Segoe UI', sans-serif;" +
                    "-fx-font-size: 14px;" +
                    "-fx-font-weight: 600;" +
                    "-fx-background-color: #3A7A52;" +
                    "-fx-text-fill: " + textColor + ";" +
                    "-fx-background-radius: 8;" +
                    "-fx-cursor: hand;"
                );
            } else {
                button.setStyle(
                    "-fx-font-family: 'Poppins', 'Segoe UI', sans-serif;" +
                    "-fx-font-size: 14px;" +
                    "-fx-font-weight: 600;" +
                    "-fx-background-color: " + CREAM + ";" +
                    "-fx-text-fill: " + ICS_BLUE + ";" +
                    "-fx-background-radius: 8;" +
                    "-fx-border-color: " + ICS_BLUE + ";" +
                    "-fx-border-width: 2px;" +
                    "-fx-border-radius: 8;" +
                    "-fx-cursor: hand;"
                );
            }
        });
        
        button.setOnMouseExited(e -> {
            styleButton(button, bgColor, textColor, isPrimary);
        });
    }
    
    private void handleLogin() {
        if (emailField.getText().trim().isEmpty() || 
            passwordField.getText().trim().isEmpty()) {
            messageLabel.setText("Please fill in all fields");
            return;
        }
        
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        
        Student foundStudent = null;
        for (Student student : userList) {
            if (student.getEmail().equalsIgnoreCase(email) && 
                student.getPassword().equals(password)) {
                foundStudent = student;
                break;
            }
        }
        
        if (foundStudent != null) {
            System.out.println("Login successful for: " + foundStudent.getFullName());
            StudentDashboard dashboard = new StudentDashboard(foundStudent);
            dashboard.setStage(stage);
        } else {
            messageLabel.setText("Invalid email or password");
        }
    }
    
    private void handleRegister() {
        RegisterView registerView = new RegisterView(fileManager, userList, savePath);
        registerView.showAndWait();
        
        if (registerView.isRegistrationSuccessful()) {
            messageLabel.setStyle(
                "-fx-font-family: 'Poppins', 'Segoe UI', sans-serif;" +
                "-fx-font-size: 12px;" +
                "-fx-text-fill: " + ICS_YELLOW + ";" +
                "-fx-font-weight: 500;"
            );
            messageLabel.setText("✓ Registration successful! Please login.");
        }
    }
  
    // CALENDAR VIEW STYLING AND CREATION
    private TableView<String[]> createCalendarTable() {
        TableView<String[]> table = new TableView<>();
        table.setStyle(
            "-fx-background-color: " + WHITE + ";" +
            "-fx-border-color: " + CREAM + ";" +
            "-fx-border-width: 1px;"
        );
        
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<String[], String> eventCol = new TableColumn<>("Event");
        eventCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[0]));
        eventCol.setPrefWidth(200);

        TableColumn<String[], String> sem1Col = new TableColumn<>("1st Semester");
        sem1Col.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[1]));
        sem1Col.setPrefWidth(120);

        TableColumn<String[], String> sem2Col = new TableColumn<>("2nd Semester");
        sem2Col.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[2]));
        sem2Col.setPrefWidth(120);

        TableColumn<String[], String> midyearCol = new TableColumn<>("Midyear");
        midyearCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[3]));
        midyearCol.setPrefWidth(100);

        table.getColumns().addAll(eventCol, sem1Col, sem2Col, midyearCol);

	     // Enable text wrapping for all columns
	     wrapColumnText(eventCol);
	     wrapColumnText(sem1Col);
	     wrapColumnText(sem2Col);
	     wrapColumnText(midyearCol);

        // Load data using FileManager
        List<String[]> calendarData = fileManager.loadAcademicCalendar();
        table.getItems().addAll(calendarData);

        return table;
    }
    // HOLIDAY TABLE CREATION
    private TableView<String[]> createHolidaysTable() {
        TableView<String[]> table = new TableView<>();
        table.setStyle(
            "-fx-background-color: " + WHITE + ";" +
            "-fx-border-color: " + CREAM + ";" +
            "-fx-border-width: 1px;"
        );
        
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        

        TableColumn<String[], String> yearCol = new TableColumn<>("Year");
        yearCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[0]));
        yearCol.setPrefWidth(80);

        TableColumn<String[], String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[1]));
        dateCol.setPrefWidth(150);

        TableColumn<String[], String> holidayCol = new TableColumn<>("Holiday");
        holidayCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[2]));
        holidayCol.setPrefWidth(250);

        table.getColumns().addAll(yearCol, dateCol, holidayCol);

     // Enable text wrapping for all columns
     wrapColumnText(yearCol);
     wrapColumnText(dateCol);
     wrapColumnText(holidayCol);


        // Load data using FileManager
        List<String[]> holidaysData = fileManager.loadHolidays();
        table.getItems().addAll(holidaysData);

        return table;
    }

 // Helper method to create wrapped text cells using Text instead of Label
    private <T> void wrapColumnText(TableColumn<T, String> column) {
        column.setCellFactory(tc -> {
            TableCell<T, String> cell = new TableCell<T, String>() {
                private final Text text = new Text();

                {
                    text.wrappingWidthProperty().bind(column.widthProperty().subtract(20));
                    text.setStyle(
                        "-fx-font-family: 'Poppins', 'Segoe UI', sans-serif;" +
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

    
    public void setStage(Stage stage) {
        this.stage = stage;
        this.stage.setTitle("ICS Registration Planner - Login");
        this.stage.setScene(this.scene);
        this.stage.show();
        
        // Source - https://stackoverflow.com/a
        // Posted by Miss Chanandler Bong
        // Retrieved 2025-11-30, License - CC BY-SA 4.0

        // Remove split pane dividers for cleaner look
           for (Node node : mainContainer.lookupAll(".split-pane-divider")) {
               node.setVisible(false);
           }

    }
    
    public Scene getScene() {
        return scene;
    }
}