package org.nia.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.nia.db.HibernateConfig;
import org.nia.logic.quests.IQuestEvent;
import org.nia.logic.quests.IQuestStep;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author IANazarov
 */
@Entity
@Getter
@Setter
@Table(name = "cwt_QuestEvent")
public class QuestEvent extends AbstractEntity {
    @Id
    @Column()
    @GeneratedValue
    private Integer publicID;
    @ManyToOne
    @JoinColumn(name = "userID", nullable = false)
    private Quest quest;
    @Column
    private Date eventTime;
    @Column(columnDefinition = "INT DEFAULT 80")
    private int winChance = 80;
    @Column
    private Boolean win;
    @Column
    private Integer linkedQuestEventID;
    @Column
    private String eventName;
    @Column
    private String eventStep;

    public static QuestEvent getCurrent(Quest quest) {
        QuestEvent res = null;
        SessionFactory factory = HibernateConfig.getSessionFactory();
        try (Session session = factory.openSession()) {
            Query<QuestEvent> query = session.createQuery("FROM QuestEvent WHERE win is null and quest.publicID = " + quest.getPublicID(), QuestEvent.class);
            List<QuestEvent> list = query.list();
            if (!list.isEmpty()) {
                res = list.get(0);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return res;
    }

    public static List<QuestEvent> getAll(Quest quest) {
        List<QuestEvent> res = new ArrayList<>();
        SessionFactory factory = HibernateConfig.getSessionFactory();
        try (Session session = factory.openSession()) {
            Query<QuestEvent> query = session.createQuery("FROM QuestEvent WHERE quest.publicID = " + quest.getPublicID(), QuestEvent.class);
            res = query.list();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return res;
    }

    public void setWinChance(int winChance) {
        this.winChance = winChance;
    }

    public void incWinChance(int delta) {
        this.winChance += delta;
    }

    public QuestEvent getLinkedQuestEvent() {
        if (linkedQuestEventID != null) {
            return QuestEvent.getByID(QuestEvent.class, linkedQuestEventID);
        }
        return null;
    }

    public void setLinkedQuestEvent(QuestEvent linkedQuest) {
        if (linkedQuest == null) {
            this.linkedQuestEventID = null;
        } else {
            this.linkedQuestEventID = linkedQuest.publicID;
        }
    }

    public IQuestEvent getIQuestEvent() {
        return quest.getQuestEnum().getIQuest().getEvent(eventName);
    }

    public void setIQuestEvent(IQuestEvent event) {
        this.eventName = event.getName();
    }

    public IQuestStep getStep() {
        return getIQuestEvent().getQuestStep(eventStep);
    }

    public void setStep(IQuestStep step) {
        this.eventStep = step.getName();
    }
}
