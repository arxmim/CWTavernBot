package org.nia.model;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.nia.db.HibernateConfig;

/**
 * @author IANazarov
 */
class AbstractEntity {
    public boolean save() {
        boolean res = false;
        SessionFactory factory = HibernateConfig.getSessionFactory();
        try (Session session = factory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.saveOrUpdate(this);
            tx.commit();
            session.refresh(this);
            res = true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return res;
    }

    public static <T extends AbstractEntity> T getByID(Class<T> clz, Long publicID) {
        T res = null;
        SessionFactory factory = HibernateConfig.getSessionFactory();
        try (Session session = factory.openSession()) {
            res = session.get(clz, publicID);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return res;
    }
}
