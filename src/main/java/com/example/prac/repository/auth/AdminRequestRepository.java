package com.example.prac.repository.auth;

import com.example.prac.model.authEntity.AdminRequest;
import lombok.AllArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@AllArgsConstructor
@Repository
public class AdminRequestRepository {

    private final SessionFactory sessionFactory;


    // Сохранение заявки
    public void save(AdminRequest adminRequest) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.save(adminRequest);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        }
    }

    // Получение заявки по id
    public AdminRequest findById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(AdminRequest.class, id);
        }
    }

    // Обновление заявки
    public void update(AdminRequest adminRequest) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.update(adminRequest);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        }
    }

    // Получение всех заявок
    @SuppressWarnings("unchecked")
    public List<AdminRequest> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("from AdminRequest").list();
        }
    }

}
