package com.example.prac.controllers;

import com.example.prac.DTO.data.LabelDTOResponse;
import com.example.prac.DTO.data.LabelDTOwId;
import com.example.prac.model.authEntity.User;
import com.example.prac.model.dataEntity.Label;
import com.example.prac.service.data.LabelService;
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
@RequestMapping("/api/labels")
public class LabelController {

    private final LabelService labelService;

    public LabelController(LabelService labelService) {
        this.labelService = labelService;
    }

    @GetMapping
    public ResponseEntity<List<LabelDTOwId>> getUserLabels() {
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User currentUser = (User) authentication.getPrincipal();
            


            List<Label> labels = labelService.getLabelsByUser(currentUser);
            
            List<LabelDTOwId> response = labels.stream()
                    .map(DtoUtil::convertToLabelDTOwIdResponse) 
                    .collect(Collectors.toList());

            return ResponseEntity.ok(response);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
