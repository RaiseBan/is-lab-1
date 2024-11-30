package com.example.prac.repository.data;

import com.example.prac.model.authEntity.User;
import com.example.prac.model.dataEntity.Album;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlbumRepository extends JpaRepository<Album, Long> {

    // Метод для поиска всех альбомов по владельцу
    List<Album> findByOwner(User user);

    // Удаление можно делать с помощью стандартного метода delete
    void delete(Album album);

}
