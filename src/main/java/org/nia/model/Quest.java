package org.nia.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.nia.db.HibernateConfig;
import org.nia.logic.quests.QuestsEnum;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author IANazarov
 */
@Entity
@Getter
@Setter
@Table(name = "cwt_Quest")
public class Quest {
    @Id
    @Column()
    @GeneratedValue
    private Integer publicID;
    @ManyToOne
    @JoinColumn(name = "userID", nullable = false)
    private User user;
    @Column(name = "questName")
    @Enumerated(EnumType.STRING)
    private QuestsEnum questEnum;
    @Column(nullable = false)
    private Date startTime;
    @Column()
    private Date eventTime;
    @Column()
    private Date returnTime;
    @Column(columnDefinition = "INT DEFAULT 0")
    private int goldEarned = 0;

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

    public static Quest getCurrent(User user) {
        Quest res = null;
        SessionFactory factory = HibernateConfig.getSessionFactory();
        try (Session session = factory.openSession()) {
            Query query = session.createQuery("FROM Quest WHERE returnTime is null and user = " + user.getUserID());
            List list = query.list();
            if (!list.isEmpty()) {
                res = (Quest) list.get(0);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return res;
    }

    @SuppressWarnings("unchecked")
    public static Quest getRandomActive(QuestsEnum questsEnum) {
        List<Quest> qList = new ArrayList<>();
        SessionFactory factory = HibernateConfig.getSessionFactory();
        try (Session session = factory.openSession()) {
            Query query = session.createQuery("FROM Quest WHERE returnTime is null and eventTime > current_date and questName = " + questsEnum.name());
            qList = query.list();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        Quest res = null;
        if (!qList.isEmpty()) {
            res = qList.get(new Random().nextInt(qList.size()));
        }
        return res;
    }

    public static Quest getByID(int publicID) {
        Quest res = null;
        SessionFactory factory = HibernateConfig.getSessionFactory();
        try (Session session = factory.openSession()) {
            res = session.get(Quest.class, publicID);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return res;
    }

    public int getReward() {
        int START_SUM = 1;
        List<QuestEvent> all = QuestEvent.getAll(this);
        int sum = all.stream().mapToInt(e -> {
            if (e.getWin()) {
                return e.getIQuestEvent().getReward();
            } else {
                return -e.getIQuestEvent().getReward();
            }
        }).sum();
        Date returnTime = getReturnTime();
        Date startTime = getStartTime();
        int duration = (int) TimeUnit.MINUTES.convert(returnTime.getTime() - startTime.getTime(), TimeUnit.MILLISECONDS);
        if (duration > 10) {
            int progressInterval = duration / 10;
            sum += START_SUM * progressInterval;
        }
        if (sum < 0) {
            sum = 0;
        }
        return sum;
    }
}
