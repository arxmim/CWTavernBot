package org.nia.model;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.nia.db.ConnectionDB;
import org.nia.db.DatabaseManager;
import org.nia.logic.DrinkType;
import org.nia.logic.Food;
import org.nia.logic.Location;
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
    private boolean isBarmen;
    private boolean isAdmin;
    private int gold;
    private Food food;
    private Food wantedFood;
    private int foodCount;
    private int eatTotal;
    private int alkoCount;
    private int drinkedTotal;
    private Date visitTavern;
    private Location location;
    private Date locationReturnTime;

    static User getByID(int userID) {
        User res = null;
        try {
            ConnectionDB connectionDB = DatabaseManager.getInstance().getConnectionDB();
            PreparedStatement preparedStatement = connectionDB.getPreparedStatement("Select nick, name, isBarmen, alkoCount, lastDrinkTime" +
                    ", drinkedTotal, drinkType, wanted, isAdmin, gold" +
                    ", visitTavern, location, locationReturnTime, food, wantedFood" +
                    ", foodCount, eatTotal from cwt_User where UserID = ?");
            preparedStatement.setInt(1, userID);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                res = new User();
                res.userID = userID;
                res.nick = resultSet.getString(1);
                res.name = resultSet.getString(2);
                res.isBarmen = resultSet.getBoolean(3);
                res.alkoCount = resultSet.getInt(4);
                res.lastDrinkTime = resultSet.getTimestamp(5);
                res.drinkedTotal = resultSet.getInt(6);
                try {
                    res.drinkType = DrinkType.valueOf(resultSet.getString(7));
                } catch (Exception ignored) {
                }
                try {
                    res.wanted = DrinkType.valueOf(resultSet.getString(8));
                } catch (Exception ignored) {
                }
                res.isAdmin = resultSet.getBoolean(9);
                res.gold = resultSet.getInt(10);
                res.visitTavern = resultSet.getTimestamp(11);
                res.location = Location.valueOf(resultSet.getString(12));
                res.locationReturnTime = resultSet.getTimestamp(13);
                try {
                    res.food = Food.valueOf(resultSet.getString(14));
                } catch (Exception ignored) {
                }
                try {
                    res.wantedFood = Food.valueOf(resultSet.getString(15));
                } catch (Exception ignored) {
                }
                res.foodCount = resultSet.getInt(16);
                res.eatTotal = resultSet.getInt(17);

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
            res.isBarmen = false;
            res.drinkedTotal = 0;
            res.isAdmin = false;
            res.gold = 30;
            res.visitTavern = null;
            res.location = Location.TAVERN;
            res.locationReturnTime = null;
            res.foodCount = 0;
            res.eatTotal = 0;
            res.save();
        }
        return res;
    }

    public static User getByNick(String nick) {
        User res = null;
        nick = nick.replace("@", "");
        try {
            ConnectionDB connectionDB = DatabaseManager.getInstance().getConnectionDB();
            PreparedStatement preparedStatement = connectionDB.getPreparedStatement("Select userID, name, isBarmen, alkoCount, lastDrinkTime" +
                    ", drinkedTotal, drinkType, wanted, isAdmin, gold" +
                    ", visitTavern, location, locationReturnTime, food, wantedFood" +
                    ", foodCount, eatTotal from cwt_User where nick = ?");
            preparedStatement.setString(1, nick);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                res = new User();
                res.nick = nick;
                res.userID = resultSet.getInt(1);
                res.name = resultSet.getString(2);
                res.isBarmen = resultSet.getBoolean(3);
                res.alkoCount = resultSet.getInt(4);
                res.lastDrinkTime = resultSet.getTimestamp(5);
                res.drinkedTotal = resultSet.getInt(6);
                try {
                    res.drinkType = DrinkType.valueOf(resultSet.getString(7));
                } catch (Exception ignored) {
                }
                try {
                    res.wanted = DrinkType.valueOf(resultSet.getString(8));
                } catch (Exception ignored) {
                }
                res.isAdmin = resultSet.getBoolean(9);
                res.gold = resultSet.getInt(10);
                res.visitTavern = resultSet.getTimestamp(11);
                res.location = Location.valueOf(resultSet.getString(12));
                res.locationReturnTime = resultSet.getTimestamp(13);
                try {
                    res.food = Food.valueOf(resultSet.getString(14));
                } catch (Exception ignored) {
                }
                try {
                    res.wantedFood = Food.valueOf(resultSet.getString(15));
                } catch (Exception ignored) {
                }
                res.foodCount = resultSet.getInt(16);
                res.eatTotal = resultSet.getInt(17);
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
            PreparedStatement preparedStatement = connectionDB.getPreparedStatement(
                    "Select userID, nick, name, isBarmen, alkoCount" +
                            ", lastDrinkTime, drinkedTotal, drinkType, wanted, isAdmin" +
                            ", gold, visitTavern, location, locationReturnTime, food" +
                            ", wantedFood, foodCount, eatTotal from cwt_User");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                User user = new User();
                user.userID = resultSet.getInt(1);
                user.nick = resultSet.getString(2);
                user.name = resultSet.getString(3);
                user.isBarmen = resultSet.getBoolean(4);
                user.alkoCount = resultSet.getInt(5);
                user.lastDrinkTime = resultSet.getTimestamp(6);
                user.drinkedTotal = resultSet.getInt(7);
                try {
                    user.drinkType = DrinkType.valueOf(resultSet.getString(8));
                } catch (Exception ignored) {
                }
                try {
                    user.wanted = DrinkType.valueOf(resultSet.getString(9));
                } catch (Exception ignored) {
                }
                user.isAdmin = resultSet.getBoolean(10);
                user.gold = resultSet.getInt(11);
                user.visitTavern = resultSet.getTimestamp(12);
                user.location = Location.valueOf(resultSet.getString(13));
                user.locationReturnTime = resultSet.getTimestamp(14);
                try {
                    user.food = Food.valueOf(resultSet.getString(15));
                } catch (Exception ignored) {
                }
                try {
                    user.wantedFood = Food.valueOf(resultSet.getString(16));
                } catch (Exception ignored) {
                }
                user.foodCount = resultSet.getInt(17);
                user.eatTotal = resultSet.getInt(18);
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
                        ", isBarmen = ?" +
                        ", alkoCount = ?" +
                        ", lastDrinkTime = ?" +
                        ", drinkedTotal = ?" +
                        ", drinkType = ?" +
                        ", wanted = ?" +
                        ", isAdmin = ?" +
                        ", gold = ?" +
                        ", visitTavern = ?" +
                        ", location = ?" +
                        ", locationReturnTime = ?" +
                        ", food = ?" +
                        ", wantedFood = ?" +
                        ", foodCount = ?" +
                        ", eatTotal = ?" +
                        " where UserID = ?");
                preparedStatement.setString(1, nick);
                preparedStatement.setString(2, name);
                preparedStatement.setBoolean(3, isBarmen);
                preparedStatement.setInt(4, alkoCount);
                if (lastDrinkTime != null) {
                    preparedStatement.setTimestamp(5, new Timestamp(lastDrinkTime.getTime()));
                } else {
                    preparedStatement.setNull(5, Types.TIMESTAMP);
                }
                preparedStatement.setInt(6, drinkedTotal);
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
                preparedStatement.setBoolean(9, isAdmin);
                preparedStatement.setInt(10, gold);
                if (visitTavern != null) {
                    preparedStatement.setTimestamp(11, new Timestamp(visitTavern.getTime()));
                } else {
                    preparedStatement.setNull(11, Types.TIMESTAMP);
                }
                preparedStatement.setString(12, location.name());
                if (locationReturnTime != null) {
                    preparedStatement.setTimestamp(13, new Timestamp(locationReturnTime.getTime()));
                } else {
                    preparedStatement.setNull(13, Types.TIMESTAMP);
                }
                if (food != null) {
                    preparedStatement.setString(14, food.name());
                } else {
                    preparedStatement.setNull(14, Types.VARCHAR);
                }
                if (wantedFood != null) {
                    preparedStatement.setString(15, wantedFood.name());
                } else {
                    preparedStatement.setNull(15, Types.VARCHAR);
                }
                preparedStatement.setInt(16, foodCount);
                preparedStatement.setInt(17, eatTotal);
                preparedStatement.setInt(18, userID);
                preparedStatement.execute();
            } else {
                preparedStatement = connectionDB.getPreparedStatement(
                        "INSERT INTO cwt_User (UserID, nick, name, isBarmen, alkoCount" +
                                ", lastDrinkTime, drinkedTotal, drinkType, wanted, isAdmin" +
                                ", gold, visitTavern, location, locationReturnTime, food" +
                                ", wantedFood, foodCount, eatTotal) VALUES" +
                                " (?, ?, ?, ?, ?" +
                                ", ?, ?, ?, ?, ?" +
                                ", ?, ?, ?, ?, ?" +
                                ", ?, ?, ?)");
                preparedStatement.setInt(1, userID);
                preparedStatement.setString(2, nick);
                preparedStatement.setString(3, name);
                preparedStatement.setBoolean(4, isBarmen);
                preparedStatement.setInt(5, alkoCount);
                if (lastDrinkTime != null) {
                    preparedStatement.setTimestamp(6, new Timestamp(lastDrinkTime.getTime()));
                } else {
                    preparedStatement.setNull(6, Types.TIMESTAMP);
                }
                preparedStatement.setInt(7, drinkedTotal);
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
                preparedStatement.setBoolean(10, isAdmin);
                preparedStatement.setInt(11, gold);
                if (visitTavern != null) {
                    preparedStatement.setTimestamp(12, new Timestamp(visitTavern.getTime()));
                } else {
                    preparedStatement.setNull(12, Types.TIMESTAMP);
                }
                preparedStatement.setString(13, location.name());
                if (locationReturnTime != null) {
                    preparedStatement.setTimestamp(14, new Timestamp(locationReturnTime.getTime()));
                } else {
                    preparedStatement.setNull(14, Types.TIMESTAMP);
                }
                if (food != null) {
                    preparedStatement.setString(15, food.name());
                } else {
                    preparedStatement.setNull(15, Types.VARCHAR);
                }
                if (wantedFood != null) {
                    preparedStatement.setString(16, wantedFood.name());
                } else {
                    preparedStatement.setNull(16, Types.VARCHAR);
                }
                preparedStatement.setInt(17, foodCount);
                preparedStatement.setInt(18, eatTotal);
                preparedStatement.execute();
            }
            res = true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    public void setIsBarmen(boolean isBarmen) {
        this.isBarmen = isBarmen;
    }

    public boolean isBarmen() {
        return isBarmen;
    }

    public boolean isAdmin() {
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

    public void setDrinkedTotal(int drinkedTotal) {
        this.drinkedTotal = drinkedTotal;
    }

    public void setLastDrinkTime(Date lastDrinkTime) {
        this.lastDrinkTime = lastDrinkTime;
    }

    public Date getLastDrinkTime() {
        return lastDrinkTime;
    }

    public int getDrinkedTotal() {
        return drinkedTotal;
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
            PreparedStatement preparedStatement = connectionDB.getPreparedStatement("Select top 12 nick, drinkedTotal from cwt_User order by drinkedTotal desc");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                User user = new User();
                user.nick = resultSet.getString(1);
                user.drinkedTotal = resultSet.getInt(2) / 2;
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

    public int getGold() {
        return gold;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }

    public boolean IsVisitTavernToday() {
        return DateUtils.isSameDay(visitTavern, new Date());
    }

    public Location getLocation() {
        return location;
    }

    public boolean inTavern() {
        return location == Location.TAVERN;
    }
    public boolean onQuest() {
        return location.isQuest();
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setLocationReturnTime(Date locationReturnTime) {
        this.locationReturnTime = locationReturnTime;
    }

    public Date getLocationReturnTime() {
        return locationReturnTime;
    }

    public void setFoodCount(int foodCount) {
        this.foodCount = foodCount;
    }

    public void setFood(Food food) {
        this.food = food;
    }

    public void setWantedFood(Food wantedFood) {
        this.wantedFood = wantedFood;
    }

    public int getFoodCount() {
        return foodCount;
    }

    public void setEatTotal(int eatTotal) {
        this.eatTotal = eatTotal;
    }

    public int getEatTotal() {
        return eatTotal;
    }

    public Food getFood() {
        return food;
    }

    public Food getWantedFood() {
        return wantedFood;
    }
}
