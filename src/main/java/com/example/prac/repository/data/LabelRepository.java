package com.example.prac.repository.data;

import com.example.prac.model.authEntity.User;
import com.example.prac.model.dataEntity.Label;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class LabelRepository {

    @Autowired
    private SessionFactory sessionFactory;

    public List<Label> findByOwner(User user) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM Label WHERE owner = :owner", Label.class)
                    .setParameter("owner", user)
                    .list();
        }
        }


    public Label findById(long id) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(Label.class, id);
        }
    }

    public void delete(Label label) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.merge(label);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }
    public void save(Label label){
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.save(label);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }
    public void update(Label label){
        try (Session session = sessionFactory.openSession()) {
            session.update(label);
        }
    }
}
