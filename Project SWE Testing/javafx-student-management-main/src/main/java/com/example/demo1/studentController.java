package com.example.demo1;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;

public class studentController implements Initializable {
    public ArrayList<String> lessons = new ArrayList<>();
    public static ArrayList<String> selectedLessons = new ArrayList<>();
    public static String username;
    public static String studentCode;

    @FXML private TextField nameField, stdName, loginTimeField, stdCode;
    @FXML private VBox lessonContainer;

    private ArrayList<CheckBox> checkBoxes = new ArrayList<>();
    private ArrayList<String> LessonsId = new ArrayList<>();
    public AnchorPane createLessonComponent(String course, String professor, String unit, String examDate, String examVenue, String classLocation, String lessonId) {
        AnchorPane anchorPane = new AnchorPane();
        anchorPane.setPrefHeight(200.0);
        anchorPane.setPrefWidth(800.0);
        anchorPane.setStyle("-fx-background-color: #cde0e1;");

        // Create CheckBox
        CheckBox checkBox = new CheckBox();
        checkBox.setLayoutX(19.0);
        checkBox.setLayoutY(32.0);
        checkBox.setPrefHeight(144.0);
        checkBox.setPrefWidth(772.0);
        checkBox.setStyle("-fx-background-color: #e5e5dc; -fx-border-radius: 40; -fx-border-color: green; -fx-border-width: 2; -fx-background-radius: 40;");
        DropShadow dropShadow = new DropShadow();
        dropShadow.setHeight(67.79);
        dropShadow.setRadius(30.47);
        dropShadow.setWidth(56.09);
        dropShadow.setColor(Color.web("#027027"));
        checkBox.setEffect(dropShadow);

        // Add the checkbox to the list
        checkBoxes.add(checkBox);

        // Add the lessonId to the LessonsId list
        LessonsId.add(lessonId);

        // Create Text elements
        Text courseText = new Text("Course:");
        courseText.setFill(Color.web("#610c0c"));
        courseText.setLayoutX(116.0);
        courseText.setLayoutY(50.0);
        courseText.setStyle("-fx-font-size: 15;");
        courseText.setUnderline(true);

        Text professorText = new Text("Professor:");
        professorText.setFill(Color.web("#610c0c"));
        professorText.setLayoutX(380.0);
        professorText.setLayoutY(50.0);
        professorText.setStyle("-fx-font-size: 15;");
        professorText.setUnderline(true);

        Text unitText = new Text("Unit:");
        unitText.setFill(Color.web("#610c0c"));
        unitText.setLayoutX(619.0);
        unitText.setLayoutY(50.0);
        unitText.setStyle("-fx-font-size: 15;");
        unitText.setUnderline(true);

        Text examDateText = new Text("Exam date time:");
        examDateText.setFill(Color.web("#610c0c"));
        examDateText.setLayoutX(82.0);
        examDateText.setLayoutY(120.0);
        examDateText.setStyle("-fx-font-size: 15;");
        examDateText.setUnderline(true);

        Text examVenueText = new Text("Examination venue:");
        examVenueText.setFill(Color.web("#610c0c"));
        examVenueText.setLayoutX(359.0);
        examVenueText.setLayoutY(120.0);
        examVenueText.setStyle("-fx-font-size: 15;");
        examVenueText.setUnderline(true);

        Text classLocationText = new Text("Class location:");
        classLocationText.setFill(Color.web("#610c0c"));
        classLocationText.setLayoutX(596.0);
        classLocationText.setLayoutY(120.0);
        classLocationText.setStyle("-fx-font-size: 15;");
        classLocationText.setUnderline(true);

        // Create TextField elements
        TextField courseField = new TextField(course);
        courseField.setEditable(false);
        courseField.setOpacity(0.5);
        courseField.setLayoutX(173.0);
        courseField.setLayoutY(30.0);
        courseField.setPrefHeight(26.0);
        courseField.setPrefWidth(115.0);
        courseField.setStyle("-fx-background-color: #e5e5dc;");

        TextField professorField = new TextField(professor);
        professorField.setEditable(false);
        professorField.setOpacity(0.5);
        professorField.setLayoutX(465.0);
        professorField.setLayoutY(30.0);
        professorField.setPrefHeight(26.0);
        professorField.setPrefWidth(115.0);
        professorField.setStyle("-fx-background-color: #e5e5dc;");

        TextField unitField = new TextField(unit);
        unitField.setEditable(false);
        unitField.setOpacity(0.5);
        unitField.setLayoutX(651.0);
        unitField.setLayoutY(30.0);
        unitField.setPrefHeight(26.0);
        unitField.setPrefWidth(115.0);
        unitField.setStyle("-fx-background-color: #e5e5dc;");

        TextField examDateField = new TextField(examDate);
        examDateField.setEditable(false);
        examDateField.setOpacity(0.5);
        examDateField.setLayoutX(210.0);
        examDateField.setLayoutY(100.0);
        examDateField.setPrefHeight(26.0);
        examDateField.setPrefWidth(130.0);
        examDateField.setStyle("-fx-background-color: #e5e5dc;");

        TextField examVenueField = new TextField(examVenue);
        examVenueField.setEditable(false);
        examVenueField.setOpacity(0.5);
        examVenueField.setLayoutX(494.0);
        examVenueField.setLayoutY(100.0);
        examVenueField.setPrefHeight(26.0);
        examVenueField.setPrefWidth(115.0);
        examVenueField.setStyle("-fx-background-color: #e5e5dc;");

        TextField classLocationField = new TextField(classLocation);
        classLocationField.setEditable(false);
        classLocationField.setOpacity(0.5);
        classLocationField.setLayoutX(694.0);
        classLocationField.setLayoutY(100.0);
        classLocationField.setPrefHeight(26.0);
        classLocationField.setPrefWidth(115.0);
        classLocationField.setStyle("-fx-background-color: #e5e5dc;");

        // Add elements to AnchorPane
        anchorPane.getChildren().addAll(
                checkBox,
                courseText, professorText, unitText,
                examDateText, examVenueText, classLocationText,
                courseField, professorField, unitField,
                examDateField, examVenueField, classLocationField
        );

        return anchorPane;
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        selectedLessons.clear();

        // Get username
        String signName = signupController.name;
        String LoginName = LoginController.name;

        if (signName != null) {
            nameField.setText(signName);
            stdName.setText(signName);
            username = signName;
        } else {
            nameField.setText(LoginName);
            stdName.setText(LoginName);
            username = LoginName;
        }

        stdCode();
        loginTimeField.setText(LoginController.time);

        // Show data
        handleData();
    }

    public void handleData() {
        try (BufferedReader br = new BufferedReader(new FileReader("LessonsFiles" + function.getTerm() + "Lessons.txt"))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] parts = line.split(", ");

                // Create a dynamic AnchorPane for this lesson
                AnchorPane lessonComponent = createLessonComponent(
                        parts[0], // Course
                        parts[1], // Professor
                        parts[2], // Unit
                        parts[7], // Exam date
                        parts[8], // Exam venue
                        parts[9], // Class location
                        parts[6]  // Lesson ID
                );

                // Add the dynamically created component to the VBox container
                lessonContainer.getChildren().add(lessonComponent);
            }
        } catch (IOException e) {
            function.AddLog(username, "Error reading lessons file: " + e.getMessage());
        }
    }

    public void stdCode() {
        try (BufferedReader br = new BufferedReader(new FileReader("Users.txt"))) {
            String line;
            boolean flag = true;
            while ((line = br.readLine()) != null && flag) {
                String[] data = line.split(", ");
                if (data[0].equals(username) && data[3].equals("STUDENT")) {
                    stdCode.setText(studentCode = data[1]);
                    flag = false;
                }
            }
        } catch (IOException e) {
            function.AddLog(username, "get student code - " + e.getMessage());
        }
    }

    public void confirm(ActionEvent event) throws IOException {
        // Debugging the username
        System.out.println("Username: " + username+" -"+checkBoxes.size());

        // Debugging the loop and selection
        for (int i = 0; i < checkBoxes.size(); i++) {
            System.out.println("Index: " + i + ", Selected: " + checkBoxes.get(i).isSelected());
            if (checkBoxes.get(i).isSelected()) {
                saveLesson(username, LessonsId.get(i));
                System.out.println("Saved Lesson ID: " + LessonsId.get(i));
            }
        }

        // Debugging scene transition
        try {
            Parent loader = FXMLLoader.load(getClass().getResource("studentLessons.fxml"));

            Scene scene = new Scene(loader);
            Stage app_stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            app_stage.setScene(scene);
            app_stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Debugging the ActionEvent
        System.out.println("Event Source: " + event.getSource());
    }


    public void saveLesson(String studentName, String lessonId) {
        try (BufferedReader br = new BufferedReader(new FileReader(function.getTerm()+"StudentLessons.txt"))) {
            String line;

            // Debug: Start of the file reading process
            System.out.println("Starting to read the file...");

            while ((line = br.readLine()) != null) {
                // Debug: Print the current line
                System.out.println("Read line: " + line);

                String[] data = line.split(", ");
                // Debug: Print the parsed data array
                System.out.println("Parsed data: " + Arrays.toString(data));

                if (studentName.equals(data[0])) {
                    lessons.add(data[3]);
                    // Debug: Log when a lesson is added for the student
                    System.out.println("Lesson added for student " + studentName + ": " + data[3]);
                }
            }

            // Debug: Print the lessons before adding the new one
            System.out.println("Current lessons: " + lessons);

            if (!lessons.contains(lessonId)) {
                selectedLessons.add(lessonId);
                // Debug: Log the addition of a new lesson
                System.out.println("New lesson added to selectedLessons: " + lessonId);
            }

        } catch (Exception e) {
            // Debug: Print the exception message
            System.out.println("Error: " + e.getMessage());
            function.AddLog(username, "error while saving new lessons: " + e.getMessage());
        }

// Debug: Print the final state of selectedLessons
        System.out.println("Final selected lessons: " + selectedLessons);

    }

    public void back(ActionEvent event) throws IOException {
        Parent loader = FXMLLoader.load(getClass().getResource("login.fxml"));
        Scene scene = new Scene(loader);
        Stage app_stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        app_stage.setScene(scene);
        app_stage.show();
    }

    public void exit(ActionEvent event) throws IOException {
        Parent loader = FXMLLoader.load(getClass().getResource("login.fxml"));
        Scene scene = new Scene(loader);
        Stage app_stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        app_stage.setScene(scene);
        app_stage.show();
    }

    public void goReport(ActionEvent event) throws IOException {
        Parent loader = FXMLLoader.load(getClass().getResource("report.fxml"));
        Scene scene = new Scene(loader);
        Stage app_stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        app_stage.setScene(scene);
        app_stage.show();
    }
}
