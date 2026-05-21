package application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.Modality;
import handler.FileManager;
import model.Student;
import java.nio.file.Path;
import java.util.ArrayList;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import java.util.List;
import javafx.scene.text.Text;
import javafx.scene.control.Control;
import javafx.scene.text.Font;

public class LoginView {
    private static final String ICS_BLUE = "#1753A0";
    private static final String ICS_YELLOW = "#F2ED0C";
    private static final String LIGHT_BLUE = "#4B8BDE";
    private static final String CREAM = "#F5F1E8";
    private static final String WHITE = "#FFFFFF";
    private static final String DARK_TEXT = "#2C2C2C";
    private static final String LIGHT_TEXT = "#666666";
    private static final String FONT_FAMILY = "'Inter', 'Segoe UI', sans-serif";

    private Stage stage;
    private Scene scene;

    // MAIN CONTAINERS
    private StackPane rootContainer;
    private VBox mainContainer;
    private GridPane grid;

    // Fields & labels
    private TextField emailField;
    private PasswordField passwordField;
    private Label messageLabel;

    // File manager and data
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

        loadCustomFonts();
        setProperties();
    }

    private void loadCustomFonts() {
        try {
            Font.loadFont(getClass().getResourceAsStream("/fonts/Inter_18pt-Regular.ttf"), 12);
            Font.loadFont(getClass().getResourceAsStream("/fonts/Inter_18pt-Medium.ttf"), 12);
            Font.loadFont(getClass().getResourceAsStream("/fonts/Inter_18pt-SemiBold.ttf"), 12);
            Font.loadFont(getClass().getResourceAsStream("/fonts/Inter_18pt-Bold.ttf"), 12);
            System.out.println("Inter fonts loaded successfully in LoginView!");
        } catch (Exception e) {
            System.out.println("Could not load Inter fonts: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setProperties() {
        // Root container with StackPane for layering
        rootContainer = new StackPane();
        
        // Background image layer
        ImageView backgroundImage = null;
        try {
            Image bgImg = new Image("file:src/img/background2.jpg");
            backgroundImage = new ImageView(bgImg);
            backgroundImage.setPreserveRatio(false);
            backgroundImage.fitWidthProperty().bind(rootContainer.widthProperty());
            backgroundImage.fitHeightProperty().bind(rootContainer.heightProperty());
            backgroundImage.setOpacity(0.4);
        } catch (Exception e) {
            System.out.println("Background image not found: " + e.getMessage());
            // Create placeholder if image not found
            backgroundImage = new ImageView();
        }
        
        // Gradient overlay
        VBox gradientOverlay = new VBox();
        gradientOverlay.setStyle(
            "-fx-background-color: linear-gradient(to bottom, rgba(44, 44, 44, 0.85), rgba(23, 83, 160, 0.85));"
        );
        
        mainContainer = new VBox(30);
        mainContainer.setAlignment(Pos.CENTER);
        mainContainer.setPadding(new Insets(40));

        // ICS Logo
        ImageView leftlogoView = null;
        try {
            Image leftlogo = new Image("file:src/img/ics-logo.png");
            leftlogoView = new ImageView(leftlogo);
            leftlogoView.setFitWidth(100);
            leftlogoView.setPreserveRatio(true);
        } catch (Exception e) {
            System.out.println("Left logo not found: " + e.getMessage());
        }
        
        // DANGAL Logo
        ImageView rightlogoView = null;
        try {
            Image rightlogo = new Image("file:src/img/dangal.png");
            rightlogoView = new ImageView(rightlogo);
            rightlogoView.setFitWidth(280);
            rightlogoView.setPreserveRatio(true);
        } catch (Exception e) {
            System.out.println("Right logo not found: " + e.getMessage());
        }

        // Create HBox to hold both logos side by side
        HBox logosContainer = new HBox(20);
        logosContainer.setAlignment(Pos.CENTER);
        if (leftlogoView != null) {
            logosContainer.getChildren().add(leftlogoView);
        }
        if (rightlogoView != null) {
            logosContainer.getChildren().add(rightlogoView);
        }

        // Initialize grid layout with card design
        grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPadding(new Insets(35, 40, 35, 40));
        grid.setMaxWidth(450);
        grid.setStyle(
            "-fx-background-color: " + WHITE + ";" +
            "-fx-background-radius: 15;" +
            "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.15), 20, 0, 0, 5);"
        );

        Label titleLabel = new Label("ICS Registration Planner");
        titleLabel.setStyle(
            "-fx-font-family: " + FONT_FAMILY + ";" +
            "-fx-font-size: 28px;" +
            "-fx-font-weight: 700;" +
            "-fx-text-fill: " + ICS_BLUE + ";"
        );
        titleLabel.setWrapText(true);
        titleLabel.setMaxWidth(350);
        grid.add(titleLabel, 0, 0, 2, 1);

        // Initialize text fields with improved styling
        emailField = new TextField();
        emailField.setPromptText("Enter email");
        emailField.setMaxWidth(350);
        emailField.setStyle(
            "-fx-font-family: " + FONT_FAMILY + ";" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: 200;" +
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
                    "-fx-font-family: " + FONT_FAMILY + ";" +
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
                    "-fx-font-family: " + FONT_FAMILY + ";" +
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
        passwordField.setMaxWidth(350);
        passwordField.setStyle(
            "-fx-font-family: " + FONT_FAMILY + ";" +
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
                    "-fx-font-family: " + FONT_FAMILY + ";" +
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
                    "-fx-font-family: " + FONT_FAMILY + ";" +
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

        // Initialize labels with Inter font
        Label emailLabel = new Label("Email");
        emailLabel.setStyle(
            "-fx-font-family: " + FONT_FAMILY + ";" +
            "-fx-font-size: 13px;" +
            "-fx-font-weight: 600;" +
            "-fx-text-fill: " + DARK_TEXT + ";"
        );

        Label passwordLabel = new Label("Password");
        passwordLabel.setStyle(
            "-fx-font-family: " + FONT_FAMILY + ";" +
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
            "-fx-font-family: " + FONT_FAMILY + ";" +
            "-fx-font-size: 12px;" +
            "-fx-text-fill: " + ICS_BLUE + ";" +
            "-fx-font-weight: 500;"
        );
        grid.add(messageLabel, 0, 6, 2, 1);

        // Initialize buttons with improved styling
        Button loginBtn = new Button("Login");
        styleButton(loginBtn, ICS_BLUE, WHITE, true);
        loginBtn.setPrefWidth(110);

        Button registerBtn = new Button("Register");
        styleButton(registerBtn, ICS_BLUE, WHITE, true);
        registerBtn.setPrefWidth(110);

        Button exitButton = new Button("Exit");
        styleButton(exitButton, ICS_BLUE, WHITE, true);
        exitButton.setPrefWidth(110);

        // Setup button layout - ALL BUTTONS IN ONE LINE
        HBox buttonBox = new HBox(10, loginBtn, registerBtn, exitButton);
        buttonBox.setAlignment(Pos.CENTER);
        grid.add(buttonBox, 0, 7, 2, 1);

        // Button event handlers
        loginBtn.setOnAction(e -> handleLogin());
        registerBtn.setOnAction(e -> handleRegister());
        exitButton.setOnAction(e -> System.exit(0));

        // ---------- Show Calendar Button (white background) ----------
        Button showCalendarBtn = new Button("View Academic Calendar");
        styleButton(showCalendarBtn, WHITE, ICS_BLUE, false);
        showCalendarBtn.setPrefWidth(240);
        showCalendarBtn.setOnAction(e -> showCalendarPopup());

        // Add all to main container
        mainContainer.getChildren().addAll(logosContainer, grid, showCalendarBtn);

        // Layer all elements: background image -> gradient overlay -> main content
        rootContainer.getChildren().addAll(backgroundImage, gradientOverlay, mainContainer);

        // Create scene
        scene = new Scene(rootContainer, 900, 700);
    }

    private void showCalendarPopup() {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Academic Calendar & Holidays");

        // Create background with same gradient
        StackPane popupRoot = new StackPane();
        
        // Background image for popup
        ImageView popupBgImage = null;
        try {
            Image bgImg = new Image("file:src/img/background.png");
            popupBgImage = new ImageView(bgImg);
            popupBgImage.setPreserveRatio(false);
            popupBgImage.fitWidthProperty().bind(popupRoot.widthProperty());
            popupBgImage.fitHeightProperty().bind(popupRoot.heightProperty());
            popupBgImage.setOpacity(0.3);
        } catch (Exception e) {
            System.out.println("Background image not found: " + e.getMessage());
            popupBgImage = new ImageView();
        }
        
        // Gradient overlay for popup
        VBox popupGradient = new VBox();
        popupGradient.setStyle(
            "-fx-background-color: linear-gradient(to bottom, rgba(44, 44, 44, 0.85), rgba(23, 83, 160, 0.85));"
        );

        VBox popupContainer = new VBox(20);
        popupContainer.setAlignment(Pos.TOP_CENTER);
        popupContainer.setPadding(new Insets(30));

        // Academic Calendar Section
        Label calendarTitle = new Label("Academic Calendar 2025-2026");
        calendarTitle.setStyle(
            "-fx-font-family: " + FONT_FAMILY + ";" +
            "-fx-font-size: 24px;" +
            "-fx-font-weight: 700;" +
            "-fx-text-fill: " + WHITE + ";"
        );

        TableView<String[]> calendarTable = createCalendarTable();
        calendarTable.setPrefHeight(300);

        // Holidays Section
        Label holidaysTitle = new Label("Holidays");
        holidaysTitle.setStyle(
            "-fx-font-family: " + FONT_FAMILY + ";" +
            "-fx-font-size: 24px;" +
            "-fx-font-weight: 700;" +
            "-fx-text-fill: " + WHITE + ";"
        );

        TableView<String[]> holidaysTable = createHolidaysTable();
        holidaysTable.setPrefHeight(250);

        // Close button
        Button closeBtn = new Button("Close");
        styleButton(closeBtn, WHITE, ICS_BLUE, false);
        closeBtn.setPrefWidth(150);
        closeBtn.setOnAction(e -> popupStage.close());

        popupContainer.getChildren().addAll(calendarTitle, calendarTable, holidaysTitle, holidaysTable, closeBtn);

        ScrollPane scrollPane = new ScrollPane(popupContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle(
            "-fx-background: transparent;" +
            "-fx-background-color: transparent;"
        );

        // Layer popup elements
        popupRoot.getChildren().addAll(popupBgImage, popupGradient, scrollPane);

        Scene popupScene = new Scene(popupRoot, 900, 700);
        popupStage.setScene(popupScene);
        popupStage.showAndWait();
    }
   
    private void styleButton(Button button, String bgColor, String textColor, boolean isPrimary) {
        button.setPrefHeight(45);
        button.setStyle(
            "-fx-font-family: " + FONT_FAMILY + ";" +
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
                    "-fx-font-family: " + FONT_FAMILY + ";" +
                    "-fx-font-size: 14px;" +
                    "-fx-font-weight: 600;" +
                    "-fx-background-color: " + LIGHT_BLUE + ";" +
                    "-fx-text-fill: " + textColor + ";" +
                    "-fx-background-radius: 8;" +
                    "-fx-cursor: hand;"
                );
            } else {
                // White button hover - change to light cream
                button.setStyle(
                    "-fx-font-family: " + FONT_FAMILY + ";" +
                    "-fx-font-size: 14px;" +
                    "-fx-font-weight: 600;" +
                    "-fx-background-color: " + CREAM + ";" +
                    "-fx-text-fill: " + ICS_BLUE + ";" +
                    "-fx-background-radius: 8;" +
                    "-fx-cursor: hand;"
                );
            }
        });

        button.setOnMouseExited(e -> {
            // Reset to original style
            button.setStyle(
                "-fx-font-family: " + FONT_FAMILY + ";" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: 600;" +
                "-fx-background-color: " + bgColor + ";" +
                "-fx-text-fill: " + textColor + ";" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;"
            );
        });
    }

    // Handles login and checks if all required fields are filled
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
    
    // Handles Register and checks if there is a duplicate registration
    private void handleRegister() {
    	RegisterView registerView = new RegisterView(fileManager, userList, savePath);
        registerView.showAndWait();

        if (registerView.isRegistrationSuccessful()) {
            messageLabel.setStyle(
                "-fx-font-family: " + FONT_FAMILY + ";" +
                "-fx-font-size: 12px;" +
                "-fx-text-fill: " + ICS_YELLOW + ";" +
                "-fx-font-weight: 500;"
            );
            messageLabel.setText("Registration successful! Please login.");
        }
    }

    private TableView<String[]> createCalendarTable() {
        TableView<String[]> table = new TableView<>();
        
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<String[], String> eventCol = new TableColumn<>("Event");
        eventCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[0]));
        eventCol.setPrefWidth(200);

        TableColumn<String[], String> sem1Col = new TableColumn<>("1st Semester");
        sem1Col.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[1]));
        sem1Col.setPrefWidth(120);
        // Center align this column
        sem1Col.setCellFactory(col -> {
            TableCell<String[], String> cell = new TableCell<String[], String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? null : item);
                    setStyle("-fx-alignment: CENTER;");
                }
            };
            return cell;
        });

        TableColumn<String[], String> sem2Col = new TableColumn<>("2nd Semester");
        sem2Col.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[2]));
        sem2Col.setPrefWidth(120);
        // Center align this column
        sem2Col.setCellFactory(col -> {
            TableCell<String[], String> cell = new TableCell<String[], String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? null : item);
                    setStyle("-fx-alignment: CENTER;");
                }
            };
            return cell;
        });

        TableColumn<String[], String> midyearCol = new TableColumn<>("Midyear");
        midyearCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[3]));
        midyearCol.setPrefWidth(100);
        // Center align this column
        midyearCol.setCellFactory(col -> {
            TableCell<String[], String> cell = new TableCell<String[], String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? null : item);
                    setStyle("-fx-alignment: CENTER;");
                }
            };
            return cell;
        });

        table.getColumns().addAll(eventCol, sem1Col, sem2Col, midyearCol);

        // Enable text wrapping for Event column only
        wrapColumnText(eventCol);

        // Load data using FileManager
        List<String[]> calendarData = fileManager.loadAcademicCalendar();
        if (calendarData != null) {
            table.getItems().addAll(calendarData);
        }

        // Apply inline CSS for header styling
        table.setStyle(
            "-fx-background-color: white;" +
            ".column-header-background { -fx-background-color: #2C2C2C; }" +
            ".column-header { -fx-background-color: #2C2C2C; -fx-text-fill: white; }" +
            ".column-header .label { -fx-text-fill: white; -fx-font-weight: bold; }" +
            ".filler { -fx-background-color: #2C2C2C; }"
        );

        return table;
    }

    private TableView<String[]> createHolidaysTable() {
        TableView<String[]> table = new TableView<>();
        
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<String[], String> yearCol = new TableColumn<>("Year");
        yearCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[0]));
        yearCol.setPrefWidth(80);
        // Center align this column
        yearCol.setCellFactory(col -> {
            TableCell<String[], String> cell = new TableCell<String[], String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? null : item);
                    setStyle("-fx-alignment: CENTER;");
                }
            };
            return cell;
        });

        TableColumn<String[], String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[1]));
        dateCol.setPrefWidth(150);
        // Center align this column
        dateCol.setCellFactory(col -> {
            TableCell<String[], String> cell = new TableCell<String[], String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? null : item);
                    setStyle("-fx-alignment: CENTER;");
                }
            };
            return cell;
        });

        TableColumn<String[], String> holidayCol = new TableColumn<>("Holiday");
        holidayCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[2]));
        holidayCol.setPrefWidth(250);
        // Center align this column
        holidayCol.setCellFactory(col -> {
            TableCell<String[], String> cell = new TableCell<String[], String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? null : item);
                    setStyle("-fx-alignment: CENTER;");
                }
            };
            return cell;
        });

        table.getColumns().addAll(yearCol, dateCol, holidayCol);

        // Load data using FileManager
        List<String[]> holidaysData = fileManager.loadHolidays();
        if (holidaysData != null) {
            table.getItems().addAll(holidaysData);
        }

        // Header Styling
        table.setStyle(
            "-fx-background-color: " + DARK_TEXT + ";" +
            ".column-header-background { -fx-background-color: " + DARK_TEXT + "; }" +
            ".column-header { -fx-background-color: #2C2C2C; -fx-text-fill: white; }" +
            ".column-header .label { -fx-text-fill: white; -fx-font-weight: bold; }" +
            ".filler { -fx-background-color: #2C2C2C; }"
        );

        return table;
    }

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

    public void setStage(Stage stage) {
        this.stage = stage;
        this.stage.setTitle("ICS Registration Planner - Home");
        this.stage.setScene(this.scene);
        this.stage.show();
    }

    public Scene getScene() {
        return scene;
    }
}