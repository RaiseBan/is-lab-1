package com.example.prac.DTO.data.wrappers;


import com.example.prac.model.dataEntity.Coordinates;
import jakarta.validation.Valid;
import lombok.Data;

@Data
public class CoordinatesWrapper {
    private Long coordinatesId;
    @Valid
    private Coordinates coordinates; 
}
