package com.example.demo1;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
        
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime now = LocalDateTime.now();
        
        String[] miladiDate = dtf.format(now).split("-");
        int[] shamsiDate = gregorian_to_jalali(Integer.parseInt(miladiDate[0]), Integer.parseInt(miladiDate[1]), Integer.parseInt(miladiDate[2]));
        
        if(shamsiDate[1]<8)
            entranceDate = shamsiDate[0]+"1";
        else
            entranceDate = shamsiDate[0]+"2";
        
        return entranceDate;
    }
    
    //convert shamsi to miladi date
    public static int[] gregorian_to_jalali(int gy, int gm, int gd) {
        int[] out = {
                (gm > 2) ? (gy + 1) : gy,
                0,
                0
        };
        {
            int[] g_d_m = { 0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334 };
            out[2] = 355666 + (365 * gy) + ((int) ((out[0] + 3) / 4)) - ((int) ((out[0] + 99) / 100)) + ((int) ((out[0] + 399) / 400)) + gd + g_d_m[gm - 1];
        }
        out[0] = -1595 + (33 * ((int) (out[2] / 12053)));
        out[2] %= 12053;
        out[0] += 4 * ((int) (out[2] / 1461));
        out[2] %= 1461;
        if (out[2] > 365) {
            out[0] += (int) ((out[2] - 1) / 365);
            out[2] = (out[2] - 1) % 365;
        }
        if (out[2] < 186) {
            out[1] = 1 + (int)(out[2] / 31);
            out[2] = 1 + (out[2] % 31);
        } else {
            out[1] = 7 + (int)((out[2] - 186) / 30);
            out[2] = 1 + ((out[2] - 186) % 30);
        }
        return out;
    }

    public static boolean isCorrectTime(String entrance) {
        // Parse entrance year and term
        String[] entranceParts = entrance.split("-");
        int entranceYear = Integer.parseInt(entranceParts[0]);
        int entranceTerm = Integer.parseInt(entranceParts[1]); // 1 = Spring, 2 = Fall

        // Define the "correct date" dynamically based on the entrance year and term
        String correctDate;
        if (entranceTerm == 1) {
            correctDate = entranceYear + "-12-01"; // Spring term ends around early December
        } else {
            correctDate = (entranceYear + 1) + "-05-01"; // Fall term ends around early May
        }

        // Get the current date in Jalali format
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime now = LocalDateTime.now();
        String[] miladiDate = dtf.format(now).split("-");
        int[] nowArr = gregorian_to_jalali(
                Integer.parseInt(miladiDate[0]),
                Integer.parseInt(miladiDate[1]),
                Integer.parseInt(miladiDate[2])
        );

        // Format current Jalali date
        String nowDate = String.format("%04d-%02d-%02d", nowArr[0], nowArr[1], nowArr[2]);

        // Allow a range of valid dates if needed
        // Example: Allow access for 7 days before and after the correct date
        int validRangeDays = 7;
        boolean isWithinRange = isDateWithinRange(nowDate, correctDate, validRangeDays);

        return isWithinRange;
    }

    // Helper method to check if a date is within a valid range
    private static boolean isDateWithinRange(String currentDate, String targetDate, int rangeDays) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        try {
            LocalDateTime current = LocalDateTime.parse(currentDate, dtf);
            LocalDateTime target = LocalDateTime.parse(targetDate, dtf);

            return !current.isBefore(target.minusDays(rangeDays)) && !current.isAfter(target.plusDays(rangeDays));
        } catch (Exception e) {
            return false; // Return false in case of parsing errors
        }
    }

}
