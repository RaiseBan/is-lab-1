package com.example.prac.DTO.info;

import com.example.prac.model.info.ImportHistory;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ImportHistoryDto {
    private Long id;
    private String status;
    private String username;
    private Integer totalObjectsCount; // Общее количество объектов
    private Integer addedObjectsCount; // Успешно добавленные объекты

    public static ImportHistoryDto fromEntity(ImportHistory history) {
        ImportHistoryDto dto = new ImportHistoryDto();
        dto.id = history.getId();
        dto.status = history.getStatus();
        dto.username = history.getUser().getUsername();
        dto.totalObjectsCount = history.getTotalObjectsCount();
        dto.addedObjectsCount = history.getAddedObjectsCount();
        return dto;
    }
}
