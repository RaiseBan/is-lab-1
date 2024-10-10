package com.example.prac.model.dataEntity;
import lombok.Data;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
@Entity
@Table(name = "coordinates")
@Data
public class Coordinates {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private long id;

    @Min(value = -599, message = "X must be greater than -599")
    @Column(name = "x", nullable = false)
    private int x;

    @NotNull(message = "Y cannot be null")
    @Column(name = "y", nullable = false)
    private double y;
}
