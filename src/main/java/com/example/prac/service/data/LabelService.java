package com.example.prac.service.data;

import com.example.prac.model.authEntity.User;
import com.example.prac.model.dataEntity.Label;
import com.example.prac.repository.data.LabelRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
@Transactional
public class LabelService {

    private final LabelRepository labelRepository;

    public List<Label> getLabelsByUser(User user) {
        List<Label> labels = labelRepository.findByOwner(user);
        
        

        return labels;
    }
}
