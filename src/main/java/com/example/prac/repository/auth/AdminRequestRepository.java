package com.example.prac.repository.auth;

import com.example.prac.model.authEntity.AdminRequest;
import com.example.prac.model.authEntity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdminRequestRepository extends JpaRepository<AdminRequest, Long> {

    // Найти запрос по requester (пользователь, сделавший запрос)
    Optional<AdminRequest> findByRequester(User requester);

    // Найти все запросы
    List<AdminRequest> findAll();

}
