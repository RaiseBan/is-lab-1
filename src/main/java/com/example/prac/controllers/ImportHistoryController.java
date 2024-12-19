package com.example.prac.controllers;

import com.example.prac.DTO.info.ImportHistoryDto;
import com.example.prac.model.authEntity.User;
import com.example.prac.model.info.ImportHistory;
import com.example.prac.repository.info.ImportHistoryRepository;
import com.example.prac.service.info.ImportHistoryService;
import com.example.prac.service.minio.MinIOService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@RequestMapping("/api/import-history")
public class ImportHistoryController {

    private final ImportHistoryService importHistoryService;
    private final ImportHistoryRepository importHistoryRepository;
    private final MinIOService minioService;


    @GetMapping
    public ResponseEntity<List<ImportHistoryDto>> getImportHistory() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        List<ImportHistory> history = importHistoryService.getImportHistory(currentUser);
        List<ImportHistoryDto> dtos = history.stream()
                .map(ImportHistoryDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
    @GetMapping("/{importId}/file")
    public ResponseEntity<InputStreamResource> downloadImportedFile(@PathVariable Long importId) {
        try {
            ImportHistory history = importHistoryRepository.findById(importId)
                    .orElseThrow(() -> new IllegalArgumentException("Import history not found for ID: " + importId));

            InputStream fileStream = minioService.downloadFile(history.getFileName());

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + history.getOriginalFileName() + "\"")
                    .body(new InputStreamResource(fileStream));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }


}
