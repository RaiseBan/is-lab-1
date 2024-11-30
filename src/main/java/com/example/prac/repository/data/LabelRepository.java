package com.example.prac.repository.data;

import com.example.prac.model.authEntity.User;
import com.example.prac.model.dataEntity.Label;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LabelRepository extends JpaRepository<Label, Long> {

    // Метод для поиска всех Label по владельцу
    List<Label> findByOwner(User user);

    // Для удаления можно использовать стандартный метод delete
    void delete(Label label);

}
