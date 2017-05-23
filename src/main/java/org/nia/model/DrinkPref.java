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

    public DrinkPref() {
    }

    private DrinkPref(User user, DrinkType drinkType, int toDrink, int toThrow, int toBeThrown) {
        this.user = user;
        this.drinkType = drinkType;
        this.toDrink = toDrink;
        this.toThrow = toThrow;
        this.toBeThrown = toBeThrown;
    }

    public static List<DrinkPref> getByUser(User usr) {
        List<DrinkPref> res = Collections.emptyList();
        SessionFactory factory = HibernateConfig.getSessionFactory();
        try (Session session = factory.openSession()) {
            Query<DrinkPref> query = session.createQuery("FROM DrinkPref WHERE user.userID = " + usr.getUserID(), DrinkPref.class);
            res = query.list();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return res;
    }

    static DrinkPref getByUserAndDrinkType(User usr, DrinkType drinkType) {
        DrinkPref res = null;
        SessionFactory factory = HibernateConfig.getSessionFactory();
        try (Session session = factory.openSession()) {
            Query<DrinkPref> query = session.createQuery("FROM DrinkPref WHERE user.userID = " + usr.getUserID() + " and drinkType=:drinkType", DrinkPref.class);
            query.setParameter("drinkType", drinkType);
            List<DrinkPref> list = query.list();
            if (!list.isEmpty()) {
                res = list.get(0);
            } else {
                res = new DrinkPref(usr, drinkType, 0, 0, 0);
            }
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

    public int getToDrinkNormalized() {
        return toDrink / 2;
    }

    void incToDrink(int plus) {
        this.toDrink += plus;
    }

    void incToThrow() {
        this.toThrow++;
    }

    void incToBeThrown() {
        this.toBeThrown++;
    }
}
