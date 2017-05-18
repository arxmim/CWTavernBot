package org.nia.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.nia.db.HibernateConfig;
import org.nia.logic.lists.DrinkType;

import javax.persistence.*;
import java.util.Collections;
import java.util.List;

/**
 * @author Иван, 11.03.2017.
 */
@Entity
@Getter
@Setter
@Table(name = "cwt_DrinkPrefs")
public class DrinkPref {
    @Id
    @Column()
    @GeneratedValue
    private int publicID;
    @ManyToOne
    @JoinColumn(name = "userID")
    private User user;
    @Enumerated(EnumType.STRING)
    private DrinkType drinkType;
    @Column
    private int toDrink;
    @Column
    private int toThrow;
    @Column
    private int toBeThrown;

    @SuppressWarnings("unchecked")
    static List<DrinkPref> getByUser(User usr) {
        List<DrinkPref> res = Collections.emptyList();
        int userID = usr.getUserID();
        SessionFactory factory = HibernateConfig.getSessionFactory();
        try (Session session = factory.openSession()) {
            Query query = session.createQuery("FROM DrinkPref WHERE User = " + userID);
            res = query.list();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return res;
    }

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
