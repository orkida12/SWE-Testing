package com.example.demo1;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

import static java.lang.Integer.parseInt;

public class signupController implements Initializable {
    public static final int adminID=1;

    private static final int ADMIN_ID_END = 9;

    private static final int studentID = 10;
    private static final int STUDENT_ID_END = 100;
    private static final int teacherID = 101;
    private static final int TEACHER_ID_END = 200;

    // Method to get the next ID for a specific role
    public static int getNextId(String role) {
        int start, end=0;

        // Determine the range based on the role
        switch (role.toUpperCase()) {
            case "STUDENT":
                start = studentID;
                end = STUDENT_ID_END;
                break;
            case "TEACHER":
                start = teacherID;
                end = TEACHER_ID_END;
                break;

            case "ADMIN":
                start = adminID;
                end = ADMIN_ID_END;
                break;
            default:
                throw new IllegalArgumentException("Invalid role: " + role);
        }
        boolean[] idUsed = new boolean[end - start + 1];

        try (BufferedReader reader = new BufferedReader(new FileReader("Users.txt"))) {
            String line;

            while ((line = reader.readLine()) != null) {
                String[] userDetails = line.split(", ");
                int id = Integer.parseInt(userDetails[1]);

                // Mark IDs that are currently in use
                if (id >= start && id <= end) {
                    idUsed[id - start] = true;
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading Users.txt: " + e.getMessage());
        }

        // Find the lowest unused ID within the range
        for (int i = 0; i < idUsed.length; i++) {
            if (!idUsed[i]) {
                return start + i;
            }
        }

        // If no IDs are available, the range is exhausted
        throw new IllegalStateException("ID range for " + role + " is exhausted!");
    }


    private static final String passSalt = "9aR#5@jE!bFz^0p*2LcW8";
    public static String name;
    public static String entranceDate;
    public Button btn_signup;
    public Hyperlink link_login;

    @FXML private Label error;
    @FXML private TextField usernameField, IDNum ;
    @FXML private PasswordField password, passwordConfirm;
    @FXML private ChoiceBox<String> choiceBox;
    private final String[] capacity= {"STUDENT","TEACHER"};
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        choiceBox.getItems().addAll(capacity);
        //handling entrance date
        entranceDate = function.getTerm();
        
    }
  public void choiceBox(ActionEvent event) {
       String capacity = choiceBox.getValue();
   }
    @FXML
    public void goLogin(ActionEvent event) throws IOException {
        Parent loader = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("login.fxml")));
        Scene scene = new Scene(loader);
        Stage app_stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        app_stage.setScene(scene);
        app_stage.show();
    }
    

    public void button(ActionEvent event) throws IOException {
        name = usernameField.getText();
        String IdNumber = IDNum.getText();
        String pass = password.getText();
        String confirm = passwordConfirm.getText();
        String rol = choiceBox.getValue();

        if(!(name.isEmpty() || IdNumber.isEmpty() || pass.isEmpty() || confirm.isEmpty() || rol.isEmpty())) {
            if (checkExistId(parseInt(IdNumber))) {
                if(checkExist(name)){
                    if (pass.length() >= 6) {
                        if (pass.equals(confirm)) {

                            signUp(name, IdNumber, pass, rol);
                            function.AddLog(name, "success signup");
                            if(rol.equals("TEACHER"))
                            {
                                adminTeacherController.writeTeacherUsernamesToFile();
                            }

                            Parent loader = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(rol.toLowerCase() + ".fxml")));
                            Scene scene = new Scene(loader);
                            Stage app_stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                            app_stage.setScene(scene);
                            app_stage.show();

                        } else {
                            error.setText("password and confirm does not match");
                        }
                    } else {
                        error.setText("Password must have at least 6 character");
                    }
                } else
                    error.setText("Username already used");
            } else
                error.setText("This id number already exist");
        }else
            error.setText("Fill the form");

    }

//    <------------------------------------ Methods ----------------------------------------->

    //TODO id nr ta vendos ne read mode ne momentin qe krijojme account,
    //mundesisht nuk duhet fare ketu po sa per reference

public void signUp(String user, String id_number, String pass, String accessibility) {
    // Add salt to the password
    pass += passSalt;
    String hashedPass = function.hashString(pass);

    // File name for the main user file
    String mainFile = "Users.txt";

    // Determine the specific file for the role
    String roleFile = accessibility.equalsIgnoreCase("STUDENT") ? "Student.txt"
            : accessibility.equalsIgnoreCase("TEACHER") ? "Teachers.txt"
            : null;

    try (BufferedWriter writer = new BufferedWriter(new FileWriter(mainFile, true))) {
        int newId = getNextId(roleFile);


        // Write to the main user file
        writer.write(user + ", ");
        writer.write(newId + ", ");

        writer.write(hashedPass + ", ");
        writer.write(accessibility + ", ");
        if (accessibility.equalsIgnoreCase("STUDENT")) {
            writer.write(entranceDate);
        }
        writer.newLine();

        // Write to the role-specific file if applicable
        if (roleFile != null) {
            try (BufferedWriter roleWriter = new BufferedWriter(new FileWriter(roleFile, true))) {
                if(accessibility.equalsIgnoreCase("STUDENT")){
                    roleWriter.write(studentID + ", ");
                }else if (accessibility.equalsIgnoreCase("TEACHER")){
                    roleWriter.write(teacherID + ", ");
                }else {
                    roleWriter.write(adminID + ", ");
                }
                roleWriter.write(user + ": ");
                if (accessibility.equalsIgnoreCase("STUDENT")) {
                    roleWriter.write(entranceDate);
                }
                roleWriter.newLine();
            }
        }

        function.AddLog(name, "Data appended to the file successfully - signup");
    } catch (IOException error) {
        function.AddLog(name, "Error writing to the file - signup: " + error.getMessage());
    }
}
    
    private static boolean checkExist(String user){
        String fileName = "Users.txt";
        File users = new File(fileName);
        String line;
        boolean flag = true;
        boolean result = false;
        try (BufferedReader reader = new BufferedReader(new FileReader(users))){
            while (((line = reader.readLine()) != null) && flag){
                String[] data = line.split(", ");
                if (data[0].equals(user))
                    flag=false;
            }
            if (!flag){
                result = true;
            }
            
        } catch (IOException e) {
            function.AddLog(name,"Error reading file-signup: " + e.getMessage());
        }
        return !result;
    }
    
    private static boolean checkExistId(int id) {
        String fileName = "Users.txt";
        File users = new File(fileName);
        String line;
        boolean flag = true;
        boolean result = false;
        try (BufferedReader reader = new BufferedReader(new FileReader(users))){
            while (((line = reader.readLine()) != null) && flag){
                String[] data = line.split(", ");
                System.out.println(parseInt(data[1]));
                if (parseInt(data[1])== id)
                    flag=false;
            }
            if (!flag){
                result = true;
            }

        } catch (IOException e) {
            function.AddLog(name,"Error reading file-signup: " + e.getMessage());
        }
        return !result;
    }
}

