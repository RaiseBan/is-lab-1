package com.example.prac.service.data;

import com.example.prac.errorHandler.ResourceNotFoundException;
import com.example.prac.model.authEntity.User;
import com.example.prac.model.dataEntity.Coordinates;
import com.example.prac.repository.data.CoordinatesRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
@Transactional
public class CoordinatesService {

    private final CoordinatesRepository coordinatesRepository;

    public List<Coordinates> getCoordinatesByUser(User user) {
        
        return coordinatesRepository.findByOwner(user);
    }
}
