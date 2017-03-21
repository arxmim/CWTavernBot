package org.nia.model;

import org.nia.db.ConnectionDB;
import org.nia.db.DatabaseManager;
import org.nia.logic.quests.IQuestEvent;
import org.nia.logic.quests.IQuestStep;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author IANazarov
 */
public class QuestEvent {
    private Integer publicID;
    private Quest quest;
    private IQuestEvent iQuestEvent;
    private Date eventTime;
    private IQuestStep step;
    private Boolean win;

    public void save() {
        try {
            ConnectionDB connectionDB = DatabaseManager.getInstance().getConnectionDB();
            if (publicID != null) {
                PreparedStatement preparedStatement = connectionDB.getPreparedStatement("update cwt_QuestEvent set questID = ?" +
                        ", eventName = ?" +
                        ", eventStep = ?" +
                        ", eventTime = ?" +
                        ", win = ?" +
                        " where PublicID = ?");
                preparedStatement.setInt(1, quest.getPublicID());
                preparedStatement.setString(2, iQuestEvent.getName());
                preparedStatement.setString(3, step.getName());
                preparedStatement.setTimestamp(4, new Timestamp(eventTime.getTime()));
                preparedStatement.setBoolean(5, win);
                preparedStatement.setInt(6, publicID);
                preparedStatement.execute();
            } else {
                PreparedStatement preparedStatement = connectionDB.getPreparedStatement("INSERT INTO cwt_QuestEvent " +
                        "(questID, eventName, eventStep, eventTime, win) " +
                        "VALUES (?, ?, ?, ?, ?)");
                preparedStatement.setInt(1, quest.getPublicID());
                preparedStatement.setString(2, iQuestEvent.getName());
                preparedStatement.setString(3, step.getName());
                preparedStatement.setTimestamp(4, new Timestamp(eventTime.getTime()));
                preparedStatement.setBoolean(5, win);
                preparedStatement.execute();
                preparedStatement = connectionDB.getPreparedStatement("select publicID from cwt_QuestEvent where questID = ? and eventTime = ?");
                preparedStatement.setInt(1, quest.getPublicID());
                preparedStatement.setTimestamp(2, new Timestamp(eventTime.getTime()));
                ResultSet resultSet = preparedStatement.executeQuery();
                resultSet.next();
                publicID = resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static QuestEvent getCurrent(Quest quest) {
        QuestEvent res = null;
        try {
            ConnectionDB connectionDB = DatabaseManager.getInstance().getConnectionDB();
            PreparedStatement preparedStatement = connectionDB.getPreparedStatement(
                    "select publicID, eventName, eventStep, eventTime from cwt_QuestEvent where questID = ? and win is null");
            preparedStatement.setInt(1, quest.getPublicID());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                res = new QuestEvent();
                res.publicID = resultSet.getInt(1);
                res.quest = quest;
                try {
                    res.iQuestEvent = quest.getQuestEnum().getIQuest().getEvent(resultSet.getString(2));
                } catch (Exception ignored) {
                }
                try {
                    res.step = res.iQuestEvent.getQuestStep(resultSet.getString(3));
                } catch (Exception ignored) {
                }
                res.eventTime = resultSet.getTimestamp(4);
                res.win = null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    public static List<QuestEvent> getAll(Quest quest) {
        List<QuestEvent> res = new ArrayList<>();
        try {
            ConnectionDB connectionDB = DatabaseManager.getInstance().getConnectionDB();
            PreparedStatement preparedStatement = connectionDB.getPreparedStatement(
                    "select publicID, eventName, eventStep, eventTime, win from cwt_QuestEvent where questID = ?");
            preparedStatement.setInt(1, quest.getPublicID());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                QuestEvent event = new QuestEvent();
                event.publicID = resultSet.getInt(1);
                event.quest = quest;
                try {
                    event.iQuestEvent = quest.getQuestEnum().getIQuest().getEvent(resultSet.getString(2));
                } catch (Exception ignored) {
                }
                try {
                    event.step = event.iQuestEvent.getQuestStep(resultSet.getString(3));
                } catch (Exception ignored) {
                }
                event.eventTime = resultSet.getTimestamp(4);
                event.win =  resultSet.getBoolean(5);
                res.add(event);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    public Integer getPublicID() {
        return publicID;
    }

    public void setPublicID(Integer publicID) {
        this.publicID = publicID;
    }

    public Quest getQuest() {
        return quest;
    }

    public void setQuest(Quest quest) {
        this.quest = quest;
    }

    public IQuestEvent getIQuestEvent() {
        return iQuestEvent;
    }

    public void setIQuestEvent(IQuestEvent iQuestEvent) {
        this.iQuestEvent = iQuestEvent;
    }

    public Date getEventTime() {
        return eventTime;
    }

    public void setEventTime(Date eventTime) {
        this.eventTime = eventTime;
    }

    public IQuestStep getStep() {
        return step;
    }

    public void setStep(IQuestStep step) {
        this.step = step;
    }

    public Boolean getWin() {
        return win;
    }

    public void setWin(Boolean win) {
        this.win = win;
    }
}
