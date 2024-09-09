package com.example.prac.controllers;


import com.example.prac.model.Task;
import com.example.prac.service.TaskService;
import jakarta.servlet.Servlet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/tasks")
public class TaskController {
    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;

    }

    @GetMapping("{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
        Task task = this.taskService.findTaskById(id);
        return ResponseEntity.ok().body(task);
    }
    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody Task task){
        Task createdTask = taskService.save(task);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdTask.getId())
                .toUri();
        return ResponseEntity.created(location).body(createdTask);
    }
    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteTaskById(@PathVariable Long id){
        taskService.deleteTaskById(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        Task updatedTask = taskService.updateTask(id, updates);
        return ResponseEntity.ok().body(updatedTask);
    }
    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks(@RequestHeader("Authorization") String token) {
        List<Task> tasks = taskService.getAllTasks(token.substring(7));
        return ResponseEntity.ok().body(tasks);
    }

}
