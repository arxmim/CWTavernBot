package org.nia.db;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.nia.PropertiesLoader;

/**
 * @author IANazarov
 */
public class HibernateConfig {

    private static SessionFactory factory;

    public static SessionFactory getSessionFactory() {
        if (factory == null) {
            factory = new Configuration()
                    .setProperty("hibernate.connection.url", PropertiesLoader.INSTANCE.getConnectionString())
                    .configure().buildSessionFactory();
        }
        return factory;
    }
}
