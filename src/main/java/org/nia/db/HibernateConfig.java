package org.nia.db;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * @author IANazarov
 */
public class HibernateConfig {

    private static SessionFactory factory;

    public static SessionFactory getSessionFactory() {
        if (factory == null) {
            factory = new Configuration().configure().buildSessionFactory();
        }
        return factory;
    }
}
