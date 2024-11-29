package com.example.prac.service.info;

import com.example.prac.model.authEntity.User;
import com.example.prac.model.info.ImportHistory;
import com.example.prac.repository.info.ImportHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ImportHistoryService {

    private final ImportHistoryRepository repository;

    @Autowired
    public ImportHistoryService(ImportHistoryRepository repository) {
        this.repository = repository;
    }

    public List<ImportHistory> getImportHistory(User currentUser) {
        if (currentUser.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ADMIN"))) {
            return repository.findAll();
        }
        return repository.findByUser_Id(currentUser.getId());

    }
}
