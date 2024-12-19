package com.example.prac.model.info;

import com.example.prac.model.authEntity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class ImportHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String status;
    private Integer totalObjectsCount;
    private Integer addedObjectsCount;
    private String fileName; // Уникальное имя файла
    private String originalFileName; // Оригинальное имя файла

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}

