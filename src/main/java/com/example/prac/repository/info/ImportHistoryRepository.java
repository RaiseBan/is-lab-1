package com.example.prac.repository.info;

import com.example.prac.model.info.ImportHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ImportHistoryRepository extends JpaRepository<ImportHistory, Long> {

    // Метод для получения всех операций, запущенных конкретным пользователем
    List<ImportHistory> findByUser_Id(Long userId);

    // Метод для получения всех операций (используется администратором)
    List<ImportHistory> findAll();
}
