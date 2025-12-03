package application;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import model.Course;
import model.Student;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ScheduleComparisonView {
    private static final String ICS_BLUE = "#1753A0";
    private static final String LIGHT_BLUE = "#4B8BDE";
    private static final String CREAM = "#F5F1E8";
    private static final String WHITE = "#FFFFFF";
    private static final String DARK_TEXT = "#2C2C2C";
    private static final String ICS_YELLOW = "#F2ED0C";
    private static final String LIGHT_TEXT = "#666666";
    private static final String FONT_FAMILY = "'Poppins', 'Segoe UI', sans-serif";
    
    private Student student;
    private Runnable onScheduleChanged;
    
    private GridPane leftGrid;
    private GridPane rightGrid;
    private ComboBox<String> leftCombo;
    private ComboBox<String> rightCombo;
    private Label leftTitleLabel;
    private Label rightTitleLabel;
    private VBox mainContent;

    public ScheduleComparisonView(Student student, Runnable onScheduleChanged) {
        this.student = student;
        this.onScheduleChanged = onScheduleChanged;
    }
    public VBox createContent() {
        mainContent = new VBox(20);
        mainContent.setPadding(new Insets(20));
        mainContent.setStyle("-fx-background-color: " + WHITE + ";");

        // ===== MANAGEMENT SECTION (ONE LINE) =====
        HBox managementRow = new HBox(20);
        managementRow.setAlignment(Pos.CENTER_LEFT);
        managementRow.setPadding(new Insets(15, 20, 15, 20));
        managementRow.setStyle(
            "-fx-background-color: " + CREAM + ";" +
            "-fx-background-radius: 10;"
        );

        Label currentActiveLabel = new Label("Active Schedule: " + student.getActiveScheduleName());
        currentActiveLabel.setStyle(
            "-fx-font-family: " + FONT_FAMILY + ";" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: 600;" +
            "-fx-text-fill: " + DARK_TEXT + ";" +
            "-fx-background-color: " + ICS_YELLOW + ";" +
            "-fx-padding: 8 15 8 15;" +
            "-fx-background-radius: 5;"
        );

        Label actionsLabel = new Label("Actions:");
        actionsLabel.setStyle(
            "-fx-font-family: " + FONT_FAMILY + ";" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: 600;" +
            "-fx-text-fill: " + DARK_TEXT + ";"
        );

        Button saveAsButton = createStyledButton("💾 Save As...", "Save your current active schedule with a new name");
        Button newScheduleButton = createStyledButton("➕ New", "Start a fresh empty schedule");
        Button switchButton = createStyledButton("🔄 Switch", "Load a different schedule to work on");
        Button deleteButton = createStyledButton("🗑️ Delete", "Remove a schedule permanently");

        ScheduleManager scheduleManager = new ScheduleManager(student, () -> {
            onScheduleChanged.run();
            currentActiveLabel.setText("Active Schedule: " + student.getActiveScheduleName());
            refreshView();
        });

        saveAsButton.setOnAction(e -> { scheduleManager.showSaveAsDialog(); currentActiveLabel.setText("Active Schedule: " + student.getActiveScheduleName()); });
        newScheduleButton.setOnAction(e -> { scheduleManager.showNewScheduleDialog(); currentActiveLabel.setText("Active Schedule: " + student.getActiveScheduleName()); });
        switchButton.setOnAction(e -> { scheduleManager.showSwitchScheduleDialog(); currentActiveLabel.setText("Active Schedule: " + student.getActiveScheduleName()); });
        deleteButton.setOnAction(e -> { scheduleManager.showDeleteScheduleDialog(); currentActiveLabel.setText("Active Schedule: " + student.getActiveScheduleName()); });

        managementRow.getChildren().addAll(currentActiveLabel, actionsLabel, saveAsButton, newScheduleButton, switchButton, deleteButton);

        // ===== COMPARISON SECTION =====
        VBox comparisonBox = new VBox(15);
        
        // Calendar split pane with selectors on each side
        SplitPane calendarSplit = new SplitPane();
        calendarSplit.setDividerPositions(0.5);

        // LEFT SIDE
        VBox leftSide = new VBox(10);
        leftSide.setPadding(new Insets(10));
        leftSide.setStyle("-fx-background-color: " + WHITE + ";");

        // Left selector (one line)
        HBox leftSelectorBox = new HBox(10);
        leftSelectorBox.setAlignment(Pos.CENTER_LEFT);
        
        Label leftLabel = new Label("Schedule:");
        leftLabel.setStyle(
            "-fx-font-family: " + FONT_FAMILY + ";" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: 600;" +
            "-fx-text-fill: " + DARK_TEXT + ";"
        );

        leftCombo = new ComboBox<>();
        leftCombo.setPromptText("Select schedule");
        leftCombo.setPrefWidth(200);
        styleComboBox(leftCombo);
        leftCombo.setOnAction(e -> refreshComparison());

        leftSelectorBox.getChildren().addAll(leftLabel, leftCombo);

        leftTitleLabel = new Label("");
        leftTitleLabel.setStyle(
            "-fx-font-family: " + FONT_FAMILY + ";" +
            "-fx-font-size: 16px;" +
            "-fx-font-weight: 700;" +
            "-fx-text-fill: " + ICS_BLUE + ";"
        );

        leftGrid = createCalendarGrid();
        leftSide.getChildren().addAll(leftSelectorBox, leftGrid);

        // RIGHT SIDE
        VBox rightSide = new VBox(10);
        rightSide.setPadding(new Insets(10));
        rightSide.setStyle("-fx-background-color: " + WHITE + ";");

        // Right selector (one line)
        HBox rightSelectorBox = new HBox(10);
        rightSelectorBox.setAlignment(Pos.CENTER_LEFT);
        
        Label rightLabel = new Label("Schedule:");
        rightLabel.setStyle(
            "-fx-font-family: " + FONT_FAMILY + ";" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: 600;" +
            "-fx-text-fill: " + DARK_TEXT + ";"
        );

        rightCombo = new ComboBox<>();
        rightCombo.setPromptText("Select schedule");
        rightCombo.setPrefWidth(200);
        styleComboBox(rightCombo);
        rightCombo.setOnAction(e -> refreshComparison());

        rightSelectorBox.getChildren().addAll(rightLabel, rightCombo);

        rightTitleLabel = new Label("");
        rightTitleLabel.setStyle(
            "-fx-font-family: " + FONT_FAMILY + ";" +
            "-fx-font-size: 16px;" +
            "-fx-font-weight: 700;" +
            "-fx-text-fill: " + ICS_BLUE + ";"
        );

        rightGrid = createCalendarGrid();
        rightSide.getChildren().addAll(rightSelectorBox, rightGrid);

        calendarSplit.getItems().addAll(leftSide, rightSide);

        // Wrap entire split pane in ONE scroll pane (like About tab)
        ScrollPane comparisonScroll = new ScrollPane(calendarSplit);
        comparisonScroll.setFitToWidth(true);
        comparisonScroll.setPannable(true);
        comparisonScroll.setStyle("-fx-background: " + WHITE + "; -fx-border-color: transparent;");
        VBox.setVgrow(comparisonScroll, Priority.ALWAYS);

        comparisonBox.getChildren().add(comparisonScroll);
        VBox.setVgrow(comparisonBox, Priority.ALWAYS);

        mainContent.getChildren().addAll(managementRow, comparisonBox);
        VBox.setVgrow(comparisonBox, Priority.ALWAYS);

        // Initialize
        updateComboBoxes();
        
        return mainContent;
    }

    // Modified button creation with smaller width
    private Button createStyledButton(String text, String tooltip) {
        Button button = new Button(text);
        button.setTooltip(new Tooltip(tooltip));
        button.setPrefWidth(120); // Reduced for one-line fit
        button.setPrefHeight(35);
        button.setStyle(
            "-fx-font-family: " + FONT_FAMILY + ";" +
            "-fx-font-size: 12px;" +
            "-fx-font-weight: 600;" +
            "-fx-background-color: " + ICS_BLUE + ";" +
            "-fx-text-fill: " + WHITE + ";" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;"
        );

        button.setOnMouseEntered(e -> button.setStyle(
            "-fx-font-family: " + FONT_FAMILY + ";" +
            "-fx-font-size: 12px;" +
            "-fx-font-weight: 600;" +
            "-fx-background-color: " + LIGHT_BLUE + ";" +
            "-fx-text-fill: " + WHITE + ";" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;"
        ));

        button.setOnMouseExited(e -> button.setStyle(
            "-fx-font-family: " + FONT_FAMILY + ";" +
            "-fx-font-size: 12px;" +
            "-fx-font-weight: 600;" +
            "-fx-background-color: " + ICS_BLUE + ";" +
            "-fx-text-fill: " + WHITE + ";" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;"
        ));

        return button;
    }

    private void styleComboBox(ComboBox<String> combo) {
        combo.setStyle(
            "-fx-font-family: " + FONT_FAMILY + ";" +
            "-fx-font-size: 13px;" +
            "-fx-background-color: " + WHITE + ";" +
            "-fx-border-color: " + ICS_BLUE + ";" +
            "-fx-border-width: 2px;" +
            "-fx-border-radius: 6;" +
            "-fx-background-radius: 6;"
        );
    }

    public void refreshView() {
        updateComboBoxes();
        refreshComparison();
    }

    private void updateComboBoxes() {
        Platform.runLater(() -> {
            List<String> schedules = new ArrayList<>(student.getSavedScheduleNames());
            
            String currentLeft = leftCombo.getValue();
            String currentRight = rightCombo.getValue();
            
            leftCombo.getItems().clear();
            rightCombo.getItems().clear();
            
            leftCombo.getItems().addAll(schedules);
            rightCombo.getItems().addAll(schedules);
            
            if (schedules.contains(currentLeft)) {
                leftCombo.setValue(currentLeft);
            } else if (!schedules.isEmpty()) {
                leftCombo.setValue(student.getActiveScheduleName());
            }
            
            if (schedules.contains(currentRight)) {
                rightCombo.setValue(currentRight);
            } else if (schedules.size() > 1) {
                for (String s : schedules) {
                    if (!s.equals(leftCombo.getValue())) {
                        rightCombo.setValue(s);
                        break;
                    }
                }
            }
        });
    }

    private void refreshComparison() {
        String leftSchedule = leftCombo.getValue();
        String rightSchedule = rightCombo.getValue();

        clearGrid(leftGrid);
        clearGrid(rightGrid);

        if (leftSchedule != null) {
            leftTitleLabel.setText("");
            fillCalendar(leftGrid, student.getSchedule(leftSchedule), LIGHT_BLUE, WHITE, ICS_BLUE);
        } else {
            leftTitleLabel.setText("");
        }

        if (rightSchedule != null) {
            rightTitleLabel.setText("");
            fillCalendar(rightGrid, student.getSchedule(rightSchedule), "#90EE90", DARK_TEXT, "#228B22");
        } else {
            rightTitleLabel.setText("");
        }
    }

    private GridPane createCalendarGrid() {
        GridPane grid = new GridPane();
        grid.setGridLinesVisible(true);
        grid.setHgap(5);
        grid.setVgap(5);
        grid.setPadding(new Insets(10));
        grid.setStyle("-fx-background-color: " + WHITE + ";");

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
            grid.add(dayLabel, i + 1, 0);
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
            grid.add(timeLabel, 0, i + 1);
        }

        return grid;
    }

    private void clearGrid(GridPane grid) {
        grid.getChildren().removeIf(node -> {
            Integer r = GridPane.getRowIndex(node);
            Integer c = GridPane.getColumnIndex(node);
            return !((r != null && r == 0) || (c != null && c == 0));
        });
    }

    private void fillCalendar(GridPane grid, List<Course> courses, String bgColor, String textColor, String borderColor) {
        for (Course c : courses) {
            if (c.getTimes() == null || c.getTimes().trim().isEmpty() || c.getTimes().equalsIgnoreCase("TBA")) {
                continue;
            }

            String[] parts = c.getTimes().split("-");
            if (parts.length < 2) continue;

            int startHour = parseHour(parts[0].trim());
            int endHour = parseHour(parts[1].trim());
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

            int startRow = start24 - 6;
            int endRow = end24 - 6;
            int rowSpan = Math.max(1, endRow - startRow);

            for (String rawDay : expandDays(c.getDays())) {
                int col = dayToColumn(rawDay);
                if (col == -1) continue;

                Label courseBlock = new Label(c.getCourseCode() + "\n" + c.getSection());
                courseBlock.setStyle(
                    "-fx-background-color: " + bgColor + ";" +
                    "-fx-border-color: " + borderColor + ";" +
                    "-fx-border-width: 2px;" +
                    "-fx-text-fill: " + textColor + ";" +
                    "-fx-font-family: " + FONT_FAMILY + ";" +
                    "-fx-font-size: 12px;" +
                    "-fx-font-weight: 600;" +
                    "-fx-alignment: center;" +
                    "-fx-background-radius: 5;" +
                    "-fx-border-radius: 5;"
                );
                courseBlock.setMinSize(100, 50 * rowSpan);
                courseBlock.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                
                Tooltip tooltip = new Tooltip(
                    c.getCourseTitle() + "\n" +
                    "Units: " + c.getUnits() + "\n" +
                    "Time: " + c.getTimes() + "\n" +
                    "Room: " + c.getRooms()
                );
                Tooltip.install(courseBlock, tooltip);
                
                grid.add(courseBlock, col + 1, startRow + 1, 1, rowSpan);
            }
        }
    }

    private int parseHour(String timeStr) {
        try {
            String hourPart = timeStr.split(":")[0].trim();
            return Integer.parseInt(hourPart);
        } catch (Exception e) {
            return -1;
        }
    }

    private static int dayToColumn(String day) {
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

    private static List<String> expandDays(String dayString) {
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

    public void onScheduleChanged(String newActiveName) {
        refreshView();
    }
}