package com.example.prac.aspect;

import com.example.prac.model.dataEntity.MusicBand;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Aspect
@Component
@RequiredArgsConstructor
public class MusicBandValidationAspect {

    private final SessionFactory sessionFactory;

    @Before("execution(* com.example.prac.repository.data.MusicBandRepository.save(..)) || " +
            "execution(* com.example.prac.repository.data.MusicBandRepository.saveAll(..))")
    public void validateBeforeSave(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();

        try (Session session = sessionFactory.openSession()) {
            if (args.length > 0) {
                if (args[0] instanceof MusicBand) {
                    // Если это одиночный MusicBand
                    validateMusicBand((MusicBand) args[0], session);
                } else if (args[0] instanceof List) {
                    // Если это список MusicBand
                    @SuppressWarnings("unchecked")
                    List<MusicBand> musicBands = (List<MusicBand>) args[0];
                    for (MusicBand band : musicBands) {
                        validateMusicBand(band, session);
                    }
                }
            }
        }
    }

    private void validateMusicBand(MusicBand musicBand, Session session) {
        Query query = session.createQuery(
                "SELECT COUNT(mb.id) FROM MusicBand mb WHERE mb.name = :name AND mb.owner.id = :ownerId");
        query.setParameter("name", musicBand.getName());
        query.setParameter("ownerId", musicBand.getOwner().getId());

        Long count = (Long) query.getSingleResult();
        if (count > 0) {
            throw new IllegalArgumentException("Music band with the name '" + musicBand.getName() + "' already exists for the user.");
        }
    }
}
