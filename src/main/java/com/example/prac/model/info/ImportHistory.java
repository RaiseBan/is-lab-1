package com.example.prac.model.info;

import com.example.prac.model.authEntity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class ImportHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String status; // SUCCESS или FAILED

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private Integer totalObjectsCount; // Общее количество объектов

    private Integer addedObjectsCount; // Успешно добавленные объекты

    // Геттеры и сеттеры
}
