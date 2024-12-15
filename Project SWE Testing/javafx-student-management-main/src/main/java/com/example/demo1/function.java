package com.example.demo1;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class function {
    public static void AddLog(String username, String message){
        try(BufferedWriter bw = new BufferedWriter(new FileWriter("record.log", true))){
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            String time = dtf.format(now);
            String msg="["+time+"]  user: "+username+" -> "+message;
            bw.write(msg);
            bw.newLine();
            
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
        
    }
    
    public static String hashString(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(input.getBytes());
            StringBuilder hexString = new StringBuilder(2 * hashedBytes.length);
            
            for (byte b : hashedBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing the string", e);
        }
    }
    
    public static String getTerm(){
        String entranceDate;

        // Get the current date and time
        LocalDateTime now = LocalDateTime.now();
        int year = now.getYear();

        // Determine if the current date is in the fall or spring semester
        int month = now.getMonthValue();
        if (month >= 1 && month <= 6) { // Spring semester (Jan to Jun)
            entranceDate = "1" + year + "1";
        } else { // Fall semester (Jul to Dec)
            entranceDate = "1" + year + "2";
        }

        return entranceDate;
    }



    public static boolean isCorrectTime(String entrance) {
        // Parse the entrance string to extract the year and term
        int entranceYear = Integer.parseInt(entrance.substring(1, 5)); // Extract year (e.g., 2024)
        int entranceTerm = Integer.parseInt(entrance.substring(5, 6)); // Extract term (1 = Spring, 2 = Fall)

        // Define the "correct date" dynamically based on the entrance year and term
        LocalDate correctDate;
        if (entranceTerm == 1) {
            // Spring term
            correctDate = LocalDate.of(entranceYear, 06, 30);
        } else {
            // Fall term
            correctDate = LocalDate.of(entranceYear + 1, 03, 1);
        }

        // Get the current date
        LocalDate now = LocalDate.now();

        // Check if the current date is within the range
        return !now.isAfter(correctDate);
    }

}
