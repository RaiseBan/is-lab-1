package com.example.prac.DTO.data.wrappers;


import com.example.prac.model.dataEntity.Label;
import jakarta.validation.Valid;
import lombok.Data;

@Data
public class LabelWrapper {
    private Long labelId;
    @Valid
    private Label label; 
}
