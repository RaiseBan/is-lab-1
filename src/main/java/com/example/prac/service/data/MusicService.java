package com.example.prac.service.data;

import com.example.prac.DTO.data.MusicDTORequest;
import com.example.prac.model.authEntity.User;
import com.example.prac.model.dataEntity.MusicBand;
import com.example.prac.model.dataEntity.MusicGenre;
import com.example.prac.repository.data.MusicBandRepository;
import com.example.prac.utils.DtoUtil;
import com.example.prac.webSocket.MusicWebSocketHandler;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.hibernate.HibernateException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class MusicService {

    private final MusicBandRepository musicBandRepository;
    private final MusicWebSocketHandler musicWebSocketHandler;

    // Сохранение объекта MusicBand
    public MusicBand saveMusicBand(MusicDTORequest musicDTORequest, User user) {
        try {
            System.out.println("1g");
            MusicBand musicBand = new MusicBand();
            musicBand.setName(musicDTORequest.getName());
            musicBand.setCoordinates(musicDTORequest.getCoordinates());
            musicBand.setGenre(musicDTORequest.getGenre() != null ? MusicGenre.valueOf(musicDTORequest.getGenre()) : null);
            musicBand.setNumberOfParticipants(musicDTORequest.getNumberOfParticipants());
            musicBand.setSinglesCount(musicDTORequest.getSinglesCount());
            musicBand.setDescription(musicDTORequest.getDescription());
            musicBand.setBestAlbum(musicDTORequest.getBestAlbum());
            musicBand.setAlbumsCount(musicDTORequest.getAlbumsCount());
            musicBand.setEstablishmentDate(ZonedDateTime.parse(musicDTORequest.getEstablishmentDate()));
            musicBand.setLabel(musicDTORequest.getLabel());
            musicBand.setOwner(user);
            musicBand.setCreatedBy(user);


            musicBandRepository.save(musicBand);
            musicWebSocketHandler.sendUpdate("create", DtoUtil.convertToResponse(musicBand));
            return musicBand;
        } catch (HibernateException e) {
            throw new RuntimeException("Failed to save music band", e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Получение списка всех MusicBand
    public List<MusicBand> getAllMusicBands() {
        try {
            return musicBandRepository.findAll();
        } catch (HibernateException e) {
            throw new RuntimeException("Failed to fetch all music bands", e);
        }
    }

    // Получение MusicBand по ID
    public MusicBand getMusicBandById(long id) {
        try {
            MusicBand musicBand = musicBandRepository.findById(id);
            if (musicBand == null) {
                throw new IllegalArgumentException("MusicBand with id " + id + " not found");
            }
            return musicBand;
        } catch (HibernateException e) {
            throw new RuntimeException("Failed to fetch music band by id", e);
        }
    }

    // Обновление MusicBand
    @Transactional
    public MusicBand updateMusicBand(MusicBand musicBand, MusicDTORequest musicDTORequest, User user) {
        try {
            if (musicBand == null) {
                throw new IllegalArgumentException("MusicBand with id not found");
            }

            // Обновляем только допустимые поля
            musicBand.setName(musicDTORequest.getName());
            musicBand.setCoordinates(musicDTORequest.getCoordinates());
            musicBand.setGenre(musicDTORequest.getGenre() != null ? MusicGenre.valueOf(musicDTORequest.getGenre()) : null);
            musicBand.setNumberOfParticipants(musicDTORequest.getNumberOfParticipants());
            musicBand.setSinglesCount(musicDTORequest.getSinglesCount());
            musicBand.setDescription(musicDTORequest.getDescription());
            musicBand.setBestAlbum(musicDTORequest.getBestAlbum());
            musicBand.setAlbumsCount(musicDTORequest.getAlbumsCount());
            musicBand.setEstablishmentDate(ZonedDateTime.parse(musicDTORequest.getEstablishmentDate()));
            musicBand.setLabel(musicDTORequest.getLabel());
            musicBand.setUpdatedBy(user);


            musicBandRepository.update(musicBand);
            musicWebSocketHandler.sendUpdate("update", DtoUtil.convertToResponse(musicBand));
            return musicBand;
        } catch (HibernateException e) {
            throw new RuntimeException("Failed to update music band", e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Удаление MusicBand по ID
    public void deleteMusicBandById(long id) {
        try {
            MusicBand musicBand = musicBandRepository.findById(id);
            if (musicBand == null) {
                throw new IllegalArgumentException("MusicBand with id " + id + " not found");
            }
            musicBandRepository.delete(musicBand);
            musicWebSocketHandler.sendUpdate("delete", id);
        } catch (HibernateException e) {
            throw new RuntimeException("Failed to delete music band", e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
