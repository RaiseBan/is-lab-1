package com.example.prac.DTO.data;

import com.example.prac.DTO.data.wrappers.AlbumWrapper;
import com.example.prac.DTO.data.wrappers.CoordinatesWrapper;
import com.example.prac.DTO.data.wrappers.LabelWrapper;
import com.example.prac.validators.ValidObject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MusicDTORequest {

    @NotBlank(message = "Name cannot be null or empty")
    @NotNull(message = "Name cannot be null")
    private String name;

    @ValidObject
    @Valid
    private CoordinatesWrapper coordinatesWrapper; 

    private String genre;

    @Min(value = 1, message = "Number of participants must be greater than 0")
    private long numberOfParticipants;

    @NotNull(message = "Singles count cannot be null")
    @Min(value = 1, message = "Singles count must be greater than 0")
    private Long singlesCount;

    @NotBlank(message = "Description cannot be null or empty")
    private String description;

    @ValidObject
    @Valid
    private AlbumWrapper bestAlbumWrapper; 

    @Min(value = 1, message = "Albums count must be greater than 0")
    private long albumsCount;

    @NotNull(message = "Establishment date cannot be null")
    private String establishmentDate;

    @ValidObject
    @Valid
    private LabelWrapper labelWrapper; 
}
