package com.example.prac.controllers;

import com.example.prac.DTO.data.CoordinatesDTOResponse;
import com.example.prac.DTO.data.CoordinatesDTOwId;
import com.example.prac.model.authEntity.User;
import com.example.prac.model.dataEntity.Coordinates;
import com.example.prac.service.data.CoordinatesService;
import com.example.prac.utils.DtoUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/coordinates")
public class CoordinatesController {

    private final CoordinatesService coordinatesService;

    public CoordinatesController(CoordinatesService coordinatesService) {
        this.coordinatesService = coordinatesService;
    }

    
    @GetMapping
    public ResponseEntity<List<CoordinatesDTOwId>> getUserCoordinates() {
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User currentUser = (User) authentication.getPrincipal();
            
            List<Coordinates> coordinates = coordinatesService.getCoordinatesByUser(currentUser);
            
            List<CoordinatesDTOwId> response = coordinates.stream()
                    .map(DtoUtil::convertToCoordinatesDTOwIdResponse)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(response);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
