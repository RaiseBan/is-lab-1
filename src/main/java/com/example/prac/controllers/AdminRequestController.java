package com.example.prac.controllers;

import com.example.prac.DTO.admin.AdminRequestDTO;
import com.example.prac.model.authEntity.AdminRequest;

import com.example.prac.model.authEntity.User;
import com.example.prac.service.auth.AdminRequestService;
import com.example.prac.service.auth.AuthenticationService;
import com.example.prac.utils.AdminRequestMapper;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@RestController
@RequestMapping("/api/admin-request")
public class AdminRequestController {

    private final AdminRequestService adminRequestService;
    private final AuthenticationService authenticationService; // Для обновления роли пользователя
    private final AdminRequestMapper adminRequestMapper;

    // 1. Создание запроса на админку
    @PostMapping("/request")
    public ResponseEntity<String> requestAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal(); // Получаем текущего пользователя

        // Создаем новый объект AdminRequest
        AdminRequest adminRequest = new AdminRequest();
        adminRequest.setRequester(currentUser);
        adminRequest.setApprovedByAll(false); // Изначально не одобрено всеми
        adminRequest.setApprovedBy(new ArrayList<>()); // Список пуст

        if (adminRequestService.createAdminRequest(adminRequest)) {
            return ResponseEntity.status(HttpStatus.CREATED).body("Запрос на админку создан.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Запрос уже был отправлен.");
        }
    }


    // 2. Просмотр всех запросов (только для админов)
    @GetMapping("/all")
    public ResponseEntity<List<AdminRequestDTO>> getAllRequests() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        System.out.println(currentUser.getAuthorities());
        if (currentUser.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ADMIN"))) {
            System.out.println("jhello");
            List<AdminRequest> requests = adminRequestService.getAllAdminRequests();
            System.out.println("gg");
            List<AdminRequestDTO> requestDTOs = requests.stream()
                    .map(adminRequestMapper::toDTO)
                    .toList();
            System.out.println(12);
            return ResponseEntity.ok(requestDTOs);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    // 3. Одобрение запроса админом
    @PostMapping("/{id}/approve")
    public ResponseEntity<String> approveRequest(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        if (currentUser.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ADMIN"))) {


            boolean isApproved = adminRequestService.approveRequest(id, currentUser);
            if (isApproved) {
                return ResponseEntity.ok("Запрос успешно одобрен.");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Запрос уже одобрен этим админом или не найден.");
            }
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    // 4. Получение информации по конкретному запросу
    @GetMapping("/{id}")
    public ResponseEntity<AdminRequest> getRequestById(@PathVariable Long id) {
        AdminRequest adminRequest = adminRequestService.getAdminRequestById(id);
        if (adminRequest != null) {
            return ResponseEntity.ok(adminRequest);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
