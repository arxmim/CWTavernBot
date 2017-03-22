package org.nia.model;

import org.nia.db.ConnectionDB;
import org.nia.db.DatabaseManager;
import org.nia.logic.quests.QuestsEnum;

import java.sql.*;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author IANazarov
 */
public class Quest {
    private Integer publicID;
    private User user;
    private QuestsEnum quest;
    private Date startTime;
    private Date eventTime;
    private Date returnTime;
    private int goldEarned;

    public void save() {
        try {
            ConnectionDB connectionDB = DatabaseManager.getInstance().getConnectionDB();
            if (publicID != null) {
                PreparedStatement preparedStatement = connectionDB.getPreparedStatement("update cwt_Quest set userID = ?" +
                        ", questName = ?" +
                        ", startTime = ?" +
                        ", eventTime = ?" +
                        ", returnTime = ?" +
                        ", goldEarned = ?" +
                        " where PublicID = ?");
                preparedStatement.setInt(1, user.getUserID());
                preparedStatement.setString(2, quest.name());
                preparedStatement.setTimestamp(3, new Timestamp(startTime.getTime()));
                if (eventTime != null) {
                    preparedStatement.setTimestamp(4, new Timestamp(eventTime.getTime()));
                } else {
                    preparedStatement.setNull(4, Types.TIMESTAMP);
                }
                if (returnTime != null) {
                    preparedStatement.setTimestamp(5, new Timestamp(returnTime.getTime()));
                } else {
                    preparedStatement.setNull(5, Types.TIMESTAMP);
                }
                preparedStatement.setInt(6, goldEarned);
                preparedStatement.setInt(7, publicID);
                preparedStatement.execute();
            } else {
                PreparedStatement preparedStatement = connectionDB.getPreparedStatement("INSERT INTO cwt_Quest " +
                        "(userID, questName, startTime, eventTime, returnTime" +
                        ", goldEarned) " +
                        "VALUES (?, ?, ?, ?, ?" +
                        ", ?)");
                preparedStatement.setInt(1, user.getUserID());
                preparedStatement.setString(2, quest.name());
                preparedStatement.setTimestamp(3, new Timestamp(startTime.getTime()));
                if (eventTime != null) {
                    preparedStatement.setTimestamp(4, new Timestamp(eventTime.getTime()));
                } else {
                    preparedStatement.setNull(4, Types.TIMESTAMP);
                }
                if (returnTime != null) {
                    preparedStatement.setTimestamp(5, new Timestamp(returnTime.getTime()));
                } else {
                    preparedStatement.setNull(5, Types.TIMESTAMP);
                }
                preparedStatement.setInt(6, goldEarned);
                preparedStatement.execute();
                preparedStatement = connectionDB.getPreparedStatement("select publicID from cwt_Quest where userID = ? and questName = ?" +
                        " and startTime = ?");
                preparedStatement.setInt(1, user.getUserID());
                preparedStatement.setString(2, quest.name());
                preparedStatement.setTimestamp(3, new Timestamp(startTime.getTime()));
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    publicID = resultSet.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Quest getCurrent(User user) {
        Quest res = null;
        try {
            ConnectionDB connectionDB = DatabaseManager.getInstance().getConnectionDB();
            PreparedStatement preparedStatement = connectionDB.getPreparedStatement(
                    "select publicID, questName, startTime, eventTime, goldEarned from cwt_Quest where userID = ? and returnTime is null");
            preparedStatement.setInt(1, user.getUserID());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                res = new Quest();
                res.publicID = resultSet.getInt(1);
                res.user = user;
                try {
                    res.quest = QuestsEnum.valueOf(resultSet.getString(2));
                } catch (Exception ignored) {
                }
                res.startTime = resultSet.getTimestamp(3);
                res.eventTime = resultSet.getTimestamp(4);
                res.returnTime = null;
                res.goldEarned = resultSet.getInt(5);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    Integer getPublicID() {
        return publicID;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public QuestsEnum getQuestEnum() {
        return quest;
    }

    public void setQuest(QuestsEnum quest) {
        this.quest = quest;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEventTime() {
        return eventTime;
    }

    public void setEventTime(Date eventTime) {
        this.eventTime = eventTime;
    }

    public void setGoldEarned(int goldEarned) {
        this.goldEarned = goldEarned;
    }

    private Date getReturnTime() {
        return returnTime;
    }

    public void setReturnTime(Date returnTime) {
        this.returnTime = returnTime;
    }

    public int getReward() {
        int INCREMENT = 1;
        int STEP_HOURS = 1;
        int START_SUM = 1;
        int MAX_PROGRESS_HOURS = 4;
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
        int duration = (int) TimeUnit.HOURS.convert(returnTime.getTime() - startTime.getTime(), TimeUnit.MILLISECONDS);
        if (duration > 0) {
            if (duration > MAX_PROGRESS_HOURS) {
                int progressInterval = MAX_PROGRESS_HOURS / STEP_HOURS;
                int constantInterval = (duration - MAX_PROGRESS_HOURS) / STEP_HOURS;
                int finalProgressElement = START_SUM + (progressInterval - 1) * INCREMENT;
                sum += (START_SUM + finalProgressElement) / 2 * progressInterval;
                sum += constantInterval * finalProgressElement;
            } else {
                int progressInterval = duration / STEP_HOURS;
                int finalProgressElement = START_SUM + (progressInterval - 1) * INCREMENT;
                sum += (START_SUM + finalProgressElement) / 2 * progressInterval;
            }
        }
        if (sum < 0) {
            sum = 0;
        }
        return sum;
    }
}