package com.example.prac.DTO.data;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CoordinatesDTOResponse {


    @Min(value = -599, message = "X must be greater than -599")
    private int x;

    @NotNull(message = "Y cannot be null")
    private double y;
}
