package org.nia.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
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
public class VoteUser extends AbstractEntity {
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

    public static List<VoteUser> getAll(Integer votingID) {
        List<VoteUser> res = new ArrayList<>();
        SessionFactory factory = HibernateConfig.getSessionFactory();
        try (Session session = factory.openSession()) {
            Query<VoteUser> query = session.createQuery("FROM VoteUser WHERE voting.publicID = " + votingID, VoteUser.class);
            res = query.list();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return res;
    }

    static List<VoteUser> getByUser(int userID, Integer votingID) {
        List<VoteUser> res = new ArrayList<>();
        SessionFactory factory = HibernateConfig.getSessionFactory();
        try (Session session = factory.openSession()) {
            Query<VoteUser> query = session.createQuery("FROM VoteUser WHERE user.userID = " + userID + " AND voting.publicID = " + votingID, VoteUser.class);
            res = query.list();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return res;
    }
}
