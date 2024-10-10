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
    @Column(name = "id", nullable = false, unique = true)
    private long id;

    @NotBlank(message = "Name cannot be null or empty")
    @Column(name = "name", nullable = false)
    private String name;

    @Min(value = 1, message = "Tracks count must be greater than 0")
    @Column(name = "tracks", nullable = false)
    private int tracks;

    @NotNull(message = "Length cannot be null")
    @Min(value = 1, message = "Length must be greater than 0")
    @Column(name = "length", nullable = false)
    private Integer length;
}
