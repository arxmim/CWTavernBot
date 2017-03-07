package org.nia.model;

import org.nia.db.ConnectionDB;
import org.nia.db.DatabaseManager;
import org.nia.strings.Emoji;
import org.telegram.telegrambots.api.objects.Message;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Иван, 24.02.2017.
 */
public class Team {
    private String name;

    public Team(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static String getStat(Message message) {
        int userID = message.getFrom().getId();
        try {
            ConnectionDB connectionDB = DatabaseManager.getInstance().getConnectionDB();
            PreparedStatement preparedStatement = connectionDB.getPreparedStatement("Select Team from cwo_Profile where UserID = ?");
            preparedStatement.setInt(1, userID);
            ResultSet resultSet = preparedStatement.executeQuery();
            String team = "Без отряда";
            if (resultSet.next()) {
                team = resultSet.getString(1);
            }
            if (team.equals("Без отряда")) {
                return "У вас нет отряда, сначала найдите себе отряд!";
            }
            preparedStatement = connectionDB.getPreparedStatement("Select sum(atk), sum(def), avg(lvl), count(1) from cwo_Profile where Team = ? group by Team");
            preparedStatement.setString(1, team);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return "Отряд: " + team
                        + "\n" + Emoji.ATK + "Суммарная атака:" + resultSet.getInt(1)
                        + "\n" + Emoji.DEF + "Суммарная защита:" + resultSet.getInt(2)
                        + "\nСредний уровень:" + resultSet.getInt(3)
                        + "\nЧисло бойцов:" + resultSet.getInt(4);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Статистика временно недоступна";
    }

    public static Team getTeam(Message message) {
        Team res = null;
        int userID = message.getFrom().getId();
        try {
            ConnectionDB connectionDB = DatabaseManager.getInstance().getConnectionDB();
            PreparedStatement preparedStatement = connectionDB.getPreparedStatement("Select Team from cwo_Profile where UserID = ?");
            preparedStatement.setInt(1, userID);
            ResultSet resultSet = preparedStatement.executeQuery();
            String teamName = "Без отряда";
            if (resultSet.next()) {
                teamName = resultSet.getString(1);
            }
            if (teamName.equals("Без отряда")) {
                return null;
            }
            res = new Team(teamName);
//            preparedStatement = connectionDB.getPreparedStatement("Select name from cwo_Profile where Team = ? group by Team");
//            preparedStatement.setString(1, teamName);
//            resultSet = preparedStatement.executeQuery();
//            while (resultSet.next()) {
//                res.addProfile(Profile.get(resultSet.getString(1)));
//            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }
}
