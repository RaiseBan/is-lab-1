package com.example.prac.utils;


import com.example.prac.DTO.admin.AdminRequestDTO;
import com.example.prac.model.authEntity.AdminRequest;
import com.example.prac.model.authEntity.User;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class AdminRequestMapper {

    public AdminRequestDTO toDTO(AdminRequest adminRequest) {
        AdminRequestDTO dto = new AdminRequestDTO();
        dto.setId(adminRequest.getId());
        dto.setRequesterUsername(adminRequest.getRequester().getUsername());
        dto.setApprovedByAll(adminRequest.isApprovedByAll());
        dto.setApprovedByUsernames(adminRequest.getApprovedBy().stream()
                .map(User::getUsername)
                .collect(Collectors.toList()));
        return dto;
    }
}
