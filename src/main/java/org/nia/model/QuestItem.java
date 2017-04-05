package org.nia.model;

import org.nia.db.ConnectionDB;
import org.nia.db.DatabaseManager;
import org.nia.logic.lists.items.EQuestItem;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Иван, 06.04.2017.
 */
public class QuestItem {

    private Integer publicID;
    private Quest quest;
    private EQuestItem questItem;
    private int itemCount;


    public void save() {
        try {
            ConnectionDB connectionDB = DatabaseManager.getInstance().getConnectionDB();
            if (publicID != null) {
                PreparedStatement preparedStatement = connectionDB.getPreparedStatement("update cwt_QuestItem set questID = ?" +
                        ", questItem = ?" +
                        ", itemCount = ?" +
                        " where PublicID = ?");
                preparedStatement.setInt(1, quest.getPublicID());
                preparedStatement.setString(2, questItem.name());
                preparedStatement.setInt(3, itemCount);
                preparedStatement.setInt(4, publicID);
                preparedStatement.execute();
            } else {
                PreparedStatement preparedStatement = connectionDB.getPreparedStatement("INSERT INTO cwt_QuestItem " +
                        "(questID, questItem, itemCount) " +
                        "VALUES (?, ?, ?)");
                preparedStatement.setInt(1, quest.getPublicID());
                preparedStatement.setString(2, questItem.name());
                preparedStatement.setInt(3, itemCount);
                preparedStatement.execute();
                preparedStatement = connectionDB.getPreparedStatement("select TOP 1 publicID from cwt_QuestItem where questID = ? and questItem = ? order by publicID desc");
                preparedStatement.setInt(1, quest.getPublicID());
                preparedStatement.setString(2, questItem.name());
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


    public static List<QuestItem> getAll(Quest quest) {
        List<QuestItem> res = new ArrayList<>();
        try {
            ConnectionDB connectionDB = DatabaseManager.getInstance().getConnectionDB();
            PreparedStatement preparedStatement = connectionDB.getPreparedStatement(
                    "select publicID, questItem, itemCount from cwt_QuestItem where questID = ?");
            preparedStatement.setInt(1, quest.getPublicID());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                QuestItem questItem = new QuestItem();
                questItem.publicID = resultSet.getInt(1);
                questItem.quest = quest;
                try {
                    questItem.questItem = EQuestItem.valueOf(resultSet.getString(2));
                } catch (Exception ignored) {
                }
                questItem.itemCount = resultSet.getInt(3);
                res.add(questItem);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    public void delete() {
        try {
            ConnectionDB connectionDB = DatabaseManager.getInstance().getConnectionDB();
            PreparedStatement preparedStatement = connectionDB.getPreparedStatement(
                    "delete from cwt_QuestItem where publicID = ?");
            preparedStatement.setInt(1, publicID);
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
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

    public EQuestItem getQuestItem() {
        return questItem;
    }

    public void setQuestItem(EQuestItem questItem) {
        this.questItem = questItem;
    }

    public int getItemCount() {
        return itemCount;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }
}
