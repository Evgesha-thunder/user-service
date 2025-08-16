package com.bulish;

import com.bulish.model.User;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateTestUtil {

    private static SessionFactory sessionFactory;

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            Configuration configuration = new Configuration();
            configuration.configure("/hibernate-test.cfg.xml");
            configuration.setProperty("hibernate.connection.url", PostgresTestContainer.getJdbcUrl());
            configuration.setProperty("hibernate.connection.username", PostgresTestContainer.getUsername());
            configuration.setProperty("hibernate.connection.password", PostgresTestContainer.getPassword());
            configuration.addAnnotatedClass(User.class);

            sessionFactory = configuration.buildSessionFactory();
        }
        return sessionFactory;
    }
}
