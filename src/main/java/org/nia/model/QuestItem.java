package org.nia.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.nia.db.HibernateConfig;
import org.nia.logic.lists.items.EQuestItem;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Иван, 06.04.2017.
 */
@Entity(name = "cwt_QuestItem")
@Getter
@Setter
public class QuestItem {
    @Id
    @Column()
    @GeneratedValue
    private Integer publicID;
    @ManyToOne
    @JoinColumn(name = "questID")
    private Quest quest;
    @Enumerated(EnumType.STRING)
    private EQuestItem questItem;
    @Column
    private int itemCount;


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


    @SuppressWarnings("unchecked")
    public static List<QuestItem> getAll(Quest quest) {
        List<QuestItem> res = new ArrayList<>();
        SessionFactory factory = HibernateConfig.getSessionFactory();
        try (Session session = factory.openSession()) {
            Query query = session.createQuery("FROM cwt_QuestItem WHERE quest = " + quest.getPublicID());
            res = query.list();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return res;
    }

    public boolean delete() {
        boolean res = false;
        SessionFactory factory = HibernateConfig.getSessionFactory();
        try (Session session = factory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.delete(this);
            tx.commit();
            res = true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return res;
    }
}
