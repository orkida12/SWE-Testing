package com.example.demo1;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;
public class reportController implements Initializable {
    @FXML private TextField stdCode, stdName, nameField, termField, entranceField, gpa;
    @FXML private TableView<myLessons> myTable;
    @FXML private TableColumn<myLessons, Integer> row;
    @FXML private TableColumn<myLessons, String> lesson, unit, classTime, teacher, examTime, score, status;
    @FXML private ComboBox<String> termBox;

    public String term = function.getTerm();
    public static String entrance;
    private final ArrayList<String> capacity = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("Initializing reportController...");

        String signEntr = signupController.entranceDate;
        String loginEntr = LoginController.entrance;

        if (signEntr != null) {
            entrance = signEntr;
        } else {
            entrance = loginEntr;
        }
        System.out.println("Entrance date: " + entrance);

        entranceField.setText(entrance);

        String term = function.getTerm();
        System.out.println("Current term: " + term);

        row.setCellValueFactory(new PropertyValueFactory<>("Row"));
        lesson.setCellValueFactory(new PropertyValueFactory<>("Lesson"));
        teacher.setCellValueFactory(new PropertyValueFactory<>("Teacher"));
        classTime.setCellValueFactory(new PropertyValueFactory<>("ClassTime"));
        unit.setCellValueFactory(new PropertyValueFactory<>("Unit"));
        examTime.setCellValueFactory(new PropertyValueFactory<>("ExamTime"));
        score.setCellValueFactory(new PropertyValueFactory<>("Score"));
        status.setCellValueFactory(new PropertyValueFactory<>("Status"));

        myTable.getItems().clear();

        stdName.setText(studentController.username);
        nameField.setText(studentController.username);
        stdCode.setText(studentController.studentCode);

        System.out.println("Student info - Name: " + studentController.username + ", Code: " + studentController.studentCode);

        showLessons(myTable, studentController.username, term);

        handleTerms(capacity, entrance);
    }
    //TODO username  ne vend qe te perdoret id
    public void showLessons(TableView<myLessons> table, String username, String term) {
        table.getItems().clear();
        System.out.println("Loading lessons for user: " + username + ", term: " + term);

        try (BufferedReader stReader = new BufferedReader(new FileReader(term + "StudentLessons"))) {
            float sum = 0;
            int count = 1;
            String stLine;

            while ((stLine = stReader.readLine()) != null) {
                System.out.println("Reading line: " + stLine);
                String[] stParts = stLine.split(", ");

                if (stParts[2].equals(username)) {
                    String score = stParts[2];
                    String lessonId = stParts[3].trim();

                    try (BufferedReader lesReader = new BufferedReader(new FileReader("LessonsFiles" + term + "Lessons.txt"))) {
                        String lesLine;
                        boolean flag = true;
                        while ((lesLine = lesReader.readLine()) != null && flag) {
                            String[] lesParts = lesLine.split(", ");
                            String lesLessonId = lesParts[0].trim(); // Trim spaces for comparison
                            System.out.println("Matching lesson ID: " + lessonId + " in line: " + lesLine);

                            if (lesLessonId.equals(lessonId)) {
                                System.out.println("Matching lessonId found: " + lessonId);

                                String status = "Unknown";
                                if (!score.equals("null")) {
                                    try {
                                        float parsedScore = Float.parseFloat(score);
                                        sum += parsedScore;

                                        if (parsedScore > 10) {
                                            status = "Passed";
                                        } else {
                                            status = "Failed";
                                        }
                                    } catch (NumberFormatException e) {
                                        System.out.println("Error parsing score: " + score);
                                    }
                                } else {
                                    score = "";
                                }

                                table.getItems().add(new myLessons(
                                        count++, lesParts[0], lesParts[1], lesParts[2],
                                        lesParts[5], lesParts[7], score, status
                                ));
                                flag = false;
                            }
                        }
                    } catch (IOException e) {
                        System.out.println("Error reading lessons file: " + e.getMessage());
                        function.AddLog(username, e.getMessage());
                    }
                }
            }

            // Writing GPA
            if (count != 1) { // If lessons were found, count was incremented from 1
                DecimalFormat df = new DecimalFormat("#.##");
                gpa.setText(df.format(sum / (count - 1))); // Subtract 1 because count starts at 1
            } else {
                gpa.setText("0");
            }
        } catch (IOException e) {
            System.out.println("Error reading student lessons file: " + e.getMessage());
            function.AddLog(username, e.getMessage());
        }
    }

    public void changeTerm() {
        String item = termBox.getValue();
        termField.setText(item);
        term = termBox.getValue();
        System.out.println("Term changed to: " + term);
        showLessons(myTable, studentController.username, term);
    }
//TODO logjika me kot e mundshme
    public void handleTerms(ArrayList<String> array, String entrance) {
        System.out.println("Populating terms based on entrance: " + entrance);

        // Directly add the entrance term to the array without modifications
        array.add(entrance);

        // Add the terms directly to the ComboBox without changes
        termBox.getItems().addAll(array);

        System.out.println("Terms added: " + array);
       /* termBox.getItems().addAll(capacity);
        System.out.println("Terms added: " + capacity);*/
    }

    public void exit(ActionEvent event) throws IOException {
        System.out.println("Exiting application...");
        Parent loader = FXMLLoader.load(getClass().getResource("login.fxml"));
        Scene scene = new Scene(loader);
        Stage app_stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        app_stage.setScene(scene);
        app_stage.show();
        function.AddLog(studentController.username, "Logout");
    }

    public void back(ActionEvent event) throws IOException {
        System.out.println("Returning to student view...");
        Parent loader = FXMLLoader.load(getClass().getResource("student.fxml"));
        Scene scene = new Scene(loader);
        Stage app_stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        app_stage.setScene(scene);
        app_stage.show();
    }

    public static class myLessons {
        String Lesson, Teacher, Unit, ClassTime, ExamTime, Score, Status;
        int Row;

        public myLessons(int row, String lesson, String teacher, String unit, String classTime, String examTime, String score, String status) {
            Lesson = lesson;
            Teacher = teacher;
            Unit = unit;
            ClassTime = classTime;
            ExamTime = examTime;
            Score = score;
            Status = status;
            Row = row;
        }

        public int getRow() {
            return Row;
        }

        public String getLesson() {
            return Lesson;
        }

        public String getTeacher() {
            return Teacher;
        }

        public String getUnit() {
            return Unit;
        }

        public String getClassTime() {
            return ClassTime;
        }

        public String getExamTime() {
            return ExamTime;
        }

        public String getScore() {
            return Score;
        }

        public String getStatus() {
            return Status;
        }
    }
}
