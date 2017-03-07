package org.nia.model;

import org.nia.db.ConnectionDB;
import org.nia.db.DatabaseManager;
import org.telegram.telegrambots.api.objects.Message;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author IANazarov
 */
public class User {
    private int userID;
    private String nick;
    private String name;
    private boolean isAdmin;
    private int alkoCount;
    private int drinkedToday;

    public static User getFromMessage(Message message) {
        User res = null;
        try {
            int userID = message.getFrom().getId();
            ConnectionDB connectionDB = DatabaseManager.getInstance().getConnectionDB();
            PreparedStatement preparedStatement = connectionDB.getPreparedStatement("Select nick, name, isAdmin, alkoCount, drinkedToday from cwt_User where UserID = ?");
            preparedStatement.setInt(1, userID);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                res = new User();
                res.userID = userID;
                res.nick = resultSet.getString(1);
                res.name = resultSet.getString(2);
                res.isAdmin = resultSet.getBoolean(3);
                res.alkoCount = resultSet.getInt(4);
                res.drinkedToday = resultSet.getInt(5);
            } else {
                res = new User();
                org.telegram.telegrambots.api.objects.User from = message.getFrom();
                res.nick = from.getUserName();
                res.name = from.getFirstName() + " " + from.getLastName();
                res.userID = userID;
                res.alkoCount = 0;
                res.isAdmin = false;
                res.drinkedToday = 0;
                res.save();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }
    public static User getByNick(String nick) {
        User res = null;
        try {
            ConnectionDB connectionDB = DatabaseManager.getInstance().getConnectionDB();
            PreparedStatement preparedStatement = connectionDB.getPreparedStatement("Select userID, name, isAdmin, alkoCount, drinkedToday from cwt_User where nick = ?");
            preparedStatement.setString(1, nick);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                res = new User();
                res.nick = nick;
                res.userID = resultSet.getInt(1);
                res.name = resultSet.getString(2);
                res.isAdmin = resultSet.getBoolean(3);
                res.alkoCount = resultSet.getInt(4);
                res.drinkedToday = resultSet.getInt(5);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    public boolean save() {
        boolean res = false;
        try {
            ConnectionDB connectionDB = DatabaseManager.getInstance().getConnectionDB();
            PreparedStatement preparedStatement = connectionDB.getPreparedStatement("Select userID from cwt_User where UserID = ?");
            preparedStatement.setInt(1, userID);
            ResultSet resultSet = preparedStatement.executeQuery();
            boolean exists = resultSet.next();
            if (exists) {
                preparedStatement = connectionDB.getPreparedStatement("update cwt_User set nick = ?" +
                        ", name = ?" +
                        ", isAdmin = ?" +
                        ", alkoCount = ?" +
                        ", drinkedToday = ?" +
                        " where UserID = ?");
                preparedStatement.setString(1, nick);
                preparedStatement.setString(2, name);
                preparedStatement.setBoolean(3, isAdmin);
                preparedStatement.setInt(4, alkoCount);
                preparedStatement.setInt(5, drinkedToday);
                preparedStatement.setInt(6, userID);
                preparedStatement.execute();
            } else {
                preparedStatement = connectionDB.getPreparedStatement("INSERT INTO cwt_User (UserID, nick, name, isAdmin, alkoCount, drinkedToday) " +
                        "VALUES (?, ?, ?, ?, ?, ?)");
                preparedStatement.setInt(1, userID);
                preparedStatement.setString(2, nick);
                preparedStatement.setString(3, name);
                preparedStatement.setBoolean(4, isAdmin);
                preparedStatement.setInt(5, alkoCount);
                preparedStatement.setInt(6, drinkedToday);
                preparedStatement.execute();
            }
            res = true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    public void setIsAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public boolean IsAdmin() {
        return isAdmin;
    }

    public String getNick() {
        return nick;
    }
}
