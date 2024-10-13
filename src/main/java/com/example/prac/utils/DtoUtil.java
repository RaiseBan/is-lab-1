package com.example.prac.utils;

import com.example.prac.DTO.data.*;
import com.example.prac.model.dataEntity.Album;
import com.example.prac.model.dataEntity.Coordinates;
import com.example.prac.model.dataEntity.Label;
import com.example.prac.model.dataEntity.MusicBand;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class DtoUtil {
    public static MusicDTOResponse convertToResponse(MusicBand musicBand) {
        MusicDTOResponse response = new MusicDTOResponse();
        response.setId(musicBand.getId());
        response.setName(musicBand.getName());

        CoordinatesDTOResponse coordinatesResponse = new CoordinatesDTOResponse();
        coordinatesResponse.setX(musicBand.getCoordinates().getX());
        coordinatesResponse.setY(musicBand.getCoordinates().getY());
        response.setCoordinates(coordinatesResponse);

        if (musicBand.getGenre() != null) {
            response.setGenre(musicBand.getGenre().name());
        }else{
            response.setGenre(null);
        }

        response.setNumberOfParticipants(Math.toIntExact(musicBand.getNumberOfParticipants()));
        response.setSinglesCount(Math.toIntExact(musicBand.getSinglesCount()));
        response.setDescription(musicBand.getDescription());

        BestAlbumDTOResponse bestAlbumResponse = new BestAlbumDTOResponse();
        bestAlbumResponse.setName(musicBand.getBestAlbum().getName());
        bestAlbumResponse.setTracks(musicBand.getBestAlbum().getTracks());
        bestAlbumResponse.setLength(musicBand.getBestAlbum().getLength());
        response.setBestAlbum(bestAlbumResponse);

        response.setAlbumsCount(Math.toIntExact(musicBand.getAlbumsCount()));
        response.setEstablishmentDate(musicBand.getEstablishmentDate().toString());
        response.setCreationDate(musicBand.getCreationDate().toString());

        LabelDTOResponse labelResponse = new LabelDTOResponse();
        labelResponse.setName(musicBand.getLabel().getName());
        response.setLabel(labelResponse);

        response.setOwnerUsername(musicBand.getOwner().getUsername());
        return response;
    }

    public static AlbumDTOwId convertToAlbumDTOwIdResponse(Album album) {
        AlbumDTOwId response = new AlbumDTOwId();
        response.setId(album.getId());
        response.setName(album.getName());
        response.setTracks(album.getTracks());
        response.setLength(album.getLength());
        return response;
    }
    public static ZonedDateTime convertToZonedDateTime(String dateString) {
        return ZonedDateTime.of(
                LocalDate.parse(dateString, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                        .atStartOfDay(),
                ZoneOffset.UTC // Явно указываем UTC
        );
    }


    public static LabelDTOwId convertToLabelDTOwIdResponse(Label label) {
        LabelDTOwId response = new LabelDTOwId();
        response.setId(label.getId());
        response.setName(label.getName());
        return response;
    }

    public static CoordinatesDTOwId convertToCoordinatesDTOwIdResponse(Coordinates coordinates) {
        CoordinatesDTOwId response = new CoordinatesDTOwId();
        response.setId(coordinates.getId());
        response.setX(coordinates.getX());
        response.setY(coordinates.getY());
        return response;
    }



}
