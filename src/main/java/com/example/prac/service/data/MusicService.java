package com.example.prac.service.data;

import com.example.prac.DTO.data.MusicDTORequest;
import com.example.prac.DTO.data.MusicDTOResponse;
import com.example.prac.errorHandler.ResourceNotFoundException;
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
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class MusicService {

    private final MusicBandRepository musicBandRepository;
    private final MusicWebSocketHandler musicWebSocketHandler;    public MusicBand saveMusicBand(MusicDTORequest musicDTORequest, User user) {
        try {
                        MusicBand musicBand = new MusicBand();
            musicBand.setName(musicDTORequest.getName());
            musicBand.setCoordinates(musicDTORequest.getCoordinates());
            try {
                musicBand.setGenre(MusicGenre.valueOf(musicDTORequest.getGenre()));
            }catch (Exception e){
                musicBand.setGenre(null);
            }

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
    }    public List<MusicBand> getAllMusicBands() {
        try {
            return musicBandRepository.findAll();
        } catch (HibernateException e) {
            throw new RuntimeException("Failed to fetch all music bands", e);
        }
    }    public MusicBand getMusicBandById(long id) {
        try {
            MusicBand musicBand = musicBandRepository.findById(id);
            if (musicBand == null) {
                throw new IllegalArgumentException("MusicBand with id " + id + " not found");
            }
            return musicBand;
        } catch (Exception e) {
            throw new ResourceNotFoundException("no such MusicBand");
        }
    }    @Transactional
    public MusicBand updateMusicBand(MusicBand musicBand, MusicDTORequest musicDTORequest, User user) {
        try {
            if (musicBand == null) {
                throw new IllegalArgumentException("MusicBand with id not found");
            }            musicBand.setName(musicDTORequest.getName());
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
    }    public void deleteMusicBandById(long id) {
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
    }    public List<Object[]> getMusicBandCountByCreationDate() {
        return musicBandRepository.getMusicBandCountByCreationDate();
    }    public Long countMusicBandsWithLabelMoreThan(String labelThreshold) {
        return musicBandRepository.countMusicBandsWithLabelMoreThan(labelThreshold);
    }    public List<MusicDTOResponse> findMusicBandsByDescription(String substring) {
        List<MusicBand> musicBands = musicBandRepository.findMusicBandsByDescriptionSubstring(substring);        return musicBands.stream().map(DtoUtil::convertToResponse).collect(Collectors.toList());
    }    public void addSingleToMusicBand(Long bandId, int singlesCount) throws IOException {
        musicBandRepository.addSingleToMusicBand(bandId, singlesCount);

        MusicBand musicBand = this.getMusicBandById(bandId);
        musicWebSocketHandler.sendUpdate("update", DtoUtil.convertToResponse(musicBand));
    }    public void removeParticipantFromMusicBand(long bandId) throws IOException {
        musicBandRepository.removeParticipantFromMusicBand(bandId);
        MusicBand musicBand = this.getMusicBandById(bandId);
        musicWebSocketHandler.sendUpdate("update", DtoUtil.convertToResponse(musicBand));
    }


}
