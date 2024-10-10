package com.example.prac.utils;

import com.example.prac.DTO.data.BestAlbumDTOResponse;
import com.example.prac.DTO.data.CoordinatesDTOResponse;
import com.example.prac.DTO.data.LabelDTOResponse;
import com.example.prac.DTO.data.MusicDTOResponse;
import com.example.prac.model.dataEntity.MusicBand;
import org.springframework.stereotype.Service;

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
}
