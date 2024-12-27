package com.example.demo1;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class adminTeacherController implements Initializable {
    
    @FXML private TableView<Lesson> table;
    @FXML private TableColumn<Lesson, Integer> row;
    @FXML private TableColumn<Lesson, String> teacher, lessons;
    @FXML private TableColumn<Lesson, Button> action;
    
    @FXML private TextField nameField;
    
    
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        table.getItems().clear();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        row.setCellValueFactory(new PropertyValueFactory<>("Row"));
        teacher.setCellValueFactory(new PropertyValueFactory<>("Teacher"));
        lessons.setCellValueFactory(new PropertyValueFactory<>("Lessons"));
        action.setCellValueFactory(new PropertyValueFactory<>("Del"));
        nameField.setText(adminController.username);
        handleData(table);
    }
    // Method to retrieve only teacher usernames from Users.txt
    public  static List<String> retrieveTeacherUsernames() {
        List<String> teacherUsernames = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader("Users.txt"))) {
            String line;

            // Read Users.txt line by line
            while ((line = reader.readLine()) != null) {
                String[] userDetails = line.split(", ");

                // Check if the role is TEACHER and add the username
                if (userDetails.length >= 4 && userDetails[3].equalsIgnoreCase("TEACHER")) {
                    teacherUsernames.add(userDetails[0]); // Add only the username
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading Users.txt: " + e.getMessage());
        }

        return teacherUsernames;
    }

    // Method to write teacher usernames to teachers.txt
    public static void writeTeacherUsernamesToFile() {
        List<String> teacherUsernames = retrieveTeacherUsernames();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("teachers.txt"))) {
            for (String teacher : teacherUsernames) {
                writer.write(teacher + ":"); // Write each teacher username with a colon
                writer.newLine();            // Add a new line after each entry
            }
            System.out.println("Teacher usernames successfully written to teachers.txt");
        } catch (IOException e) {
            System.out.println("Error writing to teachers.txt: " + e.getMessage());
        }
    }

    public void handleData(TableView table) {
        try (BufferedReader br = new BufferedReader(new FileReader("Teachers.txt"))) {
            String line;
            int count = 1;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(":\\s*");
                String itemName = data[0].replace(":", "");
                if (data.length == 2) {
                    table.getItems().add(new Lesson(count++, itemName, data[1]));
                } else {
                    table.getItems().add(new Lesson(count++, itemName, ""));
                }
            }
        } catch (IOException e) {
            function.AddLog(adminController.username, e.getMessage());
        }
    }





    EventHandler<MouseEvent> tableClicked = mouseEvent -> {
        if (mouseEvent.getClickCount() == 1 && !table.getSelectionModel().isEmpty()) {
            Lesson rowData = table.getSelectionModel().getSelectedItem();
            delTeacher(rowData.getTeacher());
            table.getItems().remove(rowData);
            function.AddLog(adminController.username, "Teacher '"+rowData.getTeacher()+"' deleted");
        }
    };

    public static void delTeacher(String name) {
        //remove teacher from 'Teachers.txt'
        ArrayList<String> lessonsToDel = new ArrayList<>();

        // Step 1: Remove teacher and update remaining IDs
        ArrayList<String> updatedTeachers = new ArrayList<>();
        int nextId = 101; // Start of teacher ID range

        try (BufferedReader reader = new BufferedReader(new FileReader("Teachers.txt"));
             BufferedWriter writer = new BufferedWriter(new FileWriter("TempTeachers.txt"))) {

            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.contains(name)) {
                    // Reassign the ID
                    String[] teacherDetails = line.split(": ");
                    String[] idAndName = teacherDetails[0].split(", ");
                    String updatedLine = nextId + ", " + idAndName[1] + ": " + teacherDetails[1];
                    writer.write(updatedLine);
                    writer.newLine();
                    updatedTeachers.add(updatedLine);
                    nextId++; // Increment the ID for the next teacher
                } else {
                    // Extract lessons associated with the teacher to be removed
                    Collections.addAll(lessonsToDel, line.split(": ")[1].split(", "));
                }
            }

        } catch (IOException e) {
            function.AddLog(adminController.username, "Error removing teacher from Teachers.txt: " + e.getMessage());
            return;
        }

        try {
            Files.move(Paths.get("TempTeachers.txt"), Paths.get("Teachers.txt"), REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }



        // Remove lessons from 'Lessons.txt' and reassign IDs
        File lessonFile = new File("LessonsFiles" + function.getTerm() + "Lessons.txt");
        File tmpLessonFile = new File("tempLessons.txt");
        ArrayList<String> updatedLessons = new ArrayList<>();
        int nextIdL = 500; // Start of lesson ID range

        try (BufferedReader reader = new BufferedReader(new FileReader(lessonFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tmpLessonFile))) {

            String line;
            while ((line = reader.readLine()) != null) {
                boolean keep = true;

                for (String lessonToDel : lessonsToDel) {
                    // Check if the line matches the lesson to be deleted
                    String[] details = line.split(", ");
                    if (details[1].equals(lessonToDel) && details[3].equals(name)) { // Match lesson name and professor
                        keep = false;
                        break;
                    }
                }

                if (keep) {
                    // Reassign the lesson ID
                    String[] details = line.split(", ");
                    details[0] = String.valueOf(nextIdL++); // Update the lesson ID
                    String updatedLine = String.join(", ", details);

                    // Write updated line to the temporary file
                    writer.write(updatedLine);
                    writer.newLine();
                }
            }

        } catch (IOException e) {
            function.AddLog(adminController.username, "Error processing Lessons.txt: " + e.getMessage());
        }

        try {
            Files.move(Paths.get("tempLessons.txt"), Paths.get("LessonsFiles" + function.getTerm() + "Lessons.txt"), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            function.AddLog(adminController.username, "Error replacing Lessons.txt: " + e.getMessage());

        }

    }
    
    @FXML void GoAddLesson(ActionEvent event) throws IOException {
        Parent loader = FXMLLoader.load(getClass().getResource("adminAddLesson.fxml"));
        Scene scene = new Scene(loader);
        Stage app_stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        app_stage.setScene(scene);
        app_stage.show();
    }
    
    @FXML void GoProfList(ActionEvent event) throws IOException {
        Parent loader = FXMLLoader.load(getClass().getResource("adminTeacher.fxml"));
        Scene scene = new Scene(loader);
        Stage app_stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        app_stage.setScene(scene);
        app_stage.show();
    }
    
    @FXML void exit(ActionEvent event) throws IOException {
        Parent loader = FXMLLoader.load(getClass().getResource("login.fxml"));
        Scene scene = new Scene(loader);
        Stage app_stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        app_stage.setScene(scene);
        app_stage.show();
        function.AddLog(adminController.username, "Logout");
    }
    
    @FXML void goHome(ActionEvent event) throws IOException {
        Parent loader = FXMLLoader.load(getClass().getResource("admin.fxml"));
        Scene scene = new Scene(loader);
        Stage app_stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        app_stage.setScene(scene);
        app_stage.show();
    }
    
    @FXML void goLessonList(ActionEvent event) throws IOException {
        Parent loader = FXMLLoader.load(getClass().getResource( "adminLesson.fxml"));
        Scene scene = new Scene(loader);
        Stage app_stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        app_stage.setScene(scene);
        app_stage.show();
    }

    
    public class Lesson{
        String Teacher, Lessons;
        int Row;
        Button Del;
        
        public Lesson(int row, String teacher, String lessons) {
            Teacher = teacher;
            Lessons = lessons;
            Row = row;
            
            Del= new Button("Delete");
            Del.setOnMouseClicked(tableClicked);
            Del.setPrefWidth(50);
            Del.setPrefHeight(20);
            Del.setStyle("-fx-background-color: red ; -fx-cursor: hand;" +
                    " -fx-font-size: 10px; -fx-font-weight: bold;" +
                    "-fx-text-fill: white; -fx-padding-left: 5;");
        }
        
        public String getTeacher() {
            return Teacher;
        }
        public String getLessons() {
            return Lessons;
        }
        public int getRow() {
            return Row;
        }
        public Button getDel() {
            return Del;
        }
        
    }
}