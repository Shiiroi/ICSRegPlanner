package application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import handler.FileManager;
import model.Student;
import java.nio.file.Path;
import java.util.ArrayList;

public class RegisterView {
    // ICS Color Palette
    private static final String ICS_BLUE = "#1753A0";
    private static final String ICS_YELLOW = "#F2ED0C";
    private static final String LIGHT_BLUE = "#4B8BDE";
    private static final String CREAM = "#F5F1E8";
    private static final String WHITE = "#FFFFFF";
    private static final String DARK_TEXT = "#2C2C2C";
    private static final String LIGHT_TEXT = "#666666";
    
    private Stage stage;
    private boolean registrationSuccessful = false;
    private FileManager fileManager;
    private ArrayList<Student> userList;
    private Path savePath;
    
    public RegisterView(FileManager fileManager, ArrayList<Student> userList, Path savePath) {
        this.fileManager = fileManager;
        this.userList = userList;
        this.savePath = savePath;
        this.stage = new Stage();
        this.stage.initModality(Modality.APPLICATION_MODAL);
        this.stage.setTitle("Register New Account");
        setupUI();
    }
    
    private void setupUI() {
        VBox container = new VBox(20);
        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(30));
        container.setStyle("-fx-background-color: linear-gradient(to bottom, " + CREAM + ", " + WHITE + ");");
        
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(15);
        grid.setVgap(8);  // Reduced gap for better spacing control
        grid.setPadding(new Insets(30, 35, 30, 35));
        grid.setStyle(
            "-fx-background-color: " + WHITE + ";" +
            "-fx-background-radius: 15;" +
            "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.15), 20, 0, 0, 5);"
        );
        
        Label titleLabel = new Label("Create Account");
        titleLabel.setStyle(
            "-fx-font-family: 'Poppins', 'Segoe UI', sans-serif;" +
            "-fx-font-size: 24px;" +
            "-fx-font-weight: 700;" +
            "-fx-text-fill: " + ICS_BLUE + ";"
        );
        grid.add(titleLabel, 0, 0, 2, 1);
        
        Label subtitleLabel = new Label("Fill in your details to register");
        subtitleLabel.setStyle(
            "-fx-font-family: 'Poppins', 'Segoe UI', sans-serif;" +
            "-fx-font-size: 13px;" +
            "-fx-font-weight: 400;" +
            "-fx-text-fill: " + LIGHT_TEXT + ";"
        );
        grid.add(subtitleLabel, 0, 1, 2, 1);
        
        TextField firstNameField = new TextField();
        TextField middleNameField = new TextField();
        TextField lastNameField = new TextField();
        TextField emailField = new TextField();
        PasswordField passwordField = new PasswordField();
        PasswordField confirmPasswordField = new PasswordField();
        ComboBox<String> programCombo = new ComboBox<>();
        programCombo.getItems().addAll(
            "BS Computer Science",
            "MS Computer Science",
            "Master of Information Technology",
            "PhD Computer Science"
        );
        
        // Style all text fields
        styleTextField(firstNameField, "First Name");
        styleTextField(middleNameField, "Middle Name (Optional)");
        styleTextField(lastNameField, "Last Name");
        styleTextField(emailField, "Email Address");
        styleTextField(passwordField, "Password");
        styleTextField(confirmPasswordField, "Confirm Password");
        
        programCombo.setPromptText("Select Program");
        programCombo.setPrefWidth(400);
        programCombo.setStyle(
            "-fx-font-family: 'Poppins', 'Segoe UI', sans-serif;" +
            "-fx-font-size: 13px;" +
            "-fx-background-color: " + CREAM + ";" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: transparent;" +
            "-fx-border-radius: 8;"
        );
        
        // Add labels and fields with proper row spacing
        int currentRow = 2;
        addFormRow(grid, currentRow, "First Name", firstNameField);
        currentRow += 2;  // Skip a row between label and next label
        
        addFormRow(grid, currentRow, "Middle Name", middleNameField);
        currentRow += 2;
        
        addFormRow(grid, currentRow, "Last Name", lastNameField);
        currentRow += 2;
        
        addFormRow(grid, currentRow, "Email", emailField);
        currentRow += 2;
        
        addFormRow(grid, currentRow, "Password", passwordField);
        currentRow += 2;
        
        addFormRow(grid, currentRow, "Confirm Password", confirmPasswordField);
        currentRow += 2;
        
        addFormRow(grid, currentRow, "Program", programCombo);
        currentRow += 2;
        
        Label messageLabel = new Label();
        messageLabel.setStyle(
            "-fx-font-family: 'Poppins', 'Segoe UI', sans-serif;" +
            "-fx-font-size: 12px;" +
            "-fx-text-fill: " + ICS_BLUE + ";" +
            "-fx-font-weight: 500;"
        );
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(400);
        grid.add(messageLabel, 0, currentRow, 2, 1);
        currentRow++;
        
        Button registerButton = new Button("Register");
        styleButton(registerButton, ICS_BLUE, WHITE, true);
        
        Button cancelButton = new Button("Cancel");
        styleButton(cancelButton, "transparent", ICS_BLUE, false);
        cancelButton.setStyle(
            cancelButton.getStyle() +
            "-fx-border-color: " + ICS_BLUE + ";" +
            "-fx-border-width: 2px;" +
            "-fx-border-radius: 8;"
        );
        
        HBox buttonBox = new HBox(15, registerButton, cancelButton);
        buttonBox.setAlignment(Pos.CENTER);
        grid.add(buttonBox, 0, currentRow, 2, 1);
        
        registerButton.setOnAction(e -> {
            String error = validateFields(firstNameField, lastNameField, emailField, 
                                         passwordField, confirmPasswordField, programCombo);
            if (error != null) {
                messageLabel.setStyle(
                    "-fx-font-family: 'Poppins', 'Segoe UI', sans-serif;" +
                    "-fx-font-size: 12px;" +
                    "-fx-text-fill: " + ICS_BLUE + ";" +
                    "-fx-font-weight: 500;"
                );
                messageLabel.setText(error);
            } else {
                Student newStudent = new Student(
                    firstNameField.getText(),
                    middleNameField.getText(),
                    lastNameField.getText(),
                    emailField.getText(),
                    passwordField.getText(),
                    programCombo.getValue()
                );
                userList.add(newStudent);
                fileManager.save(userList, savePath);
                registrationSuccessful = true;
                stage.close();
            }
        });
        
        cancelButton.setOnAction(e -> stage.close());
        
        container.getChildren().add(grid);
        Scene scene = new Scene(container, 500, 750);
        stage.setScene(scene);
    }
    
    private void addFormRow(GridPane grid, int row, String labelText, Control field) {
        Label label = new Label(labelText);
        label.setStyle(
            "-fx-font-family: 'Poppins', 'Segoe UI', sans-serif;" +
            "-fx-font-size: 12px;" +
            "-fx-font-weight: 600;" +
            "-fx-text-fill: " + DARK_TEXT + ";"
        );
        // Label goes in row
        grid.add(label, 0, row, 2, 1);
        
        // Field goes in row + 1 (next row)
        field.setPrefWidth(400);
        grid.add(field, 0, row + 1, 2, 1);
    }
    
    private void styleTextField(TextField field, String prompt) {
        field.setPromptText(prompt);
        field.setPrefWidth(400);
        field.setStyle(
            "-fx-font-family: 'Poppins', 'Segoe UI', sans-serif;" +
            "-fx-font-size: 13px;" +
            "-fx-padding: 10px;" +
            "-fx-background-color: " + CREAM + ";" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: transparent;" +
            "-fx-border-radius: 8;" +
            "-fx-prompt-text-fill: " + LIGHT_TEXT + ";"
        );
        
        field.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                field.setStyle(
                    "-fx-font-family: 'Poppins', 'Segoe UI', sans-serif;" +
                    "-fx-font-size: 13px;" +
                    "-fx-padding: 10px;" +
                    "-fx-background-color: " + WHITE + ";" +
                    "-fx-background-radius: 8;" +
                    "-fx-border-color: " + ICS_YELLOW + ";" +
                    "-fx-border-width: 2px;" +
                    "-fx-border-radius: 8;" +
                    "-fx-prompt-text-fill: " + LIGHT_TEXT + ";"
                );
            } else {
                field.setStyle(
                    "-fx-font-family: 'Poppins', 'Segoe UI', sans-serif;" +
                    "-fx-font-size: 13px;" +
                    "-fx-padding: 10px;" +
                    "-fx-background-color: " + CREAM + ";" +
                    "-fx-background-radius: 8;" +
                    "-fx-border-color: transparent;" +
                    "-fx-border-radius: 8;" +
                    "-fx-prompt-text-fill: " + LIGHT_TEXT + ";"
                );
            }
        });
    }
    
    private void styleButton(Button button, String bgColor, String textColor, boolean isPrimary) {
        button.setPrefWidth(isPrimary ? 400 : 190);
        button.setPrefHeight(42);
        button.setStyle(
            "-fx-font-family: 'Poppins', 'Segoe UI', sans-serif;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: 600;" +
            "-fx-background-color: " + bgColor + ";" +
            "-fx-text-fill: " + textColor + ";" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;"
        );
        
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
    
    private String validateFields(
    		TextField firstName, TextField lastName, TextField email,
            PasswordField password, PasswordField confirmPassword,
            ComboBox<String> program) {
		if (firstName.getText().isEmpty() || lastName.getText().isEmpty()) {
			return "First and Last name are required";
		}
		
		String emailError = validateEmail(email.getText());
			if (emailError != null) {
			return emailError;
		}
		
		if (password.getText().length() < 4) {
			return "Password must be at least 4 characters";
		}
		if (!password.getText().equals(confirmPassword.getText())) {
			return "Passwords do not match";
		}
		if (program.getValue() == null) {
			return "Please select a program";
		}
		
		return null;
		}
		
		private String validateEmail(String emailText) {
			if (emailText.isEmpty()) {
			return "Email is required";
		}
		
		if (!emailText.contains("@") || !emailText.contains(".com")) {
			return "Please enter a valid email address (e.g., name@mail.com)";
		}
		
		for (Student s : userList) {
			if (s.getEmail().equalsIgnoreCase(emailText)) {
			return "Email already registered";
			}
		}
		
		return null;
		}

    
    public void showAndWait() {
        stage.showAndWait();
    }
    
    public boolean isRegistrationSuccessful() {
        return registrationSuccessful;
    }
}