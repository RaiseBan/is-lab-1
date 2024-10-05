package com.example.prac.DTO.data;


import lombok.Data;

@Data
public class MusicDTOResponse {
    private Long id;
    private String name;
    private CoordinatesDTOResponse coordinates;
    private String genre;
    private int numberOfParticipants;
    private int singlesCount;
    private String description;
    private BestAlbumDTOResponse bestAlbum;
    private int albumsCount;
    private String establishmentDate;
    private String creationDate;
    private LabelDTOResponse label;
    private String ownerUsername;
}
