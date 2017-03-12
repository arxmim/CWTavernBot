package org.nia.model;

import org.apache.commons.lang3.StringUtils;
import org.nia.db.ConnectionDB;
import org.nia.db.DatabaseManager;
import org.nia.logic.DrinkType;
import org.telegram.telegrambots.api.objects.Message;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author IANazarov
 */
public class User {
    private int userID;
    private String nick;
    private String name;
    private Date lastDrinkTime;
    private DrinkType drinkType;
    private DrinkType wanted;
    private boolean isAdmin;
    private int alkoCount;
    private int drinkedToday;

    public static User getByID(int userID) {
        User res = null;
        try {
            ConnectionDB connectionDB = DatabaseManager.getInstance().getConnectionDB();
            PreparedStatement preparedStatement = connectionDB.getPreparedStatement("Select nick, name, isAdmin, alkoCount, lastDrinkTime, drinkedToday, drinkType, wanted from cwt_User where UserID = ?");
            preparedStatement.setInt(1, userID);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                res = new User();
                res.userID = userID;
                res.nick = resultSet.getString(1);
                res.name = resultSet.getString(2);
                res.isAdmin = resultSet.getBoolean(3);
                res.alkoCount = resultSet.getInt(4);
                res.lastDrinkTime = resultSet.getTimestamp(5);
                res.drinkedToday = resultSet.getInt(6);
                try {
                    res.drinkType = DrinkType.valueOf(resultSet.getString(7));
                } catch (Exception ignored) {
                }
                try {
                    res.wanted = DrinkType.valueOf(resultSet.getString(8));
                } catch (Exception ignored) {
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    public static User getFromMessage(Message message) {
        return getFromMessage(message.getFrom());
    }

    public static User getFromMessage(org.telegram.telegrambots.api.objects.User user) {
        int userID = user.getId();
        User res = getByID(userID);
        if (res == null) {
            res = new User();
            res.nick = user.getUserName();
            res.name = user.getFirstName();
            res.userID = userID;
            res.alkoCount = 0;
            res.isAdmin = false;
            res.drinkedToday = 0;
            res.save();
        }
        return res;
    }

    public static User getByNick(String nick) {
        User res = null;
        nick = nick.replace("@", "");
        try {
            ConnectionDB connectionDB = DatabaseManager.getInstance().getConnectionDB();
            PreparedStatement preparedStatement = connectionDB.getPreparedStatement("Select userID, name, isAdmin, alkoCount, lastDrinkTime, drinkedToday, drinkType, wanted from cwt_User where nick = ?");
            preparedStatement.setString(1, nick);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                res = new User();
                res.nick = nick;
                res.userID = resultSet.getInt(1);
                res.name = resultSet.getString(2);
                res.isAdmin = resultSet.getBoolean(3);
                res.alkoCount = resultSet.getInt(4);
                res.lastDrinkTime = resultSet.getTimestamp(5);
                res.drinkedToday = resultSet.getInt(6);
                try {
                    res.drinkType = DrinkType.valueOf(resultSet.getString(7));
                } catch (Exception ignored) {
                }
                try {
                    res.wanted = DrinkType.valueOf(resultSet.getString(8));
                } catch (Exception ignored) {
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    public static List<User> getAll() {
        List<User> res = new ArrayList<>();
        try {
            ConnectionDB connectionDB = DatabaseManager.getInstance().getConnectionDB();
            PreparedStatement preparedStatement = connectionDB.getPreparedStatement("Select userID, nick, name, isAdmin, alkoCount, lastDrinkTime, drinkedToday, drinkType, wanted from cwt_User");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                User user = new User();
                user.userID = resultSet.getInt(1);
                user.nick = resultSet.getString(2);
                user.name = resultSet.getString(3);
                user.isAdmin = resultSet.getBoolean(4);
                user.alkoCount = resultSet.getInt(5);
                user.lastDrinkTime = resultSet.getTimestamp(6);
                user.drinkedToday = resultSet.getInt(7);
                try {
                    user.drinkType = DrinkType.valueOf(resultSet.getString(8));
                } catch (Exception ignored) {
                }
                try {
                    user.wanted = DrinkType.valueOf(resultSet.getString(9));
                } catch (Exception ignored) {
                }
                res.add(user);

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
                        ", lastDrinkTime = ?" +
                        ", drinkedToday = ?" +
                        ", drinkType = ?" +
                        ", wanted = ?" +
                        " where UserID = ?");
                preparedStatement.setString(1, nick);
                preparedStatement.setString(2, name);
                preparedStatement.setBoolean(3, isAdmin);
                preparedStatement.setInt(4, alkoCount);
                if (lastDrinkTime != null) {
                    preparedStatement.setTimestamp(5, new Timestamp(lastDrinkTime.getTime()));
                } else {
                    preparedStatement.setNull(5, Types.TIMESTAMP);
                }
                preparedStatement.setInt(6, drinkedToday);
                if (drinkType != null) {
                    preparedStatement.setString(7, drinkType.name());
                } else {
                    preparedStatement.setNull(7, Types.VARCHAR);
                }
                if (wanted != null) {
                    preparedStatement.setString(8, wanted.name());
                } else {
                    preparedStatement.setNull(8, Types.VARCHAR);
                }
                preparedStatement.setInt(9, userID);
                preparedStatement.execute();
            } else {
                preparedStatement = connectionDB.getPreparedStatement("INSERT INTO cwt_User (UserID, nick, name, isAdmin, alkoCount, lastDrinkTime, drinkedToday, drinkType, wanted) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
                preparedStatement.setInt(1, userID);
                preparedStatement.setString(2, nick);
                preparedStatement.setString(3, name);
                preparedStatement.setBoolean(4, isAdmin);
                preparedStatement.setInt(5, alkoCount);
                if (lastDrinkTime != null) {
                    preparedStatement.setTimestamp(6, new Timestamp(lastDrinkTime.getTime()));
                } else {
                    preparedStatement.setNull(6, Types.TIMESTAMP);
                }
                preparedStatement.setInt(7, drinkedToday);
                if (drinkType != null) {
                    preparedStatement.setString(8, drinkType.name());
                } else {
                    preparedStatement.setNull(8, Types.VARCHAR);
                }
                if (wanted != null) {
                    preparedStatement.setString(9, wanted.name());
                } else {
                    preparedStatement.setNull(9, Types.VARCHAR);
                }
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

    public void setAlkoCount(int alkoCount) {
        this.alkoCount = alkoCount;
    }

    public int getAlkoCount() {
        return alkoCount;
    }

    @Override
    public String toString() {
        if (!StringUtils.isEmpty(nick)) {
            return "@" + nick;
        } else {
            return name;
        }
    }

    public void setDrinkedToday(int drinkedToday) {
        this.drinkedToday = drinkedToday;
    }

    public void setLastDrinkTime(Date lastDrinkTime) {
        this.lastDrinkTime = lastDrinkTime;
    }

    public Date getLastDrinkTime() {
        return lastDrinkTime;
    }

    public int getDrinkedToday() {
        return drinkedToday;
    }

    public DrinkType getDrinkType() {
        return drinkType;
    }

    public void setDrinkType(DrinkType drinkType) {
        this.drinkType = drinkType;
    }

    public static List<User> getTop() {
        List<User> res = new ArrayList<>();
        try {
            ConnectionDB connectionDB = DatabaseManager.getInstance().getConnectionDB();
            PreparedStatement preparedStatement = connectionDB.getPreparedStatement("Select top 12 nick, drinkedToday from cwt_User order by drinkedToday desc");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                User user = new User();
                user.nick = resultSet.getString(1);
                user.drinkedToday = resultSet.getInt(2) / 2;
                res.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    public DrinkType getWanted() {
        return wanted;
    }

    public void setWanted(DrinkType wanted) {
        this.wanted = wanted;
    }

    public int getUserID() {
        return userID;
    }
}
