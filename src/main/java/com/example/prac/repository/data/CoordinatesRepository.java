package com.example.prac.repository.data;

import com.example.prac.model.authEntity.User;
import com.example.prac.model.dataEntity.Coordinates;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CoordinatesRepository extends JpaRepository<Coordinates, Long> {

    // Метод для поиска всех Coordinates по владельцу
    List<Coordinates> findByOwner(User user);

    // Для удаления можно использовать стандартный метод delete
    void delete(Coordinates coordinates);

}
