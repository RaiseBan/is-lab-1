package com.example.prac.model.dataEntity;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import jakarta.persistence.*;


@Entity
@Table(name = "album")
@Data
public class Album {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    private long id;

    @NotBlank(message = "Name cannot be null or empty")
    @Column(nullable = false)
    private String name;  // Поле не может быть null, строка не может быть пустой

    @Min(value = 1, message = "Tracks count must be greater than 0")
    @Column(nullable = false)
    private int tracks;  // Значение должно быть больше 0

    @NotNull(message = "Length cannot be null")
    @Min(value = 1, message = "Length must be greater than 0")
    @Column(nullable = false)
    private Integer length;  // Поле не может быть null и значение должно быть больше 0
}
