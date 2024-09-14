package com.example.prac.DTO;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TaskDTORequest {
    private String title;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String color;

}
