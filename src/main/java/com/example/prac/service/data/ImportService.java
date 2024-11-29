package com.example.prac.service.data;

import com.example.prac.DTO.data.MusicDTORequest;
import com.example.prac.DTO.info.ImportHistoryDto;
import com.example.prac.model.authEntity.User;
import com.example.prac.model.dataEntity.Album;
import com.example.prac.model.dataEntity.Coordinates;
import com.example.prac.model.dataEntity.Label;
import com.example.prac.model.dataEntity.MusicBand;
import com.example.prac.model.dataEntity.MusicGenre;
import com.example.prac.model.info.ImportHistory;
import com.example.prac.repository.info.ImportHistoryRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ImportService {

    private final ObjectMapper objectMapper;
    private final ImportHistoryRepository importHistoryRepository;
    private final SessionFactory sessionFactory;

    public ImportHistoryDto importMusicBandsFromFile(MultipartFile file, User user) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        List<MusicDTORequest> musicDTOList = objectMapper.readValue(
                file.getInputStream(),
                new TypeReference<>() {}
        );

        int successfulImports = 0;
        int totalObjects = musicDTOList.size();
        List<String> errorMessages = new ArrayList<>();

        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();

            try {
                for (MusicDTORequest dto : musicDTOList) {
                    try {
                        processSingleMusicBand(dto, user, session);
                        successfulImports++;
                    } catch (ConstraintViolationException e) {
                        errorMessages.add("Validation failed for DTO: " + dto + ". Error: " + e.getMessage());
                        throw e; // Откат всей транзакции
                    } catch (IllegalArgumentException e) {
                        errorMessages.add("Invalid data for DTO: " + dto + ". Error: " + e.getMessage());
                        throw e; // Откат всей транзакции
                    } catch (Exception e) {
                        errorMessages.add("Unexpected error while processing DTO: " + dto + ". Error: " + e.getMessage());
                        throw e; // Откат всей транзакции
                    }
                }

                transaction.commit();
            } catch (Exception e) {
                transaction.rollback();
                // После отката продолжаем процесс для записи истории
                System.err.println("Transaction failed: " + e.getMessage());
            }
        }

        // Записываем историю импорта даже если транзакция была откатана
        String status = (successfulImports == totalObjects) ? "SUCCESS" : "FAILED";
        ImportHistory history = createImportHistory(totalObjects, successfulImports, user, status, errorMessages);

        return ImportHistoryDto.fromEntity(history);
    }

    private void processSingleMusicBand(MusicDTORequest dto, User user, Session session) {
        validateMusicDTO(dto);

        MusicBand musicBand = convertToEntity(dto, user, session);
        session.persist(musicBand);
    }

    private ImportHistory createImportHistory(int totalObjects, int addedObjects, User user, String status, List<String> errorMessages) {
        ImportHistory history = new ImportHistory();
        history.setStatus(status);
        history.setUser(user);
        history.setTotalObjectsCount(totalObjects);
        history.setAddedObjectsCount(addedObjects);
//        history.setErrorMessages(String.join("; ", errorMessages)); // Добавляем сообщения об ошибках
        return importHistoryRepository.save(history);
    }

    private void validateMusicDTO(MusicDTORequest dto) throws ConstraintViolationException {
        Set<ConstraintViolation<MusicDTORequest>> violations =
                Validation.buildDefaultValidatorFactory().getValidator().validate(dto);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException("Validation failed", violations);
        }
    }

    private MusicBand convertToEntity(MusicDTORequest dto, User user, Session session) {
        MusicBand musicBand = new MusicBand();

        // Загрузить пользователя из текущей сессии
        User managedUser = session.get(User.class, user.getId());
        if (managedUser == null) {
            throw new IllegalArgumentException("User not found in the database.");
        }
        musicBand.setOwner(managedUser);

        // Сохранение координат
        Coordinates coordinates = dto.getCoordinatesWrapper().getCoordinates();
        if (coordinates != null) {
            coordinates.setOwner(managedUser);
            session.persist(coordinates);
            musicBand.setCoordinates(coordinates);
        } else {
            throw new IllegalArgumentException("Coordinates cannot be null");
        }

        // Сохранение альбома
        Album album = dto.getBestAlbumWrapper().getBestAlbum();
        if (album != null) {
            album.setOwner(managedUser);
            session.persist(album);
            musicBand.setBestAlbum(album);
        } else {
            throw new IllegalArgumentException("Best album cannot be null");
        }

        // Сохранение лейбла
        Label label = dto.getLabelWrapper().getLabel();
        if (label != null) {
            label.setOwner(managedUser);
            session.persist(label);
            musicBand.setLabel(label);
        } else {
            throw new IllegalArgumentException("Label cannot be null");
        }

        // Установка остальных полей
        musicBand.setName(dto.getName());
        musicBand.setGenre(MusicGenre.valueOf(dto.getGenre()));
        musicBand.setNumberOfParticipants(dto.getNumberOfParticipants());
        musicBand.setSinglesCount(dto.getSinglesCount());
        musicBand.setDescription(dto.getDescription());
        musicBand.setAlbumsCount(dto.getAlbumsCount());
        musicBand.setEstablishmentDate(ZonedDateTime.parse(dto.getEstablishmentDate()));
        musicBand.setCreatedBy(managedUser);

        return musicBand;
    }
}
