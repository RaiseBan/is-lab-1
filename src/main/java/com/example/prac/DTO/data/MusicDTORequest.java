package com.example.prac.DTO.data;

import com.example.prac.model.dataEntity.Album;
import com.example.prac.model.dataEntity.Coordinates;
import com.example.prac.model.dataEntity.Label;
import lombok.Data;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
public class MusicDTORequest {

    @NotBlank(message = "Name cannot be null or empty")
    private String name;

    @NotNull(message = "Coordinates cannot be null")
    private Coordinates coordinates; // Вложенная сущность Coordinates

    private String genre;

    @Min(value = 1, message = "Number of participants must be greater than 0")
    private long numberOfParticipants;

    @NotNull(message = "Singles count cannot be null")
    @Min(value = 1, message = "Singles count must be greater than 0")
    private Long singlesCount;

    @NotBlank(message = "Description cannot be null or empty")
    private String description;

    @NotNull(message = "Best album cannot be null")
    private Album bestAlbum; // Вложенная сущность Album

    @Min(value = 1, message = "Albums count must be greater than 0")
    private long albumsCount;

    @NotNull(message = "Establishment date cannot be null")
    private String establishmentDate;

    @NotNull(message = "Label cannot be null")
    private Label label; // Вложенная сущность Label
}
