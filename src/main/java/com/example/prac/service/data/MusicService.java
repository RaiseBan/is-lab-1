package com.example.prac.service.data;

import com.example.prac.DTO.data.MusicDTORequest;
import com.example.prac.DTO.data.MusicDTOResponse;
import com.example.prac.errorHandler.ResourceNotFoundException;
import com.example.prac.model.authEntity.User;
import com.example.prac.model.dataEntity.*;
import com.example.prac.repository.data.AlbumRepository;
import com.example.prac.repository.data.CoordinatesRepository;
import com.example.prac.repository.data.LabelRepository;
import com.example.prac.repository.data.MusicBandRepository;
import com.example.prac.utils.DateFormatConverter;
import com.example.prac.utils.DtoUtil;
import com.example.prac.webSocket.MusicWebSocketHandler;
import jakarta.persistence.OptimisticLockException;
import jakarta.validation.ConstraintViolationException;
import lombok.AllArgsConstructor;
import org.hibernate.HibernateException;
import org.hibernate.event.spi.SaveOrUpdateEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class MusicService {

    private final MusicBandRepository musicBandRepository;
    private final AlbumRepository albumRepository;
    private final LabelRepository labelRepository;
    private final CoordinatesRepository coordinatesRepository;
    private final MusicWebSocketHandler musicWebSocketHandler;


    public MusicBand saveMusicBand(MusicDTORequest musicDTORequest, User user)  {

        try {
            MusicBand musicBand = new MusicBand();
            musicBand.setName(musicDTORequest.getName());


            if (musicDTORequest.getCoordinatesWrapper().getCoordinatesId() != null) {

                Coordinates existingCoordinates = coordinatesRepository.findById(musicDTORequest.getCoordinatesWrapper().getCoordinatesId());
                musicBand.setCoordinates(existingCoordinates);
            } else if (musicDTORequest.getCoordinatesWrapper().getCoordinates() != null) {

                Coordinates newCoordinates = musicDTORequest.getCoordinatesWrapper().getCoordinates();
                newCoordinates.setOwner(user);
                coordinatesRepository.save(newCoordinates);
                musicBand.setCoordinates(newCoordinates);
            }


            if (musicDTORequest.getBestAlbumWrapper() != null && musicDTORequest.getBestAlbumWrapper().getBestAlbumId() != null) {
                Album existingAlbum = albumRepository.findById(musicDTORequest.getBestAlbumWrapper().getBestAlbumId());
                musicBand.setBestAlbum(existingAlbum);
            } else if (musicDTORequest.getBestAlbumWrapper() != null && musicDTORequest.getBestAlbumWrapper().getBestAlbum() != null) {
                Album newAlbum = musicDTORequest.getBestAlbumWrapper().getBestAlbum();
                newAlbum.setOwner(user);
                albumRepository.save(newAlbum);
                musicBand.setBestAlbum(newAlbum);
            }


            if (musicDTORequest.getLabelWrapper() != null && musicDTORequest.getLabelWrapper().getLabelId() != null) {
                Label existingLabel = labelRepository.findById(musicDTORequest.getLabelWrapper().getLabelId());
                musicBand.setLabel(existingLabel);
            } else if (musicDTORequest.getLabelWrapper() != null && musicDTORequest.getLabelWrapper().getLabel() != null) {
                Label newLabel = musicDTORequest.getLabelWrapper().getLabel();
                newLabel.setOwner(user);
                labelRepository.save(newLabel);
                musicBand.setLabel(newLabel);
            }


            try {
                musicBand.setGenre(MusicGenre.valueOf(musicDTORequest.getGenre()));
            } catch (Exception e) {
                musicBand.setGenre(null);
            }


            musicBand.setNumberOfParticipants(musicDTORequest.getNumberOfParticipants());
            musicBand.setSinglesCount(musicDTORequest.getSinglesCount());
            musicBand.setDescription(musicDTORequest.getDescription());
            musicBand.setAlbumsCount(musicDTORequest.getAlbumsCount());
            musicBand.setEstablishmentDate(ZonedDateTime.parse(musicDTORequest.getEstablishmentDate()));
            musicBand.setOwner(user);
            musicBand.setCreatedBy(user);


            musicBandRepository.save(musicBand);

            musicWebSocketHandler.sendUpdate("create", DtoUtil.convertToResponse(musicBand));

            return musicBand;
        }catch (ConstraintViolationException e){
            throw new ConstraintViolationException("fail", e.getConstraintViolations());
        }
        catch (HibernateException e) {
            throw new RuntimeException("Failed to save music band", e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public List<MusicBand> getAllMusicBands() {
        try {
            return musicBandRepository.findAll();
        } catch (HibernateException e) {
            throw new RuntimeException("Failed to fetch all music bands", e);
        }
    }

    public MusicBand getMusicBandById(long id) {
        try {
            MusicBand musicBand = musicBandRepository.findById(id);

            if (musicBand == null) {
                throw new IllegalArgumentException("MusicBand with id " + id + " not found");
            }
            return musicBand;
        } catch (Exception e) {
            throw new ResourceNotFoundException("no such MusicBand");
        }
    }

    public MusicBand updateMusicBand(MusicBand musicBand, MusicDTORequest musicDTORequest, User user) {
        try {
            if (musicBand == null) {
                throw new IllegalArgumentException("MusicBand with id not found");
            }


            musicBand.setName(musicDTORequest.getName());
            musicBand.setNumberOfParticipants(musicDTORequest.getNumberOfParticipants());
            musicBand.setSinglesCount(musicDTORequest.getSinglesCount());
            musicBand.setDescription(musicDTORequest.getDescription());
            musicBand.setAlbumsCount(musicDTORequest.getAlbumsCount());


            if (musicDTORequest.getGenre() == null || musicDTORequest.getGenre().equals("")) {
                musicBand.setGenre(null);
            } else {
                musicBand.setGenre(MusicGenre.valueOf(musicDTORequest.getGenre()));
            }


            Coordinates coordinates = musicDTORequest.getCoordinatesWrapper().getCoordinates();
            if (coordinates != null) {
                coordinatesRepository.update(coordinates);
                musicBand.setCoordinates(coordinates);
            }


            Album bestAlbum = musicDTORequest.getBestAlbumWrapper().getBestAlbum();
            if (bestAlbum != null) {

                albumRepository.update(bestAlbum);

                musicBand.setBestAlbum(bestAlbum);
            }


            Label label = musicDTORequest.getLabelWrapper().getLabel();
            if (label != null) {


                labelRepository.update(label);

                musicBand.setLabel(label);
            }


            try {
                String formattedDate = DateFormatConverter.convertDate(musicDTORequest.getEstablishmentDate());


                musicBand.setEstablishmentDate(DtoUtil.convertToZonedDateTime(formattedDate));
            } catch (Exception e) {
            }


            musicBand.setUpdatedBy(user);

            System.out.println(1);
            musicBandRepository.update(musicBand);
            System.out.println("2");
            musicWebSocketHandler.sendUpdate("update", DtoUtil.convertToResponse(musicBand));

            return musicBand;

        }catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }



    public void deleteMusicBandById(Long id) {
        MusicBand musicBand = musicBandRepository.findById(id);

        // Отвязываем зависимые объекты
        if (musicBand.getCoordinates() != null) {
            Coordinates coordinates = musicBand.getCoordinates();
            musicBand.setCoordinates(null); // Отвязываем
            coordinatesRepository.delete(coordinates); // Удаляем
        }

        if (musicBand.getBestAlbum() != null) {
            Album album = musicBand.getBestAlbum();
            musicBand.setBestAlbum(null); // Отвязываем
            albumRepository.delete(album); // Удаляем
        }

        if (musicBand.getLabel() != null) {
            Label label = musicBand.getLabel();
            musicBand.setLabel(null); // Отвязываем
            labelRepository.delete(label); // Удаляем
        }

        // Удаляем саму группу
        musicBandRepository.delete(musicBand);

        // Отправляем обновление по WebSocket
        try {
            musicWebSocketHandler.sendUpdate("delete", id);
        } catch (IOException e) {
//            e.printStackTrace();
            System.out.println("SDSD");
            throw new RuntimeException(e);
        }
    }


    public List<Object[]> getMusicBandCountByCreationDate() {
        return musicBandRepository.getMusicBandCountByCreationDate();
    }

    public Long countMusicBandsWithLabelMoreThan(String labelThreshold) {
        return musicBandRepository.countMusicBandsWithLabelMoreThan(labelThreshold);
    }

    public List<MusicDTOResponse> findMusicBandsByDescription(String substring) {
        List<MusicBand> musicBands = musicBandRepository.findMusicBandsByDescriptionSubstring(substring);
        return musicBands.stream().map(DtoUtil::convertToResponse).collect(Collectors.toList());
    }

    public void addSingleToMusicBand(Long bandId, int singlesCount) throws IOException {
        musicBandRepository.addSingleToMusicBand(bandId, singlesCount);

        MusicBand musicBand = this.getMusicBandById(bandId);
        musicWebSocketHandler.sendUpdate("update", DtoUtil.convertToResponse(musicBand));
    }

    public void removeParticipantFromMusicBand(long bandId) throws IOException {
        musicBandRepository.removeParticipantFromMusicBand(bandId);
        MusicBand musicBand = this.getMusicBandById(bandId);
        musicWebSocketHandler.sendUpdate("update", DtoUtil.convertToResponse(musicBand));
    }


}
