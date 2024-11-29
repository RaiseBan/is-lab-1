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
import com.example.prac.repository.data.MusicBandRepository;
import com.example.prac.repository.info.ImportHistoryRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ImportService {

    private final ObjectMapper objectMapper;
    private final ImportHistoryRepository importHistoryRepository;

    private final MusicBandRepository musicBandRepository;
    @Transactional
    public ImportHistoryDto importMusicBandsFromFile(MultipartFile file, User user) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        // Читаем JSON из файла
        List<MusicDTORequest> musicDTOList = objectMapper.readValue(
                file.getInputStream(),
                new TypeReference<>() {}
        );

        int successfulImports = 0; // Счётчик успешных обработанных объектов
        int totalObjects = musicDTOList.size(); // Общее количество объектов

        for (MusicDTORequest dto : musicDTOList) {
            try {
                // Обрабатываем один объект
                processSingleMusicBand(dto, user);
                successfulImports++; // Увеличиваем счётчик успешных импортов
            } catch (ConstraintViolationException e) {
                // Логируем ошибку валидации, но продолжаем обработку
                System.err.println("Validation failed for DTO: " + dto + ", Error: " + e.getMessage());
            } catch (Exception e) {
                // Логируем любую другую ошибку
                System.err.println("Unexpected error while processing DTO: " + dto + ", Error: " + e.getMessage());
            }
        }

        // Определяем статус импорта
        String status = (successfulImports == totalObjects) ? "SUCCESS" : "FAILED";

        // Создаём запись в истории
        ImportHistory history = createImportHistory(totalObjects, successfulImports, user, status);

        // Возвращаем DTO, используя метод fromEntity
        return ImportHistoryDto.fromEntity(history);
    }


    @Transactional
    protected void processSingleMusicBand(MusicDTORequest dto, User user) {
        // Валидация DTO
        validateMusicDTO(dto);

        // Конвертация и сохранение
        MusicBand musicBand = convertToEntity(dto, user);
        // Сохранение через репозиторий (или EntityManager)
        // Например:
         musicBandRepository.save(musicBand);
    }

    private ImportHistory createImportHistory(int totalObjects, int addedObjects, User user, String status) {
        ImportHistory history = new ImportHistory();
        history.setStatus(status);
        history.setUser(user);
        history.setTotalObjectsCount(totalObjects); // Общее количество объектов
        history.setAddedObjectsCount(addedObjects); // Успешно добавленные объекты

        // Сохраняем историю в базе данных
        return importHistoryRepository.save(history);
    }


    private void validateMusicDTO(MusicDTORequest dto) throws ConstraintViolationException {
        Set<ConstraintViolation<MusicDTORequest>> violations =
                Validation.buildDefaultValidatorFactory().getValidator().validate(dto);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException("Validation failed", violations);
        }
    }

    private MusicBand convertToEntity(MusicDTORequest dto, User user) {
        MusicBand musicBand = new MusicBand();

        musicBand.setOwner(user);

        // Установка остальных полей
        musicBand.setName(dto.getName());
        musicBand.setGenre(MusicGenre.valueOf(dto.getGenre()));
        musicBand.setNumberOfParticipants(dto.getNumberOfParticipants());
        musicBand.setSinglesCount(dto.getSinglesCount());
        musicBand.setDescription(dto.getDescription());
        musicBand.setAlbumsCount(dto.getAlbumsCount());
        musicBand.setEstablishmentDate(ZonedDateTime.parse(dto.getEstablishmentDate()));
        musicBand.setCreatedBy(user);

        // Пример сохранения связанных объектов:
        // Если используются связанные сущности, например Coordinates, Album или Label
        // musicBand.setCoordinates(dto.getCoordinatesWrapper().getCoordinates());
        // musicBand.setBestAlbum(dto.getBestAlbumWrapper().getBestAlbum());
        // musicBand.setLabel(dto.getLabelWrapper().getLabel());

        return musicBand;
    }
}
