package org.nia.model;

import org.nia.db.ConnectionDB;
import org.nia.db.DatabaseManager;
import org.nia.logic.quests.IQuestEvent;
import org.nia.logic.quests.IQuestStep;

import java.sql.*;
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
    private int winChance = 80;
    private IQuestStep step;
    private Boolean win;
    private Integer linkedQuestEventID;
    private QuestEvent linkedQuestEvent;

    public void save() {
        try {
            ConnectionDB connectionDB = DatabaseManager.getInstance().getConnectionDB();
            if (publicID != null) {
                PreparedStatement preparedStatement = connectionDB.getPreparedStatement("update cwt_QuestEvent set questID = ?" +
                        ", eventName = ?" +
                        ", eventStep = ?" +
                        ", eventTime = ?" +
                        ", win = ?" +
                        ", winChance = ?" +
                        ", linkedQuestEventID = ?" +
                        " where PublicID = ?");
                preparedStatement.setInt(1, quest.getPublicID());
                preparedStatement.setString(2, iQuestEvent.getName());
                preparedStatement.setString(3, step.getName());
                preparedStatement.setTimestamp(4, new Timestamp(eventTime.getTime()));
                if (win != null) {
                    preparedStatement.setBoolean(5, win);
                } else {
                    preparedStatement.setNull(5, Types.BIT);
                }
                preparedStatement.setInt(6, winChance);
                if (linkedQuestEventID != null) {
                    preparedStatement.setInt(7, linkedQuestEventID);
                } else {
                    preparedStatement.setNull(7, Types.INTEGER);
                }
                preparedStatement.setInt(8, publicID);
                preparedStatement.execute();
            } else {
                PreparedStatement preparedStatement = connectionDB.getPreparedStatement("INSERT INTO cwt_QuestEvent " +
                        "(questID, eventName, eventStep, eventTime, win" +
                        ", winChance, linkedQuestEventID) " +
                        "VALUES (?, ?, ?, ?, ?" +
                        ", ?, ?)");
                preparedStatement.setInt(1, quest.getPublicID());
                preparedStatement.setString(2, iQuestEvent.getName());
                preparedStatement.setString(3, step.getName());
                preparedStatement.setTimestamp(4, new Timestamp(eventTime.getTime()));
                if (win != null) {
                    preparedStatement.setBoolean(5, win);
                } else {
                    preparedStatement.setNull(5, Types.BIT);
                }
                preparedStatement.setInt(6, winChance);
                if (linkedQuestEventID != null) {
                    preparedStatement.setInt(7, linkedQuestEventID);
                } else {
                    preparedStatement.setNull(7, Types.INTEGER);
                }
                preparedStatement.execute();
                preparedStatement = connectionDB.getPreparedStatement("select publicID from cwt_QuestEvent where questID = ? order by publicID desc limit 1");
                preparedStatement.setInt(1, quest.getPublicID());
                ResultSet resultSet = preparedStatement.executeQuery();
                while (!resultSet.next()) {
                    resultSet = preparedStatement.executeQuery();
                }
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
                    "select publicID, eventName, eventStep, eventTime, winChance" +
                            ", linkedQuestEventID from cwt_QuestEvent where questID = ? and win is null");
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
                res.winChance = resultSet.getInt(5);
                res.linkedQuestEventID = resultSet.getInt(6);
                res.linkedQuestEventID = resultSet.wasNull() ? null : res.linkedQuestEventID;
                res.win = null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    public static QuestEvent getByID(Integer publicID) {
        QuestEvent res = null;
        try {
            ConnectionDB connectionDB = DatabaseManager.getInstance().getConnectionDB();
            PreparedStatement preparedStatement = connectionDB.getPreparedStatement(
                    "select questID, eventName, eventStep, eventTime, win" +
                            ", winChance, linkedQuestEventID from cwt_QuestEvent where publicID = ?");
            preparedStatement.setInt(1, publicID);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                res = new QuestEvent();
                res.publicID = publicID;
                res.quest = Quest.getByID(resultSet.getInt(1));
                try {
                    res.iQuestEvent = res.quest.getQuestEnum().getIQuest().getEvent(resultSet.getString(2));
                } catch (Exception ignored) {
                }
                try {
                    res.step = res.iQuestEvent.getQuestStep(resultSet.getString(3));
                } catch (Exception ignored) {
                }
                res.eventTime = resultSet.getTimestamp(4);
                res.win = resultSet.getBoolean(5);
                res.win = resultSet.wasNull() ? null : res.win;
                res.winChance = resultSet.getInt(6);
                res.linkedQuestEventID = resultSet.getInt(7);
                res.linkedQuestEventID = resultSet.wasNull() ? null : res.linkedQuestEventID;
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
                    "select publicID, eventName, eventStep, eventTime, win" +
                            ", winChance, linkedQuestEventID from cwt_QuestEvent where questID = ?");
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
                event.win = resultSet.getBoolean(5);
                event.win = resultSet.wasNull() ? null : event.win;
                event.winChance = resultSet.getInt(6);
                event.linkedQuestEventID = resultSet.getInt(7);
                event.linkedQuestEventID = resultSet.wasNull() ? null : event.linkedQuestEventID;
                res.add(event);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    public static int getCount(Quest quest) {
        int res = 0;
        try {
            ConnectionDB connectionDB = DatabaseManager.getInstance().getConnectionDB();
            PreparedStatement preparedStatement = connectionDB.getPreparedStatement(
                    "select count(1) from cwt_QuestEvent where questID = ?");
            preparedStatement.setInt(1, quest.getPublicID());
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            res = resultSet.getInt(1);
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


    public int getWinChance() {
        return winChance;
    }

    public void setWinChance(int winChance) {
        this.winChance = winChance;
    }
    public void incWinChance(int delta) {
        this.winChance += delta;
    }

    public QuestEvent getLinkedQuestEvent() {
        if (linkedQuestEvent == null && linkedQuestEventID != null) {
            linkedQuestEvent = QuestEvent.getByID(linkedQuestEventID);
        }
        return linkedQuestEvent;
    }

    public void setLinkedQuestEvent(QuestEvent linkedQuest) {
        if (linkedQuest == null) {
            this.linkedQuestEventID = null;
        } else {
            this.linkedQuestEventID = linkedQuest.publicID;
        }
        this.linkedQuestEvent = null;
    }
}
