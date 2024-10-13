package com.example.prac.DTO.data.wrappers;


import com.example.prac.model.dataEntity.Album;
import jakarta.validation.Valid;
import lombok.Data;

@Data
public class AlbumWrapper {
    private Long bestAlbumId;
    @Valid
    private Album bestAlbum; 
}
