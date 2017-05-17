package org.nia.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.nia.db.HibernateConfig;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Иван, 08.05.2017.
 */
@Entity(name = "cwt_VoteOption")
@Getter
@Setter
public class VoteOption {
    @Column
    @Id
    @GeneratedValue
    Integer publicID;
    @Column
    String text;
    @ManyToOne
    @JoinColumn(name = "votingID")
    Voting voting;

    @SuppressWarnings("unchecked")
    public static List<VoteOption> getAll(Integer votingID) {
        List<VoteOption> res = new ArrayList<>();
        SessionFactory factory = HibernateConfig.getSessionFactory();
        try (Session session = factory.openSession()) {
            Query query = session.createQuery("FROM cwt_VoteOption WHERE voting = " + votingID);
            res = query.list();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return res;
    }


    @SuppressWarnings("unchecked")
    public static VoteOption getByID(Integer publicID) {
        VoteOption res = null;
        SessionFactory factory = HibernateConfig.getSessionFactory();
        try (Session session = factory.openSession()) {
            res = session.get(VoteOption.class, publicID);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return res;
    }

    @SuppressWarnings("unchecked")
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VoteOption)) return false;

        VoteOption that = (VoteOption) o;

        return publicID != null ? publicID.equals(that.publicID) : that.publicID == null;

    }

    @Override
    public int hashCode() {
        return publicID != null ? publicID.hashCode() : 0;
    }
}
