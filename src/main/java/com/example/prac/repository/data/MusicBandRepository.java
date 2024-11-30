package com.example.prac.repository.data;

import com.example.prac.model.dataEntity.MusicBand;
import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.StoredProcedureQuery;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

@Repository
public class MusicBandRepository {

    @Autowired
    private SessionFactory sessionFactory;

    public void save(MusicBand musicBand) throws SQLException{
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {

            session.doWork(connection -> {
                connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            });

            transaction = session.beginTransaction();
            session.save(musicBand);
            transaction.commit();

        }catch (ConstraintViolationException e) {
            // Здесь обрабатываем ошибку нарушения уникальности
            System.out.println("Duplicate entry found: " + e.getMessage());
            throw new ConstraintViolationException("MusicBand with the same name already exists.", e.getConstraintViolations());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("filed");
            if (transaction != null) {
                System.out.println("Rollback");
                transaction.rollback();  // Откатим транзакцию
            }
            throw new RuntimeException("Failed to save music band", e);
        }
    }



    public void update(MusicBand musicBand) throws OptimisticLockException{
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            // Устанавливаем уровень изоляции транзакции вручную
            session.doWork(connection -> {
                connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            });

            transaction = session.beginTransaction();
            session.merge(musicBand);
            transaction.commit();
        } catch (OptimisticLockException e) {
            if (transaction != null) {
                System.out.println("rollback");
                transaction.rollback();
            }
            e.printStackTrace();
            System.out.println("lock update");
            throw e;
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public void delete(MusicBand musicBand) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            session.doWork(connection -> {
                connection.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
            });
            transaction = session.beginTransaction();
            session.remove(musicBand);
            transaction.commit();
        }  catch (OptimisticLockException e) {
            if (transaction != null) {
                System.out.println("rollback");
                transaction.rollback();
            }
//            e.printStackTrace();
            System.out.println("lock delete");
            throw e;
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

    public List<Object[]> getMusicBandCountByCreationDate() {
        try (Session session = sessionFactory.openSession()) {
            return session.createNativeQuery("SELECT * FROM get_musicband_count_by_creation_date()")
                    .list();
        }
    }

    public Long countMusicBandsWithLabelMoreThan(String labelThreshold) {
        try (Session session = sessionFactory.openSession()) {
            return ((Number) session.createNativeQuery("SELECT count_musicbands_with_label_more_than(:labelThreshold)")
                    .setParameter("labelThreshold", labelThreshold)
                    .getSingleResult()).longValue();
        }
    }

    public List<MusicBand> findMusicBandsByDescriptionSubstring(String substring) {
        try (Session session = sessionFactory.openSession()) {
            return session.createNamedQuery("MusicBand.findByDescriptionSubstring", MusicBand.class)
                    .setParameter("substring", substring)
                    .getResultList();
        }
    }

    public void addSingleToMusicBand(Long bandId, int singlesCount) {
        try (Session session = sessionFactory.openSession()) {
            StoredProcedureQuery query = session.createStoredProcedureQuery("add_single_to_musicband");
            query.registerStoredProcedureParameter(1, Long.class, ParameterMode.IN);
            query.registerStoredProcedureParameter(2, Integer.class, ParameterMode.IN);
            query.setParameter(1, bandId);
            query.setParameter(2, singlesCount);
            query.execute();
        }
    }

    public void removeParticipantFromMusicBand(long bandId) {
        try (Session session = sessionFactory.openSession()) {
            StoredProcedureQuery query = session.createStoredProcedureQuery("remove_participant_from_musicband");
            query.registerStoredProcedureParameter(1, Long.class, ParameterMode.IN);
            query.setParameter(1, bandId);
            query.execute();
        }


    }

    public int countByCoordinatesId(long coordinatesId) {
        try (Session session = sessionFactory.openSession()) {
            Query<Long> query = session.createQuery(
                    "SELECT COUNT(mb.id) FROM MusicBand mb WHERE mb.coordinates.id = :coordinatesId", Long.class);
            query.setParameter("coordinatesId", coordinatesId);
            return query.uniqueResult().intValue();
        }
    }
    public int countByBestAlbumId(long bestAlbumId) {
        try (Session session = sessionFactory.openSession()) {
            Query<Long> query = session.createQuery(
                    "SELECT COUNT(mb.id) FROM MusicBand mb WHERE mb.bestAlbum.id = :bestAlbumId", Long.class);
            query.setParameter("bestAlbumId", bestAlbumId);
            return query.uniqueResult().intValue();
        }
    }
    public int countByLabelId(long labelId) {
        try (Session session = sessionFactory.openSession()) {
            Query<Long> query = session.createQuery(
                    "SELECT COUNT(mb.id) FROM MusicBand mb WHERE mb.label.id = :labelId", Long.class);
            query.setParameter("labelId", labelId);
            return query.uniqueResult().intValue();
        }
    }


    public void saveAll(List<MusicBand> musicBands) {
        try (Session session = sessionFactory.openSession()) {
            for (MusicBand musicBand : musicBands) {
                session.save(musicBand);
            }
        } catch (ConstraintViolationException e) {
            Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
            throw new ConstraintViolationException("Validation failed for one or more entities", violations);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to save music bands", e);
        }
    }





}
