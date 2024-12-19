package com.example.prac.DTO.info;

import com.example.prac.model.info.ImportHistory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ImportHistoryDto {
    private Long id;
    private String status;
    private String username;
    private Integer totalObjectsCount;
    private Integer addedObjectsCount;
    private String fileName; // Уникальное имя файла
    private String originalFileName; // Оригинальное имя файла

    public static ImportHistoryDto fromEntity(ImportHistory history) {
        ImportHistoryDto dto = new ImportHistoryDto();
        dto.id = history.getId();
        dto.status = history.getStatus();
        dto.username = history.getUser().getUsername();
        dto.totalObjectsCount = history.getTotalObjectsCount();
        dto.addedObjectsCount = history.getAddedObjectsCount();
        dto.fileName = history.getFileName(); // Уникальное имя файла
        dto.originalFileName = history.getOriginalFileName(); // Оригинальное имя файла
        return dto;
    }
}

