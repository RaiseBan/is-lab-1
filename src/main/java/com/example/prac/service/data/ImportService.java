package com.example.prac.service.data;

import com.example.prac.DTO.data.MusicDTORequest;
import com.example.prac.DTO.info.ImportHistoryDto;
import com.example.prac.model.authEntity.User;
import com.example.prac.model.dataEntity.*;
import com.example.prac.model.info.ImportHistory;
import com.example.prac.repository.info.ImportHistoryRepository;
import com.example.prac.service.minio.MinIOService;
import com.example.prac.utils.DtoUtil;
import com.example.prac.webSocket.MusicWebSocketHandler;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ImportService {

    private final ObjectMapper objectMapper;
    private final ImportHistoryRepository importHistoryRepository;
    private final MusicWebSocketHandler musicWebSocketHandler;
    private final SessionFactory sessionFactory;
    private final MinIOService minioService;

    public ImportHistoryDto importMusicBandsFromFile(MultipartFile file, User user) throws Exception {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        String originalFilename = file.getOriginalFilename();
        String uniqueFileName = UUID.randomUUID() + "_" + originalFilename;

        List<MusicDTORequest> musicDTOList = objectMapper.readValue(
                file.getInputStream(),
                new TypeReference<>() {}
        );

        int successfulImports = 0;
        int totalObjects = musicDTOList.size();
        List<String> errorMessages = new ArrayList<>();
        List<MusicBand> convertedMusicBands = new ArrayList<>();
        MusicBand temp;

        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();

            try {
                // Загружаем файл в MinIO
                minioService.uploadTempFile(uniqueFileName, file.getInputStream(), file.getContentType());

                // Обрабатываем DTO объектов
                for (MusicDTORequest dto : musicDTOList) {
                    try {
                        System.out.println("before db section");
                        if (dto.getDescription().equals("FAIL_RUNTIME")){
                            throw new RuntimeException("TEST RUNTIME ERROR...");
                        }
                        temp = processSingleMusicBand(dto, user, session);
                        convertedMusicBands.add(temp);
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

                // Подтверждаем файл в MinIO, если транзакция успешна
                minioService.commitFile(uniqueFileName);

                transaction.commit();
            } catch (Exception e) {
                transaction.rollback();
                minioService.deleteTempFile(uniqueFileName); // Откат изменений в MinIO
                System.err.println("Transaction failed: " + e.getMessage());
            }
        }

        // Записываем историю импорта, даже если транзакция была откатана
        String status = (successfulImports == totalObjects) ? "SUCCESS" : "FAILED";
        ImportHistory history = createImportHistory(
                totalObjects,
                successfulImports,
                user,
                status,
                errorMessages,
                uniqueFileName,
                originalFilename
        );

        // Отправляем обновления в WebSocket для успешных записей
        if ("SUCCESS".equals(history.getStatus())) {
            for (MusicBand musicBand : convertedMusicBands) {
                musicWebSocketHandler.sendUpdate("create", DtoUtil.convertToResponse(musicBand));
            }
        }

        return ImportHistoryDto.fromEntity(history);
    }

    private MusicBand processSingleMusicBand(MusicDTORequest dto, User user, Session session) {
        validateMusicDTO(dto);

        MusicBand musicBand = new MusicBand();
        musicBand.setName(dto.getName());
        musicBand.setOwner(user);
        musicBand.setCreatedBy(user);

        // Сохранение координат
        Coordinates coordinates = dto.getCoordinatesWrapper().getCoordinates();
        if (coordinates != null) {
            coordinates.setOwner(user);
            session.persist(coordinates);
            musicBand.setCoordinates(coordinates);
        } else {
            throw new IllegalArgumentException("Coordinates cannot be null");
        }

        // Сохранение альбома
        Album album = dto.getBestAlbumWrapper().getBestAlbum();
        if (album != null) {
            album.setOwner(user);
            session.persist(album);
            musicBand.setBestAlbum(album);
        } else {
            throw new IllegalArgumentException("Best album cannot be null");
        }

        // Сохранение лейбла
        Label label = dto.getLabelWrapper().getLabel();
        if (label != null) {
            label.setOwner(user);
            session.persist(label);
            musicBand.setLabel(label);
        } else {
            throw new IllegalArgumentException("Label cannot be null");
        }

        // Установка остальных полей
        musicBand.setGenre(MusicGenre.valueOf(dto.getGenre()));
        musicBand.setNumberOfParticipants(dto.getNumberOfParticipants());
        musicBand.setSinglesCount(dto.getSinglesCount());
        musicBand.setDescription(dto.getDescription());
        musicBand.setAlbumsCount(dto.getAlbumsCount());
        musicBand.setEstablishmentDate(ZonedDateTime.parse(dto.getEstablishmentDate()));

        if ("SIMULATE_DB_FAILURE".equals(dto.getName())){
            throw new RuntimeException("SIMULATE_DB_FAILURE");
        }
        session.persist(musicBand);
        return musicBand;
    }

    private ImportHistory createImportHistory(
            int totalObjects,
            int addedObjects,
            User user,
            String status,
            List<String> errorMessages,
            String fileName,
            String originalFileName
    ) {
        ImportHistory history = new ImportHistory();
        history.setStatus(status);
        history.setUser(user);
        history.setTotalObjectsCount(totalObjects);
        history.setAddedObjectsCount(addedObjects);
        history.setFileName(fileName);
        history.setOriginalFileName(originalFileName);
        return importHistoryRepository.save(history);
    }

    private void validateMusicDTO(MusicDTORequest dto) throws ConstraintViolationException {
        Set<ConstraintViolation<MusicDTORequest>> violations =
                Validation.buildDefaultValidatorFactory().getValidator().validate(dto);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException("Validation failed", violations);
        }
    }
}


