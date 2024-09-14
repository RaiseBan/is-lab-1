package com.example.prac.controllers;


import com.example.prac.DTO.TaskDTORequest;
import com.example.prac.DTO.TaskDTOResponse;
import com.example.prac.model.Task;
import com.example.prac.model.User;
import com.example.prac.service.TaskService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/tasks")
public class TaskController {
    private final TaskService taskService;

    @GetMapping("{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
        Task task = this.taskService.findTaskById(id);
        return ResponseEntity.ok().body(task);
    }
    @PostMapping
    public ResponseEntity<TaskDTOResponse> createTask(@RequestBody TaskDTORequest taskDTORequest){
        System.out.println(taskDTORequest);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        Task task = new Task();
        task.setTitle(taskDTORequest.getTitle());
        task.setStartTime(taskDTORequest.getStartTime());
        task.setEndTime(taskDTORequest.getEndTime());
        task.setColor(taskDTORequest.getColor());
        task.setUser(user);

        Task createdTask = taskService.save(task);

        TaskDTOResponse responseDTO = new TaskDTOResponse();
        responseDTO.setId(createdTask.getId());
        responseDTO.setTitle(createdTask.getTitle());
        responseDTO.setStartTime(createdTask.getStartTime());
        responseDTO.setEndTime(createdTask.getEndTime());
        responseDTO.setColor(createdTask.getColor());

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdTask.getId())
                .toUri();
        return ResponseEntity.created(location).body(responseDTO);
    }
    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteTaskById(@PathVariable Long id){
        taskService.deleteTaskById(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("{id}")
    public ResponseEntity<TaskDTOResponse> updateTask(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        Task updatedTask = taskService.updateTask(id, updates);
        TaskDTOResponse responseDTO = new TaskDTOResponse();
        responseDTO.setId(updatedTask.getId());
        responseDTO.setTitle(updatedTask.getTitle());
        responseDTO.setStartTime(updatedTask.getStartTime());
        responseDTO.setEndTime(updatedTask.getEndTime());
        responseDTO.setColor(updatedTask.getColor());

        return ResponseEntity.ok().body(responseDTO);
    }
    @GetMapping
    public ResponseEntity<List<TaskDTOResponse>> getAllTasks(@RequestHeader("Authorization") String token) {
        List<Task> tasks = taskService.getAllTasks(token.substring(7));

        // Преобразование списка задач в список DTO
        List<TaskDTOResponse> taskDTOs = tasks.stream().map(task -> {
            TaskDTOResponse dto = new TaskDTOResponse();
            dto.setId(task.getId());
            dto.setTitle(task.getTitle());
            dto.setStartTime(task.getStartTime());
            dto.setEndTime(task.getEndTime());
            dto.setColor(task.getColor());
            return dto;
        }).collect(Collectors.toList());

        return ResponseEntity.ok().body(taskDTOs);
    }


}
