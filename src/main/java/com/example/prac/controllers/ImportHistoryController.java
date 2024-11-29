package com.example.prac.controllers;

import com.example.prac.DTO.info.ImportHistoryDto;
import com.example.prac.model.authEntity.User;
import com.example.prac.model.info.ImportHistory;
import com.example.prac.service.info.ImportHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/import-history")
public class ImportHistoryController {

    private final ImportHistoryService service;

    @Autowired
    public ImportHistoryController(ImportHistoryService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<ImportHistoryDto>> getImportHistory() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        List<ImportHistory> history = service.getImportHistory(currentUser);
        List<ImportHistoryDto> dtos = history.stream()
                .map(ImportHistoryDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
}
