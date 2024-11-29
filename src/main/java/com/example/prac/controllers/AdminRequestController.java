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

import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
@RestController
@RequestMapping("/api/admin-request")
public class AdminRequestController {

    private final AdminRequestService adminRequestService;
    private final AuthenticationService authenticationService;
    private final AdminRequestMapper adminRequestMapper;

    @PostMapping("/request")
    public ResponseEntity<String> requestAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        AdminRequest adminRequest = new AdminRequest();
        adminRequest.setRequester(currentUser);
        adminRequest.setApprovedByAll(false);
        adminRequest.setApprovedBy(new ArrayList<>());
        if (adminRequestService.createAdminRequest(adminRequest)) {
            return ResponseEntity.status(HttpStatus.CREATED).body("Запрос на админку создан.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Запрос уже был отправлен.");
        }
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getAdminRequestStatus() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        Optional<AdminRequest> existingRequest = adminRequestService.findByRequester(currentUser);

        Map<String, Object> response = new HashMap<>();

        if (existingRequest.isPresent()) {
            response.put("status", existingRequest.get().isApprovedByAll() ? "approved" : "pending");
            response.put("message", existingRequest.get().isApprovedByAll() ? "You are now an admin!" : "Your request is being processed...");
        } else {
            response.put("status", "none");
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/all")
    public ResponseEntity<List<AdminRequestDTO>> getAllRequests() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        if (currentUser.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ADMIN"))) {
            List<AdminRequest> requests = adminRequestService.getAllAdminRequests();
            List<AdminRequestDTO> requestDTOs = requests.stream()
                    .map(adminRequestMapper::toDTO)
                    .toList();
            return ResponseEntity.ok(requestDTOs);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<String> approveRequest(@PathVariable Long id) throws Exception {
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
