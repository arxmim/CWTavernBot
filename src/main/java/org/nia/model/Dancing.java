package org.nia.model;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.time.DateUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.nia.bots.CWTavernBot;
import org.nia.db.HibernateConfig;
import org.nia.logic.lists.DanceActionList;
import org.nia.logic.lists.DanceStep;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import javax.persistence.*;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author IANazarov
 */
@Entity
@Getter
@Setter
@Table(name = "cwt_dancing")
public class Dancing extends AbstractEntity {
    @Column
    @Id
    @GeneratedValue
    Integer publicID;
    @ManyToOne
    @JoinColumn(name = "firstDancerID", nullable = false)
    User firstDancer;
    @ManyToOne
    @JoinColumn(name = "secondDancerID", nullable = false)
    User secondDancer;
    @Enumerated(EnumType.STRING)
    DanceStep currentStep;
    @Enumerated(EnumType.STRING)
    DanceActionList lastDanceAction;
    @Column
    Boolean lastActionFromFirst;
    @Column
    Date nextStepTime;
    @Column
    Boolean completed;

    public static Dancing getCurrent(User user) {
        SessionFactory sessionFactory = HibernateConfig.getSessionFactory();
        try (Session session = sessionFactory.openSession()) {
            Query<Dancing> query = session.createQuery("FROM Dancing where (firstDancer.userID = :uID " +
                    "OR secondDancer.userID = :uID) and completed is NULL", Dancing.class);
            query.setParameter("uID", user.getUserID());
            return query.uniqueResult();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    public static List<Dancing> getAllCurrent() {
        List<Dancing> res = Collections.emptyList();
        SessionFactory sessionFactory = HibernateConfig.getSessionFactory();
        try (Session session = sessionFactory.openSession()) {
            Query<Dancing> query = session.createQuery("FROM Dancing where completed is NULL", Dancing.class);
            res = query.list();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return res;
    }

    public void process() {
        if (nextStepTime == null) {
            this.nextStepTime = DateUtils.addSeconds(new Date(), currentStep.getStepDuration());
            this.save();
            SendMessage message = currentStep.getInitialSendMessage(this);
            try {
                CWTavernBot.INSTANCE.sendMessage(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        } else if (nextStepTime.before(new Date())) {
            if (getNextAction() != null) {
                this.completed = false;
                this.save();
                this.firstDancer.setDanceWithUserID(null);
                this.secondDancer.setDanceWithUserID(null);
                this.firstDancer.save();
                this.secondDancer.save();
                SendMessage message = this.currentStep.getTimedFailMessage(this);
                try {
                    CWTavernBot.INSTANCE.sendMessage(message);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } else if (currentStep.hasNextStep(this)){
                this.lastDanceAction = null;
                this.lastActionFromFirst = null;
                this.currentStep = currentStep.nextStep(this);
                this.nextStepTime = DateUtils.addSeconds(new Date(), currentStep.getStepDuration());
                this.save();
                SendMessage message = currentStep.getInitialSendMessage(this);
                try {
                    CWTavernBot.INSTANCE.sendMessage(message);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } else {
                this.completed = true;
                this.save();
                this.firstDancer.setDanceWithUserID(null);
                this.secondDancer.setDanceWithUserID(null);
                this.firstDancer.save();
                this.secondDancer.save();
                SendMessage message = this.currentStep.getSuccessMessage(this);
                try {
                    CWTavernBot.INSTANCE.sendMessage(message);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public DanceStep.DanceAction getNextAction() {
        DanceStep.DanceAction danceAction;
        if (lastDanceAction == null || lastActionFromFirst == null) {
            danceAction = null;
        } else {
            danceAction = new DanceStep.DanceAction(lastDanceAction, lastActionFromFirst);
        }
        return currentStep.getNextAfter(danceAction);
    }


}
