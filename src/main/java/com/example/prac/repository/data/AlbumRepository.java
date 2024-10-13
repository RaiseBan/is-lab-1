package com.example.prac.repository.data;

import com.example.prac.model.authEntity.User;
import com.example.prac.model.dataEntity.Album;
import lombok.AllArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class AlbumRepository {


    private final SessionFactory sessionFactory;

    public List<Album> findByOwner(User user) {

        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM Album WHERE owner = :owner", Album.class)
                    .setParameter("owner", user)
                    .list();
        }
    }

    public Album findById(long id) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(Album.class, id);
        }

    }

    public void delete(Album album) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.merge(album);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }
    public void save(Album album){


        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.save(album);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public void update(Album album){
        try (Session session = sessionFactory.openSession()) {
            session.update(album);
        }
    }
}

