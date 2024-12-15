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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ResourceBundle;

public class passwordRecoveryController implements Initializable {

    @FXML private TextField IdNumField, usernameField;
    @FXML private PasswordField newPassField, confirm;
    @FXML private Label error;
    @FXML private ChoiceBox<String> choiceBox;
    private String[] capacity = {"STUDENT","TEACHER"};
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        choiceBox.getItems().addAll(capacity);
        choiceBox.setOnAction(this::choiceBox);
    }
    
    public void choiceBox(ActionEvent event) {
        String capacity =(String) choiceBox.getValue();
    }
    
    @FXML void button(ActionEvent event) throws IOException {
        String username = usernameField.getText();
        String IdNum = IdNumField.getText();
        String confirmPass = confirm.getText();
        String newPass = newPassField.getText();
        String rol = choiceBox.getValue();
        
        if(!(username.isEmpty() || IdNum.isEmpty() || confirmPass.isEmpty() || newPass.isEmpty() || rol.isEmpty())) {
            if (newPass.length() >= 6) {
                if(confirmPass.equals(newPass)){
                    if (ChangePass(username, IdNum, newPass, rol)) {
                        
                        Parent loader = FXMLLoader.load(getClass().getResource("login.fxml"));
                        Scene scene = new Scene(loader);
                        Stage app_stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                        app_stage.setScene(scene);
                        app_stage.show();
                        
                    } else
                        error.setText("Something went wrong !");
                } else
                    error.setText("Password and confirm does not match !");
            } else
                error.setText("Password must have at least 6 character");
        }else
            error.setText("Fill the form");
    }

    public boolean ChangePass(String username, String IdNum, String newPass, String rol) {
        String hashedNewPass = function.hashString(newPass + LoginController.passSalt);
        boolean flag = true;

        try (BufferedReader reader = new BufferedReader(new FileReader("Users.txt"));
             BufferedWriter writer = new BufferedWriter(new FileWriter("Users_temp.txt"))) {

            String currentLine, newLine;

            // Iterate through each line in Users.txt
            while ((currentLine = reader.readLine()) != null) {
                // Split the line into parts (username, ID, password, role, entranceDate)
                String[] parts = currentLine.split(", ");
                if (parts.length == 5 && parts[0].equals(username) && parts[1].equals(IdNum)) {
                    // Extract entranceDate from the original line
                    String entranceDate = parts[4];

                    // Create the updated line with the new password
                    newLine = username + ", " + IdNum + ", " + hashedNewPass + ", " + rol + ", " + entranceDate;
                    writer.write(newLine);
                    writer.newLine();
                } else {
                    // Write unchanged lines to the temporary file
                    writer.write(currentLine);
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            function.AddLog(username, "Error manipulating the 'Users.txt' while password recovery: " + e.getMessage());
            flag = false;
        }

        // Replace the original file with the updated file
        try {
            Files.move(Paths.get("Users_temp.txt"), Paths.get("Users.txt"), StandardCopyOption.REPLACE_EXISTING);
            function.AddLog(username, "'Users.txt' Updated - PassRecovery");
        } catch (IOException e) {
            e.printStackTrace();
            flag = false;
        }

        return flag;
    }


    @FXML private void goLogin(ActionEvent event) throws IOException {
        Parent loader = FXMLLoader.load(getClass().getResource("login.fxml"));
        Scene scene = new Scene(loader);
        Stage app_stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        app_stage.setScene(scene);
        app_stage.show();
    }
    
}
