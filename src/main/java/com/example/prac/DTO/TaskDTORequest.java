package com.example.prac.DTO;

import com.example.prac.model.TaskStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TaskDTORequest {
    private String title;
    private String description;
    private TaskStatus status;
    private LocalDateTime deadline;
}
