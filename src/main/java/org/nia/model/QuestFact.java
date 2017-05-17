package org.nia.model;

import org.nia.db.ConnectionDB;
import org.nia.db.DatabaseManager;
import org.nia.logic.lists.facts.EQuestFact;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Иван, 06.04.2017.
 */
public class QuestFact {
    private Integer publicID;
    private Quest quest;
    private EQuestFact questFact;


    public void save() {
        try {
            ConnectionDB connectionDB = DatabaseManager.getInstance().getConnectionDB();
            if (publicID != null) {
                PreparedStatement preparedStatement = connectionDB.getPreparedStatement("update cwt_QuestFact set questID = ?" +
                        ", questFact = ?" +
                        " where PublicID = ?");
                preparedStatement.setInt(1, quest.getPublicID());
                preparedStatement.setString(2, questFact.name());
                preparedStatement.setInt(3, publicID);
                preparedStatement.execute();
            } else {
                PreparedStatement preparedStatement = connectionDB.getPreparedStatement("INSERT INTO cwt_QuestFact " +
                        "(questID, questFact) " +
                        "VALUES (?, ?)");
                preparedStatement.setInt(1, quest.getPublicID());
                preparedStatement.setString(2, questFact.name());
                preparedStatement.execute();
                preparedStatement = connectionDB.getPreparedStatement("select publicID from cwt_QuestFact where questID = ? and questFact = ? order by publicID desc limit 1");
                preparedStatement.setInt(1, quest.getPublicID());
                preparedStatement.setString(2, questFact.name());
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

    public void delete() {
        try {
            ConnectionDB connectionDB = DatabaseManager.getInstance().getConnectionDB();
            PreparedStatement preparedStatement = connectionDB.getPreparedStatement(
                    "delete from cwt_QuestFact where publicID = ?");
            preparedStatement.setInt(1, publicID);
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<QuestFact> getAll(Quest quest) {
        List<QuestFact> res = new ArrayList<>();
        try {
            ConnectionDB connectionDB = DatabaseManager.getInstance().getConnectionDB();
            PreparedStatement preparedStatement = connectionDB.getPreparedStatement(
                    "select publicID, questFact from cwt_QuestFact where questID = ?");
            preparedStatement.setInt(1, quest.getPublicID());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                QuestFact fact = new QuestFact();
                fact.publicID = resultSet.getInt(1);
                fact.quest = quest;
                try {
                    fact.questFact = EQuestFact.valueOf(resultSet.getString(2));
                } catch (Exception ignored) {
                }
                res.add(fact);
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

    public EQuestFact getQuestFact() {
        return questFact;
    }

    public void setQuestFact(EQuestFact questFact) {
        this.questFact = questFact;
    }
}
