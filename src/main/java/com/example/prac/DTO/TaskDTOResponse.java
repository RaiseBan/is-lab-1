package com.example.prac.DTO;

import com.example.prac.model.TaskStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TaskDTOResponse {
    private Long id;
    private String title;
    private String description;
    private TaskStatus status;
    private LocalDateTime deadline;

}