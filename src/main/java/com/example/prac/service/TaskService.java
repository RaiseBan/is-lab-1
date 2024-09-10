package com.example.prac.service;

import com.example.prac.errorHandler.ResourceNotFoundException;
import com.example.prac.model.Task;
import com.example.prac.model.TaskStatus;
import com.example.prac.model.User;
import com.example.prac.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;

    private final JwtService jwtService;
    public Task save(Task task){
        return taskRepository.save(task);
    }
    public Task findTaskById(Long id){
        return taskRepository.findById(id).orElseThrow( () -> new ResourceNotFoundException("Task not found with id: " + id));
    }
    public void deleteTaskById(Long id){
        taskRepository.deleteById(id);
    }

    public Task updateTask(Long id, Map<String, Object> updates) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));

        updates.forEach((key, value) -> {
            switch (key) {
                case "title":
                    task.setTitle((String) value);
                    break;
                case "description":
                    task.setDescription((String) value);
                    break;
                case "status":
                    task.setStatus(TaskStatus.valueOf((String) value));
                    break;
                case "deadline":
                    task.setDeadline(LocalDateTime.parse((String) value));
                    break;
            }
        });

        return taskRepository.save(task);
    }
    public List<Task> getAllTasks(String token){
        jwtService.extractUsername(token);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        Long userId = user.getId();
        return taskRepository.findByUserId(userId);
    }

}
