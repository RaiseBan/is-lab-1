package com.example.prac.service.data;

import com.example.prac.model.authEntity.User;
import com.example.prac.model.dataEntity.Album;
import com.example.prac.repository.data.AlbumRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
@Transactional
public class AlbumService {

    private final AlbumRepository albumRepository;
    public List<Album> getAlbumsByUser(User user) {
        
        return albumRepository.findByOwner(user);
    }
}
