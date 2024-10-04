package com.example.prac.repository.data;

import com.example.prac.model.dataEntity.MusicBand;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MusicBandRepository {

    @Autowired
    private SessionFactory sessionFactory;

    public void save(MusicBand musicBand) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.save(musicBand);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public void update(MusicBand musicBand) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.update(musicBand);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public void delete(MusicBand musicBand) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.delete(musicBand);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public MusicBand findById(long id) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(MusicBand.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public List<MusicBand> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM MusicBand").list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
