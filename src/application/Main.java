/**
 * Java GUI application that provides course registration planning for ICS students.
 * DANGAL allows students to browse course offerings, create schedules, check for conflicts,
 * and manage multiple schedule plans and compare them for the First Semester of AY 2025-2026.
 * 
 * @authors MAGWILI, VINCE ROI SOLANO
 *          MANUEL, IVAN ERICK GONZALES
 *          NIÑO, BOBBY FROI SOLIVEN
 *          OLIVO, RAFAEL SAM GELISANGA
 * @created_date 2025
 */


package application;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("ICS Registration Planner");
        primaryStage.setWidth(1280);
        primaryStage.setHeight(720);

        LoginView loginView = new LoginView();
        loginView.setStage(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}