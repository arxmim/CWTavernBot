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
@Table(name = "cwt_VoteOption")
public class VoteOption extends AbstractEntity {
    @Column
    @Id
    @GeneratedValue
    Integer publicID;
    @Column
    String text;
    @ManyToOne
    @JoinColumn(name = "votingID")
    Voting voting;

    public static List<VoteOption> getAll(Integer votingID) {
        List<VoteOption> res = new ArrayList<>();
        SessionFactory factory = HibernateConfig.getSessionFactory();
        try (Session session = factory.openSession()) {
            Query<VoteOption> query = session.createQuery("FROM VoteOption WHERE voting.publicID = " + votingID, VoteOption.class);
            res = query.list();
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
