package com.example.prac.utils;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HibernateUtil {

    private final SessionFactory sessionFactory;

    @Autowired
    public HibernateUtil(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }    public void shutdown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }
}

