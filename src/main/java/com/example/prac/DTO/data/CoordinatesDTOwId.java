package com.example.prac.DTO.data;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CoordinatesDTOwId {
    private Long id;
    @Min(value = -599, message = "X must be greater than -599")
    private int x;

    @NotNull(message = "Y cannot be null")
    private double y;
}
