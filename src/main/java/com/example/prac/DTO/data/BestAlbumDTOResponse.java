package com.example.prac.DTO.data;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BestAlbumDTOResponse {
    @NotBlank(message = "Name cannot be null or empty")
    private String name;
    @Min(value = 1, message = "Tracks count must be greater than 0")
    private int tracks;
    @NotNull(message = "Length cannot be null")
    @Min(value = 1, message = "Length must be greater than 0")
    private Integer length;}

