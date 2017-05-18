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
@Entity
@Getter
@Setter
@Table(name = "cwt_VoteUser")
public class VoteUser {
    @Column
    @Id
    @GeneratedValue
    Integer ID;
    @ManyToOne
    @JoinColumn(name = "userID")
    User user;
    @ManyToOne
    @JoinColumn(name = "votingID")
    Voting voting;
    @ManyToOne
    @JoinColumn(name = "voteOptionID")
    VoteOption voteOption;

    @SuppressWarnings("unchecked")
    public static List<VoteUser> getAll(Integer votingID) {
        List<VoteUser> res = new ArrayList<>();
        SessionFactory factory = HibernateConfig.getSessionFactory();
        try (Session session = factory.openSession()) {
            Query query = session.createQuery("FROM VoteUser WHERE voting = " + votingID);
            res = query.list();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return res;
    }

    @SuppressWarnings("unchecked")
    public static List<VoteUser> getByUser(int userID, Integer votingID) {
        List<VoteUser> res = new ArrayList<>();
        SessionFactory factory = HibernateConfig.getSessionFactory();
        try (Session session = factory.openSession()) {
            Query query = session.createQuery("FROM VoteUser WHERE user = " + userID + " AND voting = " + votingID);
            res = query.list();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return res;
    }


    @SuppressWarnings("unchecked")
    public static VoteUser getByID(Integer publicID) {
        VoteUser res = null;
        SessionFactory factory = HibernateConfig.getSessionFactory();
        try (Session session = factory.openSession()) {
            res = session.get(VoteUser.class, publicID);
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
}
