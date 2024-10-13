package com.example.prac.repository.data;

import com.example.prac.DTO.data.wrappers.CoordinatesWrapper;
import com.example.prac.model.authEntity.User;
import com.example.prac.model.dataEntity.Coordinates;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CoordinatesRepository {

    @Autowired
    private SessionFactory sessionFactory;

    
    public Coordinates findById(long id) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            return session.get(Coordinates.class, id);
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
        return null;
    }

    public void delete(Coordinates coordinates) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.merge(coordinates);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    
    public List<Coordinates> findByOwner(User user) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM Coordinates WHERE owner = :owner", Coordinates.class)
                    .setParameter("owner", user)
                    .list();
        }
    }

    public void save(Coordinates coordinates){
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.save(coordinates);
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (transaction != null) {
                transaction.rollback();
            }

        }
    }


    public void update(Coordinates coordinates){
        try (Session session = sessionFactory.openSession()) {
            session.update(coordinates);
        }
    }
}
