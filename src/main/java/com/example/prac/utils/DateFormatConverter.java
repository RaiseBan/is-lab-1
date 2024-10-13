package com.example.prac.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

public class DateFormatConverter {

    
    private static final Pattern DATE_PATTERN = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");

    public static String convertDate(String inputDate) {
        
        if (DATE_PATTERN.matcher(inputDate).matches()) {
            return inputDate; 
        }
        
        LocalDate date = LocalDate.parse(inputDate, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        return date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

}
